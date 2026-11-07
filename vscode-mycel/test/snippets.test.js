"use strict";
// Each snippet, with its placeholders expanded to defaults, must be valid Mycel (so we never ship a snippet that errors).
const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");
const { spawnSync } = require("node:child_process");
const MYCEL = process.env.MYCEL || path.join(__dirname, "..", "..", "lang", "bin", "mycel");
const snips = JSON.parse(fs.readFileSync(path.join(__dirname, "..", "snippets", "mycel.json"), "utf8"));
// placeholders expand to their first default everywhere the number repeats (like the editor does)
const expand = (lines) => { const text = lines.join("\n"), def = {}; for (const m of text.matchAll(/\$\{(\d+):((?:[^{}]|\{[^{}]*\})*)\}/g)) def[m[1]] ??= m[2];
  return text.replace(/\$\{(\d+)(?::((?:[^{}]|\{[^{}]*\})*))?\}/g, (_, n) => def[n] ?? "").replace(/\$(\d+)/g, (_, n) => def[n] ?? ""); };

for (const [name, s] of Object.entries(snips)) {
  test(`snippet ${name} (${s.prefix}) type-checks`, () => {
    const body = expand(s.body);
    const wrap = s.prefix === "for" || s.prefix === "match" ? `fn main() {\n  let value = 1\n  let items = [1, 2]\n${body.split("\n").map((l) => "  " + l).join("\n")}\n}\n` : s.prefix === "comp" ? `fn main() {\n  let items = [1, 2]\n  let _ = ${body}\n}\n` : body + "\n";
    const dir = fs.mkdtempSync(path.join(os.tmpdir(), "snip-")); const f = path.join(dir, "s.myc"); fs.writeFileSync(f, wrap);
    const r = spawnSync(MYCEL, ["check", f], { encoding: "utf8", timeout: 30000 });
    assert.equal(r.status, 0, `${r.stdout}${r.stderr}\n--- source ---\n${wrap}`);
  });
}
