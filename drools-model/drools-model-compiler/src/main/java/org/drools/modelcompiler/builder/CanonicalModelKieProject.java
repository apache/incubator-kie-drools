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

import java.util.Arrays;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.AbstractKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.kie.internal.builder.KnowledgeBuilder;

import static org.drools.modelcompiler.builder.JavaParserCompiler.getCompiler;

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
    public void writeProjectOutput( MemoryFileSystem trgMfs ) {
        MemoryFileSystem srcMfs = new MemoryFileSystem();

        String[] resources = new ModelWriter().writeModel(srcMfs, trgMfs, modelBuilder.getPackageModels());
        CompilationResult res = getCompiler().compile( resources, srcMfs, trgMfs, getClassLoader() );

        if ( res.getErrors().length != 0 ) {
            throw new RuntimeException( "Compilation errors: " + Arrays.toString( res.getErrors() ) );
        }
    }
}