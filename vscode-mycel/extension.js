"use strict";
// Mycel for VS Code: starts `mycel lsp` (setting `mycel.path`) and adds a Run command.
const vscode = require("vscode");
const path = require("path");
const { LanguageClient } = require("vscode-languageclient/node");

let client;

function mycelPath() { return vscode.workspace.getConfiguration("mycel").get("path") || "mycel"; }

async function startServer(context) {
  const cmd = mycelPath();
  client = new LanguageClient("mycel", "Mycel Language Server",
    { command: cmd, args: ["lsp"] },
    { documentSelector: [{ scheme: "file", language: "mycel" }, { scheme: "untitled", language: "mycel" }] });
  try { await client.start(); }
  catch (e) {
    client = undefined;
    vscode.window.showWarningMessage(`Mycel: could not start \`${cmd} lsp\` (${e && e.message || e}). Highlighting still works; install Mycel or set "mycel.path" for errors, hover and completion.`);
  }
}

function quote(s) { return /^[\w@%+=:,./-]+$/.test(s) ? s : `'${s.replace(/'/g, `'\\''`)}'`; }

async function runFile() {
  const ed = vscode.window.activeTextEditor;
  if (!ed || ed.document.languageId !== "mycel") return;
  if (ed.document.isUntitled) { vscode.window.showInformationMessage("Save the file first, then run it."); return; }
  await ed.document.save();
  let term = vscode.window.terminals.find((t) => t.name === "Mycel" && t.exitStatus === undefined);
  if (!term) term = vscode.window.createTerminal({ name: "Mycel", cwd: path.dirname(ed.document.fileName) });
  term.show(true); // keep focus in the editor
  term.sendText(`${quote(mycelPath())} ${quote(ed.document.fileName)}`);
}

async function activate(context) {
  context.subscriptions.push(
    vscode.commands.registerCommand("mycel.run", runFile),
    vscode.commands.registerCommand("mycel.restartServer", async () => { if (client) await client.stop().catch(() => {}); await startServer(context); }),
    vscode.workspace.onDidChangeConfiguration(async (e) => { if (e.affectsConfiguration("mycel.path")) { if (client) await client.stop().catch(() => {}); await startServer(context); } }),
  );
  await startServer(context);
}
async function deactivate() { if (client) await client.stop().catch(() => {}); }
module.exports = { activate, deactivate };
