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
import java.util.Arrays;
import java.util.List;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.EclipseJavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.AbstractKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;

import static org.drools.modelcompiler.CanonicalKieModule.PACKAGE_LIST;
import static org.drools.modelcompiler.CanonicalKieModule.RULES_FILE_NAME;
import static org.drools.modelcompiler.CanonicalKieModule.VARIABLES_FILE_NAME;

public class CanonicalModelKieProject extends KieModuleKieProject {

    private ModelBuilderImpl modelBuilder;

    public CanonicalModelKieProject( InternalKieModule kieModule, ClassLoader classLoader ) {
        super( kieModule, classLoader );
    }

    @Override
    protected KnowledgeBuilder createKnowledgeBuilder( KieBaseModelImpl kBaseModel, AbstractKieModule kModule ) {
        modelBuilder = new ModelBuilderImpl();
        return modelBuilder;
    }

    @Override
    public void writeProjectOutput(MemoryFileSystem trgMfs) {
        MemoryFileSystem srcMfs = new MemoryFileSystem();

        JavaCompiler javaCompiler = createCompiler();
        CompilationResult res = javaCompiler.compile( writeModel( srcMfs, trgMfs ),
                                                      srcMfs,
                                                      trgMfs,
                                                      getClassLoader() );

        if (res.getErrors().length != 0) {
            throw new RuntimeException( "Compilation errors: " + Arrays.toString( res.getErrors() ));
        }
    }

    public String[] writeModel( MemoryFileSystem srcMfs, MemoryFileSystem trgMfs ) {
        List<String> sources = new ArrayList<>();
        StringBuilder pkgNames = new StringBuilder();

        for (PackageModel pkgModel : modelBuilder.getPackageModels()) {
            String pkgName = pkgModel.getName();
            pkgNames.append( pkgName ).append( "\n" );
            String folderName = pkgName.replace( '.', '/' );

            if ( pkgModel.getVarsSource() != null ) {
                String varsSourceName = "src/main/java/" + folderName + "/" + VARIABLES_FILE_NAME + ".java";
                byte[] varsBytes = pkgModel.getVarsSource().getBytes();
                srcMfs.write(varsSourceName, varsBytes);
                sources.add(varsSourceName);
            }

            String rulesSourceName = "src/main/java/" + folderName + "/" + RULES_FILE_NAME + ".java";
            byte[] rulesBytes = pkgModel.getRulesSource().getBytes();
            srcMfs.write(rulesSourceName, rulesBytes);
            sources.add(rulesSourceName);
        }

        trgMfs.write( PACKAGE_LIST, pkgNames.toString().getBytes() );
        return sources.toArray(new String[sources.size()]);
    }

    private JavaCompiler createCompiler() {
        EclipseJavaCompiler javaCompiler = (EclipseJavaCompiler) JavaCompilerFactory.getInstance().loadCompiler( JavaDialectConfiguration.CompilerType.ECLIPSE, "1.8" );
        javaCompiler.setPrefix( "src/main/java/" );
        return javaCompiler;
    }
}
