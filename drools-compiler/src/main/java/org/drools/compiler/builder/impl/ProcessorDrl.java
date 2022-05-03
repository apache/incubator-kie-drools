/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.builder.impl;

import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.ParserError;
import org.drools.util.io.DescrResource;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;

import java.io.IOException;

public class ProcessorDrl extends Processor{
    public ProcessorDrl(KnowledgeBuilderConfigurationImpl configuration){
        super(configuration);
    }
    public PackageDescr process(Resource resource) throws DroolsParserException, IOException{
        PackageDescr pkg;
        boolean hasErrors = false;
        if (resource instanceof DescrResource){
            pkg = (PackageDescr) ((DescrResource) resource).getDescr();
        }else{
            final DrlParser parser = new DrlParser(this.configuration.getLanguageLevel());
            pkg = parser.parse(resource);
            this.results.addAll(parser.getErrors());
            if (pkg == null) {
                this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
            }
            hasErrors = parser.hasErrors();
        }
        if (pkg != null) {
            pkg.setResource(resource);
        }
        return hasErrors ? null : pkg;
    }
}