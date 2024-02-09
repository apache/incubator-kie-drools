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
package org.kie.dmn.feel.parser.feel11;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Parser;
import org.kie.dmn.feel.lang.Type;

class FEELTestRig extends org.antlr.v4.gui.TestRig {

    private FEEL_1_1Lexer lexer;
    private Class<? extends Parser> parserClass;
    private FEEL_1_1Parser parser;
    private ParserHelper parserHelper;

    public FEELTestRig(String[] args) throws Exception {
        this(args, Collections.emptyMap(), Collections.emptyMap());
    }

    public FEELTestRig(String[] args, Map<String, Type> inputVariableTypes, Map<String, Object> inputVariables) throws Exception {
        super(args);
        init(inputVariableTypes, inputVariables);
    }

    private void init(Map<String, Type> inputVariableTypes, Map<String, Object> inputVariables) {
        lexer = new FEEL_1_1Lexer(null);
        parserClass = FEEL_1_1Parser.class;
        parser = new FEEL_1_1Parser(null);
        parserHelper = new ParserHelper();
        parser.setHelper(parserHelper);
        FEELParser.defineVariables(inputVariableTypes, inputVariables, parser);
    }

    public static void main(String[] args) throws Exception {
        FEELTestRig testRig = new FEELTestRig(args);
        if (args.length >= 2) {
            testRig.process();
        }
    }

    @Override
    public void process() throws Exception {
        Charset charset = (encoding == null ? Charset.defaultCharset() : Charset.forName(encoding));
        if (inputFiles.size() == 0) {
            CharStream charStream = CharStreams.fromStream(System.in, charset);
            process(lexer, parserClass, parser, charStream);
            return;
        }
        for (String inputFile : inputFiles) {
            CharStream charStream = CharStreams.fromPath(Paths.get(inputFile), charset);
            if (inputFiles.size() > 1) {
                System.err.println(inputFile);
            }
            process(lexer, parserClass, parser, charStream);
        }
    }
    
    public void process(String cu) throws Exception {
        CharStream charStream = CharStreams.fromStream(new ByteArrayInputStream(cu.getBytes(StandardCharsets.UTF_8)));
        process(lexer, parserClass, parser, charStream);
    }
}