# Drools DRL Language Server (drools-lsp)

A [Language Server Protocol](https://microsoft.github.io/language-server-protocol/) implementation for the **Drools Rule Language (DRL)**. Provides real-time autocomplete, diagnostics, go-to-definition, and hover documentation for `.drl` files in any LSP-compatible editor (VS Code, IntelliJ, Neovim, Sublime Text, Eclipse, etc.).

Built on top of the existing Drools DRL10 ANTLR4 parser and AST descriptor model, so it understands the same DRL syntax as the Drools engine itself.

## Features

### Diagnostics

Parse errors are reported as you type. The server re-parses the document on every change and publishes errors with precise line/column positions.

```
rule "Incomplete Rule"
    when
        Person(name ==     <-- Error: mismatched input 'then' expecting ...
    then
end
```

### Autocomplete

Context-sensitive completions adapt to where the cursor is in the DRL file:

**Top-level** -- `package`, `import`, `global`, `rule`, `query`, `declare`, `function`

**Inside a rule header** -- rule attributes like `salience`, `no-loop`, `lock-on-active`, `agenda-group`, `dialect`, `timer`, `calendars`, etc.

**Inside `when` (LHS)** -- conditional element keywords: `not`, `exists`, `forall`, `accumulate`, `from`, `collect`, `eval`, `over`, `and`, `or`, plus imported types and declared types

**Inside `then` (RHS)** -- action keywords: `insert`, `insertLogical`, `update`, `delete`, `retract`, `modify`, `drools`, plus global variables

**Snippet templates** -- complete structures for rule, query, declare, and function blocks:

```
# Typing "rule" and selecting the snippet expands to:
rule "${1:RuleName}"
    when
        ${2:// conditions}
    then
        ${3:// actions}
end
```

### Go-to-Definition

Jump to the definition of symbols within the current file:

| Symbol at cursor | Navigates to |
|---|---|
| Rule name | `rule` declaration |
| Declared type name | `declare` block |
| Global variable | `global` declaration |
| Function name | `function` definition |
| Imported class name | `import` statement |

### Hover Documentation

Hover over any element to see Markdown-formatted documentation:

- **DRL keywords** (25+) -- contextual documentation for `when`, `then`, `salience`, `accumulate`, `no-loop`, etc.
- **Rules** -- name, parent rule (if `extends`), attributes with values, condition count
- **Declared types** -- type name, supertype, field list with types and initializers
- **Globals** -- identifier and type
- **Functions** -- full Java-style signature with return type and parameters
- **Imports** -- fully qualified class name

Example hover on a rule:

```markdown
### Rule: `Check Adult`

**Attributes**:
- `salience`: `10`
- `no-loop`: `true`

**Conditions**: 1 pattern(s)
``