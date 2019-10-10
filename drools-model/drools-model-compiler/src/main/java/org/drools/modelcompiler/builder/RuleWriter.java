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
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.PrettyPrinter;

import static org.drools.modelcompiler.builder.JavaParserCompiler.getPrettyPrinter;

public class RuleWriter {

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

    public String getClassName() {
        return pkgModel.getName() + "." + rulesFileName;
    }

    public String getMainSource() {
        return prettyPrinter.print(generatedPojo);
    }

    public List<RuleFileSource> getRuleSources() {
        List<RuleFileSource> rules = new ArrayList<>();
        for (CompilationUnit cu : rulesSource.getSplitted()) {
            final Optional<ClassOrInterfaceDeclaration> classOptional = cu.findFirst(ClassOrInterfaceDeclaration.class);
            if (classOptional.isPresent()) {

                String addFileName = classOptional.get().getNameAsString();

                rules.add(new RuleFileSource(addFileName, cu));
            }
        }
        return rules;
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
