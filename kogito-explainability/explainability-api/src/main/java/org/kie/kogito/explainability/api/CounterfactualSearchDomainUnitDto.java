/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualSearchDomainUnitDto extends CounterfactualSearchDomainDto {

    public static final String IS_FIXED_FIELD = "isFixed";
    public static final String DOMAIN_FIELD = "domain";

    @JsonProperty(IS_FIXED_FIELD)
    private Boolean isFixed;

    @JsonProperty(DOMAIN_FIELD)
    private CounterfactualDomainDto domain;

    public CounterfactualSearchDomainUnitDto() {
    }

    public CounterfactualSearchDomainUnitDto(String type) {
        this(type, Boolean.TRUE, null);
    }

    public CounterfactualSearchDomainUnitDto(String type, Boolean isFixed, CounterfactualDomainDto domain) {
        super(CounterfactualSearchDomainDto.Kind.UNIT, type);
        this.isFixed = isFixed;
        this.domain = domain;
    }

    public Boolean isFixed() {
        return isFixed;
    }

    public CounterfactualDomainDto getDomain() {
        return domain;
    }
}
