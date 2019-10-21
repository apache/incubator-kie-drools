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

package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageSources {

    private static final Logger logger = LoggerFactory.getLogger(PackageSources.class);

    private List<GeneratedFile> pojoSources = new ArrayList<>();
    private List<GeneratedFile> accumulateSources = new ArrayList<>();
    private List<GeneratedFile> ruleSources = new ArrayList<>();

    private GeneratedFile mainSource;
    private GeneratedFile domainClassSource;

    private GeneratedFile reflectConfigSource;

    private Map<String, String> modelsByUnit = new HashMap<>();

    private Collection<Class<?>> ruleUnits;

    private String rulesFileName;

    private Map<Class<?>, Collection<QueryModel>> queries;

    public static PackageSources dumpSources(PackageModel pkgModel, boolean oneClassPerRule) {
        PackageSources sources = new PackageSources();

        List<String> pojoClasses = new ArrayList<>();
        PackageModelWriter packageModelWriter = new PackageModelWriter(pkgModel, oneClassPerRule);
        for (DeclaredTypeWriter declaredType : packageModelWriter.getDeclaredTypes()) {
            sources.pojoSources.add(new GeneratedFile(declaredType.getName(), logSource( declaredType.getSource() )));
            pojoClasses.add(declaredType.getClassName());
        }

        if (!pojoClasses.isEmpty()) {
            sources.reflectConfigSource = new GeneratedFile("META-INF/native-image/" + pkgModel.getPathName() + "/reflect-config.json", reflectConfigSource(pojoClasses));
        }

        for (AccumulateClassWriter accumulateClassWriter : packageModelWriter.getAccumulateClasses()) {
            sources.accumulateSources.add(new GeneratedFile(accumulateClassWriter.getName(), logSource( accumulateClassWriter.getSource() )));
        }

        RuleWriter rules = packageModelWriter.getRules();
        sources.mainSource = new GeneratedFile(rules.getName(), logSource( rules.getMainSource() ));
        sources.modelsByUnit.putAll( rules.getModelsByUnit() );

        for (RuleWriter.RuleFileSource ruleSource : rules.getRuleSources()) {
            sources.ruleSources.add(new GeneratedFile(ruleSource.getName(), logSource( ruleSource.getSource() )));
        }

        PackageModelWriter.DomainClassesMetadata domainClassesMetadata = packageModelWriter.getDomainClassesMetadata();
        sources.domainClassSource = new GeneratedFile(domainClassesMetadata.getName(), logSource( domainClassesMetadata.getSource() ));

        sources.rulesFileName = pkgModel.getRulesFileName();

        sources.ruleUnits = pkgModel.getRuleUnits();
        if (!sources.ruleUnits.isEmpty()) {
            sources.queries = new HashMap<>();
            for (Class<?> ruleUnit : sources.ruleUnits) {
                sources.queries.put( ruleUnit, pkgModel.getQueriesInRuleUnit( ruleUnit ) );
            }
        }

        return sources;
    }

    private static String logSource(String source) {
        if ( logger.isDebugEnabled() ) {
            logger.debug( "=====" );
            logger.debug( source );
            logger.debug( "=====" );
        }
        return source;
    }

    public List<GeneratedFile> getPojoSources() {
        return pojoSources;
    }

    public List<GeneratedFile> getAccumulateSources() {
        return accumulateSources;
    }

    public List<GeneratedFile> getRuleSources() {
        return ruleSources;
    }

    public Map<String, String> getModelsByUnit() {
        return modelsByUnit;
    }

    public GeneratedFile getMainSource() {
        return mainSource;
    }

    public GeneratedFile getDomainClassSource() {
        return domainClassSource;
    }

    public Collection<Class<?>> getRuleUnits() {
        return ruleUnits;
    }

    public String getRulesFileName() {
        return rulesFileName;
    }

    public Collection<QueryModel> getQueriesInRuleUnit( Class<?> ruleUnit ) {
        return queries.get( ruleUnit );
    }

    public GeneratedFile getReflectConfigSource() {
        return reflectConfigSource;
    }

    private static String reflectConfigSource( List<String> pojoClasses) {
        return pojoClasses.stream().collect( Collectors.joining( JSON_DELIMITER, JSON_PREFIX, JSON_SUFFIX ) );
    }

    private static final String REFLECTION_PERMISSIONS =
            "        \"allDeclaredConstructors\": true,\n" +
            "        \"allPublicConstructors\": true,\n" +
            "        \"allDeclaredMethods\": true,\n" +
            "        \"allPublicMethods\": true,\n" +
            "        \"allDeclaredFields\": true,\n" +
            "        \"allPublicFields\": true\n";

    private static final String JSON_PREFIX = "[\n" +
            "    {\n" +
            "        \"name\": \"";

    private static final String JSON_DELIMITER = "\",\n" +
            REFLECTION_PERMISSIONS +
            "    },\n" +
            "    {\n" +
            "        \"name\": \"";

    private static final String JSON_SUFFIX = "\",\n" +
            REFLECTION_PERMISSIONS +
            "    }\n" +
            "]";
}
