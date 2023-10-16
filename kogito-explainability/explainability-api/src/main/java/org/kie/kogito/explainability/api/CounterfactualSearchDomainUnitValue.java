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
package org.kie.kogito.explainability.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualSearchDomainUnitValue extends CounterfactualSearchDomainValue {

    public static final String BASE_TYPE = "baseType";
    public static final String FIXED = "fixed";
    public static final String DOMAIN = "domain";

    @JsonProperty(BASE_TYPE)
    @JsonInclude(NON_NULL)
    private String baseType;

    @JsonProperty(FIXED)
    private Boolean isFixed;

    @JsonProperty(DOMAIN)
    private CounterfactualDomain domain;

    private CounterfactualSearchDomainUnitValue() {
    }

    public CounterfactualSearchDomainUnitValue(String type,
            String baseType,
            Boolean isFixed,
            CounterfactualDomain domain) {
        super(Kind.UNIT, type);
        this.baseType = baseType;
        this.isFixed = isFixed;
        this.domain = domain;
    }

    public String getBaseType() {
        return baseType;
    }

    public Boolean isFixed() {
        return isFixed;
    }

    public CounterfactualDomain getDomain() {
        return domain;
    }

}
