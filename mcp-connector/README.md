# APK Studio Claude MCP Connector

A small Cloudflare Worker that bridges APK Studio's Gemini-encoded `+s` / `+q` workflow to Claude through a remote MCP connector.

## What it does

- APK Studio uploads the already-encoded full prompt to a private queue.
- Claude's `get_pending_task` tool reads the queued prompt and target file.
- Claude generates the complete file and calls `submit_code`.
- APK Studio polls for the result and loads the returned file into its editor for review.
- It does not call Claude's model API, bypass Claude usage limits, export the ZIP, or install code.

## Deploy on Cloudflare Workers (free tier)

You need a Cloudflare account and Node.js/npm on a PC or Chromebook Linux environment.

1. Create a Workers KV namespace:
   ```sh
   npx wrangler@latest login
   npx wrangler@latest kv namespace create APK_STUDIO_KV
   ```
2. Copy the returned namespace ID into `wrangler.jsonc`, replacing `REPLACE_WITH_YOUR_KV_NAMESPACE_ID`.
3. Create a long random secret. On a PC with Node.js:
   ```sh
   node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
   ```
   Keep the generated value private. Set it as the Worker secret:
   ```sh
   npx wrangler@latest secret put AUTH_TOKEN
   ```
   Paste the random value when prompted. Use this exact value in APK Studio and in Claude's connector request header.
4. Deploy from this directory:
   ```sh
   npm install
   npx wrangler@latest deploy
   ```
5. Wrangler prints a URL such as `https://apk-studio-claude-connector.<your-subdomain>.workers.dev`. Keep it private enough that you only share it with your own Claude account. The URL alone is not sufficient to authenticate.

## Add it to Claude

In Claude, open **Customize → Connectors → Add → Custom → Web**. Add a name such as **APK Studio**, and use this MCP URL:

`https://YOUR-WORKER-URL/mcp`

Choose **No sign in** if offered, then configure the connector's request header:
- Header name: `Authorization`
- Header value: `Bearer YOUR_RANDOM_AUTH_TOKEN`

The exact connector form may vary by Claude client/account. If custom request headers aren't available in your account, do not remove authentication from the Worker; use a connector setup that supports the header.

Free Claude accounts are limited to one custom connector. After connecting, enable APK Studio in the conversation's tools menu.

## Use it

1. In APK Studio, enter the Worker base URL (without `/mcp`) and the same secret token.
2. Import a source ZIP, select a file for `+q`, or choose `+s` for a new script.
3. Enter your request and tap the normal Gemini encode button.
4. Tap **Queue for Claude connector + wait**.
5. Open Claude and ask: **“Check APK Studio for the pending task. Use its task instructions, generate the complete target file, and submit it with the connector.”**
6. Claude calls `get_pending_task`, generates the full file, and calls `submit_code`. APK Studio polls and places the result in the editor. Review the code, save, and export the ZIP yourself.

## API routes

- `GET /health` — unauthenticated health check.
- `POST /api/tasks` — authenticated APK Studio queue upload.
- `GET /api/tasks/:taskId/result` — authenticated result polling.
- `POST /mcp` — authenticated MCP JSON-RPC endpoint.

## Safety and limitations

- The shared token is a bearer secret. Never commit it to GitHub or paste it into source code. If leaked, rotate the Worker secret and update both clients.
- Only queue source code you are comfortable sending to Claude.
- The server stores one pending task at a time; a new task replaces the previous pending task. Results expire after one day.
- Claude must be open and asked to process the queued task; MCP cannot silently start a Claude conversation from APK Studio.
- Returned code is loaded into the editor for review. It is not automatically executed, installed, or exported.
- This bridge does not make Claude usage free or bypass Claude's plan limits.
