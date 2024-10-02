<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

# drools-drl-parser

In this `dev-new-parser` branch, `drools-drl-parser` has 2 implementations of the DRL parser. The new parser is under development, and the old parser is used in the current drools code base.

The old DRL parser is based on Antlr3 and contains lots of hard-coded logic in generated java codes, so it's hard to maintain e.g. [DRL6Parser](https://github.com/apache/incubator-kie-drools/blob/main/drools-drl/drools-drl-parser/src/main/java/org/drools/drl/parser/lang/DRL6Parser.java). It is the reason why the new parser is being developed. The old parser resources are placed under `src/main/java/org/drools/drl/parser/lang` and `src/main/resources/org/drools/drl/parser`.

The new DRL parser is based on Antlr4, which has Visitor and Listener support, so the parser would have cleaner separation between the parser syntax (`DRLParser.g4`) and Java code which generates Descr objects (`DRLVisitorImpl.java`). It would be easier to maintain and extend the parser. The new parser resources are placed under `src/main/java/org/drools/drl/parser/antlr4` and `src/main/antlr4/org/drools/drl/parser/antlr4`.

## The current status of the new parser development
The new DRL parser doesn't introduce new syntax at the first stage. In order to keep the compatibility with the old parser, we make use of existing unit tests.

As of 2024/02/27, we hit lots of test failures in existing drools unit tests. The test failures are filed as child issues of the parent issue [Experiment: New DRL Parser](https://github.com/apache/incubator-kie-drools/issues/5678). We will fix the test failures one by one.

## How to contribute to the development

**Contribution would be highly appreciated!** Here is a rough guide to contribute to the development.

1. Look at the parent issue [Experiment: New DRL Parser](https://github.com/apache/incubator-kie-drools/issues/5678) and choose a child issue which you want to work on.
2. If you have an ASF committer role, assign yourself to the child issue. If you don't have the role, post a comment on the child issue that you are working on it so that we can avoid duplicated work.
3. Create a feature branch based on the `dev-new-parser` branch.
4. Fix the issue and make sure that the test in problem is passed and also all tests under `drools-drl-parser-tests` are passed.
5. Add new tests to `drools-drl-parser-tests` to cover the issue. Hopefully, such a test would be a Descr comparison test. See `MiscDRLParserTest` and `DRLExprParserTest`.
6. File a pull request to the `dev-new-parser` branch. **Be careful not to file a pull request to the `main` branch.**

## Design notes

`org.drools.drl.parser.DrlParser` is a common entry point of the new/old parser implementations. Depending on a system property switch `drools.drl.antlr4.parser.enabled`, it chooses the new parser `org.drools.drl.parser.antlr4.DRLParserWrapper` or the old parser `org.drools.drl.parser.lang.DRL6Parser`.

The responsibilities of a DRL parser are:

1. Parse DRL text and generate an AST (abstract syntax tree)
2. Using the AST, generate `Descr` objects which encapsulate information to be used in the later build phase. The top level object is `PackageDescr`.
3. If there are any parse errors, populate `List<DroolsError> results`.

Common to Antlr 3 and 4, Lexer/Parser java classes are generated from grammar files. For the new parser, grammar files are `DRLLexer.g4` and `DRLParser.g4`. For the old parser, the lexer grammar is `DRL6Lexer.g`, but the parser grammar `drl.g` had been removed from the repo long time ago (https://github.com/apache/incubator-kie-drools/commit/c2ef448d8ed2f935480b9576f4dc83ba7b007a9b) because we directly customized the generated `DRL6Parser.java`. We no longer generate `DRL6Parser.java` with Antlr3 tooling.

With the Antlr3 old parser, we had to write the `Descr` object generation java code inside the parser grammar file (= parser actions). Eventually, we had gone to the direct java code customization, probably because some features were not feasible with parser actions.

With the Antlr4 new parser, we can isolate java codes from `DRLParser.g4` to `DRLVisitorImpl.java` which visits the AST and generate the `Descr` objects. There is no need to customize generated java codes (e.g. `DRLLexer.java`, `DRLParser.java` under `target/generated-sources`). When we fix or expand the parser, we should edit java sources under `src/main/java/org/drools/drl/parser/antlr4` (e.g. `DRLVisitorImpl.java`, `DRLParserWrapper.jva`).

---

For `DRL6Expressions`, we take a little different approach. `DrlExprParserFactory` is a common entry point of the new/old **expression** parser implementations. Similar to `DrlParser`, depending on a system property switch `drools.drl.antlr4.parser.enabled`, it chooses the new parser `org.drools.drl.parser.antlr4.Drl6ExprParserAntlr4` or the old parser `org.drools.drl.parser.Drl6ExprParser`.

The responsibilities of an expression parser are:

1. Parse DRL constraint text and generate an AST
2. Using the AST, generate `Descr` objects which encapsulate information to be used in the build phase. The top level object depends on the constraint.
3. If there are any parse errors, store them to be retrieved by `List<DroolsParserException> getErrors()`

As the old expression parser doesn't have the problem of "hard-coded", the new expression parser grammar file `DRL6Expressions.g4` is basically the same as the old `DRL6Expressions.g`, but just adjusted to work with Antlr4. So not using Visitor pattern at the moment. Applying Visitor pattern to the new expression parser is one of refactoring candidates.

## Additional notes

- The new parser are consist of 2 important parsers.
  - One is `DRLParser` which is generated from `DRLParser.g4`. This parser is responsible for parsing an entire DRL text and generating an AST. Descr objects are generated by applying `DRLVisitorImpl` to the AST. Drools engine uses the parser with `DRLParserWrapper`.
  - The other is `DRL6Expressions` which is generated from `Drl6Expressions.g4`. This parser is specifically used to parse LHS constraints at the later rule compile phase. Drools engine uses the parser with `Drl6ExprParserAntlr4`.

- As of 2024/02/27, we have 4 test classes under `drools-drl-parser-tests`.
  - `DRLParserTest` is a very basic test to check if the parser can parse DRL.
  - `MiscDRLParserTest` contains various DRL syntax to check if the parser generates correct Descr objects. `MiscDRLParserTest` was ported from [RuleParserTest](https://github.com/apache/incubator-kie-drools/blob/main/drools-test-coverage/test-compiler-integration/src/test/java/org/drools/mvel/compiler/lang/RuleParserTest.java) so that we can ensure the compatibility of generated Descr objects between the old implementation and the new one. `MiscDRLParserTest` still has several test cases with `@Disabled` which are relatively lower priority or edge cases. They are also the target of the ongoing development.
  - `DRLExprParserTest` is to test `DRL6Expressions` parser.
  - `DescrDumperTest` is to test the dump result of generated Descr objects. This is originally a test for `DescDumper`, but it's also useful to test `DRL6Expressions` parser.
  - It would be fine to add more test classes if necessary.

- As of 2024/02/27, the `dev-new-parser` branch even fails to build `kie-dmn-validation`. Please ignore the build failure until we fix the test failures of `drools-model-codegen`
- `drools-model-codegen` is a good place to detect various test failures. Once we clean up the test failures of `drools-model-codegen`, we would tackle `kie-dmn-validation` build and `drools-test-coverage` tests.

- `DRL5Parser` and `DRL6StrictParser` are minor variants, and they will likely be removed in the near feature.

- About similar areas:
  - `drools-compiler` contains `JavaParser` which was generated by `Java.g`. This is used by `drools-mvel` to execute java dialect. As `drools-mvel` is deprecated, it is not the scope of this new parser development.
  - `drools-model/drools-mvel-parser` contains javacc based `mvel.jj` to parse constraint and RHS in executable models. It is good enough as it is, so not the scope of this new parser development.

### (Advanced) How was the new parser developed?

**DRLParser**

1. The starting point is [DRL6Parser](https://github.com/apache/incubator-kie-drools/blob/main/drools-drl/drools-drl-parser/src/main/java/org/drools/drl/parser/lang/DRL6Parser.java). While it contains lots of customizations, we can map its javadoc (e.g. `packageStatement := PACKAGE qualifiedIdentifier SEMICOLON?`) to `DRLParser.g4` (e.g. `packagedef : PACKAGE name=drlQualifiedName SEMI? ;`).
2. `DRLLexer.g4` is written to define tokens for DRL.
3. `DRLLexer.g4` imports `JavaLexer.g4` to reuse Java tokens. `DRLParser.g4` imports `JavaParser.g4` to reuse Java grammar. These Java parser files are distributed by ANTLR4 under BSD license.
4. In `DRLLexer.g4`, basically define tokens with a prefix "DRL_" to clarify they are DRL keywords.
5. In `DRLParser.g4`, define parser rules with a prefix "drl" if the rule name conflicts with `JavaParser.g4`. Sometimes we need to do that, because such a rule may contain DRL keywords.
6. (As of 2024/02/27) this parser doesn't deeply parse rule RHS (just multiple `RHS_CHUNK`s), because Drools passes RHS text to drools-compiler as-is. In case of developing DRL editors, we may need to integrate another Java LSP to support RHS code completion, etc.
7. LHS constraint (e.g. `age > 30`) is also handled as text. Further processing will be done by `DRL6Expressions` parser in the later compiler phase.
8. `DRLParser` processes a DRL text and produces an AST. Then apply `DRLVisitorImpl` to generate PackageDescr following the visitor pattern. So the main work would be implementing `DRLParser.g4` and `DRLVisitorImpl`.
9. Errors are handled by `DRLErrorListener`

**DRL6Expressions**

1. `DRL6Expressions.g4` was copied from `DRL6Expressions.g` in the old parser. Then, it was modified to work with Antlr4.

### Refactoring candidates
- New parser related class names are not very consistent. There is a room to improve.
- `DRLParserHelper` and `DRLParserWrapper` have some duplicated code and purpose. We can merge them into one class.
- `MiscDRLParserTest` can be cleaner and fixed to align with SonarLint suggestions.
- There is parser rules overlap between `DRLParser.g4` and `DRL6Expressions.g4` after `conditionalOrExpression`. There is a room to improve. e.g. delegating the constraint parsing to `DRL6Expressions` parser.
- `DRL6Expressions` parser doesn't use Visitor pattern at the moment. Rather, it uses more parser actions. It would be nice to use Visitor pattern to generate Descr objects.

### Development environment tips
- IntelliJ IDEA has an ANTLR4 plugin, which "ANTLR Preview" window displays a parse tree. It is very useful to debug the parser rules.

### Resources
[The Definitive ANTLR 4 Reference](https://pragprog.com/titles/tpantlr2/the-definitive-antlr-4-reference/)