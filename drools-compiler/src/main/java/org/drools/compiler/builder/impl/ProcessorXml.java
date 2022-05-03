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

import org.drools.drl.parser.DroolsParserException;
import org.drools.compiler.compiler.xml.XmlPackageReader;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.Reader;

public class ProcessorXml extends Processor {
    public ProcessorXml(KnowledgeBuilderConfigurationImpl configuration){
        super(configuration);
    }

    public PackageDescr process(Resource resource) throws DroolsParserException, IOException {
        final XmlPackageReader xmlReader = new XmlPackageReader(this.configuration.getSemanticModules());
        xmlReader.getParser().setClassLoader(this.configuration.getClassLoader());

        try (Reader reader = resource.getReader()) {
            xmlReader.read(reader);
        } catch (final SAXException e) {
            throw new DroolsParserException(e.toString(),
                    e.getCause());
        }
        return xmlReader.getPackageDescr();
    }
}