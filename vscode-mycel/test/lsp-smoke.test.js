"use strict";
// The extension launches `<mycel.path> lsp`. This proves that exact command speaks LSP: initialize → diagnostics for a broken file.
const { test } = require("node:test");
const assert = require("node:assert/strict");
const { spawn, spawnSync } = require("node:child_process");
const path = require("node:path");
const { pathToFileURL } = require("node:url");
const MYCEL = process.env.MYCEL || path.join(__dirname, "..", "..", "lang", "bin", "mycel");

test("`mycel lsp` answers initialize and publishes a diagnostic", async (t) => {
  const probe = spawnSync(MYCEL, ["lsp", "--help"], { encoding: "utf8", input: "", timeout: 20000 });
  if (/unknown command|no such command|not a command/i.test(probe.stdout + probe.stderr)) { t.skip("this mycel has no `lsp` command yet (cli.ts is held by another writer); lang/lsp/test.js covers the server"); return; }
  const p = spawn(MYCEL, ["lsp"], { stdio: ["pipe", "pipe", "inherit"] });
  let buf = Buffer.alloc(0); const msgs = [];
  p.stdout.on("data", (c) => { buf = Buffer.concat([buf, c]); for (;;) { const s = buf.indexOf("\r\n\r\n"); if (s < 0) return; const n = Number(/content-length:\s*(\d+)/i.exec(buf.slice(0, s).toString())[1]); if (buf.length < s + 4 + n) return; msgs.push(JSON.parse(buf.slice(s + 4, s + 4 + n))); buf = buf.slice(s + 4 + n); } });
  const w = (m) => { const b = Buffer.from(JSON.stringify({ jsonrpc: "2.0", ...m })); p.stdin.write(`Content-Length: ${b.length}\r\n\r\n`); p.stdin.write(b); };
  const uri = pathToFileURL("/tmp/vscode-mycel-smoke.myc").href;
  try {
    w({ id: 1, method: "initialize", params: { rootUri: null, capabilities: {} } });
    w({ method: "initialized", params: {} });
    w({ method: "textDocument/didOpen", params: { textDocument: { uri, languageId: "mycel", version: 1, text: "fn main() {\n  let x: Int = \"no\"\n}\n" } } });
    for (let i = 0; i < 100 && !msgs.some((m) => m.method === "textDocument/publishDiagnostics"); i++) await new Promise((r) => setTimeout(r, 100));
    assert.ok(msgs.find((m) => m.id === 1).result.capabilities.hoverProvider);
    const d = msgs.find((m) => m.method === "textDocument/publishDiagnostics").params.diagnostics;
    assert.ok(d.length >= 1 && d[0].severity === 1, JSON.stringify(msgs));
  } finally { p.kill(); }
});
