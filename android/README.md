# APK Studio for Android

APK Studio is a native Android starter app for inspecting APK archives, editing text files inside source-project ZIPs, renaming supported Android app-name fields, exporting a modified ZIP, and generating scripts through a Gemini-to-DeepSeek pipeline.

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
- Use the rounded **Two-AI Generator** panel. Add your Gemini and DeepSeek API keys, then enter a task.
- **+s Generate new script** sends the request to Gemini to encode using the shared numeric token library, then sends the encoded request and the same library to DeepSeek for code generation.
- **+q Change selected script** follows the same pipeline but includes the selected source file so DeepSeek can make a targeted change.
- The initial library includes your sample mapping `local number = 10 → 0916820001747701-10` and starter tokens. Edit the library using one `code=meaning` mapping per line. Gemini can add new mappings, which are saved locally and shared with DeepSeek.
- Review all generated code, then save/export the source project ZIP.

## API keys and costs

Create your own keys at [Google AI Studio](https://aistudio.google.com/apikey) and [DeepSeek Platform](https://platform.deepseek.com/). API requests may incur charges or have usage limits depending on each provider and account. Keys are stored in the app's private preferences on this device. Anyone who receives an APK containing a key may be able to extract it, so do not distribute a build that contains your personal keys.

## Important current limitations

- APK import is inspection-only. The app can list archive contents, but it does not rebuild or sign modified APKs yet. Most APKs store `AndroidManifest.xml` as compiled binary XML, not ordinary text.
- The app does not compile imported source projects on the phone. Export the ZIP and build it in Android Studio or a CI workflow.
- The numeric code library is a starter library that grows as Gemini returns new mappings; it is not yet a massive pretrained dictionary. Keep the mapping meanings stable, because changing a mapping can change how future requests are interpreted.
- AI output can contain errors. Review and test all changes before using them in a real project.

## Manual build

Install Android Studio with Android SDK 35 and Java 17. Open the `android/` folder as a project, sync Gradle, then build the `app` debug variant.
