# APK Studio

APK Studio is a mobile-first Android source-project editor. The native Android app lives in android/.

## Android app

- Rounded, dark UI with spaced cards and vertically scrolling sections designed to avoid overlapping controls.
- Import source ZIPs to edit supported text files, update common Android app-name fields, and export a modified ZIP.
- Import APK files for archive inspection only; rebuilding and signing compiled APKs is not implemented.
- Two-stage AI workflow: Gemini encodes a request into a numeric token stream; Claude Code, running on your Windows PC under your eligible Claude subscription, generates code through a local authenticated bridge and MCP server.
- +s generates a new script. +q modifies the currently selected text file.
- The token library is editable and grows when Gemini returns new mappings. The same per-request library is exposed to Claude through the MCP server.

## Claude bridge setup

1. Install Node.js 18+ and Claude Code on the Windows PC.
2. Sign in to Claude Code with a plan that includes Claude Code.
3. Clone this repository to the PC and run start-bridge.bat.
4. Put the PC's local IPv4 address and printed bridge token into APK Studio on the phone. Keep both devices on the same trusted Wi-Fi.

See bridge/README.md for details and security notes.

## Build the Android APK

Open Actions → Build APK Studio Android → Run workflow. Download the apk-studio-debug artifact from a completed workflow run and extract app-debug.apk.

See android/README.md for Android setup, API-key notes, and limitations.
