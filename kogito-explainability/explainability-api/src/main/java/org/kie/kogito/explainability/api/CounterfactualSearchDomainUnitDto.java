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

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterfactualSearchDomainUnitDto extends CounterfactualSearchDomainDto {

    public static final String FIXED_FIELD = "fixed";
    public static final String DOMAIN_FIELD = "domain";

    @JsonProperty(FIXED_FIELD)
    @NotNull(message = "fixed must be provided.")
    private Boolean isFixed;

    @JsonProperty(DOMAIN_FIELD)
    @NotNull(message = "domain object must be provided.")
    private CounterfactualDomainDto domain;

    public CounterfactualSearchDomainUnitDto() {
    }

    public CounterfactualSearchDomainUnitDto(@NotNull String type,
            @NotNull Boolean isFixed,
            @NotNull CounterfactualDomainDto domain) {
        super(CounterfactualSearchDomainDto.Kind.UNIT, type);
        this.isFixed = Objects.requireNonNull(isFixed);
        this.domain = Objects.requireNonNull(domain);
    }

    public Boolean isFixed() {
        return isFixed;
    }

    public CounterfactualDomainDto getDomain() {
        return domain;
    }
}
