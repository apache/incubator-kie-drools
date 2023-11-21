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
package org.drools.model.codegen.execmodel;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PackageSources {

    private static final Logger logger = LoggerFactory.getLogger(PackageSources.class);

    protected List<GeneratedFile> pojoSources = new ArrayList<>();
    protected List<GeneratedFile> accumulateSources = new ArrayList<>();
    protected List<GeneratedFile> ruleSources = new ArrayList<>();
    protected List<GeneratedFile> lambdaClasses = new ArrayList<>();

    protected GeneratedFile mainSource;
    protected GeneratedFile domainClassSource;

    private Collection<String> modelNames;
    private final Collection<String> ruleUnitClassNames = new ArrayList<>();

    protected Collection<String> executableRulesClasses;

    public static PackageSources dumpSources(PackageModel pkgModel) {
        PackageSources sources = new PackageSources();

        PackageModelWriter packageModelWriter = new PackageModelWriter(pkgModel);
        for (DeclaredTypeWriter declaredType : packageModelWriter.getDeclaredTypes()) {
            sources.pojoSources.add(new GeneratedFile( GeneratedFileType.DECLARED_TYPE, declaredType.getName(), logSource( declaredType.getSource() )));
        }

        RuleWriter rules = writeRules( pkgModel, sources, packageModelWriter );
        sources.modelNames = rules.getClassNames();
        sources.executableRulesClasses = pkgModel.getExecutableRulesClasses();
        return sources;
    }

    protected static RuleWriter writeRules( PackageModel pkgModel, PackageSources sources, PackageModelWriter packageModelWriter ) {
        for (AccumulateClassWriter accumulateClassWriter : packageModelWriter.getAccumulateClasses()) {
            sources.accumulateSources.add(new GeneratedFile( GeneratedFileType.RULE, accumulateClassWriter.getName(), logSource( accumulateClassWriter.getSource() )));
        }

        RuleWriter rules = packageModelWriter.getRules();
        sources.mainSource = new GeneratedFile( GeneratedFileType.RULE, rules.getName(), logSource( rules.getMainSource() ));

        for (RuleWriter.RuleFileSource ruleSource : rules.getRuleSources()) {
            sources.ruleSources.add(new GeneratedFile( GeneratedFileType.RULE, ruleSource.getName(), logSource( ruleSource.getSource() )));
        }

        for (RuleUnitWriter ruleUnitWriter : packageModelWriter.getRuleUnitWriters()) {
            sources.ruleSources.addAll(ruleUnitWriter.generate());
            sources.ruleUnitClassNames.add( ruleUnitWriter.getRuleUnitClassName() );
        }

        if (pkgModel.hasRuleUnits() && pkgModel.getContext() != null && pkgModel.getContext().hasRest() && pkgModel.getContext().hasJacksonDatabind()) {
            sources.ruleSources.add(new RuleObjectMapperWriter(pkgModel.getContext()).generate());
        }

        pkgModel.getLambdaClasses()
                .values()
                .stream()
                .map(gc -> new GeneratedFile(GeneratedFileType.RULE, gc.getClassNamePath(), logSource(gc.getContents())))
                .forEach(sources.lambdaClasses::add);

        PackageModelWriter.DomainClassesMetadata domainClassesMetadata = packageModelWriter.getDomainClassesMetadata();
        sources.domainClassSource = new GeneratedFile(GeneratedFileType.RULE, domainClassesMetadata.getName(), logSource( domainClassesMetadata.getSource() ));
        return rules;
    }

    public Collection<String> getModelNames() {
        return modelNames;
    }

    public Collection<String> getRuleUnitClassNames() {
        return ruleUnitClassNames;
    }

    public Collection<String> getExecutableRulesClasses() {
        return executableRulesClasses;
    }

    protected static String logSource(String source) {
        if ( logger.isDebugEnabled() ) {
            logger.debug( "=====" );
            logger.debug( source );
            logger.debug( "=====" );
        }
        return source;
    }

    public void collectGeneratedFiles( List<GeneratedFile> generatedFiles ) {
        // add logging
        generatedFiles.addAll( pojoSources );
        generatedFiles.addAll( accumulateSources );
        generatedFiles.add( mainSource );
        generatedFiles.addAll( ruleSources );
        generatedFiles.add( domainClassSource );
        generatedFiles.addAll(lambdaClasses );
    }
}
