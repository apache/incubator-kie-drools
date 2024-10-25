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

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.drl.parser.antlr4.DRLParserHelper.compilationUnitContext2PackageDescr;

/**
 * Wrapper for DRLParser. Somewhat duplicated from DRLParserHelper, but this class is instantiated and holds errors.
 */
public class DRLParserWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DRLParserWrapper.class);

    private final List<DRLParserError> errors = new ArrayList<>();

    private final Resource resource;

    public DRLParserWrapper(Resource resource) {
        this.resource = resource;
    }

    /**
     * Main entry point for parsing DRL
     */
    public PackageDescr parse(String drl) {
        DRLParser drlParser = DRLParserHelper.createDrlParser(drl);
        return parse(drlParser);
    }

    /**
     * Main entry point for parsing DRL
     */
    public PackageDescr parse(InputStream is, String encoding) {
        DRLParser drlParser = DRLParserHelper.createDrlParser(is, encoding);
        return parse(drlParser);
    }

    /**
     * Main entry point for parsing DRL
     */
    public PackageDescr parse(Reader reader) {
        DRLParser drlParser = DRLParserHelper.createDrlParser(reader);
        return parse(drlParser);
    }

    private PackageDescr parse(DRLParser drlParser) {
        DRLErrorListener errorListener = new DRLErrorListener();
        drlParser.addErrorListener(errorListener);

        DRLParser.CompilationUnitContext cxt = drlParser.compilationUnit();

        errors.addAll(errorListener.getErrors());

        try {
            return compilationUnitContext2PackageDescr(cxt, drlParser.getTokenStream(), resource);
        } catch (Exception e) {
            LOGGER.error("Exception while creating PackageDescr", e);
            errors.add(new DRLParserError(e));
            return null;
        }
    }

    public List<DRLParserError> getErrors() {
        return errors;
    }

    public List<String> getErrorMessages() {
        return errors.stream().map(DRLParserError::getMessage).collect(Collectors.toList());
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
