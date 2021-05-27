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
import java.util.Locale;

import org.kie.kogito.trusty.storage.api.model.CounterfactualDomain;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainCategorical;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CounterfactualDomainMarshaller extends AbstractModelMarshaller<CounterfactualDomain> {

    public CounterfactualDomainMarshaller(ObjectMapper mapper) {
        super(mapper, CounterfactualDomain.class);
    }

    @Override
    public CounterfactualDomain readFrom(ProtoStreamReader reader) throws IOException {
        String type = reader.readString(CounterfactualDomain.TYPE_FIELD);

        if (CounterfactualDomainCategorical.TYPE.equals(type)) {
            return reader.readObject(toProtobufName(CounterfactualDomainCategorical.TYPE),
                    CounterfactualDomainCategorical.class);
        } else if (CounterfactualDomainRange.TYPE.equals(type)) {
            return reader.readObject(toProtobufName(CounterfactualDomainRange.TYPE),
                    CounterfactualDomainRange.class);
        }
        throw new IllegalArgumentException(String.format("An unexpected CounterfactualDomain.Type '%s' was detected.", type));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, CounterfactualDomain input) throws IOException {
        if (input instanceof CounterfactualDomainCategorical) {
            writer.writeString(CounterfactualDomain.TYPE_FIELD,
                    CounterfactualDomainCategorical.TYPE);
            writer.writeObject(toProtobufName(CounterfactualDomainCategorical.TYPE),
                    (CounterfactualDomainCategorical) input,
                    CounterfactualDomainCategorical.class);
        } else if (input instanceof CounterfactualDomainRange) {
            writer.writeString(CounterfactualDomain.TYPE_FIELD,
                    CounterfactualDomainRange.TYPE);
            writer.writeObject(toProtobufName(CounterfactualDomainRange.TYPE),
                    (CounterfactualDomainRange) input,
                    CounterfactualDomainRange.class);
        }
    }

    public static String toProtobufName(String type) {
        return type.toLowerCase(Locale.ROOT);
    }
}
