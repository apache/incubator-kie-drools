package org.kie.dmn.feel.lang.impl;

import static org.junit.Assert.*;
import static org.kie.dmn.feel.util.DynamicTypeUtils.*;

import java.util.Map;

import org.junit.Test;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class MapBackedTypeTest {
    @Test
    public void testBasic() {
        MapBackedType personType = new MapBackedType( "Person" , mapOf( entry("First Name", BuiltInType.STRING), entry("Last Name", BuiltInType.STRING) ));
        
        Map<?, ?> aPerson = prototype( entry("First Name", "John"), entry("Last Name", "Doe") );
        assertTrue( personType.isAssignableValue(aPerson) );
        assertTrue( personType.isInstanceOf(aPerson) );
        
        Map<?, ?> aCompletePerson = prototype( entry("First Name", "John"), entry("Last Name", "Doe"), entry("Address", "100 East Davie Street"));
        assertTrue( personType.isAssignableValue(aCompletePerson) );
        assertTrue( personType.isInstanceOf(aCompletePerson) );
        
        Map<?, ?> notAPerson = prototype( entry("First Name", "John") );
        assertFalse( personType.isAssignableValue(notAPerson) );
        assertFalse( personType.isInstanceOf(notAPerson) );
        
        Map<?, ?> anonymousPerson1 = prototype( entry("First Name", null), entry("Last Name", "Doe") );
        assertTrue( personType.isAssignableValue(anonymousPerson1) );
        assertTrue( personType.isInstanceOf(anonymousPerson1) );
        
        Map<?, ?> anonymousPerson2 = prototype( entry("First Name", "John"), entry("Last Name", null) );
        assertTrue( personType.isAssignableValue(anonymousPerson2) );
        assertTrue( personType.isInstanceOf(anonymousPerson2) );
        
        Map<?, ?> anonymousPerson3 = prototype( entry("First Name", null), entry("Last Name", null) );
        assertTrue( personType.isAssignableValue(anonymousPerson3) );
        assertTrue( personType.isInstanceOf(anonymousPerson3) );
        
        Map<?, ?> anonymousCompletePerson = prototype( entry("First Name", null), entry("Last Name", null), entry("Address", "100 East Davie Street"));
        assertTrue( personType.isAssignableValue(anonymousCompletePerson) );
        assertTrue( personType.isInstanceOf(anonymousCompletePerson) );
        
        Map<?, ?> nullPerson = null;
        assertTrue( personType.isAssignableValue(nullPerson) );
        assertFalse( personType.isInstanceOf(nullPerson) );
    }
}
