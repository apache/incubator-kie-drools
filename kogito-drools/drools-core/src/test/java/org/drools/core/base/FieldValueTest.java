/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.core.spi.FieldValue;

public class FieldValueTest {
    FieldValue field1;
    FieldValue field2;
    FieldValue field3;
    FieldValue field4;
    FieldValue field5;

    @Before
    public void setUp() throws Exception {
        this.field1 = FieldFactory.getInstance().getFieldValue( null );
        this.field2 = FieldFactory.getInstance().getFieldValue( null );
        this.field3 = FieldFactory.getInstance().getFieldValue( "A" );
        this.field4 = FieldFactory.getInstance().getFieldValue( "A" );
        this.field5 = FieldFactory.getInstance().getFieldValue( "B" );
    }

    /*
     * Test method for 'org.kie.base.FieldValue.hashCode()'
     */
    @Test
    public void testHashCode() {
        assertEquals( this.field1.hashCode(),
                             this.field1.hashCode() );
        assertEquals( this.field1.hashCode(),
                             this.field2.hashCode() );
        assertEquals( this.field3.hashCode(),
                             this.field3.hashCode() );
        assertEquals( this.field3.hashCode(),
                             this.field4.hashCode() );
        assertFalse( this.field1.hashCode() == this.field3.hashCode() );
        assertFalse( this.field3.hashCode() == this.field1.hashCode() );
        assertFalse( this.field3.hashCode() == this.field5.hashCode() );
    }

    /*
     * Test method for 'org.kie.base.FieldValue.equals(Object)'
     */
    @Test
    public void testEqualsObject() {
        assertEquals( this.field1,
                             this.field1 );
        assertEquals( this.field1,
                             this.field2 );
        assertEquals( this.field3,
                             this.field3 );
        assertEquals( this.field3,
                             this.field4 );
        assertFalse( this.field1.equals( this.field3 ) );
        assertFalse( this.field3.equals( this.field1 ) );
        assertFalse( this.field3.equals( this.field5 ) );
    }

}
