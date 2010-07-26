/**
 * Copyright 2010 JBoss Inc
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

package org.drools.base.extractors;

import junit.framework.Assert;

import org.drools.base.ClassFieldAccessorCache;
import org.drools.base.ClassFieldAccessorStore;
import org.drools.base.TestBean;
import org.drools.spi.InternalReadAccessor;

public class CharClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    InternalReadAccessor reader;
    TestBean  bean      = new TestBean();

    protected void setUp() throws Exception {
        ClassFieldAccessorStore store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.reader = store.getReader( TestBean.class,
                                              "charAttr",
                                              getClass().getClassLoader() );
    }

    public void testGetBooleanValue() {
        try {
            this.reader.getBooleanValue( null,
                                            this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    public void testGetByteValue() {
            Assert.assertEquals( 'a',
                                 this.reader.getByteValue( null,
                                                              this.bean ) );
    }

    public void testGetCharValue() {
            Assert.assertEquals( 'a',
                                 this.reader.getCharValue( null,
                                                              this.bean ) );
    }

    public void testGetDoubleValue() {
            Assert.assertEquals( 'a',
                                 (int) this.reader.getDoubleValue( null,
                                                                      this.bean ) );
    }

    public void testGetFloatValue() {
            Assert.assertEquals( 'a',
                                 (int) this.reader.getFloatValue( null,
                                                                     this.bean ) );
    }

    public void testGetIntValue() {
            Assert.assertEquals( 'a',
                                 this.reader.getIntValue( null,
                                                             this.bean ) );
    }

    public void testGetLongValue() {
            Assert.assertEquals( 'a',
                                 (int) this.reader.getLongValue( null,
                                                                    this.bean ) );
    }

    public void testGetShortValue() {
            Assert.assertEquals( 'a',
                                 this.reader.getShortValue( null,
                                                               this.bean ) );

    }

    public void testGetValue() {
            Assert.assertEquals( 'a',
                                 ((Character) this.reader.getValue( null,
                                                                       this.bean )).charValue() );
    }

    public void testIsNullValue() {
            Assert.assertFalse( this.reader.isNullValue( null,
                                                            this.bean ) );
    }
}
