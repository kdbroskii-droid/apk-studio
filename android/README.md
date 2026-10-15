# APK Studio for Android

APK Studio is a native Android starter app for inspecting APK archives, editing text files inside source-project ZIPs, changing supported Android app-name fields, exporting a modified ZIP, and asking Gemini to rewrite the currently selected text file.

## Build on GitHub

1. Open the repository's **Actions** tab.
2. Select **Build APK Studio Android**.
3. Tap **Run workflow** (or push a change under `android/`).
4. Open the finished workflow run and download the `apk-studio-debug` artifact.
5. Extract the ZIP and install `app-debug.apk` on your Android phone. You may need to allow installation from your browser/file manager.

The workflow uses Java 17, Gradle 8.9, and Android Gradle Plugin 8.7.3. It creates a debug-signed APK suitable for testing, not a production release.

## Use it

- Tap **Import source ZIP or APK** and select a ZIP project or APK from your phone.
- Source ZIP: choose a text file, edit it, and save the working copy. The file list is limited to 300 visible entries; import is limited to 4,000 files / 40 MiB.
- **Rename app in source files** updates text-based `android:label` values in `AndroidManifest.xml` and `app_name` in `res/values/strings.xml`, when those fields exist.
- Enter a Gemini API key and prompt, select a source text file, and tap **Ask AI to edit selected file**. Review the result before exporting.
- Export the modified project as a ZIP.

## Important current limitations

- APK import is inspection-only. The app can list archive contents, but it does not rebuild or sign modified APKs yet. Most APKs store `AndroidManifest.xml` as compiled binary XML, not ordinary text.
- The app does not yet compile imported source projects on the phone. Export the ZIP and build it in Android Studio or a CI workflow.
- AI editing sends the selected file and prompt to Google's Gemini API. Your API key is stored in this app's private preferences on your phone; it is sent to Google for API authentication. Do not paste secrets or signing keys into prompts.
- Keep an untouched backup. AI output can contain errors, so review and test every change.

## Manual build

Install Android Studio with Android SDK 35 and Java 17. Open the `android/` folder as a project, sync Gradle, then build the `app` debug variant.
