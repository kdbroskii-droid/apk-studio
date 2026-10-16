# APK Studio

APK Studio is a mobile-first Android source-project editor. The native Android app lives in android/.

## Android app

- Rounded, dark UI with spaced cards and vertically scrolling sections designed to avoid overlapping controls.
- Import source ZIPs to edit supported text files, update common Android app-name fields, and export a modified ZIP.
- Import APK files for archive inspection only; rebuilding and signing compiled APKs is not implemented.
- Gemini encodes requests into the numeric token stream and extends the local token library.
- Optional remote Claude MCP connector: queue the encoded `+s` / `+q` task, ask Claude to process it through the connector, and receive the complete file back into the editor without manual code copy/paste.
- Manual copy/paste remains available as a fallback.
- `+s` prepares a new script request. `+q` prepares a change to the currently selected text file.
- The token library is editable and grows when Gemini returns new mappings. It is saved locally on your device.

## Use the AI workflow

1. Enter a Gemini API key in APK Studio.
2. Import a source ZIP if you want to change an existing file.
3. Describe the task and choose `+s` for a new script or `+q` for the selected file.
4. Tap **Copy prompt for Claude Free**, open Claude in the app or browser, paste the prompt, and send it.
5. Copy Claude's complete code response, return to APK Studio, paste it into the response box, and tap **Apply Claude response to project**.
6. Review the file, save changes, and export the modified source ZIP.

The manual workflow does not require an MCP server. The optional connector requires a free Cloudflare Worker + KV setup and a Claude custom connector. Claude must be prompted to process queued tasks; this bridge does not start Claude conversations automatically. Gemini API usage limits and Claude plan limits still apply.

See [mcp-connector/README.md](mcp-connector/README.md) for deployment and setup instructions.

## Build the Android APK

Open Actions → Build APK Studio Android → Run workflow. Download the `apk-studio-debug` artifact from a completed workflow run and extract `app-debug.apk`.

See android/README.md for setup, API-key notes, and limitations.
