package org.drools.util.asm;

/*
 * Copyright 2005 JBoss Inc
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

import junit.framework.TestCase;

public class FieldAccessorGeneratorTest extends TestCase {

    public void testBasic() throws Exception {
        final FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        final FieldAccessorMap map = gen.newInstanceFor( TestObject.class );
        final FieldAccessor ac = map.getFieldAccessor();
        assertNotNull( ac );

        final TestObject obj = new TestObject();
        obj.setHappy( true );
        obj.setPersonName( "michael" );
        obj.setPersonAge( 31 );

        assertEquals( true,
                      ((Boolean) ac.getFieldByIndex( obj,
                                                     0 )).booleanValue() );
        assertEquals( 31,
                      ((Integer) ac.getFieldByIndex( obj,
                                                     1 )).intValue() );
        assertEquals( "michael",
                      ac.getFieldByIndex( obj,
                                          2 ) );

        Integer index = (Integer) map.getFieldNameMap().get( "personName" );
        assertEquals( 2,
                      index.intValue() );

        index = (Integer) map.getFieldNameMap().get( "personAge" );
        assertEquals( 1,
                      index.intValue() );

        index = (Integer) map.getFieldNameMap().get( "happy" );
        assertEquals( 0,
                      index.intValue() );

    }

    public void testAnother() throws Exception {
        final FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        final FieldAccessor ac = gen.getInstanceFor( TestBean.class ).getFieldAccessor();
        final TestBean obj = new TestBean();
        obj.setBlah( false );
        obj.setSomething( "no" );
        assertEquals( false,
                      ((Boolean) ac.getFieldByIndex( obj,
                                                     0 )).booleanValue() );

        //check its being cached
        final FieldAccessor ac2 = gen.getInstanceFor( TestBean.class ).getFieldAccessor();
        assertEquals( ac,
                      ac2 );
    }

    public void testInterface() throws Exception {
        final FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        final FieldAccessorMap map = gen.newInstanceFor( TestInterface.class );
        final FieldAccessor ac = map.getFieldAccessor();
        assertNotNull( ac );

        final TestInterface obj = new TestInterfaceImpl();

        assertEquals( "foo",
                      (String) ac.getFieldByIndex( obj,
                                                   0 ) );
        assertEquals( 42,
                      ((Integer) ac.getFieldByIndex( obj,
                                                     1 )).intValue() );

        Integer index = (Integer) map.getFieldNameMap().get( "something" );
        assertEquals( 0,
                      index.intValue() );

        index = (Integer) map.getFieldNameMap().get( "another" );
        assertEquals( 1,
                      index.intValue() );

    }

    public void testAbstract() throws Exception {
        final FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        final FieldAccessorMap map = gen.newInstanceFor( TestAbstract.class );
        final FieldAccessor ac = map.getFieldAccessor();
        assertNotNull( ac );

        final TestAbstract obj = new TestAbstractImpl();

        assertEquals( 42,
                      ((Integer) ac.getFieldByIndex( obj,
                                                     1 )).intValue() );
        assertEquals( "foo",
                      (String) ac.getFieldByIndex( obj,
                                                   0 ) );

        Integer index = (Integer) map.getFieldNameMap().get( "something" );
        assertEquals( 0,
                      index.intValue() );

        index = (Integer) map.getFieldNameMap().get( "another" );
        assertEquals( 1,
                      index.intValue() );

    }

    public void testInherited() throws Exception {
        final FieldAccessorGenerator gen = FieldAccessorGenerator.getInstance();
        final FieldAccessorMap map = gen.newInstanceFor( BeanInherit.class );
        final FieldAccessor ac = map.getFieldAccessor();
        assertNotNull( ac );

        final BeanInherit obj = new BeanInherit();

        assertEquals( 42,
                      ((Integer) ac.getFieldByIndex( obj,
                                                     map.getIndex( "number" ) )).intValue() );
        assertEquals( "hola",
                      (String) ac.getFieldByIndex( obj,
                                                   map.getIndex( "text" ) ) );

    }

}