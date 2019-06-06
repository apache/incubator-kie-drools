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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderUtil;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.modelcompiler.CanonicalKieModuleModel;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.PackageModel;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.kogito.codegen.ConfigGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.Generator;

import static org.drools.modelcompiler.builder.CanonicalModelKieProject.PROJECT_MODEL_CLASS;
import static org.drools.modelcompiler.builder.JavaParserCompiler.getPrettyPrinter;

public class IncrementalRuleCodegen implements Generator {

    private String packageName;
    private final Collection<File> files;

    private boolean dependencyInjection;

    private CanonicalKieModuleModel kieModel;

    private final BiFunction<Resource, List<KnowledgeBuilderResult>, PackageDescr> pkgDescrConverter;

    public IncrementalRuleCodegen( Path basePath, Collection<File> files, ResourceType resourceType ) {
        this.files = files;
        if ( resourceType == ResourceType.DRL ) {
            pkgDescrConverter = KnowledgeBuilderUtil::drlToPackageDescr;
        } else if ( resourceType == ResourceType.DTABLE ) {
            pkgDescrConverter = KnowledgeBuilderUtil::dtableToPackageDescr;
        } else {
            throw new IllegalArgumentException( "Unknown resource type " + resourceType );
        }
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public Collection<BodyDeclaration<?>> factoryMethods() {
        return null;
    }

    public List<GeneratedFile> generate() {
        Map<String, CompositePackageDescr> pkgMap = new HashMap<>();
        for (File file : files) {
            PackageDescrForResource pkg = createPackageDescr( file );
            CompositePackageDescr composite = pkgMap.get( pkg.getName() );
            if (composite == null) {
                pkgMap.put( pkg.getName(), pkg.toCompositePackageDescr() );
            } else {
                composite.addPackageDescr( pkg.resource, pkg.packageDescr );
            }
        }

        return pkgMap.values().stream().flatMap( this::generateModelForPackage ).collect( Collectors.toList() );
    }

    @Override
    public void updateConfig(ConfigGenerator cfg) {
        // no config yet
    }

    public void setDependencyInjection(boolean di) {
        this.dependencyInjection = di;
    }

    private Stream<GeneratedFile> generateModelForPackage( CompositePackageDescr pkg ) {
        CanonicalKieModuleModel kieModel = getKieModel();
        KnowledgeBuilderConfigurationImpl configuration = new KnowledgeBuilderConfigurationImpl();

        ModelBuilderImpl modelBuilder = new ModelBuilderImpl(configuration, kieModel.getReleaseId(), true);
        modelBuilder.buildPackages( Collections.singleton(pkg) );

        PackageModel pkgModel = modelBuilder.getPackageModels().get(0);
        PackageModel.RuleSourceResult rulesSourceResult = pkgModel.getRulesSource(true);

        String folderName = pkg.getName().replace( '.', '/' );

        return rulesSourceResult.getSplitted().stream().map( cu -> toGeneratedFile( folderName, cu ) );
    }

    private GeneratedFile toGeneratedFile( String folderName, CompilationUnit cu ) {
        String addFileName = cu.findFirst( ClassOrInterfaceDeclaration.class ).get().getNameAsString();
        String sourceName = folderName + "/" + addFileName + ".java";
        return new GeneratedFile( GeneratedFile.Type.RULE, sourceName, getPrettyPrinter().print( cu ).getBytes( StandardCharsets.UTF_8 ) );
    }

    private PackageDescrForResource createPackageDescr( File file ) {
        Resource resource = KieServices.get().getResources().newFileSystemResource(file);
        List<KnowledgeBuilderResult> results = new ArrayList<>();
        return new PackageDescrForResource( pkgDescrConverter.apply(resource, results), resource );
    }

    private CanonicalKieModuleModel getKieModel() {
        if (kieModel == null) {
            try {
                kieModel = ( CanonicalKieModuleModel ) Class.forName( PROJECT_MODEL_CLASS, true, Thread.currentThread().getContextClassLoader() ).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException( e );
            }
        }
        return kieModel;
    }

    private static class PackageDescrForResource {
        private final PackageDescr packageDescr;
        private final Resource resource;

        private PackageDescrForResource( PackageDescr packageDescr, Resource resource ) {
            this.packageDescr = packageDescr;
            this.resource = resource;
        }

        public CompositePackageDescr toCompositePackageDescr() {
            return new CompositePackageDescr( resource, packageDescr );
        }

        public String getName() {
            return packageDescr.getName();
        }
    }

    @Override
    public Collection<BodyDeclaration<?>> applicationBodyDeclaration() {
        return Collections.emptyList();
    }
}
