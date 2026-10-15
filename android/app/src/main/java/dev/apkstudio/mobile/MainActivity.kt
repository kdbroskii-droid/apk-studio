package dev.apkstudio.mobile

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.graphics.Color
import android.view.ViewGroup
import android.widget.*
import org.json.JSONObject
import org.json.JSONArray
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class MainActivity : Activity() {
    private val io = Executors.newSingleThreadExecutor()
    private val files = linkedMapOf<String, ByteArray>()
    private var selectedPath: String? = null
    private var sourceName = "No project imported"
    private var isApk = false
    private lateinit var status: TextView
    private lateinit var fileList: LinearLayout
    private lateinit var editor: EditText
    private lateinit var fileLabel: TextView
    private lateinit var projectLabel: TextView
    private lateinit var appNameInput: EditText
    private lateinit var aiPrompt: EditText
    private lateinit var apiKey: EditText
    private lateinit var root: LinearLayout
    private val blue = Color.rgb(67, 130, 255)
    private val bg = Color.rgb(12, 16, 26)
    private val panel = Color.rgb(22, 29, 43)
    private val white = Color.rgb(235, 241, 255)
    private val muted = Color.rgb(151, 164, 188)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        buildUi()
    }

    private fun buildUi() {
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(bg); setPadding(16, 14, 16, 12) }
        val scroll = ScrollView(this)
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(body)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        body.addView(TextView(this).apply { text = "APK STUDIO"; textSize = 26f; setTextColor(blue); setTypeface(null, 1) })
        body.addView(TextView(this).apply { text = "Your Android project workshop"; textSize = 13f; setTextColor(muted); setPadding(0, 0, 0, 14) })
        body.addView(button("Import source ZIP or APK") { openImport() })
        projectLabel = TextView(this).apply { text = sourceName; textSize = 13f; setTextColor(white); setPadding(0, 12, 0, 8) }
        body.addView(projectLabel)
        status = TextView(this).apply { text = "Import a source project ZIP to edit files, or an APK to inspect its contents."; textSize = 12f; setTextColor(muted); setPadding(0, 0, 0, 12) }
        body.addView(status)
        section(body, "APP NAME")
        appNameInput = EditText(this).apply { hint = "New visible app name"; setTextColor(white); setHintTextColor(muted); textSize = 14f; setSingleLine(true); setBackgroundColor(panel) }
        body.addView(appNameInput, matchWrap())
        body.addView(button("Rename app in source files") { renameInSource() })
        section(body, "PROJECT FILES")
        fileList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        body.addView(fileList)
        fileLabel = TextView(this).apply { text = "Select a text file to edit"; setTextColor(muted); textSize = 12f; setPadding(0, 10, 0, 6) }
        body.addView(fileLabel)
        editor = EditText(this).apply {
            hint = "File contents appear here"; gravity = android.view.Gravity.TOP or android.view.Gravity.START
            minLines = 8; setTextColor(white); setHintTextColor(muted); textSize = 12f
            typeface = android.graphics.Typeface.MONOSPACE; setPadding(10, 10, 10, 10); setBackgroundColor(panel)
        }
        body.addView(editor, LinearLayout.LayoutParams(-1, 240))
        body.addView(button("Save changes to project") { saveCurrentFile() })
        body.addView(button("Export modified project ZIP") { exportZip() })
        section(body, "AI EDITOR · GEMINI")
        apiKey = EditText(this).apply {
            hint = "Gemini API key (stored on this device)"; setTextColor(white); setHintTextColor(muted); textSize = 13f
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            setBackgroundColor(panel); setText(getPreferences(0).getString("gemini_key", "") ?: "")
        }
        body.addView(apiKey, matchWrap())
        aiPrompt = EditText(this).apply {
            hint = "Describe the change you want"; setTextColor(white); setHintTextColor(muted); textSize = 13f; minLines = 3
            gravity = android.view.Gravity.TOP; setBackgroundColor(panel)
        }
        body.addView(aiPrompt, matchWrap())
        body.addView(button("Ask AI to edit selected file") { runAiEdit() })
        body.addView(TextView(this).apply {
            text = "APK imports are read-only in this version. Compiled APK manifests need specialised rebuild/signing tools. Source ZIPs can be edited and exported. Keep backups and only modify apps you are authorised to change."
            textSize = 11f; setTextColor(muted); setPadding(0, 14, 0, 18)
        })
        setContentView(root)
    }

    private fun section(parent: LinearLayout, title: String) {
        parent.addView(TextView(this).apply { text = title; textSize = 11f; setTextColor(blue); setTypeface(null, 1); setPadding(0, 20, 0, 7) })
    }
    private fun matchWrap() = LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 8 }
    private fun button(label: String, action: () -> Unit) = Button(this).apply {
        text = label; isAllCaps = false; textSize = 13f; setTextColor(Color.WHITE)
        setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue))
        setOnClickListener { action() }
        layoutParams = LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 7 }
    }

    private fun openImport() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE); type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/zip", "application/java-archive", "application/vnd.android.package-archive", "application/octet-stream"))
        }
        startActivityForResult(intent, 100)
    }

    @Deprecated("Deprecated")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) { data?.data?.let { exportToUri(it) }; return }
        if (requestCode != 100 || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        io.execute {
            try {
                val name = queryName(uri) ?: "project.zip"
                val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: throw IOException("Could not read file")
                val apkFile = name.lowercase().endsWith(".apk")
                val imported = linkedMapOf<String, ByteArray>()
                ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
                    var entry = zip.nextEntry
                    var total = 0L
                    while (entry != null && imported.size < 4000 && total < 40L * 1024 * 1024) {
                        if (!entry.isDirectory && !entry.name.startsWith("/") && !entry.name.split("/").contains("..")) {
                            val item = zip.readBytes()
                            total += item.size
                            if (total <= 40L * 1024 * 1024) imported[entry.name] = item
                        }
                        zip.closeEntry(); entry = zip.nextEntry
                    }
                }
                runOnUiThread {
                    files.clear(); files.putAll(imported); sourceName = name; isApk = apkFile; selectedPath = null
                    projectLabel.text = name; editor.setText(""); editor.isEnabled = !isApk
                    renderFileList()
                    status.text = if (isApk) "APK inspected: " + files.size + " entries. Compiled APK editing/rebuilding is not enabled yet."
                    else "Source archive loaded: " + files.size + " files. Choose a text file to edit."
                    if (isApk) {
                        val manifest = files["AndroidManifest.xml"]
                        status.text = "APK inspected: " + files.size + " entries. Manifest: " + if (manifest == null) "missing" else if (looksText(manifest)) "text XML" else "compiled binary XML" + ". APK rebuild is not enabled."
                    }
                }
            } catch (e: Exception) { runOnUiThread { status.text = "Import failed: " + e.message } }
        }
    }

    private fun queryName(uri: Uri): String? {
        contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
            if (it.moveToFirst()) return it.getString(0)
        }
        return uri.lastPathSegment
    }

    private fun renderFileList() {
        fileList.removeAllViews()
        files.keys.sorted().take(300).forEach { path ->
            val editable = isEditable(path, files[path]!!)
            fileList.addView(TextView(this).apply {
                text = (if (editable) "▤  " else "▧  ") + path; textSize = 12f
                setTextColor(if (editable) white else muted); setPadding(9, 8, 9, 8)
                setBackgroundColor(if (selectedPath == path) panel else bg)
                setOnClickListener { selectFile(path) }
            })
        }
    }

    private fun isEditable(path: String, bytes: ByteArray): Boolean {
        val p = path.lowercase()
        return bytes.size <= 1024 * 1024 && (p.endsWith(".xml") || p.endsWith(".kt") || p.endsWith(".java") || p.endsWith(".gradle") || p.endsWith(".json") || p.endsWith(".txt") || p.endsWith(".html") || p.endsWith(".css") || p.endsWith(".js") || p.endsWith(".md") || p.endsWith(".properties") || p.endsWith(".yml") || p.endsWith(".yaml")) && looksText(bytes)
    }
    private fun looksText(bytes: ByteArray): Boolean {
        if (bytes.isEmpty()) return true
        val n = minOf(bytes.size, 2048); var printable = 0
        for (i in 0 until n) if (bytes[i].toInt() == 9 || bytes[i].toInt() == 10 || bytes[i].toInt() == 13 || bytes[i].toInt() in 32..126 || bytes[i].toInt() < 0) printable++
        return printable.toDouble() / n > 0.85
    }
    private fun selectFile(path: String) {
        if (selectedPath != null && !isApk) saveCurrentFile(false)
        selectedPath = path
        val data = files[path] ?: return
        fileLabel.text = path
        val editable = isEditable(path, data) && !isApk
        editor.setText(if (isEditable(path, data)) String(data, Charsets.UTF_8) else "Binary or large file: preview unavailable.")
        editor.isEnabled = editable
        renderFileList()
    }
    private fun saveCurrentFile(showStatus: Boolean = true) {
        val path = selectedPath
        if (path == null) { if (showStatus) status.text = "Choose a file first."; return }
        if (isApk) { if (showStatus) status.text = "APK imports are read-only. Import source ZIP to edit files."; return }
        if (!isEditable(path, files[path] ?: byteArrayOf())) { if (showStatus) status.text = "This file is not editable text."; return }
        files[path] = editor.text.toString().toByteArray(Charsets.UTF_8)
        if (showStatus) status.text = "Saved " + path + " in the working copy."
    }
    private fun renameInSource() {
        if (isApk) { status.text = "Compiled APK renaming needs a rebuild/signing pipeline; this version edits source ZIPs."; return }
        val newName = appNameInput.text.toString().trim()
        if (newName.isBlank()) { status.text = "Enter a new app name first."; return }
        saveCurrentFile(false); var changed = 0
        files.keys.toList().forEach { path ->
            if (!path.endsWith("AndroidManifest.xml")) return@forEach
            val raw = files[path] ?: return@forEach
            if (!looksText(raw)) return@forEach
            val old = String(raw, Charsets.UTF_8)
            val updated = old.replace(Regex("android:label\\s*=\\s*\"[^\"]*\""), "android:label=\"" + escapeXml(newName) + "\"")
            if (updated != old) { files[path] = updated.toByteArray(Charsets.UTF_8); changed++ }
        }
        val stringsPath = files.keys.firstOrNull { it.endsWith("res/values/strings.xml") }
        if (stringsPath != null && looksText(files[stringsPath]!!)) {
            val old = String(files[stringsPath]!!, Charsets.UTF_8)
            val regex = Regex("(<string\\s+name=\"app_name\"[^>]*>).*?(</string>)", RegexOption.DOT_MATCHES_ALL)
            val updated = regex.replace(old) { it.groupValues[1] + escapeXml(newName) + it.groupValues[2] }
            if (updated != old) { files[stringsPath] = updated.toByteArray(Charsets.UTF_8); changed++ }
        }
        status.text = if (changed > 0) "Updated app-name references in " + changed + " file(s). Review before building." else "No supported app-name field found. Check AndroidManifest.xml and strings.xml."
        renderFileList()
    }
    private fun escapeXml(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    private fun exportZip() {
        if (files.isEmpty()) { status.text = "Import a project first."; return }
        if (isApk) { status.text = "APK rebuild/export is not enabled. Export edited source ZIPs."; return }
        saveCurrentFile(false)
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE); type = "application/zip"; putExtra(Intent.EXTRA_TITLE, "modified-project.zip")
        }
        startActivityForResult(intent, 101)
    }
    private fun exportToUri(uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { output ->
                ZipOutputStream(output).use { zip ->
                    files.forEach { (path, data) ->
                        if (!path.startsWith("/") && !path.split("/").contains("..")) {
                            zip.putNextEntry(ZipEntry(path)); zip.write(data); zip.closeEntry()
                        }
                    }
                }
            }
            status.text = "Exported modified project ZIP."
        } catch (e: Exception) { status.text = "Export failed: " + e.message }
    }

    private fun runAiEdit() {
        if (isApk) { status.text = "AI editing needs a text file from a source ZIP, not a compiled APK."; return }
        val path = selectedPath ?: run { status.text = "Select a text file first."; return }
        val raw = files[path] ?: return
        if (!isEditable(path, raw)) { status.text = "Select an editable text file."; return }
        val key = apiKey.text.toString().trim(); val prompt = aiPrompt.text.toString().trim()
        if (key.isBlank() || prompt.isBlank()) { status.text = "Enter your Gemini API key and describe the change."; return }
        getPreferences(0).edit().putString("gemini_key", key).apply()
        saveCurrentFile(false); val original = String(files[path]!!, Charsets.UTF_8)
        status.text = "AI is editing " + path + "…"
        io.execute {
            try {
                val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + java.net.URLEncoder.encode(key, "UTF-8")
                val conn = URL(endpoint).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"; conn.connectTimeout = 20000; conn.readTimeout = 60000; conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                val instruction = "You are editing a file in a project the user owns. Return ONLY complete replacement file contents, with no markdown fences. Preserve unrelated code. User request: " + prompt + "\nFile path: " + path + "\nCurrent file contents:\n" + original
                val payload = JSONObject().put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", instruction))))).toString()
                conn.outputStream.use { it.write(payload.toByteArray(Charsets.UTF_8)) }
                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val response = stream.bufferedReader().use { it.readText() }
                if (code !in 200..299) throw IOException("Gemini HTTP " + code + ": " + response.take(300))
                val result = JSONObject(response).getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text").trim()
                val clean = result.removePrefix("```").removeSuffix("```").removePrefix("kotlin").removePrefix("java").trim()
                runOnUiThread { editor.setText(clean); files[path] = clean.toByteArray(Charsets.UTF_8); status.text = "AI draft applied. Review it, then save/export the project." }
                conn.disconnect()
            } catch (e: Exception) { runOnUiThread { status.text = "AI edit failed: " + e.message } }
        }
    }
    override fun onDestroy() { super.onDestroy(); io.shutdown() }
}
