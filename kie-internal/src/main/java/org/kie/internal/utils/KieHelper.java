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
package org.kie.internal.utils;

import java.io.InputStream;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.PrototypesOption;
import org.kie.api.definition.KieDescr;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.SingleValueKieBuilderOption;

import static org.kie.api.io.ResourceType.determineResourceType;

public class KieHelper {

    public final KieServices ks = KieServices.Factory.get();

    public final KieFileSystem kfs = ks.newKieFileSystem();

    private ReleaseId releaseId;

    private ClassLoader classLoader;

    private int counter = 0;

    private KieModuleModel kieModuleModel;

    public KieHelper() {}

    public KieHelper( KnowledgeBuilderOption... options ) {
        if ( options.length > 0 ) {
            KieModuleModel kmm = ks.newKieModuleModel();
            for ( KnowledgeBuilderOption opt : options ) {
                if ( opt instanceof EvaluatorOption) {
                    kmm.setConfigurationProperty( EvaluatorOption.PROPERTY_NAME + opt.getPropertyName(), ( (EvaluatorOption) opt ).getEvaluatorDefinition().getClass().getName() );
                } else if ( opt instanceof SingleValueKieBuilderOption) {
                    kmm.setConfigurationProperty(opt.getPropertyName(), opt.toString());
                }
            }
            this.setKieModuleModel( kmm );
        }
    }

    public KieBase build( KieBaseConfiguration kieBaseConf ) {
        KieContainer kieContainer = getKieContainer();
        return kieContainer.newKieBase( kieBaseConf );
    }

    public KieBase build(KieBaseOption... options) {
        return build( null, options );
    }

    public KieBase build(Class<? extends KieBuilder.ProjectType> projectType, KieBaseOption... options) {
        if (options == null || options.length == 0) {
            return getKieContainer(projectType).getKieBase();
        }
        KieBaseConfiguration kieBaseConf = ks.newKieBaseConfiguration();
        if ( options.length > 0 ) {
            if (kieModuleModel == null) {
                kieModuleModel = ks.newKieModuleModel();
            }
            KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel("KBase").setDefault(true);
            for (KieBaseOption option : options) {
                kieBaseConf.setOption(option);
                if ( option instanceof PrototypesOption o ) {
                    kieBaseModel.setPrototypes( o );
                }
                if ( option instanceof EqualityBehaviorOption o ) {
                    kieBaseModel.setEqualsBehavior( o );
                }
                if ( option instanceof KieBaseMutabilityOption o ) {
                    kieBaseModel.setMutability( o );
                }
            }
        }
        return build(projectType, kieBaseConf);
    }

    public KieBase build(Class<? extends KieBuilder.ProjectType> projectType, KieBaseConfiguration kieBaseConf) {
        KieContainer kieContainer = getKieContainer(projectType);
        return kieContainer.newKieBase(kieBaseConf);
    }

    public KieContainer getKieContainer() {
        return getKieContainer( null );
    }

    public KieContainer getKieContainer(Class<? extends KieBuilder.ProjectType> projectType) {
        InternalKieBuilder kieBuilder = (( InternalKieBuilder ) ks.newKieBuilder( kfs, classLoader ));
        kieBuilder.withKModuleModel( kieModuleModel ).buildAll(projectType);
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException(results.getMessages().toString());
        }
        ReleaseId kieContainerReleaseId;
        if (this.releaseId != null) {
            kieContainerReleaseId = this.releaseId;
        } else {
            kieContainerReleaseId = ks.getRepository().getDefaultReleaseId();
        }
        return ks.newKieContainer(kieContainerReleaseId, classLoader);
    }

    public Results verify() {
        KieBuilder kieBuilder = (( InternalKieBuilder ) ks.newKieBuilder( kfs, classLoader )).withKModuleModel( kieModuleModel ).buildAll();
        return kieBuilder.getResults();
    }

    public KieHelper setReleaseId(ReleaseId releaseId) {
        this.releaseId = releaseId;
        kfs.generateAndWritePomXML(releaseId);
        return this;
    }

    public KieHelper setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public KieHelper setKieModuleModel(KieModuleModel kieModuleModel) {
        this.kieModuleModel = kieModuleModel;
        return this;
    }

    public KieHelper addContent( KieDescr descr ) {
        return addResource( ks.getResources().newDescrResource( descr ), ResourceType.DESCR );
    }

    public KieHelper addContent(String content, ResourceType type) {
        kfs.write( generateResourceName( type ), content );
        return this;
    }

    public KieHelper addContent(String content, String name) {
        kfs.write("src/main/resources/" + name, content);
        return this;
    }

    public KieHelper addFromClassPath(String name) {
        return addFromClassPath(name, null);
    }

    public KieHelper addFromClassPath(String name, String encoding) {
        InputStream input = getClass().getResourceAsStream(name);
        if ( input == null && classLoader != null ) {
            input = classLoader.getResourceAsStream( name );
        }
        if (input == null) {
            throw new IllegalArgumentException("The file (" + name + ") does not exist as a classpath resource.");
        }
        ResourceType type = determineResourceType(name);
        kfs.write(generateResourceName(type), ks.getResources().newInputStreamResource(input, encoding));
        return this;
    }

    public KieHelper addResource(Resource resource) {
        kfs.write(resource);
        return this;
    }

    public KieHelper addResource(Resource resource, ResourceType type) {
        if (resource.getSourcePath() == null && resource.getTargetPath() == null) {
            resource.setSourcePath(generateResourceName(type));
        }
        if (resource.getResourceType() == null) {
            resource.setResourceType( type );
        }
        return addResource(resource);
    }

    private String generateResourceName(ResourceType type) {
        return "src/main/resources/file" + counter++ + "." + type.getDefaultExtension();
    }
}
