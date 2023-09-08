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
package org.drools.model.codegen.execmodel.drlx;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.expr.Expression;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.model.codegen.ExecutableModelProject;
import org.drools.model.codegen.execmodel.KJARUtils;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.DRLIdGenerator;
import org.drools.model.codegen.execmodel.generator.ModelGenerator;
import org.drools.mvel.DrlDumper;
import org.drools.mvel.parser.MvelParser;
import org.drools.mvel.parser.ParseStart;
import org.drools.io.InputStreamResource;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.mvel.parser.Providers.provider;

public class DrlxCompilerTest {

    @Test
    public void testWhitespace() {
        ParseStart<Expression> context = ParseStart.EXPRESSION;
        MvelParser mvelParser = new MvelParser(new ParserConfiguration(), false);
        ParseResult<Expression> result = mvelParser.parse(context, provider("1\n+1"));
        assertThat(result.getResult().get().toString()).isEqualTo("1 + 1");
    }

    @Test
    public void testSingleFileUnit() throws Exception {
        InputStream p = getClass().getClassLoader().getResourceAsStream("drlx1/Example.drlx");
        InputStreamResource r = new InputStreamResource(p);

        DrlxCompiler drlxCompiler = new DrlxCompiler();

        assertThat(drlxCompiler.getResults().isEmpty()).as("Should not have compiler errors\n" +
                drlxCompiler.getResults().stream()
                        .map(KnowledgeBuilderResult::toString)
                        .collect(Collectors.joining("\n"))).isTrue();

        PackageDescr packageDescr = drlxCompiler.toPackageDescr(r);
        System.out.println(new DrlDumper().dump(packageDescr));

    }

    @Test
    @Ignore("Rule Unit compiler is not available in Drools 8 yet")
    public void testCompileUnit() throws IOException {
        InputStream p = getClass().getClassLoader().getResourceAsStream("drlx1/Example.drlx");
        InputStreamResource r = new InputStreamResource(p);

        DrlxCompiler drlxCompiler = new DrlxCompiler();
        PackageDescr packageDescr = drlxCompiler.toPackageDescr(r);

        assertThat(drlxCompiler.getResults().isEmpty()).as("Should not have compiler errors").isTrue();

        KnowledgeBuilderImpl kbuilder = new KnowledgeBuilderImpl();
        PackageRegistry registry = kbuilder.getOrCreatePackageRegistry(packageDescr);
        kbuilder.getAndRegisterTypeDeclaration(
                org.drools.model.codegen.execmodel.drlx.Example.class,
                "org.drools.modelcompiler.drlx");
        InternalKnowledgePackage knowledgePackage = registry.getPackage();
        PackageModel packageModel =
                new PackageModel(
                        "com.example:dummy:1.0.0",
                        packageDescr.getName(),
                        KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY),
                        new DialectCompiletimeRegistry(),
                        new DRLIdGenerator());
        ModelGenerator.generateModel(
                kbuilder,
                knowledgePackage,
                packageDescr,
                packageModel);

        assertThat(packageModel.getRuleUnits().size()).isEqualTo(1);
    }

    @Test
    @Ignore("Rule Unit Executor is not available in Drools 8 yet")
    public void testCompileUnitFull() throws IOException {
        String path = "drlx1/Example.drlx";
        InputStream p = getClass().getClassLoader().getResourceAsStream(path);
        InputStreamResource r = new InputStreamResource(p);
        r.setSourcePath("src/main/resources/" + path);

        KieServices ks = KieServices.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "kjar-test-1.0", "1.0");
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(r);
        kfs.writePomXML(KJARUtils.getPom(releaseId));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        KieContainer kieContainer = ks.newKieContainer(releaseId);
//        RuleUnitExecutorImpl executor = new RuleUnitExecutorImpl((RuleBase) kieContainer.getKieBase(),
//                (SessionConfiguration) kieContainer.getKieSessionConfiguration());
//        executor.newDataSource("dates",
//                               LocalDate.of(2021, 1, 1));
//        assertEquals(3, executor.run(Example.class));
    }

}
