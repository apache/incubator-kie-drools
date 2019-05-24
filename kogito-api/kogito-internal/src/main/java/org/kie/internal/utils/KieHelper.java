/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.utils;

import java.io.InputStream;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.SingleValueKnowledgeBuilderOption;

import static org.kie.api.io.ResourceType.determineResourceType;

public class KieHelper {

    public final KieServices ks = KieServices.Factory.get();

    public final KieFileSystem kfs = ks.newKieFileSystem();

    private ClassLoader classLoader;

    private int counter = 0;

    public KieHelper() {}

    public KieHelper( KnowledgeBuilderOption... options ) {
        if ( options.length > 0 ) {
            KieModuleModel kmm = KieServices.Factory.get().newKieModuleModel();
            for ( KnowledgeBuilderOption opt : options ) {
                if ( opt instanceof EvaluatorOption) {
                    kmm.setConfigurationProperty( EvaluatorOption.PROPERTY_NAME + opt.getPropertyName(), ( (EvaluatorOption) opt ).getEvaluatorDefinition().getClass().getName() );
                } else if ( opt instanceof SingleValueKnowledgeBuilderOption ) {
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
        KieContainer kieContainer = getKieContainer();
        if (options == null || options.length == 0) {
            return kieContainer.getKieBase();
        }
        KieBaseConfiguration kieBaseConf = ks.newKieBaseConfiguration();
        for (KieBaseOption option : options) {
            kieBaseConf.setOption(option);
        }

        return kieContainer.newKieBase(kieBaseConf);
    }

    public KieContainer getKieContainer() {
        KieBuilder kieBuilder = ks.newKieBuilder( kfs, classLoader ).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException(results.getMessages().toString());
        }
        KieContainer kieContainer = ks.newKieContainer( ks.getRepository().getDefaultReleaseId(), classLoader );
        return kieContainer;
    }

    public Results verify() {
        KieBuilder kieBuilder = ks.newKieBuilder( kfs, classLoader ).buildAll();
        return kieBuilder.getResults();
    }

    public KieHelper setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public KieHelper setKieModuleModel(KieModuleModel kieModel) {
        kfs.writeKModuleXML(kieModel.toXML());
        return this;
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
        return addResource(resource);
    }

    private String generateResourceName(ResourceType type) {
        return "src/main/resources/file" + counter++ + "." + type.getDefaultExtension();
    }
}
