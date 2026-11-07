"use strict";
// Renders images/highlighting.png: the extension's own TextMate grammar applied to a real example, coloured with VS Code's
// "Dark+" palette, screenshotted with headless Chrome (no visible window). It shows the grammar, not a live editor.
const fs = require("fs"), path = require("path"), os = require("os");
const { spawnSync } = require("child_process");
const vsctm = require("vscode-textmate"), onig = require("vscode-oniguruma");
const root = path.join(__dirname, "..");
const SRC = process.argv[2] || path.join(root, "..", "lang", "examples", "tour", "03_functions.olx");
// first match wins (most specific first); colours are Dark+ defaults
const COLORS = [
  ["comment", "#6A9955"], ["constant.character.escape", "#D7BA7D"], ["string", "#CE9178"], ["constant.numeric", "#B5CEA8"],
  ["meta.interpolation", "#9CDCFE"], ["constant.language", "#569CD6"], ["keyword.control", "#C586C0"], ["keyword.operator.word", "#569CD6"],
  ["entity.name.function", "#DCDCAA"], ["support.type", "#4EC9B0"], ["entity.name.type", "#4EC9B0"], ["storage", "#569CD6"], ["keyword", "#D4D4D4"], ["variable.language", "#569CD6"],
];
const color = (scopes) => { for (let i = scopes.length - 1; i >= 0; i--) for (const [p, c] of COLORS) if (scopes[i].startsWith(p)) return c; return "#D4D4D4"; };
const esc = (s) => s.replace(/&/g, "&amp;").replace(/</g, "&lt;");
(async () => {
  const wasm = fs.readFileSync(path.join(root, "node_modules/vscode-oniguruma/release/onig.wasm")).buffer; await onig.loadWASM(wasm);
  const reg = new vsctm.Registry({ onigLib: Promise.resolve({ createOnigScanner: (s) => new onig.OnigScanner(s), createOnigString: (s) => new onig.OnigString(s) }),
    loadGrammar: async () => vsctm.parseRawGrammar(fs.readFileSync(path.join(root, "syntaxes/mycel.tmLanguage.json"), "utf8"), "g.json") });
  const g = await reg.loadGrammar("source.mycel");
  let st = vsctm.INITIAL; const lines = [];
  for (const line of fs.readFileSync(SRC, "utf8").split("\n").slice(0, 28)) {
    const r = g.tokenizeLine(line, st); st = r.ruleStack;
    lines.push(r.tokens.map((t) => `<span style="color:${color(t.scopes)}">${esc(line.slice(t.startIndex, t.endIndex))}</span>`).join(""));
  }
  const html = `<body style="margin:0;background:#1e1e1e"><div style="padding:6px 16px;background:#252526;color:#ccc;font:13px -apple-system,sans-serif">${path.basename(SRC)}</div><pre style="margin:0;padding:12px 16px;color:#d4d4d4;font:14px/1.5 Menlo,monospace">${lines.map((l, i) => `<span style="color:#858585">${String(i + 1).padStart(3)}  </span>${l}`).join("\n")}</pre></body>`;
  const f = path.join(os.tmpdir(), "mycel-shot.html"); fs.writeFileSync(f, html);
  const out = path.join(root, "images", "highlighting.png");
  const r = spawnSync("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", ["--headless=new", "--disable-gpu", "--hide-scrollbars", "--window-size=860,640", `--screenshot=${out}`, "file://" + f], { encoding: "utf8", timeout: 60000 });
  console.log(fs.existsSync(out) ? "wrote " + out : "failed: " + r.stderr);
})();
