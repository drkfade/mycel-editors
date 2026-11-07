# Changelog

## 1.1.0
- Highlighting for new syntax: assignment without let, destructuring swap, `in` and `not in`, list comprehensions, `if..else` values and Python's `a if c else b`, anonymous `fn(x) => ...`, `Set([..])`, f-strings, `std.web`, and error highlighting for `#[..]`, `{:}` and `then`.
- Snippets for beginner patterns: web app (`webapp`), list comprehension (`comp`), class, match, test.
- Language server enhancements: std module and member completions (`std.web` etc.), hover docs from `///` comments, quick-fixes applying compiler fix-it suggestions, cross-file go-to-definition, symbol renaming, format document / format-on-save via `mycel fmt`.

## 1.0.0
First release: highlighting, language server (diagnostics, hover, definition, completion, outline), Run command, snippets.

## 1.1.1
- Mycel Dark / Mycel Light color themes, new logo, Apache-2.0.

## 1.1.2
- Removed the bundled file icon theme; .myc/.olx use the Mycel language icon, shown by icon themes that display language icons.
