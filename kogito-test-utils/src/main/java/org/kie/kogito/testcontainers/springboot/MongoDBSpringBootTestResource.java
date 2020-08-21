/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.testcontainers.springboot;

import org.kie.kogito.resources.ConditionalSpringBootTestResource;
import org.kie.kogito.testcontainers.KogitoMongoDBContainer;

/**
 * MongoDB spring boot resource that works within the test lifecycle.
 *
 */
public class MongoDBSpringBootTestResource extends ConditionalSpringBootTestResource {

    public static final String MONGODB_CONNECTION_PROPERTY = "spring.data.mongodb.uri";

    private static final  KogitoMongoDBContainer container = new KogitoMongoDBContainer();
    
    public MongoDBSpringBootTestResource() {
        super(container);
    }

    @Override
    protected String getKogitoProperty() {
        return MONGODB_CONNECTION_PROPERTY;
    }

    @Override
    protected String getKogitoPropertyValue() {
        return container.getReplicaSetUrl();
    }

    public static class Conditional extends MongoDBSpringBootTestResource {

        public Conditional() {
            super();
            enableConditional();
        }
    }
}
