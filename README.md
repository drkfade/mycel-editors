# Mycel editor support

Editor support for [Mycel](https://ol1x.xyz/mycel/), a friendly, typed programming language in development.
Try the language in your browser: [ol1x.xyz/mycel/play](https://ol1x.xyz/mycel/play/).

| Folder | What it is |
|---|---|
| `vscode-mycel/` | VS Code extension: highlighting (incl. f-strings), Mycel Dark / Mycel Light themes, snippets, run command, language-server client |
| `intellij-ply/` | JetBrains plugin (IntelliJ IDEA and other IDEs, 2024.3+): highlighting, Mycel Dark / Mycel Light color schemes, run configurations, inline diagnostics |

The `mycel` command-line tool is not publicly released yet. Highlighting and the color schemes work on their own;
inline errors and running programs need `mycel` on your `PATH` (or set in the extension / run-configuration settings).

## Build
- VS Code: `cd vscode-mycel && npm install && npx @vscode/vsce package`
- JetBrains: `cd intellij-ply && IDE_APP="/Applications/IntelliJ IDEA CE.app" sh scripts/build.sh` (`test` runs the unit tests)

## License
Apache License 2.0 (see `LICENSE` and `NOTICE`). Copyright 2026 drky. "Mycel" and the Mycel logo are not licensed for
use beyond describing the origin of this software.
