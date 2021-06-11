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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = CounterfactualDomainDto.TYPE_FIELD)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CounterfactualDomainRangeDto.class, name = CounterfactualDomainRangeDto.TYPE),
        @JsonSubTypes.Type(value = CounterfactualDomainCategoricalDto.class, name = CounterfactualDomainCategoricalDto.TYPE),
        @JsonSubTypes.Type(value = CounterfactualDomainFixedDto.class, name = CounterfactualDomainFixedDto.TYPE),

})
public abstract class CounterfactualDomainDto {

    public static final String TYPE_FIELD = "type";

}
