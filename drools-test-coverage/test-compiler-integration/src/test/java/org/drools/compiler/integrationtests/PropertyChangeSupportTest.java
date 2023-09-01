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
package org.drools.compiler.integrationtests;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyChangeSupportTest {

    public static class DynamicFact {
        private PropertyChangeSupport support = new PropertyChangeSupport(this);
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            String old = this.name;
            this.name = name;
            support.firePropertyChange("name", old, name);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            String old = this.value;
            this.value = value;
            support.firePropertyChange("value", old, value);
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            support.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            support.removePropertyChangeListener(listener);
        }
    }

    @Test
    public void testPropertyChanged() {
        // DROOLS-3607
        String drl =
                "import " + DynamicFact.class.getCanonicalName() + ";\n" +
                "declare DynamicFact\n" +
                "  @propertyChangeSupport\n" +
                "end\n" +
                "rule rule1\n" +
                "   when\n" +
                "     $fact: DynamicFact(name == \"user1\")\n" +
                "   then\n" +
                "     $fact.setName(\"user2\");\n" +
                " end\n" +
                " \n" +
                " rule rule2\n" +
                "   when\n" +
                "     $fact: DynamicFact(name == \"user2\")\n" +
                "   then\n" +
                "     $fact.setValue($fact.getValue() + \"VAL1\");\n" +
                " end";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL).build().newKieSession();

        DynamicFact fact = new DynamicFact();
        fact.setName("user1");
        fact.setValue("");

        ksession.insert(fact);
        ksession.fireAllRules(10);

        assertThat(fact.getName()).isEqualTo("user2");

        assertThat(fact.getValue()).isEqualTo("VAL1");
    }
}