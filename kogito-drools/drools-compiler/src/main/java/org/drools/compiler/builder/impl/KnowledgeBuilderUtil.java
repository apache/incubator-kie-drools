/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.io.impl.DescrResource;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.conf.LanguageLevelOption;

public class KnowledgeBuilderUtil {

    public static PackageDescr drlToPackageDescr(Resource resource, List<KnowledgeBuilderResult> results) {
        try {
            return drlToPackageDescr(resource, null, results, DrlParser.DEFAULT_LANGUAGE_LEVEL);
        } catch (DroolsParserException | IOException e) {
            throw new RuntimeException( e );
        }
    }

    public static PackageDescr dtableToPackageDescr(Resource resource, List<KnowledgeBuilderResult> results) {
        String generatedDrl = DecisionTableFactory.loadFromResource(resource, new DecisionTableConfigurationImpl());
        try {
            return drlToPackageDescr(resource, generatedDrl, results, DrlParser.DEFAULT_LANGUAGE_LEVEL);
        } catch (DroolsParserException | IOException e) {
            throw new RuntimeException( e );
        }
    }

    public static PackageDescr drlToPackageDescr(Resource resource, List<KnowledgeBuilderResult> results, LanguageLevelOption languageLevel) throws DroolsParserException, IOException {
        return drlToPackageDescr( resource, null,  results, languageLevel );
    }

    public static PackageDescr drlToPackageDescr(Resource resource, String generatedDrl, List<KnowledgeBuilderResult> results, LanguageLevelOption languageLevel) throws DroolsParserException, IOException {
        PackageDescr pkg;
        boolean hasErrors = false;
        if (resource instanceof DescrResource ) {
            pkg = (PackageDescr) ((DescrResource) resource).getDescr();
        } else {
            final DrlParser parser = new DrlParser(languageLevel);
            pkg = generatedDrl == null ? parser.parse(resource) : parser.parse(resource, new StringReader(generatedDrl));
            results.addAll(parser.getErrors());
            hasErrors = parser.hasErrors();
        }
        if (pkg != null) {
            pkg.setResource(resource);
        }
        return hasErrors ? null : pkg;
    }
}
