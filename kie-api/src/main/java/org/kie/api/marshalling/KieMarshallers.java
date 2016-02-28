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

package org.kie.api.marshalling;

import org.kie.api.KieBase;
import org.kie.api.Service;

/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 */
public interface KieMarshallers extends Service {
    ObjectMarshallingStrategyAcceptor newClassFilterAcceptor(String[] patterns);

    ObjectMarshallingStrategy newIdentityMarshallingStrategy();

    ObjectMarshallingStrategy newIdentityMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor);

    ObjectMarshallingStrategy newSerializeMarshallingStrategy();

    ObjectMarshallingStrategy newSerializeMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor);

    /**
     * The marshalling strategies for this method are undefined and thus they are derived from the ksession's or environment
     * provided.
     *
     * @param kbase
     * @return marshaller created for the specified KieBase
     */
    Marshaller newMarshaller(KieBase kbase);

    /**
     * This will override the strategies specified in the ksession or environment.
     *
     * @param kbase
     * @param strategies
     * @return marshaller created for the specified KieBase with the custom marshalling strategies
     */
    Marshaller newMarshaller(KieBase kbase,
                             ObjectMarshallingStrategy[] strategies);
}
