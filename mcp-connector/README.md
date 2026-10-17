# APK Studio Claude MCP Connector

A Cloudflare Worker that bridges APK Studio's Gemini-encoded `+s` / `+q` workflow to Claude through a remote MCP connector.

## What it does

- APK Studio queues its already-encoded prompt and target file.
- Claude's `get_pending_task` tool reads the queued task.
- Claude generates the complete file and calls `submit_code`.
- APK Studio polls for the result and loads it into the editor for review.
- It does not call Claude's model API, bypass usage limits, export ZIPs, or install code.

## Deploy / update on Cloudflare Workers

Run these commands from the `mcp-connector` directory in your Chromebook Linux terminal.

1. Install dependencies and log in if needed:
   ```sh
   npm install
   npx wrangler whoami
   ```

2. Create the OAuth storage namespace (only if you have not already created it):
   ```sh
   npx wrangler kv namespace create OAUTH_KV
   ```
   Copy the ID printed by Wrangler into the `OAUTH_KV` entry in `wrangler.jsonc`. Keep the existing `APK_STUDIO_KV` ID unchanged.

3. Set a private sign-in code for the OAuth approval page. This is separate from the APK Studio app's existing `AUTH_TOKEN`:
   ```sh
   openssl rand -hex 32 | npx wrangler secret put LOGIN_SECRET
   ```
   Do not share this code or commit it to GitHub. If Wrangler prompts, paste a freshly generated random value instead.

4. Keep the existing `AUTH_TOKEN` secret unchanged. It is still used by the APK Studio app's `/api/tasks` routes.

5. Deploy:
   ```sh
   npm install
   npx wrangler deploy
   ```

The Worker uses Cloudflare's OAuth 2.1 provider, dynamic client registration, and Client ID Metadata Document support. The OAuth storage namespace is separate from APK Studio's task queue namespace.

## Add it to Claude

In Claude, open **Customize → Connectors → Add custom connector** and enter this MCP URL:

```
https://apk-claude-connector.apk-studio-tools.workers.dev/mcp
```

Choose the sign-in option Claude offers and complete the APK Studio connector sign-in page using the private `LOGIN_SECRET` you configured in Cloudflare. If Claude asks how to identify its OAuth client, try **Register automatically** first. If it offers **Claude's published identity**, that should also be supported after deployment because Client ID Metadata Documents are enabled.

If you previously added the connector, remove the failed one and add it again after deploying.

## Use it

1. In APK Studio, set the Worker base URL (without `/mcp`) and the existing `AUTH_TOKEN`.
2. Queue a task using the app's normal Gemini-encoded `+s` or `+q` workflow.
3. In Claude, enable APK Studio and ask it to read the pending task, generate the complete target file, and submit it.
4. Review the returned code in APK Studio before saving/exporting.

## Routes

- `GET /health` — public health check.
- `POST /api/tasks` — APK Studio upload protected by `AUTH_TOKEN`.
- `GET /api/tasks/:taskId/result` — APK Studio result polling protected by `AUTH_TOKEN`.
- `/mcp` — MCP endpoint protected by OAuth access tokens.
- `/authorize`, `/oauth/token`, `/oauth/register` and discovery metadata — OAuth flow.

## Safety and limitations

- Never share or commit either secret.
- Only queue source code you are comfortable sending to Claude.
- The server stores one pending task at a time; a new task replaces the previous one. Results expire after one day.
- Claude must be open and asked to process the queued task; MCP cannot silently start a Claude conversation from APK Studio.
- Returned code is placed in the editor for review. It is not automatically executed, installed, or exported.
