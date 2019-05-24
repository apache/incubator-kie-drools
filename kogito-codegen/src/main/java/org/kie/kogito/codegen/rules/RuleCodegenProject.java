/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.rules;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.modelcompiler.builder.CanonicalModelCodeGenerationKieProject;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.PackageModel;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.model.KieBaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleCodegenProject extends CanonicalModelCodeGenerationKieProject implements KieBuilder.ProjectType {

    public static final BiFunction<InternalKieModule, ClassLoader, KieModuleKieProject> SUPPLIER = RuleCodegenProject::new;

    private static final Logger log = LoggerFactory.getLogger(KieModuleKieProject.class);

    private ModuleSourceClass moduleGenerator;

    public RuleCodegenProject(InternalKieModule kieModule, ClassLoader classLoader) {
        super(kieModule, classLoader);
    }

    public RuleCodegenProject withModuleGenerator(ModuleSourceClass moduleGenerator) {
        this.moduleGenerator = moduleGenerator;
        return this;
    }

    @Override
    public void writeProjectOutput(MemoryFileSystem trgMfs, ResultsImpl messages) {
        super.writeProjectOutput(trgMfs, messages);

        moduleGenerator.withCdi(hasCdi());

        boolean hasRuleUnits = false;

        for (ModelBuilderImpl modelBuilder : modelBuilders) {
            List<PackageModel> packageModels = modelBuilder.getPackageModels();
            for (PackageModel packageModel : packageModels) {
                Collection<Class<?>> ruleUnits = packageModel.getRuleUnits();

                if (!ruleUnits.isEmpty()) {
                    hasRuleUnits = true;
                    for (Class<?> ruleUnit : ruleUnits) {
                        moduleGenerator.addRuleUnit(
                                new RuleUnitSourceClass(
                                        ruleUnit.getPackage().getName(),
                                        ruleUnit.getSimpleName(),
                                        packageModel.getRulesFileName() ).withCdi( hasCdi() ) );
                    }
                }
            }
        }

        if (hasRuleUnits) {
            for (RuleUnitSourceClass ruleUnit : moduleGenerator.getRuleUnits()) {
                trgMfs.write(
                        ruleUnit.generatedFilePath(),
                        ruleUnit.generate().getBytes() );

                RuleUnitInstanceSourceClass ruleUnitInstance = ruleUnit.instance();
                trgMfs.write(
                        ruleUnitInstance.generatedFilePath(),
                        ruleUnitInstance.generate().getBytes() );
            }
        } else if (hasCdi()) {
            for (KieBaseModel kBaseModel : kBaseModels.values()) {
                for (String sessionName : kBaseModel.getKieSessionModels().keySet()) {
                    CompilationUnit cu = JavaParser.parse( getClass().getResourceAsStream( "/class-templates/SessionRuleUnitTemplate.java" ) );
                    ClassOrInterfaceDeclaration template = cu.findFirst( ClassOrInterfaceDeclaration.class ).get();
                    template.setName( "SessionRuleUnit_" + sessionName );
                    template.findAll( StringLiteralExpr.class ).forEach( s -> s.setString( s.getValue().replace( "$SessionName$", sessionName ) ) );
                    trgMfs.write(
                            "org/drools/project/model/SessionRuleUnit_" + sessionName + ".java",
                            cu.toString().getBytes() );
                }
            }
        }
    }
}