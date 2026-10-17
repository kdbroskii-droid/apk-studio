package dev.apkstudio.mobile

/**
 * Expandable shared protocol dictionary for Gemini (encoder) and DeepSeek (decoder).
 * Codes are stable identifiers; never renumber an existing code after publishing it.
 * The library is deliberately plain data so it can later be moved to an editable JSON asset.
 */
object NumericLibrary {
    const val PREFIX = "0916820001747701-"

    val tokens: Map<String, String> = linkedMapOf(
        "script" to "1001", "generate" to "1002", "create" to "1003", "edit" to "1004",
        "change" to "1005", "replace" to "1006", "add" to "1007", "remove" to "1008",
        "fix" to "1009", "debug" to "1010", "explain" to "1011", "function" to "1012",
        "variable" to "1013", "number" to "1014", "string" to "1015", "boolean" to "1016",
        "true" to "1017", "false" to "1018", "null" to "1019", "return" to "1020",
        "if" to "1021", "else" to "1022", "loop" to "1023", "for" to "1024",
        "while" to "1025", "class" to "1026", "object" to "1027", "array" to "1028",
        "list" to "1029", "map" to "1030", "import" to "1031", "export" to "1032",
        "file" to "1033", "project" to "1034", "app" to "1035", "android" to "1036",
        "manifest" to "1037", "permission" to "1038", "ui" to "1039", "button" to "1040",
        "screen" to "1041", "layout" to "1042", "style" to "1043", "color" to "1044",
        "text" to "1045", "image" to "1046", "input" to "1047", "save" to "1048",
        "load" to "1049", "open" to "1050", "close" to "1051", "click" to "1052",
        "event" to "1053", "player" to "1054", "character" to "1055", "game" to "1056",
        "roblox" to "1057", "god" to "1058", "mode" to "1059", "health" to "1060",
        "damage" to "1061", "speed" to "1062", "fly" to "1063", "infinite" to "1064",
        "admin" to "1065", "server" to "1066", "client" to "1067", "network" to "1068",
        "security" to "1069", "validate" to "1070", "error" to "1071", "test" to "1072",
        "safe" to "1073", "performance" to "1074", "async" to "1075", "await" to "1076",
        "http" to "1077", "api" to "1078", "request" to "1079", "response" to "1080",
        "json" to "1081", "xml" to "1082", "kotlin" to "1083", "java" to "1084",
        "javascript" to "1085", "luau" to "1086", "python" to "1087", "functionality" to "1088",
        "existing" to "1089", "new" to "1090", "current" to "1091", "selected" to "1092",
        "all" to "1093", "only" to "1094", "and" to "1095", "or" to "1096",
        "not" to "1097", "with" to "1098", "without" to "1099", "into" to "1100",
        "from" to "1101", "to" to "1102", "the" to "1103", "a" to "1104",
        "me" to "1105", "my" to "1106", "make" to "1107", "want" to "1108",
        "please" to "1109", "when" to "1110", "then" to "1111", "because" to "1112",
        "preserve" to "1113", "unrelated" to "1114", "complete" to "1115", "working" to "1116",
        "implementation" to "1117", "dependency" to "1118", "build" to "1119", "compile" to "1120",
        "start" to "1121", "stop" to "1122", "enable" to "1123", "disable" to "1124",
        "toggle" to "1125", "increase" to "1126", "decrease" to "1127", "maximum" to "1128",
        "minimum" to "1129", "fast" to "1130", "slow" to "1131", "high" to "1132",
        "low" to "1133", "left" to "1134", "right" to "1135", "top" to "1136",
        "bottom" to "1137", "center" to "1138", "round" to "1139", "corner" to "1140",
        "padding" to "1141", "margin" to "1142", "spacing" to "1143", "overlap" to "1144",
        "responsive" to "1145", "mobile" to "1146", "desktop" to "1147", "tablet" to "1148",
        "settings" to "1149", "key" to "1150", "value" to "1151", "data" to "1152",
        "database" to "1153", "memory" to "1154", "storage" to "1155", "cache" to "1156",
        "time" to "1157", "date" to "1158", "random" to "1159", "repeat" to "1160",
        "condition" to "1161", "result" to "1162", "output" to "1163", "input" to "1164",
        "command" to "1165", "instruction" to "1166", "prompt" to "1167", "library" to "1168",
        "dictionary" to "1169", "token" to "1170", "encode" to "1171", "decode" to "1172",
        "numeric" to "1173", "code" to "1174", "format" to "1175", "version" to "1176",
        "name" to "1177", "path" to "1178", "line" to "1179", "word" to "1180",
        "words" to "1181", "numbering" to "1182", "contracted" to "1183", "compressed" to "1184"
    ).mapValues { (_, code) -> code.padStart(5, '0') }

    fun promptLibrary(): String = tokens.entries.joinToString(", ") { "${it.key}=${PREFIX}${it.value}" } +
        ", literal-number=${PREFIX}<literal-number>"
}
