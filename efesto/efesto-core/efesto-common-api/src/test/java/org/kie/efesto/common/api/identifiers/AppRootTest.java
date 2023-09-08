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
package org.kie.efesto.common.api.identifiers;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.componentroots.ComponentFoo;
import org.kie.efesto.common.api.identifiers.componentroots.ComponentRootA;
import org.kie.efesto.common.api.identifiers.componentroots.ComponentRootB;
import org.kie.efesto.common.api.identifiers.componentroots.EfestoComponentRootBar;
import org.kie.efesto.common.api.identifiers.componentroots.LocalComponentIdA;
import org.kie.efesto.common.api.identifiers.componentroots.LocalComponentIdB;
import org.kie.efesto.common.api.identifiers.componentroots.LocalComponentIdFoo;

import static org.assertj.core.api.Assertions.assertThat;

public class AppRootTest {

    @Test
    public void testAppRoot_withComponentRoot() {
        LocalComponentIdFoo retrieved = new ReflectiveAppRoot()
                .get(ComponentFoo.class)
                .get("fileName", "name", "secondName");
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void testAppRoot_withEfestoAppRootAsComponentRoot() {
        LocalComponentIdA retrievedA = new ReflectiveAppRoot()
                .get(EfestoAppRoot.class)
                .get(EfestoComponentRootBar.class)
                .get(ComponentRootA.class)
                .get("fileName", "name");
        assertThat(retrievedA).isNotNull();
        LocalComponentIdB retrievedB = new ReflectiveAppRoot()
                .get(EfestoAppRoot.class)
                .get(EfestoComponentRootBar.class)
                .get(ComponentRootB.class)
                .get("fileName", "name", "secondName");
        assertThat(retrievedB).isNotNull();
        LocalComponentIdFoo retrievedFoo = new ReflectiveAppRoot()
                .get(EfestoAppRoot.class)
                .get(EfestoComponentRootBar.class)
                .get(ComponentFoo.class)
                .get("fileName", "name", "secondName");
        assertThat(retrievedFoo).isNotNull();
    }
}
