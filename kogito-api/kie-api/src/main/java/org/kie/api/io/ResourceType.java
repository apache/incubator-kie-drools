/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.io;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResourceType
        implements
        Serializable {

    private static final long serialVersionUID = 1613735834228581906L;

    private String name;

    private String description;

    private String defaultExtension;

    private String[] otherExtensions;

    private String defaultPath;

    private static final Map<String, ResourceType> CACHE = Collections.synchronizedMap(new HashMap<String, ResourceType>());

    public ResourceType(String name,
                        String description,
                        String defaultPath,
                        String defaultExtension,
                        String... otherExtensions) {
        this.name = name;
        this.description = description;
        this.defaultPath = defaultPath;
        this.defaultExtension = defaultExtension;
        this.otherExtensions = otherExtensions;
    }

    public static ResourceType addResourceTypeToRegistry(final String resourceType,
                                                         final String description,
                                                         final String defaultPath,
                                                         final String defaultExtension,
                                                         final String... otherExtensions) {

        ResourceType resource = new ResourceType( resourceType,
                                                  description,
                                                  defaultPath,
                                                  defaultExtension,
                                                  otherExtensions );
        CACHE.put( resourceType, resource );
        return resource;
    }

    /** Drools Rule Language */
    public static final ResourceType DRL = addResourceTypeToRegistry("DRL",
                                                                     "Drools Rule Language",
                                                                     "src/main/resources",
                                                                     "drl");

    /** Drools Rule Language - Guided Globals definitions - Workaround for double-dot file extensions for 6.0 */
    public static final ResourceType GDRL = addResourceTypeToRegistry("GDRL",
                                                                      "Drools Rule Language",
                                                                      "src/main/resources",
                                                                      "gdrl");

    /** Drools Rule Language - Guided Rules (without DSL) - Workaround for double-dot file extensions for 6.0 */
    public static final ResourceType RDRL = addResourceTypeToRegistry("RDRL",
                                                                      "Drools Rule Language",
                                                                      "src/main/resources",
                                                                      "rdrl");

    /** Drools XML Rule Language */
    public static final ResourceType XDRL = addResourceTypeToRegistry("XDRL",
                                                                      "Drools XML Rule Language",
                                                                      "src/main/resources",
                                                                      "xdrl");

    /** Drools DSL */
    public static final ResourceType DSL = addResourceTypeToRegistry("DSL",
                                                                     "Drools DSL",
                                                                     "src/main/resources",
                                                                     "dsl");

    /** Drools DSL Rule */
    public static final ResourceType DSLR = addResourceTypeToRegistry("DSLR",
                                                                      "Drools DSL Rule",
                                                                      "src/main/resources",
                                                                      "dslr");

    /** Drools Rule Language - Guided Rules (with DSL) - Workaround for double-dot file extensions for 6.0 */
    public static final ResourceType RDSLR = addResourceTypeToRegistry("RDSLR",
                                                                       "Drools DSL Rule",
                                                                       "src/main/resources",
                                                                       "rdslr");

    /** Drools Rule Flow Language */
    public static final ResourceType DRF = addResourceTypeToRegistry("DRF",
                                                                     "Drools Rule Flow Language",
                                                                     "src/main/resources",
                                                                     "rf");

    /** jBPM BPMN2 Language */
    public static final ResourceType BPMN2 = addResourceTypeToRegistry("BPMN2",
                                                                       "jBPM BPMN2 Language",
                                                                       "src/main/resources",
                                                                       "bpmn", "bpmn2");

    /** Decision Table */
    public static final ResourceType DTABLE = addResourceTypeToRegistry("DTABLE",
                                                                        "Decision Table",
                                                                        "src/main/resources",
                                                                        "xls", "xlsx", "csv");

    /** Binary Package */
    public static final ResourceType PKG = addResourceTypeToRegistry("PKG",
                                                                     "Binary Package",
                                                                     "src/main/resources",
                                                                     "pkg");

    /** Drools Business Rule Language */
    public static final ResourceType BRL = addResourceTypeToRegistry("BRL",
                                                                     "Drools Business Rule Language",
                                                                     "src/main/resources",
                                                                     "brl");

    /** Change Set */
    public static final ResourceType CHANGE_SET = addResourceTypeToRegistry("CHANGE_SET",
                                                                            "Change Set",
                                                                            "src/main/resources",
                                                                            "xcs");

    /** XSD */
    public static final ResourceType XSD = addResourceTypeToRegistry("XSD",
                                                                     "XSD",
                                                                     "src/main/resources",
                                                                     "xsd");

    /** PMML */
    public static final ResourceType PMML = addResourceTypeToRegistry("PMML",
                                                                      "Predictive Model Markup Language",
                                                                      "src/main/resources",
                                                                      "pmml");

    /** DESCR */
    public static final ResourceType DESCR = addResourceTypeToRegistry("DESCR",
                                                                       "Knowledge Descriptor",
                                                                       "src/main/resources",
                                                                       "descr");

    /** JAVA */
    public static final ResourceType JAVA = addResourceTypeToRegistry("JAVA",
                                                                      "Java class",
                                                                      "src/main/java",
                                                                      "java");

    /** PROPERTIES */
    public static final ResourceType PROPERTIES = addResourceTypeToRegistry("PROPERTIES",
                                                                            "Properties file",
                                                                            "src/main/resources",
                                                                            "properties");

    /** Score Card - Workaround for double-dot file extensions for 6.0 */
    public static final ResourceType SCARD = addResourceTypeToRegistry("SCARD",
                                                                       "Score Crd",
                                                                       "src/main/resources",
                                                                       "sxls");


    /** Bayesian */
    public static final ResourceType BAYES = addResourceTypeToRegistry("Bayes",
                                                                       "Bayesian Belief Network",
                                                                       "src/main/resources",
                                                                       "xmlbif", "bif");

    /** Drools Rule Language - Guided Decision Trees - Workaround for double-dot file extensions for 6.0 */
    public static final ResourceType TDRL = addResourceTypeToRegistry("TDRL",
                                                                      "Drools Rule Language",
                                                                      "src/main/resources",
                                                                      "tdrl");

    public static final ResourceType TEMPLATE = addResourceTypeToRegistry("TEMPLATE",
                                                                          "Drools Rule Template",
                                                                          "src/main/resources",
                                                                          "template");

    public static final ResourceType DRT = addResourceTypeToRegistry("DRT",
                                                                     "Drools Rule Template",
                                                                     "src/main/resources",
                                                                     "drt");

    public static final ResourceType GDST = addResourceTypeToRegistry("GDST",
                                                                      "Guided Decision Table",
                                                                      "src/main/resources",
                                                                      "gdst");

    public static final ResourceType SCGD = addResourceTypeToRegistry("SCGD",
                                                                      "Guided Score Card",
                                                                      "src/main/resources",
                                                                      "scgd");

    public static final ResourceType SOLVER = addResourceTypeToRegistry("SOLVER",
                                                                        "OptaPlanner Solver Configuration",
                                                                        "src/main/resources",
                                                                        "solver");

    /** Decision Model and Notation (DMN) model  */
    public static final ResourceType DMN = addResourceTypeToRegistry("DMN",
                                                                       "Decision Model and Notation",
                                                                       "src/main/resources",
                                                                       "dmn");

    /** DMN FEEL expression language */
    public static final ResourceType FEEL = addResourceTypeToRegistry("FEEL",
                                                                     "Friendly Enough Expression Language",
                                                                     "src/main/resources",
                                                                     "feel");

    public static ResourceType getResourceType(final String resourceType) {
        ResourceType resource = CACHE.get(resourceType);
        if (resource == null) {
            throw new RuntimeException("Unable to determine resource type " + resourceType);
        }
        return resource;
    }

    public static ResourceType determineResourceType(final String resourceName) {
        for (ResourceType type : CACHE.values()) {
            if (type.matchesExtension(resourceName)) {
                return type;
            }
        }
        return null;
    }

    public boolean matchesExtension(String resourceName) {
        if (resourceName != null) {

            if (resourceName.endsWith("." + defaultExtension)) {
                return true;
            }
            for (String extension : otherExtensions) {
                if (resourceName.endsWith("." + extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public String getDefaultExtension() {
        return defaultExtension;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "ResourceType = '" + this.description + "'";
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ResourceType other = (ResourceType) obj;
        if (!name.equals(other.name)) return false;
        return true;
    }

}
