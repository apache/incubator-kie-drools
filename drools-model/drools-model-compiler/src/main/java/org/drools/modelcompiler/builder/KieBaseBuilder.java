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
import java.util.Collections;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.model.Model;
import org.drools.modelcompiler.CanonicalKiePackages;
import org.drools.modelcompiler.KiePackagesBuilder;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.conf.KieBaseOption;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.isPackageInKieBase;

public class KieBaseBuilder {

    private final String kBaseName;
    private final KieBaseConfiguration conf;

    public KieBaseBuilder() {
        this(null, null);
    }

    public KieBaseBuilder(KieBaseConfiguration conf) {
        this(null, conf);
    }

    public KieBaseBuilder(KieBaseModel kBaseModel, KieBaseConfiguration conf) {
        this.conf = conf;
        this.kBaseName = kBaseModel != null ? kBaseModel.getName() : "defaultkiebase";
    }

    public InternalKnowledgeBase createKieBase(CanonicalKiePackages kpkgs) {
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase( kBaseName, conf );
        kBase.addPackages( kpkgs.getKiePackages() );
        return kBase;
    }

    public static InternalKnowledgeBase createKieBaseFromModel( Model model, KieBaseOption... options ) {
        return createKieBaseFromModel( Collections.singleton( model ), options );
    }

    public static InternalKnowledgeBase createKieBaseFromModel( Model model, KieBaseConfiguration kieBaseConf ) {
        return createKieBaseFromModel( Collections.singleton( model ), kieBaseConf );
    }

    public static InternalKnowledgeBase createKieBaseFromModel( Collection<Model> models, KieBaseOption... options ) {
        KieBaseConfiguration kieBaseConf = KieServices.get().newKieBaseConfiguration();
        if (options != null) {
            for (KieBaseOption option : options) {
                kieBaseConf.setOption( option );
            }
        }
        return createKieBaseFromModel( models, kieBaseConf );
    }

    public static InternalKnowledgeBase createKieBaseFromModel( Collection<Model> models, KieBaseConfiguration kieBaseConf ) {
        KiePackagesBuilder builder = new KiePackagesBuilder(kieBaseConf);
        models.forEach( builder::addModel );
        return new KieBaseBuilder(kieBaseConf).createKieBase(builder.build());
    }

    public static InternalKnowledgeBase createKieBaseFromModel( Collection<Model> models, KieBaseModel kieBaseModel ) {
        RuleBaseConfiguration kieBaseConf = new RuleBaseConfiguration();
        kieBaseConf.setEventProcessingMode(kieBaseModel.getEventProcessingMode());
        kieBaseConf.setSessionPoolSize(kieBaseModel.getSessionsPool().getSize());

        KiePackagesBuilder builder = new KiePackagesBuilder(kieBaseConf);
        models.stream().filter( m -> isPackageInKieBase(kieBaseModel, m.getPackageName()) ).forEach( builder::addModel );
        return new KieBaseBuilder(kieBaseModel, kieBaseConf).createKieBase(builder.build());
    }
}
