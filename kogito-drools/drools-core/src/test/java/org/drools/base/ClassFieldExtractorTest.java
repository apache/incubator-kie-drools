package org.drools.base;

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

import org.drools.util.asm.BeanInherit;
import org.drools.util.asm.InterfaceChild;
import org.drools.util.asm.TestAbstract;
import org.drools.util.asm.TestAbstractImpl;
import org.drools.util.asm.TestBean;
import org.drools.util.asm.TestInterface;
import org.drools.util.asm.TestInterfaceImpl;

public class ClassFieldExtractorTest extends TestCase {

    public void testBasic() throws Exception {
        final Object[] objArray = new Object[1];

        final TestBean obj = new TestBean();
        obj.setBlah( false );
        obj.setSomething( "no" );
        obj.setObjArray( objArray );

        final ClassFieldExtractor ext = ClassFieldExtractorCache.getExtractor( TestBean.class,
                                                                               "blah" );
        assertEquals( false,
                      ((Boolean) ext.getValue( obj )).booleanValue() );

        final ClassFieldExtractor ext2 = ClassFieldExtractorCache.getExtractor( TestBean.class,
                                                                                "fooBar" );
        assertEquals( "fooBar",
                      ext2.getValue( obj ) );

        final ClassFieldExtractor ext3 = ClassFieldExtractorCache.getExtractor( TestBean.class,
                                                                                "objArray" );
        assertEquals( objArray,
                      ext3.getValue( obj ) );

    }

    public void testInterface() throws Exception {

        final TestInterface obj = new TestInterfaceImpl();
        final ClassFieldExtractor ext = ClassFieldExtractorCache.getExtractor( TestInterface.class,
                                                                               "something" );

        assertEquals( "foo",
                      (String) ext.getValue( obj ) );

    }

    public void testAbstract() throws Exception {

        final ClassFieldExtractor ext = ClassFieldExtractorCache.getExtractor( TestAbstract.class,
                                                                               "something" );
        final TestAbstract obj = new TestAbstractImpl();
        assertEquals( "foo",
                      (String) ext.getValue( obj ) );

    }

    public void testInherited() throws Exception {
        final ClassFieldExtractor ext = ClassFieldExtractorCache.getExtractor( BeanInherit.class,
                                                                               "text" );
        final BeanInherit obj = new BeanInherit();
        assertEquals( "hola",
                      (String) ext.getValue( obj ) );

    }

    public void testMultipleInterfaces() throws Exception {
        final ConcreteChild obj = new ConcreteChild();
        final ClassFieldExtractor ext = ClassFieldExtractorCache.getExtractor( InterfaceChild.class,
                                                                               "foo" );
        assertEquals( 42,
                      ((Number) ext.getValue( obj )).intValue() );
    }

    public void testLong() throws Exception {
        final ClassFieldExtractor ext = ClassFieldExtractorCache.getExtractor( TestBean.class,
                                                                               "longField" );
        final TestBean bean = new TestBean();
        assertEquals( 424242,
                      ((Number) ext.getValue( bean )).longValue() );
    }

}