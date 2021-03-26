/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.infinispan.protostream.FileDescriptorSource;

import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class ProtostreamProducer {

    @Inject
    ObjectMapper mapper;

    @Produces
    FileDescriptorSource kogitoTypesDescriptor() throws IOException {
        FileDescriptorSource source = new FileDescriptorSource();
        source.addProtoFile("decision.proto", Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/decision.proto"));
        return source;
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller decisionMarshaller() {
        return new DecisionMarshaller(mapper);
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller decisionInputMarshaller() {
        return new DecisionInputMarshaller(mapper);
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller decisionOutcomeMarshaller() {
        return new DecisionOutcomeMarshaller(mapper);
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller explainabilityResultMarshaller() {
        return new ExplainabilityResultMarshaller(mapper);
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller featureImportanceMarshaller() {
        return new FeatureImportanceModelMarshaller(mapper);
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller messageExceptionFieldMarshaller() {
        return new MessageExceptionFieldMarshaller(mapper);
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller messageMarshaller() {
        return new MessageMarshaller(mapper);
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller saliencyMarshaller() {
        return new SaliencyModelMarshaller(mapper);
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller typedValueMarshaller() {
        return new TypedVariableMarshaller(mapper);
    }

    @Produces
    org.infinispan.protostream.MessageMarshaller dmnModelMarshaller() {
        return new DMNModelWithMetadataMarshaller(mapper);
    }
}
