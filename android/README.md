# APK Studio for Android

APK Studio is a native Android starter app for inspecting APK archives, editing text files inside source-project ZIPs, renaming supported Android app-name fields, exporting a modified ZIP, and generating scripts through Gemini plus Claude Code/MCP.

## Build on GitHub

1. Open the repository's Actions tab.
2. Select Build APK Studio Android.
3. Open the latest completed workflow run and download the apk-studio-debug artifact.
4. Extract the ZIP and install app-debug.apk on your Android phone. You may need to allow installation from your browser/file manager.

The workflow creates a debug-signed APK suitable for testing, not a production release.

## Use the app

- Import a source ZIP or APK. Source ZIPs can be edited; APK imports are inspection-only.
- Enter your Gemini API key. Gemini encodes your request using the shared numeric token library.
- On your Windows PC, install Node.js 18+ and Claude Code. Sign in to Claude Code with a plan that includes Claude Code.
- Start the PC bridge by double-clicking start-bridge.bat in the repository folder. Keep the terminal open.
- Copy the bridge token printed in the terminal. Find your PC's local IPv4 address with ipconfig.
- In APK Studio, set the bridge URL to http://YOUR-PC-IP:8787/generate and enter the token. Phone and PC must be on the same trusted Wi-Fi.
- +s generates a new script. +q modifies the selected editable text file. Claude reads the same token library and current file through the repository MCP server.
- Review generated code, save it to the working project, and export the modified source ZIP.

## API keys, subscription and costs

Gemini uses a Google AI API key and provider usage limits may apply. Claude is invoked through the locally signed-in Claude Code CLI session, not through a Claude API key. Claude Code availability depends on your account plan; check Anthropic's current plan documentation.

## Important limitations and safety

- The bridge runs on your PC and must stay open. The PC must be awake and connected to the same Wi-Fi as your phone.
- Keep the bridge token private. The bridge uses local HTTP, so use a trusted private network and never expose port 8787 to the public internet.
- APK import is inspection-only. The app does not rebuild or sign modified APKs yet.
- The app does not compile imported source projects on the phone. Export the ZIP and build it in Android Studio or CI.
- The numeric token library is a starter dictionary that grows with new mappings; it is not a giant pretrained dictionary. Keep meanings stable.
- MCP checks generated output only at a basic level; it is not a compiler or security audit. Review and test all generated code before use.
