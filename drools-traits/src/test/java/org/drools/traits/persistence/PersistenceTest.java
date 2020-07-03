/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.traits.persistence;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.drools.core.factmodel.traits.Traitable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.definition.type.Position;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;

import static org.drools.traits.persistence.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.traits.persistence.DroolsPersistenceUtil.OPTIMISTIC_LOCKING;
import static org.drools.traits.persistence.DroolsPersistenceUtil.PESSIMISTIC_LOCKING;
import static org.drools.traits.persistence.DroolsPersistenceUtil.createEnvironment;

@RunWith(Parameterized.class)
public class PersistenceTest {

    private Map<String, Object> context;
    private Environment env;
    private boolean locking;

    public PersistenceTest(String locking) {
        this.locking = PESSIMISTIC_LOCKING.equals(locking);
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] {
                { OPTIMISTIC_LOCKING },
                { PESSIMISTIC_LOCKING }
        };
        return Arrays.asList(locking);
    }

    @Before
    public void setUp() throws Exception {
        context = DroolsPersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        env = createEnvironment(context);
        if( locking ) {
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
    }

    @Traitable
    public static class Door implements Serializable {

        private static final long serialVersionUID = 4173662501120948262L;
        @Position(0)
        private String fromLocation;
        @Position(1)
        private String toLocation;

        public Door() {
            this(null, null);
        }

        public Door(String fromLocation, String toLocation) {
            this.fromLocation = fromLocation;
            this.toLocation = toLocation;
        }

        public String getFromLocation() {
            return fromLocation;
        }

        public void setFromLocation(String fromLocation) {
            this.fromLocation = fromLocation;
        }

        public String getToLocation() {
            return toLocation;
        }

        public void setToLocation(String toLocation) {
            this.toLocation = toLocation;
        }
    }

    @Test
    public void testTraitsSerialization() throws Exception {
        String drl = "package org.drools.persistence.kie.persistence.session\n" +
                "\n" +
                "import java.util.List\n" +
                "\n" +
                "import " + Door.class.getCanonicalName() + ";\n" +
                "\n" +
                "declare trait WoodenDoor\n" +
                "    from : String\n" +
                "    to : String\n" +
                "    wood : String\n" +
                "end\n" +
                "\n" +
                "rule \"wooden door\"\n" +
                "    no-loop\n" +
                "    when\n" +
                "        $door : Door()\n" +
                "    then\n" +
                "        WoodenDoor woodenDoor = don( $door, WoodenDoor.class );\n" +
                "end";

        KieServices ks = KieServices.Factory.get();

        Resource drlResource = ks.getResources().newByteArrayResource(drl.getBytes() );
        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", drlResource );
        ks.newKieBuilder( kfs ).buildAll();

        KieBase kbase = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).getKieBase();
        KieSession ksession = ks.getStoreServices().newKieSession(kbase, null, env );

        ksession.insert(new Door());
        ksession.fireAllRules();
    }


}
