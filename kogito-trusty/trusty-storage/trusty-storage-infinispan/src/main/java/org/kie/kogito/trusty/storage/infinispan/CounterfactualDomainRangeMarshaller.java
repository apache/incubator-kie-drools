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
package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;

import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CounterfactualDomainRangeMarshaller extends AbstractModelMarshaller<CounterfactualDomainRange> {

    public CounterfactualDomainRangeMarshaller(ObjectMapper mapper) {
        super(mapper, CounterfactualDomainRange.class);
    }

    @Override
    public CounterfactualDomainRange readFrom(ProtoStreamReader reader) throws IOException {
        return new CounterfactualDomainRange(
                jsonFromString(reader.readString(CounterfactualDomainRange.LOWER_BOUND)),
                jsonFromString(reader.readString(CounterfactualDomainRange.UPPER_BOUND)));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, CounterfactualDomainRange input) throws IOException {
        writer.writeString(CounterfactualDomainRange.LOWER_BOUND, stringFromJson(input.getLowerBound()));
        writer.writeString(CounterfactualDomainRange.UPPER_BOUND, stringFromJson(input.getUpperBound()));
    }
}
