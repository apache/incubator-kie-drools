/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.core.reteoo.builder.NodeFactory;
import org.drools.core.reteoo.builder.PhreakNodeFactory;
import org.kie.api.internal.utils.KieService;

public interface CoreComponentFactory extends KieService {

    NodeFactory getNodeFactoryService();

    InternalKnowledgePackage createKnowledgePackage(String name);

    class Holder {
        private static final CoreComponentFactory INSTANCE = KieService.load( CoreComponentFactory.class );
    }

    static CoreComponentFactory get() {
        return CoreComponentFactory.Holder.INSTANCE != null ? CoreComponentFactory.Holder.INSTANCE : DroolsCoreComponentFactory.INSTANCE;
    }

    class DroolsCoreComponentFactory implements CoreComponentFactory {

        private static final DroolsCoreComponentFactory INSTANCE = new DroolsCoreComponentFactory();

        private NodeFactory nodeFactory = PhreakNodeFactory.getInstance();

        public NodeFactory getNodeFactoryService() {
            return nodeFactory;
        }

        public InternalKnowledgePackage createKnowledgePackage(String name) {
            return new KnowledgePackageImpl(name);
        }
    }
}
