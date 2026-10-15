# APK Studio

APK Studio is a mobile-first Android source-project editor. The native Android app lives in `android/`.

## Android app

- Rounded, dark UI with spaced cards and vertically scrolling sections designed to avoid overlapping controls.
- Import source ZIPs to edit supported text files, update common Android app-name fields, and export a modified ZIP.
- Import APK files for archive inspection only; rebuilding and signing compiled APKs is not implemented.
- Two-stage AI workflow: Gemini encodes a request into a numeric token stream; DeepSeek reads that stream with the same editable token library and generates code.
- `+s` generates a new script. `+q` modifies the currently selected text file.
- The token library is saved locally and can be extended; new mappings returned by Gemini are added to the library and passed to DeepSeek.

## Build the Android APK

Open **Actions → Build APK Studio Android → Run workflow**. Download the `apk-studio-debug` artifact from a completed workflow run and extract `app-debug.apk`.

See [android/README.md](android/README.md) for setup, API-key notes, and limitations.
