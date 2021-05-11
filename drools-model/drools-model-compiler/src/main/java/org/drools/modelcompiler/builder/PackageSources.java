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
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.modelcompiler.util.lambdareplace.ExecModelLambdaPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageSources {

    private static final Logger logger = LoggerFactory.getLogger(PackageSources.class);

    protected List<GeneratedFile> pojoSources = new ArrayList<>();
    protected List<GeneratedFile> accumulateSources = new ArrayList<>();
    protected List<GeneratedFile> ruleSources = new ArrayList<>();
    protected List<GeneratedFile> lambdaClasses = new ArrayList<>();

    protected GeneratedFile mainSource;
    protected GeneratedFile domainClassSource;

    private Collection<String> modelNames;

    public static PackageSources dumpSources(PackageModel pkgModel) {
        PackageSources sources = new PackageSources();

        PackageModelWriter packageModelWriter = new PackageModelWriter(pkgModel);
        for (DeclaredTypeWriter declaredType : packageModelWriter.getDeclaredTypes()) {
            sources.pojoSources.add(new GeneratedFile( GeneratedFile.Type.DECLARED_TYPE, declaredType.getName(), logSource( declaredType.getSource() )));
        }

        RuleWriter rules = writeRules( pkgModel, sources, packageModelWriter );
        sources.modelNames = rules.getClassNames();
        return sources;
    }

    protected static RuleWriter writeRules( PackageModel pkgModel, PackageSources sources, PackageModelWriter packageModelWriter ) {
        for (AccumulateClassWriter accumulateClassWriter : packageModelWriter.getAccumulateClasses()) {
            sources.accumulateSources.add(new GeneratedFile(accumulateClassWriter.getName(), logSource( accumulateClassWriter.getSource() )));
        }

        RuleWriter rules = packageModelWriter.getRules();
        sources.mainSource = new GeneratedFile(rules.getName(), logSource( rules.getMainSource() ));

        List<ExecModelLambdaPostProcessor.ReplacedLambdaResult> allReplacedLambdaResults = new ArrayList<>();

        for (RuleWriter.RuleFileSource ruleSource : rules.getRuleSources()) {
            sources.ruleSources.add(new GeneratedFile(ruleSource.getName(), logSource( ruleSource.getSource() )));
            allReplacedLambdaResults.addAll(ruleSource.getLambdaResults());
        }


        if(pkgModel.getConfiguration().isParallelLambdaExternalization()) {
            if(logger.isDebugEnabled()) {
                logger.debug("Using parallel lambda externalization");
            }
            parallelWriteLambdaClasses(sources, allReplacedLambdaResults);

        } else {
            if(logger.isDebugEnabled()) {
                logger.debug("Using sequential lambda externalization");
            }
            sequentialWriteLambdaClasses(sources, allReplacedLambdaResults);
        }
        PackageModelWriter.DomainClassesMetadata domainClassesMetadata = packageModelWriter.getDomainClassesMetadata();
        sources.domainClassSource = new GeneratedFile(domainClassesMetadata.getName(), logSource( domainClassesMetadata.getSource() ));
        return rules;
    }

    private static void parallelWriteLambdaClasses(PackageSources sources, List<ExecModelLambdaPostProcessor.ReplacedLambdaResult> allReplacedLambdaResults) {
        try {
            KnowledgeBuilderImpl.ForkJoinPoolHolder.COMPILER_POOL.submit(() -> {
                Map<String, String> distinctLambdaClasses =
                        allReplacedLambdaResults
                        .parallelStream()
                        .collect(Collectors.toMap(k -> k.getExternalisedLambda().getClassNamePath(),
                                                  v -> v.getExternalisedLambda().getCompilationUnitAsString(), (a, b) -> a));

                writeLambdaClasses(sources, distinctLambdaClasses);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Externalized Lambda Creation Interrupted", e);
        }
    }

    private static void sequentialWriteLambdaClasses(PackageSources sources, List<ExecModelLambdaPostProcessor.ReplacedLambdaResult> allReplacedLambdaResults) {
        Map<String, String> distinctLambdaClasses = new HashMap<>();
        for (ExecModelLambdaPostProcessor.ReplacedLambdaResult k : (allReplacedLambdaResults)) {
            distinctLambdaClasses.putIfAbsent(k.getExternalisedLambda().getClassNamePath(), k.getExternalisedLambda().getCompilationUnitAsString());
        }

        writeLambdaClasses(sources, distinctLambdaClasses);
    }

    private static void writeLambdaClasses(PackageSources sources, Map<String, String> distinctLambdaClasses) {
        List<GeneratedFile> generatedFiles = sources.lambdaClasses;
        for (Map.Entry<String, String> gc : distinctLambdaClasses.entrySet()) {
            GeneratedFile generatedFile = new GeneratedFile(gc.getKey(), logSource(gc.getValue()));
            generatedFiles.add(generatedFile);
        }
    }

    public Collection<String> getModelNames() {
        return modelNames;
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
