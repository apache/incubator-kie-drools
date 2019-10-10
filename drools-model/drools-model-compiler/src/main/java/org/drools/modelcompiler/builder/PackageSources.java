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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageSources {

    private static final Logger logger = LoggerFactory.getLogger(PackageSources.class);

    private List<GeneratedFile> pojoSources = new ArrayList<>();
    private List<GeneratedFile> accumulateSources = new ArrayList<>();
    private List<GeneratedFile> ruleSources = new ArrayList<>();

    private GeneratedFile mainSource;
    private GeneratedFile domainClassSource;

    private String modelName;

    private Collection<Class<?>> ruleUnits;

    private String rulesFileName;

    public static PackageSources dumpSources(PackageModel pkgModel) {
        PackageSources sources = new PackageSources();

        PackageModelWriter packageModelWriter = new PackageModelWriter(pkgModel);
        for (DeclaredTypeWriter declaredType : packageModelWriter.getDeclaredTypes()) {
            sources.pojoSources.add(new GeneratedFile(declaredType.getName(), logSource( declaredType.getSource() )));
        }

        for (AccumulateClassWriter accumulateClassWriter : packageModelWriter.getAccumulateClasses()) {
            sources.accumulateSources.add(new GeneratedFile(accumulateClassWriter.getName(), logSource( accumulateClassWriter.getSource() )));
        }

        RuleWriter rules = packageModelWriter.getRules();
        sources.mainSource = new GeneratedFile(rules.getName(), logSource( rules.getMainSource() ));
        sources.modelName = rules.getClassName();

        for (RuleWriter.RuleFileSource ruleSource : rules.getRuleSources()) {
            sources.ruleSources.add(new GeneratedFile(ruleSource.getName(), logSource( ruleSource.getSource() )));
        }

        PackageModelWriter.DomainClassesMetadata domainClassesMetadata = packageModelWriter.getDomainClassesMetadata();
        sources.domainClassSource = new GeneratedFile(domainClassesMetadata.getName(), logSource( domainClassesMetadata.getSource() ));

        sources.rulesFileName = pkgModel.getRulesFileName();
        return sources;
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

    public String getModelName() {
        return modelName;
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

    private static String logSource(String source) {
        if ( logger.isDebugEnabled() ) {
            logger.debug( "=====" );
            logger.debug( source );
            logger.debug( "=====" );
        }
        return source;
    }
}

