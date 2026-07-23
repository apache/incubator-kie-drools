/*
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
package org.kie.kogito.app.audit.jpa.queries;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jakarta.persistence.EntityManager;

public class MappingFile {

    public static String findInDefault(EntityManager entityManager, String queryName) {
        return findInFile("META-INF/data-audit-orm.xml", entityManager, queryName);
    }

    public static String findInFile(String file, EntityManager entityManager, String queryName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        cl = (cl == null) ? MappingFile.class.getClassLoader() : cl;
        try (InputStream is = cl.getResourceAsStream(file)) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(is);
            Node entityMappingsNode = doc.getFirstChild(); // entity-mappings

            NodeList nativeQueries = entityMappingsNode.getChildNodes();
            for (int i = 0; i < nativeQueries.getLength(); i++) {
                Node nativeQuery = nativeQueries.item(i);
                if ("named-native-query".equals(nativeQuery.getNodeName())) {
                    String name = nativeQuery.getAttributes().getNamedItem("name").getNodeValue();
                    String sqlQuery = null;
                    NodeList children = nativeQuery.getChildNodes();
                    for (int j = 0; j < children.getLength(); j++) {
                        Node query = children.item(j);
                        if ("query".equals(query.getNodeName())) {
                            sqlQuery = query.getTextContent();
                        }
                    }

                    if (name.equals(queryName)) {
                        return sqlQuery;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

}
