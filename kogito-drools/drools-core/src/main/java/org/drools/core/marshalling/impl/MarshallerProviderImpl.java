/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.marshalling.impl;

import org.kie.api.KieBase;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;

public class MarshallerProviderImpl implements KieMarshallers {

    public ObjectMarshallingStrategyAcceptor newClassFilterAcceptor(String[] patterns) {
        return new ClassObjectMarshallingStrategyAcceptor( patterns );
    }

    public ObjectMarshallingStrategy newIdentityMarshallingStrategy() {
        return new IdentityPlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT );
    }

    public ObjectMarshallingStrategy newIdentityMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        return new IdentityPlaceholderResolverStrategy( acceptor );
    }



    public ObjectMarshallingStrategy newSerializeMarshallingStrategy() {
        return new SerializablePlaceholderResolverStrategy( ClassObjectMarshallingStrategyAcceptor.DEFAULT  );
    }

    public ObjectMarshallingStrategy newSerializeMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        return new SerializablePlaceholderResolverStrategy( acceptor );
    }
    
    public Marshaller newMarshaller(KieBase kbase) {
        return newMarshaller(kbase, null );
    }
    
    public Marshaller newMarshaller(KieBase kbase, ObjectMarshallingStrategy[] strategies) {
        return new ProtobufMarshaller( kbase , new MarshallingConfigurationImpl( strategies, true, true ) );
    }
    
}
