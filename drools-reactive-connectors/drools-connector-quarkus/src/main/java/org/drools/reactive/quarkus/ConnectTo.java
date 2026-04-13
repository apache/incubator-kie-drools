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
package org.drools.reactive.quarkus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

/**
 * CDI qualifier annotation that marks a {@link org.drools.ruleunits.api.DataStream}
 * field to be automatically wired to a reactive connector.
 *
 * <p>Usage example:
 * <pre>{@code
 * public class FraudDetectionUnit implements RuleUnitData {
 *     @ConnectTo(connector = "kafka", name = "transactions")
 *     private DataStream<Transaction> transactions;
 * }
 * }</pre>
 *
 * <p>The connector configuration is read from {@code application.properties}
 * using the key prefix {@code drools.connector.<name>.*}.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface ConnectTo {

    /** The connector type: "kafka", "pulsar", or "debezium". */
    String connector();

    /** The logical name of this connector instance, used as config key prefix. */
    String name();
}
