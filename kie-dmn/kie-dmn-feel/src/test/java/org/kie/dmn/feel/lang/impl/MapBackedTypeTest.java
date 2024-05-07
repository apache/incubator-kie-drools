/**
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
package org.kie.dmn.feel.lang.impl;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;
import static org.kie.dmn.feel.util.DynamicTypeUtils.prototype;

class MapBackedTypeTest {
    @Test
    void basic() {
        MapBackedType personType = new MapBackedType( "Person" , mapOf( entry("First Name", BuiltInType.STRING), entry("Last Name", BuiltInType.STRING) ));
        
        Map<?, ?> aPerson = prototype( entry("First Name", "John"), entry("Last Name", "Doe") );
        assertThat(personType.isAssignableValue(aPerson)).isTrue();
        assertThat(personType.isInstanceOf(aPerson)).isTrue();
        
        Map<?, ?> aCompletePerson = prototype( entry("First Name", "John"), entry("Last Name", "Doe"), entry("Address", "100 East Davie Street"));
        assertThat(personType.isAssignableValue(aCompletePerson)).isTrue();
        assertThat(personType.isInstanceOf(aCompletePerson)).isTrue();
        
        Map<?, ?> notAPerson = prototype( entry("First Name", "John") );
        assertThat(personType.isAssignableValue(notAPerson)).isFalse();
        assertThat(personType.isInstanceOf(notAPerson)).isFalse();
        
        Map<?, ?> anonymousPerson1 = prototype( entry("First Name", null), entry("Last Name", "Doe") );
        assertThat(personType.isAssignableValue(anonymousPerson1)).isTrue();
        assertThat(personType.isInstanceOf(anonymousPerson1)).isTrue();
        
        Map<?, ?> anonymousPerson2 = prototype( entry("First Name", "John"), entry("Last Name", null) );
        assertThat(personType.isAssignableValue(anonymousPerson2)).isTrue();
        assertThat(personType.isInstanceOf(anonymousPerson2)).isTrue();
        
        Map<?, ?> anonymousPerson3 = prototype( entry("First Name", null), entry("Last Name", null) );
        assertThat(personType.isAssignableValue(anonymousPerson3)).isTrue();
        assertThat(personType.isInstanceOf(anonymousPerson3)).isTrue();
        
        Map<?, ?> anonymousCompletePerson = prototype( entry("First Name", null), entry("Last Name", null), entry("Address", "100 East Davie Street"));
        assertThat(personType.isAssignableValue(anonymousCompletePerson)).isTrue();
        assertThat(personType.isInstanceOf(anonymousCompletePerson)).isTrue();
        
        Map<?, ?> nullPerson = null;
        assertThat(personType.isAssignableValue(nullPerson)).isTrue();
        assertThat(personType.isInstanceOf(nullPerson)).isFalse();
    }
}
