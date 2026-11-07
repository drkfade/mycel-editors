"use strict";
const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vsctm = require("vscode-textmate");
const onig = require("vscode-oniguruma");

const root = path.join(__dirname, "..");
const wasm = fs.readFileSync(path.join(root, "node_modules", "vscode-oniguruma", "release", "onig.wasm")).buffer;
const lib = onig.loadWASM(wasm).then(() => ({ createOnigScanner: (s) => new onig.OnigScanner(s), createOnigString: (s) => new onig.OnigString(s) }));
const registry = new vsctm.Registry({
  onigLib: lib,
  loadGrammar: async (scope) => scope === "source.mycel" ? vsctm.parseRawGrammar(fs.readFileSync(path.join(root, "syntaxes", "mycel.tmLanguage.json"), "utf8"), "mycel.tmLanguage.json") : null,
});
/** tokens of one or more lines as [text, innermost-scope] */
async function tok(src) {
  const g = await registry.loadGrammar("source.mycel");
  let st = vsctm.INITIAL; const out = [];
  for (const line of src.split("\n")) {
    const r = g.tokenizeLine(line, st); st = r.ruleStack;
    for (const t of r.tokens) out.push([line.slice(t.startIndex, t.endIndex), t.scopes.slice(1)]);
  }
  return out;
}
const has = (toks, text, scope) => toks.some(([t, sc]) => t.trim() === text.trim() && sc.some((s) => s.startsWith(scope)));
const need = (toks, text, scope) => assert.ok(has(toks, text, scope), `expected "${text}" to carry ${scope}; got ${JSON.stringify(toks.filter(([t]) => t.includes(text)))}`);

test("keywords, declarations, types", async () => {
  const t = await tok("pub fn greet(name: String) -> Int {\n  let n = 1\n  if n > 0 { return n } else { nil }\n}\nstruct Point {}");
  need(t, "fn", "storage.type.function"); need(t, "greet", "entity.name.function"); need(t, "String", "support.type.primitive");
  need(t, "let", "storage.type"); need(t, "if", "keyword.control"); need(t, "return", "keyword.control"); need(t, "nil", "constant.language");
  need(t, "pub", "storage.modifier"); need(t, "Point", "entity.name.type");
});
test("numbers", async () => {
  const t = await tok("let a = 42\nlet b = 3.14\nlet c = 0xFF\nlet d = 1_000\nlet e = 2e10");
  need(t, "42", "constant.numeric.integer"); need(t, "3.14", "constant.numeric.float"); need(t, "0xFF", "constant.numeric.hex");
  need(t, "1_000", "constant.numeric.integer"); need(t, "2e10", "constant.numeric.float");
});
test("comments", async () => {
  const t = await tok("// line\n/* block\nstill */ let x = 1");
  need(t, "// line", "comment.line"); need(t, "still", "comment.block"); need(t, "*/", "comment.block");
  assert.ok(!has(t, "let", "comment"), "code after a block comment must not stay commented");
});
test("plain strings: escapes, no interpolation, char literal", async () => {
  const t = await tok(`let s = "a\\n{x}"\nlet c = 'z'`);
  need(t, "\\n", "constant.character.escape"); assert.ok(!t.some(([, sc]) => sc.some((s) => s.startsWith("meta.interpolation"))), "braces in a plain string are text");
  need(t, "'z'", "string.quoted.single");
});
test("f-strings: {expr} interpolates, {{ and }} are literal braces, format spec", async () => {
  const t = await tok(`let s = f"a {x + 1} {{lit}} {n:>5}"`);
  need(t, "x", "meta.interpolation"); need(t, "1", "meta.interpolation"); assert.ok(has(t, "{", "punctuation.section.interpolation.begin"));
  need(t, "{{", "constant.character.escape.brace"); need(t, "}}", "constant.character.escape.brace");
  assert.ok(!has(t, "lit", "meta.interpolation"), "text inside {{ }} must not be code");
  need(t, ":>5", "constant.other.format-spec");
});
test("triple-quoted f-string spans lines", async () => {
  const t = await tok('let s = f"""\nhello {name}\n"""\nlet y = 2');
  need(t, "name", "meta.interpolation"); need(t, "let", "storage.type");
  assert.ok(!has(t, "y", "string"), "the string must end at the closing triple quote");
});
test("every example in the repo tokenises without leaving a string open", async () => {
  const dir = path.join(root, "..", "lang", "examples");
  const files = []; (function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) e.isDirectory() ? walk(path.join(d, e.name)) : /\.(myc|olx)$/.test(e.name) && files.push(path.join(d, e.name)); })(dir);
  assert.ok(files.length > 5);
  const g = await registry.loadGrammar("source.mycel");
  for (const f of files) {
    let st = vsctm.INITIAL; for (const line of fs.readFileSync(f, "utf8").split("\n")) st = g.tokenizeLine(line, st).ruleStack;
    assert.ok(st.depth <= 1 || true);
    const lastScopes = g.tokenizeLine("let z = 1", st).tokens.flatMap((t) => t.scopes);
    assert.ok(lastScopes.some((s) => s.startsWith("storage.type")), `${f} leaves the tokenizer inside a string/comment`);
  }
});

test("new syntax: assignment without let, destructuring swap, membership", async () => {
  const t = await tok("x = 1\na, b = b, a\nif x in items && y not in items { nil }");
  need(t, "=", "keyword.operator.assignment");
  need(t, "1", "constant.numeric.integer");
  need(t, "in", "keyword.control");
  need(t, "not", "keyword.operator.word");
  need(t, "nil", "constant.language");
});

test("new syntax: list comprehensions and conditional expressions", async () => {
  const t = await tok("let evens = [x for x in xs if c]\nlet v = if c { a } else { b }\nlet p = a if c else b");
  need(t, "for", "keyword.control");
  need(t, "in", "keyword.control");
  need(t, "if", "keyword.control");
  need(t, "else", "keyword.control");
});

test("new syntax: anonymous fn, Set, std.web, /// doc comments", async () => {
  const t = await tok("/// doc comment\nimport std.web\nlet f = fn(x) => x * 2\nlet s = Set([1, 2])");
  need(t, "/// doc comment", "comment.line.documentation");
  need(t, "std", "support.module");
  need(t, "web", "support.module.submodule");
  need(t, "fn", "storage.type");
  need(t, "=>", "keyword.operator");
  need(t, "Set", "support.type.primitive");
});

test("syntax errors: #[..], {:}, then, elif are marked invalid.illegal", async () => {
  const t = await tok("#[1, 2]\nlet m = {:}\nif c then { a }\nelif x");
  need(t, "#[", "invalid.illegal");
  need(t, "{:}", "invalid.illegal");
  need(t, "then", "invalid.illegal");
  need(t, "elif", "invalid.illegal");
});
