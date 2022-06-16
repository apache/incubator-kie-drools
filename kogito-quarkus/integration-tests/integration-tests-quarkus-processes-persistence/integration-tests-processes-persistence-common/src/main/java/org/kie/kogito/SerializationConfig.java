/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = { Person.class, Person[].class, Address.class, Instant.class, ArrayList.class, String.class, BigDecimal.class, Number.class, BigInteger.class, ZonedDateTime.class,
        byte[].class, int[].class }, classNames = { "java.time.Ser" }, serialization = true)
public class SerializationConfig {
}