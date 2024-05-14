/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.antlr4;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.conf.LanguageLevelOption;

/**
 * Collection of static helper methods for DRLParser
 */
public class DRLParserHelper {

    private DRLParserHelper() {
    }

    /**
     * Entry point for parsing DRL.
     * Unlike DRLParserWrapper.parse(), this method does not collect errors.
     */
    public static PackageDescr parse(String drl) {
        DRLParser drlParser = createDrlParser(drl);
        return compilationUnitContext2PackageDescr(drlParser.compilationUnit(), drlParser.getTokenStream());
    }

    public static DRLParser createDrlParser(String drl) {
        CharStream charStream = CharStreams.fromString(drl);
        return createDrlParser(charStream);
    }

    public static DRLParser createDrlParser(InputStream is, String encoding) {
        try {
            CharStream charStream = encoding != null
                    ? CharStreams.fromStream(is, Charset.forName(encoding))
                    : CharStreams.fromStream(is);
            return createDrlParser(charStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static DRLParser createDrlParser(Reader reader) {
        try {
            CharStream charStream = CharStreams.fromReader(reader);
            return createDrlParser(charStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static DRLParser createDrlParser(CharStream charStream) {
        DRLLexer drlLexer = new DRLLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(drlLexer);
        DRLParser parser = new DRLParser(commonTokenStream);
        ParserHelper helper = new ParserHelper(commonTokenStream, LanguageLevelOption.DRL6);
        parser.setHelper(helper);
        return parser;
    }

    /**
     * DRLVisitorImpl visits a parse tree and creates a PackageDescr
     */
    public static PackageDescr compilationUnitContext2PackageDescr(DRLParser.CompilationUnitContext ctx, TokenStream tokenStream) {
        DRLVisitorImpl visitor = new DRLVisitorImpl(tokenStream);
        Object descr = visitor.visit(ctx);
        if (descr instanceof PackageDescr) {
            return (PackageDescr) descr;
        } else {
            throw new DRLParserException("CompilationUnitContext should produce PackageDescr. descr = " + descr.getClass());
        }
    }

    /**
     * Given a row and column of the input DRL, return the index of the matched token
     */
    public static Integer computeTokenIndex(DRLParser parser, int row, int col) {
        for (int i = 0; i < parser.getInputStream().size(); i++) {
            Token token = parser.getInputStream().get(i);
            int start = token.getCharPositionInLine();
            int stop = token.getCharPositionInLine() + token.getText().length();
            if (token.getLine() > row) {
                return token.getTokenIndex() - 1;
            } else if (token.getLine() == row && start >= col) {
                return token.getTokenIndex() == 0 ? 0 : token.getTokenIndex() - 1;
            } else if (token.getLine() == row && start < col && stop >= col) {
                return token.getTokenIndex();
            }
        }
        return null;
    }

    /**
     * RuleContext.getText() connects all nodes including ErrorNode. This method appends texts only from valid nodes
     */
    public static String getTextWithoutErrorNode(ParseTree tree) {
        if (tree.getChildCount() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            if (!(child instanceof ErrorNode)) {
                if (child instanceof TerminalNode) {
                    builder.append(child.getText());
                } else {
                    builder.append(getTextWithoutErrorNode(child));
                }
            }
        }

        return builder.toString();
    }
}
