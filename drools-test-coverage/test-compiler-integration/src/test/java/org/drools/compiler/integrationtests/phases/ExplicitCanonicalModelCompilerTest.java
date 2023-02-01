/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.phases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.model.codegen.execmodel.GeneratedFile;
import org.drools.model.codegen.execmodel.PackageSources;
import org.drools.model.codegen.tool.ExplicitCanonicalModelCompiler;
import org.drools.io.ClassPathResource;
import org.junit.Test;
import org.kie.api.io.Resource;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class ExplicitCanonicalModelCompilerTest {

    @Test
    public void testCompile() throws DroolsParserException, IOException {
        Resource resource = new ClassPathResource("org/drools/compiler/integrationtests/phases/ExplicitCompilerTest.drl");
        KnowledgeBuilderConfigurationImpl configuration = new KnowledgeBuilderConfigurationImpl();
        BuildResultCollectorImpl results = new BuildResultCollectorImpl();

        DrlResourceHandler handler = new DrlResourceHandler(configuration);
        final PackageDescr packageDescr = handler.process(resource);
        handler.getResults().forEach(results::addBuilderResult);

        CompositePackageDescr compositePackageDescr = new CompositePackageDescr(resource, packageDescr);
        Collection<CompositePackageDescr> compositePackageDescrs = List.of(compositePackageDescr);

        ExplicitCanonicalModelCompiler<PackageSources> compiler =
                ExplicitCanonicalModelCompiler.of(
                        compositePackageDescrs, configuration, PackageSources::dumpSources);

        compiler.process();

        List<GeneratedFile> generatedSources = new ArrayList<>();
        for (PackageSources src : compiler.getPackageSources()) {
            src.collectGeneratedFiles(generatedSources);
        }

        assertThat(generatedSources.size()).isEqualTo(4);

    }

}
