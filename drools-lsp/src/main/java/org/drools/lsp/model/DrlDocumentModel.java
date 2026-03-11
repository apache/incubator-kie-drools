/*
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
package org.drools.lsp.model;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.antlr4.DRL10Parser;
import org.drools.drl.parser.antlr4.DRL10ParserHelper;
import org.drools.drl.parser.antlr4.DRLErrorListener;
import org.drools.drl.parser.antlr4.DRLParserError;
import org.drools.drl.parser.antlr4.DRLVisitorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the parsed state of a single DRL document. Maintains the source text,
 * ANTLR4 parse tree, PackageDescr AST, and parser errors. Supports re-parsing
 * on document updates.
 */
public class DrlDocumentModel {

    private static final Logger LOG = LoggerFactory.getLogger(DrlDocumentModel.class);

    private final String uri;
    private String text;
    private PackageDescr packageDescr;
    private DRL10Parser parser;
    private DRL10Parser.CompilationUnitContext parseTree;
    private List<DRLParserError> errors;

    public DrlDocumentModel(String uri, String text) {
        this.uri = uri;
        this.text = text;
        parse();
    }

    public void update(String newText) {
        this.text = newText;
        parse();
    }

    private void parse() {
        try {
            parser = DRL10ParserHelper.createDrlParser(text);
            DRLErrorListener errorListener = new DRLErrorListener();
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            parseTree = parser.compilationUnit();
            errors = errorListener.getErrors();

            try {
                DRLVisitorImpl visitor = new DRLVisitorImpl(parser.getTokenStream(), null);
                Object descr = visitor.visit(parseTree);
                if (descr instanceof PackageDescr) {
                    packageDescr = (PackageDescr) descr;
                } else {
                    packageDescr = null;
                }
            } catch (Exception e) {
                LOG.warn("Failed to build PackageDescr from parse tree for {}", uri, e);
                packageDescr = null;
            }
        } catch (Exception e) {
            LOG.error("Parse failed for {}", uri, e);
            packageDescr = null;
            parseTree = null;
            errors = Collections.emptyList();
        }
    }

    public String getUri() {
        return uri;
    }

    public String getText() {
        return text;
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }

    public DRL10Parser getParser() {
        return parser;
    }

    public DRL10Parser.CompilationUnitContext getParseTree() {
        return parseTree;
    }

    public List<DRLParserError> getErrors() {
        return errors != null ? errors : Collections.emptyList();
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * Returns the lines of the document text, useful for position-based lookups.
     */
    public String[] getLines() {
        return text.split("\\r?\\n", -1);
    }

    /**
     * Returns the word at the given 0-based line and column position.
     */
    public String getWordAt(int line, int column) {
        String[] lines = getLines();
        if (line < 0 || line >= lines.length) {
            return "";
        }
        String lineText = lines[line];
        if (column < 0 || column > lineText.length()) {
            return "";
        }

        int start = column;
        while (start > 0 && isIdentifierChar(lineText.charAt(start - 1))) {
            start--;
        }
        int end = column;
        while (end < lineText.length() && isIdentifierChar(lineText.charAt(end))) {
            end++;
        }
        return lineText.substring(start, end);
    }

    private static boolean isIdentifierChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '.';
    }
}
