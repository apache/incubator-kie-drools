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

import java.util.Collection;

import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.model.Model;
import org.drools.modelcompiler.KiePackagesBuilder;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.definition.KiePackage;

public class KieBaseBuilder {

    private final KiePackagesBuilder builder;
    private final String kBaseName;
    private final KieBaseConfiguration conf;

    public KieBaseBuilder() {
        this(null, KieBaseBuilder.class.getClassLoader(), null);
    }

    public KieBaseBuilder(KieBaseConfiguration conf) {
        this(null, KieBaseBuilder.class.getClassLoader(), conf);
    }

    public KieBaseBuilder(KieBaseModelImpl kBaseModel, ClassLoader cl, KieBaseConfiguration conf) {
        if (conf == null) {
            conf = getKnowledgeBaseConfiguration(kBaseModel, cl);
        } else if (conf instanceof RuleBaseConfiguration ) {
            ((RuleBaseConfiguration)conf).setClassLoader(cl);
        }

        this.kBaseName = kBaseModel != null ? kBaseModel.getName() : "defaultkiebase";
        this.conf = conf;
        this.builder = new KiePackagesBuilder(conf);
    }

    public InternalKnowledgeBase createKieBase() {
        Collection<KiePackage> pkgs = builder.getKnowledgePackages();
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase( kBaseName, conf );
        kBase.addPackages( pkgs );
        return kBase;
    }

    public KieBaseBuilder addModel( Model model ) {
        builder.addModel(model);
        return this;
    }

    private static KieBaseConfiguration getKnowledgeBaseConfiguration( KieBaseModelImpl kBaseModel, ClassLoader cl ) {
        KieBaseConfiguration kbConf = KieServices.get().newKieBaseConfiguration( null, cl );
        if (kBaseModel != null) {
            kbConf.setOption( kBaseModel.getEqualsBehavior() );
            kbConf.setOption( kBaseModel.getEventProcessingMode() );
            kbConf.setOption( kBaseModel.getDeclarativeAgenda() );
        }
        return kbConf;
    }

    public static InternalKnowledgeBase createKieBaseFromModel( Model model, KieBaseOption... options ) {
        if (options == null || options.length == 0) {
            return new KieBaseBuilder().addModel( model ).createKieBase();
        }

        KieBaseConfiguration kieBaseConf = KieServices.get().newKieBaseConfiguration();
        for (KieBaseOption option : options) {
            kieBaseConf.setOption(option);
        }
        return new KieBaseBuilder(kieBaseConf).addModel( model ).createKieBase();
    }
}
