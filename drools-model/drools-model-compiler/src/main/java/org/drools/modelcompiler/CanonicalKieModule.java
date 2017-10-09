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

package org.drools.modelcompiler;

import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.util.IoUtils;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class CanonicalKieModule extends ZipKieModule {

    public static final String PACKAGE_LIST = "META-INF/packages";
    public static final String RULES_FILE_NAME = "Rules";
    public static final String VARIABLES_FILE_NAME = "Variables";

    private final Collection<String> ruleClassesNames;

    public CanonicalKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file ) {
        this( releaseId, kieProject, file, null );
    }

    public CanonicalKieModule( ReleaseId releaseId, KieModuleModel kieProject, File file, Collection<String> ruleClassesNames ) {
        super( releaseId, kieProject, file );
        this.ruleClassesNames = ruleClassesNames;
    }

    @Override
    public Map<String, byte[]> getClassesMap( boolean includeTypeDeclarations ) {
        return super.getClassesMap( true );
    }

    @Override
    public InternalKnowledgeBase createKieBase( KieBaseModelImpl kBaseModel, KieProject kieProject, ResultsImpl messages, KieBaseConfiguration conf ) {
        ClassLoader kieProjectCL = kieProject.getClassLoader();
        KieBaseBuilder builder = new KieBaseBuilder( kBaseModel, kieProjectCL, conf );

        if (ruleClassesNames == null) {
            String packages = null;
            try {
                packages = new String( IoUtils.readBytesFromInputStream( kieProjectCL.getResourceAsStream( PACKAGE_LIST ) ) );
            } catch (IOException e) {
                throw new RuntimeException( e );
            }
            for ( String pkg : packages.split( "\n" ) ) {
                builder.addModel( createInstance( kieProjectCL, pkg + "." + RULES_FILE_NAME ) );
            }
        } else {
            ruleClassesNames.forEach( s -> builder.addModel( createInstance( kieProjectCL, s ) ) );
        }

        return builder.createKieBase();
    }

    private <T> T createInstance(ClassLoader cl, String className) {
        try {
            return (T) cl.loadClass( className ).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException( e );
        }
    }
}
