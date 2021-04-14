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
import java.util.ArrayList;

import org.kie.kogito.tracing.typedvalue.TypedValue.Kind;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomain;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CounterfactualSearchDomainMarshaller extends AbstractModelMarshaller<CounterfactualSearchDomain> {

    public CounterfactualSearchDomainMarshaller(ObjectMapper mapper) {
        super(mapper, CounterfactualSearchDomain.class);
    }

    @Override
    public CounterfactualSearchDomain readFrom(ProtoStreamReader reader) throws IOException {
        return new CounterfactualSearchDomain(
                enumFromString(reader.readString(CounterfactualSearchDomain.KIND_FIELD), Kind.class),
                reader.readString(CounterfactualSearchDomain.NAME_FIELD),
                reader.readString(CounterfactualSearchDomain.TYPE_REF_FIELD),
                reader.readCollection(CounterfactualSearchDomain.COMPONENTS_FIELD, new ArrayList<>(), CounterfactualSearchDomain.class),
                reader.readBoolean(CounterfactualSearchDomain.IS_FIXED),
                reader.readObject(CounterfactualSearchDomain.DOMAIN, CounterfactualDomain.class));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, CounterfactualSearchDomain input) throws IOException {
        writer.writeString(CounterfactualSearchDomain.KIND_FIELD, stringFromEnum(input.getKind()));
        writer.writeString(CounterfactualSearchDomain.NAME_FIELD, input.getName());
        writer.writeString(CounterfactualSearchDomain.TYPE_REF_FIELD, input.getTypeRef());
        writer.writeCollection(CounterfactualSearchDomain.COMPONENTS_FIELD, input.getComponents(), CounterfactualSearchDomain.class);
        writer.writeBoolean(CounterfactualSearchDomain.IS_FIXED, input.isFixed());
        writer.writeObject(CounterfactualSearchDomain.DOMAIN, input.getDomain(), CounterfactualDomain.class);
    }
}
