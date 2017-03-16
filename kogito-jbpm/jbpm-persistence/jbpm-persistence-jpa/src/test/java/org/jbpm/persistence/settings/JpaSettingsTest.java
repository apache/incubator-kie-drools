/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.persistence.settings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JpaSettings.class)
public class JpaSettingsTest {

    JpaSettings jpaSettings;

    @Before
    public void setUp() {
        System.clearProperty("org.kie.ds.jndi");
        jpaSettings = spy(JpaSettings.get());
    }

    @Test
    public void testReadFromPersistenceXml() {
        String jndiName = jpaSettings.getDataSourceJndiName();
        assertEquals(jndiName, "jdbc/testDS1");
    }

    @Test
    public void testSetCustomJndiName() {
        jpaSettings.setDataSourceJndiName("jdbc/myDS");
        String jndiName = jpaSettings.getDataSourceJndiName();
        assertEquals(jndiName, "jdbc/myDS");
    }

    @Test
    public void testDefaultJndiName() throws Exception {
        // Ensure no persistence-xml is found
        when(jpaSettings,
                PowerMockito.method(JpaSettings.class, "getJndiNameFromPersistenceXml"))
                .withNoArguments().thenReturn(null);

        String jndiName = jpaSettings.getDataSourceJndiName();
        assertEquals(jndiName, "java:jboss/datasources/ExampleDS");
    }

    @Test
    public void testDefaultSystemProperty() throws Exception {
        // Ensure no persistence-xml is found
        when(jpaSettings,
                PowerMockito.method(JpaSettings.class, "getJndiNameFromPersistenceXml"))
                .withNoArguments().thenReturn(null);

        System.setProperty("org.kie.ds.jndi", "jdbc/MyDS");
        String jndiName = jpaSettings.getDataSourceJndiName();
        assertEquals(jndiName, "jdbc/MyDS");
    }
}
