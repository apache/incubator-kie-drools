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

import java.util.Collections;
import java.util.List;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomain;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.BooleanTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.CollectionTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.EnumTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.ObjectTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.StringTestField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;

import static org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain.DOMAIN;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain.IS_FIXED;
import static org.kie.kogito.trusty.storage.api.model.TypedVariable.COMPONENTS_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariable.KIND_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariable.NAME_FIELD;
import static org.kie.kogito.trusty.storage.api.model.TypedVariable.TYPE_REF_FIELD;

public class CounterfactualSearchDomainMarshallerTest extends MarshallerTestTemplate<CounterfactualSearchDomain> {

    private static final List<AbstractTestField<CounterfactualSearchDomain, ?>> TEST_FIELD_LIST = List.of(
            new EnumTestField<>(KIND_FIELD, TypedValue.Kind.UNIT, CounterfactualSearchDomain::getKind, CounterfactualSearchDomain::setKind, TypedValue.Kind.class),
            new StringTestField<>(NAME_FIELD, "testName", CounterfactualSearchDomain::getName, CounterfactualSearchDomain::setName),
            new StringTestField<>(TYPE_REF_FIELD, "testTypeRef", CounterfactualSearchDomain::getTypeRef, CounterfactualSearchDomain::setTypeRef),
            new CollectionTestField<>(COMPONENTS_FIELD, Collections.emptyList(), CounterfactualSearchDomain::getComponents, CounterfactualSearchDomain::setComponents,
                    CounterfactualSearchDomain.class),
            new BooleanTestField<>(IS_FIXED, Boolean.TRUE, CounterfactualSearchDomain::isFixed, CounterfactualSearchDomain::setFixed),
            new ObjectTestField<>(DOMAIN, new CounterfactualDomainRange(new IntNode(1), new IntNode(2)), CounterfactualSearchDomain::getDomain, CounterfactualSearchDomain::setDomain,
                    CounterfactualDomain.class));

    public CounterfactualSearchDomainMarshallerTest() {
        super(CounterfactualSearchDomain.class);
    }

    @Override
    protected CounterfactualSearchDomain buildEmptyObject() {
        return new CounterfactualSearchDomain();
    }

    @Override
    protected MessageMarshaller<CounterfactualSearchDomain> buildMarshaller() {
        return new CounterfactualSearchDomainMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<CounterfactualSearchDomain, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }
}
