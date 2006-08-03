package org.drools.facttemplates;

import org.drools.base.ValueType;

import junit.framework.TestCase;

public class TestFieldTemplate extends TestCase {
    public void  testFieldTemplate() {
        FieldTemplate cheeseName = new FieldTemplateImpl("name", 5, String.class);
        assertEquals( "name", cheeseName.getName() ); 
        assertEquals( ValueType.STRING_TYPE, cheeseName.getValueType() );
        assertEquals( 5, cheeseName.getIndex() );
        
    }
}
