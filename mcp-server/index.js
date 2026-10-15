import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  ListToolsRequestSchema,
  CallToolRequestSchema
} from "@modelcontextprotocol/sdk/types.js";
import fs from "node:fs/promises";

const server = new Server(
  { name: "apkstudio-mcp", version: "1.0.0" },
  { capabilities: { tools: {} } }
);

const tools = [
  {
    name: "get_token_library",
    description: "Read the exact shared code=meaning numeric token library for this APK Studio request. Never reinterpret or change existing mappings.",
    inputSchema: { type: "object", properties: {}, additionalProperties: false }
  },
  {
    name: "read_current_file",
    description: "Read the selected source file provided for this request. For +q, preserve unrelated code and change only what the encoded request asks.",
    inputSchema: { type: "object", properties: {}, additionalProperties: false }
  },
  {
    name: "validate_generated_code",
    description: "Check generated source for basic output problems before returning it to APK Studio.",
    inputSchema: {
      type: "object",
      properties: { code: { type: "string", description: "The complete generated file contents." } },
      required: ["code"],
      additionalProperties: false
    }
  }
];

server.setRequestHandler(ListToolsRequestSchema, async () => ({ tools }));

server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const name = request.params.name;
  const args = request.params.arguments ?? {};
  try {
    if (name === "get_token_library") {
      const file = process.env.APKSTUDIO_TOKEN_LIBRARY_PATH;
      if (!file) return result("No request token-library path was provided.");
      const content = await fs.readFile(file, "utf8");
      return result(content || "(empty token library)");
    }
    if (name === "read_current_file") {
      const file = process.env.APKSTUDIO_CURRENT_FILE_PATH;
      if (!file) return result("No current source file was supplied; this is expected for +s.");
      try {
        return result(await fs.readFile(file, "utf8"));
      } catch (error) {
        if (error?.code === "ENOENT") return result("No current source file was supplied; this is expected for +s.");
        throw error;
      }
    }
    if (name === "validate_generated_code") {
      const code = String(args.code ?? "");
      const issues = [];
      if (!code.trim()) issues.push("Output is empty.");
      if (code.length > 200000) issues.push("Output exceeds the 200,000-character safety limit.");
      if (code.startsWith(String.fromCharCode(96, 96, 96)) || code.includes("\n" + String.fromCharCode(96, 96, 96))) {
        issues.push("Remove Markdown code fences; return raw file contents.");
      }
      if (code.includes(String.fromCharCode(0))) issues.push("Output contains NUL characters.");
      return result(issues.length ? "Review required: " + issues.join(" ") : "Basic checks passed. This is not a compiler; review and test the code before using it.");
    }
    return { isError: true, content: [{ type: "text", text: "Unknown tool: " + name }] };
  } catch (error) {
    return { isError: true, content: [{ type: "text", text: "MCP tool failed: " + (error?.message ?? String(error)) }] };
  }
});

function result(text) {
  return { content: [{ type: "text", text }] };
}

const transport = new StdioServerTransport();
await server.connect(transport);
