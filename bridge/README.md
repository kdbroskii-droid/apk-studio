# APK Studio Claude bridge

This bridge runs locally on your Windows PC. It uses the signed-in Claude Code CLI session, not an Anthropic API key, and launches this repository's MCP server for each generation.

## Requirements

- Windows 10/11
- Node.js 18 or newer
- Claude Code CLI installed and signed in with a plan that includes Claude Code (currently Claude Pro or Max)
- Phone and PC on the same trusted Wi-Fi network

## Start

1. Install Claude Code using Anthropic's official instructions: https://docs.anthropic.com/en/docs/claude-code/getting-started
2. Open a terminal in this repository and run claude once. Complete sign-in using your eligible Claude subscription.
3. Double-click start-bridge.bat.
4. If Windows Firewall asks, allow Node.js only on your Private network. Do not port-forward this service to the public internet.
5. Copy the bridge access token printed in the terminal.
6. Find the PC's local IPv4 address with ipconfig. In APK Studio, set the URL to http://YOUR-PC-IP:8787/generate and paste the token.
7. Keep the terminal open while generating scripts.

The token is generated locally and saved in bridge/.bridge-token (ignored by Git). The bridge accepts generation requests only with that token. The /health endpoint is unauthenticated and returns status only.

## MCP tools

- get_token_library: reads the request's exact shared token dictionary.
- read_current_file: reads the selected file for +q.
- validate_generated_code: performs basic output checks; it is not a compiler or security audit.

Claude is invoked with the repo's .mcp.json. The bridge does not automatically write generated code to your PC. APK Studio receives the output for review and stores it in its in-memory project until you save/export.

## Security notes

The bridge listens on the local network at port 8787. Keep the token private, use only a trusted home/private network, and stop the bridge when finished. Do not expose port 8787 to the internet. The phone-to-PC connection uses HTTP on the local network, so use it only on a trusted network. The bridge does not log prompts or generated source to its console, but Claude Code itself processes your request according to its own account terms and settings.
