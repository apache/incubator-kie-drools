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

package org.drools.compiler.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import static org.drools.compiler.integrationtests.SerializationHelper.serializeObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FactHandleSerializationTest {

    @Test
    public void testFactHandleSerialization() throws IOException, ClassNotFoundException {
        String drl =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p1 : Person()\n" +
                "  $p2 : Person(name != \"Mark\", this != $p1, age > $p1.age)\n" +
                "then\n" +
                "  $r.setValue($p2.getName() + \" is older than \" + $p1.getName());\n" +
                "end";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        Result result = new Result();
        ksession.insert(result);

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mario is older than Mark", result.getValue());

        markFH = serializeObject(markFH);
        edsonFH = serializeObject(edsonFH);
        marioFH = serializeObject(marioFH);

        result.setValue(null);
        ksession.delete(marioFH);
        ksession.fireAllRules();
        assertNull(result.getValue());

        mark.setAge(34);
        ksession.update(markFH, mark, "age");

        ksession.fireAllRules();
        assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void deserializeOldSession() throws IOException, ClassNotFoundException, URISyntaxException {
        String drl =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $r : Result()\n" +
                        "  $p1 : Person()\n" +
                        "  $p2 : Person(name != \"Mark\", this != $p1, age > $p1.age)\n" +
                        "then\n" +
                        "  $r.setValue($p2.getName() + \" is older than \" + $p1.getName());\n" +
                        "end";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();
        KieMarshallers kieMarshallers = KieServices.Factory.get().getMarshallers();
        Marshaller marshaller = kieMarshallers.newMarshaller(ksession.getKieBase());

        // This is using an old serialized session
        final byte[] serializedEdson = Files.readAllBytes(
                Paths.get(this.getClass().getResource("/org/drools/compiler/integrationtests/serializedEdsonSession").toURI()));
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(serializedEdson)) {
            final KieSession unmarshalledSession = marshaller.unmarshall(bais);
            final Collection<FactHandle> factHandles = unmarshalledSession.getFactHandles();
            assertNotNull(factHandles);
            assertEquals(1, factHandles.size());
        }
    }

    public static class Result implements Serializable {
        private Object value;

        public Object getValue() {
            return value;
        }

        public void setValue( Object value ) {
            this.value = value;
        }
    }
}
