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
package org.kie.kogito.process.dynamic;

import java.util.Map;

public class RestCallInfo {

    private String endpoint;
    private String host;
    private Integer port;
    private String method;
    private Map<String, Object> arguments;
    private String outputExpression;
    private String outputExpressionLang = "jq";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public String getOutputExpression() {
        return outputExpression;
    }

    public void setOutputExpression(String outputExpression) {
        this.outputExpression = outputExpression;
    }

    public String getOutputExpressionLang() {
        return outputExpressionLang;
    }

    public void setOutputExpressionLang(String outputExpressionLang) {
        this.outputExpressionLang = outputExpressionLang;
    }

    @Override
    public String toString() {
        return "RestCallInfo [endpoint=" + endpoint + ", host=" + host + ", port=" + port + ", method=" + method
                + ", arguments=" + arguments + ", outputExpression=" + outputExpression + ", outputExpressionLang="
                + outputExpressionLang + "]";
    }
}
