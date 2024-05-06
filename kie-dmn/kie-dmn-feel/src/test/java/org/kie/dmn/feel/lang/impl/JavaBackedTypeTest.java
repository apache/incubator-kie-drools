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
package org.kie.dmn.feel.lang.impl;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.FEELType;
import org.kie.dmn.feel.model.Person;

import static org.assertj.core.api.Assertions.assertThat;

class JavaBackedTypeTest {

    @Test
    void person() {
        CompositeType personType = (CompositeType) JavaBackedType.of( Person.class );
        Set<String> personProperties = personType.getFields().keySet();
        
        assertThat(personProperties).contains("home address");
        assertThat(personProperties).contains("address");
        
        // for consistency and to fix possible hierarchy resolution problem, we add the property also as per standard JavaBean specs.
        assertThat(personProperties).contains("homeAddress");
    }
    
    @FEELType
    public static class MyPojoNoMethodAnn {
        private String a;
        private String b;
        public MyPojoNoMethodAnn(String a, String b) {
            super();
            this.a = a;
            this.b = b;
        }
        
        public String getA() {
            return a;
        }
        
        public void setA(String a) {
            this.a = a;
        }
        
        public String getB() {
            return b;
        }
        
        public void setB(String b) {
            this.b = b;
        }
        
        @SuppressWarnings("unused") // used for tests.
        private String getBPrivate() {
            return b;
        }
    }

    @Test
    void myPojoNoMethodAnn() {
        CompositeType personType = (CompositeType) JavaBackedType.of( MyPojoNoMethodAnn.class );
        Set<String> personProperties = personType.getFields().keySet();
        
        assertThat(personProperties).contains("a");
        assertThat(personProperties).contains("b");
        
        // should not include private methods
        assertThat(personProperties).doesNotContain("bPrivate");
        
        // should not include methods which are actually Object methods.
        assertThat(personProperties).doesNotContain("equals");
        assertThat(personProperties).doesNotContain("wait");
    }
}
