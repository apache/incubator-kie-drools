/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.models.testscenarios.backend;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

import org.mvel2.MVEL;

public class PopulatingWithMVELTest {

    @Test
    public void testMVELPopulate() throws Exception {
        Object q = MVEL.eval("new org.drools.workbench.models.testscenarios.backend.DumbFact()");

        Map m = new HashMap();
        m.put("obj", q);
        m.put("val", "mike");
        MVEL.eval("obj.name = val", m);

        m = new HashMap();
        m.put("obj", q);
        m.put("val", "42");
        MVEL.eval("obj.age = val", m);

        m = new HashMap();
        m.put("obj", q);
        m.put("val", "44");
        MVEL.eval("obj.number = val", m);

        DumbFact d = (DumbFact) q;

        assertEquals("mike", d.getName());
        assertEquals(42, d.getAge());
        assertEquals(new Long(44), d.getNumber());
    }

    @Test
    public void testMVELFactChecker() throws Exception {
        //now we have a bean check it can be verified
        final DumbFact d = new DumbFact();
        d.setAge(42);
        Map m = new HashMap() {{
                put("d", d);
                put("val", "42");
        }};

        assertTrue(MVEL.evalToBoolean("d.age == val", m));
    }

}
