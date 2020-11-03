/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.printer.PrettyPrinter;
import org.drools.modelcompiler.util.lambdareplace.ExecModelLambdaPostProcessor;
import org.drools.modelcompiler.util.lambdareplace.NonExternalisedLambdaFoundException;

import static org.drools.modelcompiler.builder.JavaParserCompiler.getPrettyPrinter;

public class RuleWriter {

    public static final boolean EXTERNALIZE_LAMBDAS = true;

    public static final String DROOLS_CHECK_NON_EXTERNALISED_LAMBDA = "drools.check.nonExternalisedLambda";
    private static boolean checkNonExternalisedLambda = Boolean.parseBoolean(System.getProperty(DROOLS_CHECK_NON_EXTERNALISED_LAMBDA, "false"));

    private final PrettyPrinter prettyPrinter = getPrettyPrinter();

    private final CompilationUnit generatedPojo;
    private final PackageModel pkgModel;
    private final String rulesFileName;
    private final PackageModel.RuleSourceResult rulesSource;

    public RuleWriter(String rulesFileName, PackageModel.RuleSourceResult rulesSource, PackageModel pkgModel) {
        this.rulesFileName = rulesFileName;
        this.rulesSource = rulesSource;
        this.generatedPojo = rulesSource.getMainRuleClass();
        this.pkgModel = pkgModel;
    }

    public String getName() {
        return pkgModel.getPathName() + "/" + rulesFileName + ".java";
    }

    public Collection<String> getClassNames() {
        return rulesSource.getModelsByUnit().values();
    }

    public String getMainSource() {
        return prettyPrinter.print(generatedPojo);
    }

    public Map<String, String> getModelsByUnit() {
        return rulesSource.getModelsByUnit();
    }

    public List<RuleFileSource> getRuleSources() {
        List<RuleFileSource> rules = new ArrayList<>();
        for (CompilationUnit cu : rulesSource.getModelClasses()) {
            final Optional<ClassOrInterfaceDeclaration> classOptional = cu.findFirst(ClassOrInterfaceDeclaration.class);
            if (classOptional.isPresent()) {

                String addFileName = classOptional.get().getNameAsString();

                if (EXTERNALIZE_LAMBDAS && pkgModel.getConfiguration().isExternaliseCanonicalModelLambda()) {
                    CompilationUnit postProcessedCU = cu.clone();
                    if (pkgModel.getRuleUnits().isEmpty()) {
                        new ExecModelLambdaPostProcessor(pkgModel, postProcessedCU).convertLambdas();
                        if (checkNonExternalisedLambda) {
                            checkNonExternalisedLambda(postProcessedCU);
                        }
                    }
                    rules.add(new RuleFileSource(addFileName, postProcessedCU));
                } else {
                    rules.add(new RuleFileSource(addFileName, cu));
                }
            }
        }
        return rules;
    }

    private void checkNonExternalisedLambda(CompilationUnit postProcessedCU) {
        List<LambdaExpr> lambdaExprs = postProcessedCU.findAll(LambdaExpr.class).stream().collect(Collectors.toList());
        if (lambdaExprs.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        lambdaExprs.stream().forEach(lExpr -> sb.append(lExpr.toString() + "\n"));
        throw new NonExternalisedLambdaFoundException("Non externalised lambda found in " + rulesFileName + "\n" + sb.toString());
    }
    public static boolean isCheckNonExternalisedLambda() {
        return checkNonExternalisedLambda;
    }
    public static void setCheckNonExternalisedLambda(boolean checkNonExternalisedLambda) {
        RuleWriter.checkNonExternalisedLambda = checkNonExternalisedLambda;
    }

    public class RuleFileSource {

        protected final CompilationUnit source;
        private final String name;

        private RuleFileSource(String name, CompilationUnit source) {
            this.name = name;
            this.source = source;
        }

        public String getName() {
            return pkgModel.getPathName() + "/" + name + ".java";
        }

        public String getSource() {
            return prettyPrinter.print(source);
        }
    }
}
