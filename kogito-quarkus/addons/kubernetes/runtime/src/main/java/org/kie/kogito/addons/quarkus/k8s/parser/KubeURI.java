/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.addons.quarkus.k8s.parser;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.kogito.addons.quarkus.k8s.KubeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubeURI {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());

    private String protocol;
    private GVK gvk;
    private String namespace;
    private String resourceName;
    private String rawUrl;

    // KubeURI query parameters
    private String customPortName;
    private Map<String, String> customLabels;

    /**
     * @param rawKubeURI
     */
    public KubeURI(String rawKubeURI) {
        String[] protoAndValues = rawKubeURI.split(":");
        if (protoAndValues.length <= 1) {
            logger.error("the provided URI {} is not valid", rawKubeURI);
        }

        String[] values = protoAndValues[1].split("/");

        this.protocol = protoAndValues[0];
        this.rawUrl = rawKubeURI;
        switch (values.length) {
            case 5:
                this.gvk = new GVK(values[0], values[1], values[2]);
                this.namespace = values[3];
                this.resourceName = values[values.length - 1];
                break;

            case 4:
                // GVK can be g/v/k or v/k
                // for now only v1 api version is supported
                if (values[0].equals("v1")) {
                    this.gvk = new GVK(values[0], values[1]);
                    this.namespace = values[2];
                } else {
                    this.gvk = new GVK(values[0], values[1], values[2]);
                }
                this.resourceName = values[values.length - 1];
                break;

            case 3:
                // GVK is only v/k
                this.gvk = new GVK(values[0], values[1]);
                this.resourceName = values[values.length - 1];
                break;

            case 2:
            case 1:
                // just function, get the current namespace to query the func.
                logger.debug("Custom Functions calls are not yet implemented  for more information please visit https://issues.redhat.com/browse/KOGITO-8443");
                break;

            default:
                logger.error("KubeURI {} is not valid", rawKubeURI);
        }

        if (rawUrl.contains("?")) {
            setAttributes(rawUrl);
        }

        if (resourceName == null || resourceName.isBlank()) {
            throw new IllegalArgumentException("resource name can't be empty");
        } else if (!KubeConstants.SUPPORTED_PROTOCOLS.contains(protocol)) {
            throw new IllegalArgumentException("the provided protocol [" + protocol + "] is not " +
                    "supported, supported values are " +
                    KubeConstants.SUPPORTED_PROTOCOLS);
        } else {
            logger.debug(" KubeURI successfully parsed: {}", this);
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public GVK getGvk() {
        return gvk;
    }

    /**
     * target namespace to query the resource.
     *
     * @return if provided within the KubeURI return it, otherwise the engine will use the current namespace within the
     *         kubernetes context
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * if the namespace is not provided, it will be set by the engine to use the current namespace/context
     * 
     * @param namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getUrl() {
        return rawUrl;
    }

    public String getResourceName() {
        return resourceName;
    }

    private void setAttributes(String attrs) {
        // if there is more than one & at the query parameters, ignore the rest of them
        for (String str : attrs.split("\\?")[1].split("&")) {
            int indexOf = str.indexOf("=");
            String param = str.substring(0, indexOf);
            String value = str.substring(indexOf + 1);
            switch (param) {
                case KubeConstants.CUSTOM_PORT_NAME_PROPERTY:
                    this.customPortName = value;
                    break;

                case KubeConstants.CUSTOM_RESOURCE_LABEL_PROPERTY:
                    this.customLabels = Arrays.asList(value.split(";"))
                            .stream()
                            .map(str1 -> str1.split("="))
                            .collect(Collectors.toMap(lblName -> lblName[0], lblValue -> lblValue[1]));
                    break;
                default:
                    logger.warn("The given parameters {} is not supported", param);
            }
        }
    }

    public String getCustomPortName() {
        return customPortName;
    }

    public Map<String, String> getCustomLabel() {
        return customLabels;
    }

    @Override
    public String toString() {
        return "KubeURI{" +
                "protocol='" + protocol + '\'' +
                ", gvk=" + gvk.toString() +
                ", namespace='" + namespace + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", rawUrl='" + rawUrl + '\'' +
                ", customPortName='" + customPortName + '\'' +
                ", customLabels=" + customLabels +
                '}';
    }
}
