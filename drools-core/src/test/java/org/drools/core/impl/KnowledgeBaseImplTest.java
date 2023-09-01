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
package org.drools.core.impl;

import java.util.Collections;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;
import org.junit.Test;
import org.kie.internal.conf.CompositeBaseConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeBaseImplTest {

    @Test
    public void testStaticImports() {

        KnowledgeBaseImpl base = new KnowledgeBaseImpl("default", (CompositeBaseConfiguration)  RuleBaseFactory.newKnowledgeBaseConfiguration());

        // assume empty knowledge base
        assertThat(base.getPackages()).isEmpty();

        // add package with function static import into knowledge base
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.drools.test" );
        pkg.addStaticImport( "org.drools.function.myFunction" );
        base.addPackage( pkg );

        // verify package has been added
        assertThat(base.getPackages()).hasSize(1);

        // retrieve copied and merged package from the base
        InternalKnowledgePackage copy = base.getPackage( "org.drools.test" );
        assertThat(copy.getStaticImports()).isEqualTo(Collections.singleton("org.drools.function.myFunction"));
    }
}
