## drools-parser

This module is a reimplementation of the DRL (Drools Rule Language) parser based on ANTLR4.

The current [DRL6Parser](https://github.com/apache/incubator-kie-drools/blob/main/drools-drl/drools-drl-parser/src/main/java/org/drools/drl/parser/lang/DRL6Parser.java) is based on ANTLR3 and contains a lot of custom modifications, which is hard to maintain. This new module should keep the separation between the parser syntax (`DRLParser.g4`) and the Descr generation (`DRLVisitorImpl.java`).

This module started with a part of LSP to develop DRL editors, but it is not limited to that. This module will also replace DRL6Parser in the drools code base.

### How is this developed?

1. The starting point is [DRL6Parser](https://github.com/apache/incubator-kie-drools/blob/main/drools-drl/drools-drl-parser/src/main/java/org/drools/drl/parser/lang/DRL6Parser.java). While it contains lots of customizations, we can map its javadoc (e.g. `packageStatement := PACKAGE qualifiedIdentifier SEMICOLON?`) to `DRLParser.g4` (e.g. `packagedef : PACKAGE name=drlQualifiedName SEMI? ;`).
2. `DRLLexer.g4` is written to define tokens for DRL.
3. `DRLLexer.g4` imports `JavaLexer.g4` to reuse Java tokens. `DRLParser.g4` imports `JavaParser.g4` to reuse Java grammar. These Java parser files are distributed by ANTLR4 under BSD license.
4. In `DRLLexer.g4`, basically define tokens with a prefix "DRL_" to clarify they are DRL keywords.
5. In `DRLParser.g4`, define parser rules with a prefix "drl" if the rule name conflicts with `JavaParser.g4`. Sometimes we need to do that, because such a rule may contain DRL keywords.
6. (As of 2023/10/31) this parser doesn't deeply parse rule RHS (just multiple `RHS_CHUNK`s), because Drools passes RHS text to drools-compiler as-is. In case of developing DRL editors, we may need to integrate another Java LSP to support RHS code completion, etc.
7. LHS constraint (e.g. `age > 30`) is also handled as text. Further processing will be done in the later compiler phase.
8. `DRLParser` processes a DRL text and produces an AST(abstract syntax tree). Then apply `DRLVisitorImpl` to generate PackageDescr following the visitor pattern. So the main work would be implementing `DRLParser.g4` and `DRLVisitorImpl`.
9. Errors are handled by `DRLErrorListener`
10. (As of 2023/10/31) We have 2 test classes. `DRLParserTest` is a very basic test to check if the parser can parse DRL. `MiscDRLParserTest` contains various DRL syntax to check if the parser generates correct Descr objects. `MiscDRLParserTest` was ported from [RuleParserTest](https://github.com/apache/incubator-kie-drools/blob/main/drools-test-coverage/test-compiler-integration/src/test/java/org/drools/mvel/compiler/lang/RuleParserTest.java) so that we can ensure the compatibility of generated Descr objects between the current implementation and the new one.
11. As `DRL6Parser` contains hard-coded customizations, sometimes we need to read and understand the `DRL6Parser` source codes to meet the compatibility.
12. (As of 2023/10/31) `MiscDRLParserTest` still has several test cases with `@Disabled` which are relatively lower priority or edge cases. They need to be resolved at some point in the future. To fix the issues, file a JIRA, remove the `@Disabled` annotation, and fix the implementation to pass the test case.

### Next steps

1. Create a feature branch in drools repo and replace `DRL6Parser` with this new parser.
2. We will detect issues in the new parser by running the existing tests in drools repo. If we find any issues, we will fix them in the new parser and add new tests to cover them. Such tests would be more or less Descr comparison tests, so we would add a new test class which is similar to `MiscDRLParserTest`.

### Refactoring candidates
- `DRLParserHelper` and `DRLParserWrapper` have some duplicated code and purpose. We can merge them into one class.
- `MiscDRLParserTest` can be cleaner and fixed to align with SonarLint suggestions.
- Constraint related parser rules after `conditionalOrExpression` are written in antlr3 style. They could be refactored to antlr4 style (like `lhsExpression`).

### Development tips
- IntelliJ IDEA has an ANTLR4 plugin, which "ANTLR Preview" window displays a parse tree. It is very useful to debug the parser rules.

### Resources
[The Definitive ANTLR 4 Reference](https://pragprog.com/titles/tpantlr2/the-definitive-antlr-4-reference/)