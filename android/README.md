# APK Studio for Android

APK Studio is a native Android starter app for inspecting APK archives, editing text files inside source-project ZIPs, renaming supported Android app-name fields, exporting a modified ZIP, and generating scripts through Gemini plus a manual Claude Free copy/paste workflow.

## Build on GitHub

1. Open the repository's Actions tab.
2. Select Build APK Studio Android.
3. Open the latest completed workflow run and download the `apk-studio-debug` artifact.
4. Extract the ZIP and install `app-debug.apk` on your Android phone. You may need to allow installation from your browser/file manager.

The workflow creates a debug-signed APK suitable for testing, not a production release.

## Use the app

- Import a source ZIP or APK. Source ZIPs can be edited; APK imports are inspection-only.
- Enter your Gemini API key. Gemini encodes your request using the shared numeric token library.
- Describe the task and choose `+s` to prepare a new script or `+q` to modify the selected editable text file.
- Tap **Copy prompt for Claude Free**. Open the regular Claude app or website, paste the prompt, and send it.
- Copy Claude's complete code response back into APK Studio and paste it into the response box.
- Tap **Apply Claude response to project**, review the code, save changes, and export the modified source ZIP.

You do not need Claude Code, a PC bridge, an MCP server, or a paid Claude subscription for this manual workflow. Claude Free has usage limits, and the copy/paste step is manual.

## API keys and costs

Gemini uses a Google AI API key; Google's API usage limits and any applicable charges depend on your account and current terms. APK Studio does not use a Claude API key. The Claude stage happens manually in your existing Claude Free chat.

## Important limitations and safety

- APK import is inspection-only. The app does not rebuild or sign modified APKs yet.
- The app does not compile imported source projects on the phone. Export the ZIP and build it in Android Studio or CI.
- The numeric token library is a starter dictionary that grows with new mappings; it is not a giant pretrained dictionary. Keep meanings stable.
- Generated code is not automatically compiled or security-audited. Review and test it before using it.
- Treat API keys as secrets. Only paste your Gemini key into the app you trust, and do not share it in prompts or source code.
