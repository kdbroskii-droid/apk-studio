# APK Studio

APK Studio is a mobile-first Android source-project editor starter. The existing browser prototype is in `index.html`; the native Android app is in `android/`.

## Android app

Import source ZIPs to edit supported text files, update common Android app-name fields, use Gemini for AI-assisted file edits, and export a project ZIP. APK files can currently be inspected only; rebuilding compiled APKs is not implemented.

## Get the Android build

Open **Actions → Build APK Studio Android → Run workflow**. Download the `apk-studio-debug` artifact from the completed workflow run and extract `app-debug.apk`.

See [android/README.md](android/README.md) for setup and limitations.
