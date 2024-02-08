/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.graphql;

import org.kie.kogito.index.api.DateTimeCoercing;

import graphql.schema.GraphQLScalarType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class GraphQLScalarTypeProducer {

    private DateTimeCoercing dateTimeCoercing;

    @Inject
    public GraphQLScalarTypeProducer(DateTimeCoercing dateTimeCoercing) {
        this.dateTimeCoercing = dateTimeCoercing;
    }

    @Produces
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("An ISO-8601 compliant DateTime Scalar")
                .coercing(dateTimeCoercing)
                .build();
    }
}
