/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.beliefs.bayes.assembler;

import org.drools.beliefs.bayes.BayesNetwork;
import org.drools.beliefs.bayes.JunctionTreeBuilder;
import org.drools.beliefs.bayes.model.Bif;
import org.drools.beliefs.bayes.model.XmlBifParser;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.assembler.KieAssemblerService;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.io.ResourceTypePackage;

import java.util.Map;

public class BayesAssemblerService implements KieAssemblerService {

    public BayesAssemblerService() {
    }


    @Override
    public ResourceType getResourceType() {
        return ResourceType.BAYES;
    }

    @Override
    public void addResource(KnowledgeBuilder kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        BayesNetwork network;
        JunctionTreeBuilder builder;

        Bif bif = XmlBifParser.loadBif(resource, kbuilder.getErrors());
        if (bif == null) {
            return;
        }

        try {
            network = XmlBifParser.buildBayesNetwork(bif);
        } catch (Exception e) {
            kbuilder.getErrors().add(new BayesNetworkAssemblerError(resource, "Unable to parse opening Stream:\n" + e.toString()));
            return;
        }

        try {
            builder = new JunctionTreeBuilder(network);
        }  catch ( Exception e ) {
            kbuilder.getErrors().add(new BayesNetworkAssemblerError(resource, "Unable to build Junction Tree:\n" + e.toString()));
            return;
        }

        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        PackageRegistry pkgReg = kbuilderImpl.getPackageRegistry( network.getPackageName() );
        if ( pkgReg == null ) {
            pkgReg = kbuilderImpl.newPackage( new PackageDescr( network.getPackageName() ) );
        }

        InternalKnowledgePackage kpkgs = pkgReg.getPackage();




        Map<ResourceType, ResourceTypePackage> rpkg = kpkgs.getResourceTypePackages();
        BayesPackage bpkg = (BayesPackage) rpkg.get(ResourceType.BAYES);
        if ( bpkg == null ) {
            bpkg = new BayesPackage();
            rpkg.put(ResourceType.BAYES, bpkg);
        }
        bpkg.addJunctionTree(network.getName(), builder.build());
    }

    @Override
    public Class getServiceInterface() {
        return KieAssemblerService.class;
    }
}
