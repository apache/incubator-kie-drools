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
package org.drools.mvel.compiler.testframework;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import org.mvel2.MVEL;

public class FactPopulatorTest {

    @Test
    public void testMVELPopulate() throws Exception {
        Object q = MVEL.eval("new " + DumbFact.class.getName() + "()");

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

        assertThat(d.getName()).isEqualTo("mike");
        assertThat(d.getAge()).isEqualTo(42);
        assertThat(d.getNumber()).isEqualTo(new Long(44));
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

        assertThat(MVEL.evalToBoolean("d.age == val", m)).isTrue();
    }

}
