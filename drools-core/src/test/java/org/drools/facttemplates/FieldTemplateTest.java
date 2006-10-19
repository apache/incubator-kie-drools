package org.drools.facttemplates;

import junit.framework.TestCase;

import org.drools.base.ValueType;

public class FieldTemplateTest extends TestCase {
    public void testFieldTemplate() {
        final FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                          5,
                                                          String.class );
        assertEquals( "name",
                      cheeseName.getName() );
        assertEquals( ValueType.STRING_TYPE,
                      cheeseName.getValueType() );
        assertEquals( 5,
                      cheeseName.getIndex() );

    }
}
