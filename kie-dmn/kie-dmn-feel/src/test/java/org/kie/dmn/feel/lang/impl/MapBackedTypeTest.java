package org.kie.dmn.feel.lang.impl;

import static org.kie.dmn.feel.util.DynamicTypeUtils.*;

import java.util.Map;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class MapBackedTypeTest {
    @Test
    public void testBasic() {
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
