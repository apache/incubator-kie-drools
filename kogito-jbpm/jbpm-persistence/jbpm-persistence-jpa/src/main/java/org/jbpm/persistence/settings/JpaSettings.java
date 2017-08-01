/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.persistence.settings;

import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class which gives access to some JPA's settings like, for instance, the JDI datasource name.
 * <p>(see <a href="https://issues.jboss.org/browse/JBPM-4913">JBPM-4913</a>: Ability to read the underlying JNDI datasource name)</p>
 */
public class JpaSettings {

    private static JpaSettings INSTANCE = new JpaSettings();

    public static JpaSettings get() {
        return INSTANCE;
    }

    private static final Logger logger = LoggerFactory.getLogger(JpaSettings.class);
    private String dataSourceJndiName = null;

    private JpaSettings() {
    }

    public String getDataSourceJndiName() {
        if (dataSourceJndiName == null) {
            dataSourceJndiName = findJndiDataSourceName();
        }
        return dataSourceJndiName;
    }

    public void setDataSourceJndiName(String dataSourceJndiName) {
        this.dataSourceJndiName = dataSourceJndiName;
    }

    private String findJndiDataSourceName() {
        String defaultName = System.getProperty("org.kie.ds.jndi", "java:jboss/datasources/ExampleDS");
        try {
            String jndiName = getJndiNameFromPersistenceXml();
            if (jndiName != null) {
                return jndiName;
            }
        } catch (XMLStreamException e) {
            logger.warn("Unable to find out JNDI name fo data source " +
                    "due to {} using default {}", e.getMessage(), defaultName, e);
        }
        return defaultName;
    }

    private String getJndiNameFromPersistenceXml() throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/persistence.xml");
        XMLStreamReader reader = factory.createXMLStreamReader(is);
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && "jta-data-source".equals(reader.getLocalName())) {
                return reader.getElementText();
            }
        }
        return null;

    }
}
