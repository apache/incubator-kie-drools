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
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomain;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainCategorical;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.EnumTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.ObjectTestField;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.kie.kogito.trusty.storage.api.model.CounterfactualDomain.CATEGORICAL;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualDomain.TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CounterfactualDomainMarshallerWithDomainCategoricalTest extends MarshallerTestTemplate<CounterfactualDomain> {

    private static final CounterfactualDomainCategorical domain = new CounterfactualDomainCategorical();

    private static final Function<CounterfactualDomain, CounterfactualDomainCategorical> getter = mock(Function.class);

    private static final BiConsumer<CounterfactualDomain, CounterfactualDomainCategorical> setter = mock(BiConsumer.class);

    private static final List<AbstractTestField<CounterfactualDomain, ?>> TEST_FIELD_LIST = List.of(
            new EnumTestField<>(TYPE,
                    CounterfactualDomain.Type.CATEGORICAL,
                    CounterfactualDomain::getType,
                    CounterfactualDomain::setType,
                    CounterfactualDomain.Type.class),
            new ObjectTestField<>(CATEGORICAL,
                    domain,
                    getter,
                    setter,
                    CounterfactualDomainCategorical.class));

    public CounterfactualDomainMarshallerWithDomainCategoricalTest() {
        super(CounterfactualDomain.class);
        when(getter.apply(any())).thenReturn(domain);
    }

    @Override
    protected CounterfactualDomainCategorical buildEmptyObject() {
        return domain;
    }

    @Override
    protected MessageMarshaller<CounterfactualDomain> buildMarshaller() {
        return new CounterfactualDomainMarshaller(new ObjectMapper()) {
            @Override
            public CounterfactualDomain readFrom(ProtoStreamReader reader) throws IOException {
                when(reader.readObject(eq(CATEGORICAL), any())).thenReturn(domain);
                return super.readFrom(reader);
            }
        };
    }

    @Override
    protected List<AbstractTestField<CounterfactualDomain, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }

}
