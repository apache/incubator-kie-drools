/*
 * Copyright 2010 JBoss Inc
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

package org.drools.marshalling.impl;

import org.drools.marshalling.MarshallingConfiguration;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyStore;

public class MarshallingConfigurationImpl
    implements
    MarshallingConfiguration {
    private ObjectMarshallingStrategyStore objectMarshallingStrategyStore;
    private boolean                            marshallProcessInstances;
    private boolean                            marshallWorkItems;

    public MarshallingConfigurationImpl() {
        this( null,
              true,
              true );
    }

    public MarshallingConfigurationImpl(ObjectMarshallingStrategy[] strategies,
                                        boolean marshallProcessInstances,
                                        boolean marshallWorkItems) {
        if ( strategies != null ) {
            this.objectMarshallingStrategyStore = new ObjectMarshallingStrategyStoreImpl( strategies );
        }
        this.marshallProcessInstances = marshallProcessInstances;
        this.marshallWorkItems = marshallWorkItems;
    }

    public boolean isMarshallProcessInstances() {
        return this.marshallProcessInstances;
    }

    public void setMarshallProcessInstances(boolean marshallProcessInstances) {
        this.marshallProcessInstances = marshallProcessInstances;
    }

    public boolean isMarshallWorkItems() {
        return this.marshallWorkItems;
    }

    public void setMarshallWorkItems(boolean marshallWorkItems) {
        this.marshallWorkItems = marshallWorkItems;
    }

    public ObjectMarshallingStrategyStore getObjectMarshallingStrategyStore() {
        return this.objectMarshallingStrategyStore;
    }

    public void setPlaceholderResolverStrategyFactory(ObjectMarshallingStrategyStore placeholderResolverStrategyFactory) {
        this.objectMarshallingStrategyStore = placeholderResolverStrategyFactory;
    }

}
