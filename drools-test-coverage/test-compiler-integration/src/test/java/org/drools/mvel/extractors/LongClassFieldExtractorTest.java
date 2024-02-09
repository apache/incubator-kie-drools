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
package org.drools.mvel.extractors;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.TestBean;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.within;

public class LongClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    private static final long VALUE     = 5;

    ReadAccessor extractor;
    TestBean                  bean      = new TestBean();

    @Before
    public void setUp() throws Exception {
        ClassFieldAccessorStore store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        extractor = store.getReader( TestBean.class,
                                "longAttr" );
    }

    @Test
    public void testGetBooleanValue() {
        try {
            this.extractor.getBooleanValue( null,
                                            this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetByteValue() {
        try {
            assertThat(this.extractor.getByteValue(null,
                    this.bean)).isEqualTo((byte)LongClassFieldExtractorTest.VALUE);
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    @Test
    public void testGetCharValue() {
        try {
            this.extractor.getCharValue( null,
                                         this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetDoubleValue() {
        try {
            assertThat(this.extractor.getDoubleValue(null,
                    this.bean)).isCloseTo(LongClassFieldExtractorTest.VALUE, within(0.01));
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    @Test
    public void testGetFloatValue() {
        try {
            assertThat(this.extractor.getFloatValue(null,
                    this.bean)).isCloseTo(LongClassFieldExtractorTest.VALUE, within(0.01f));
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    @Test
    public void testGetIntValue() {
        try {
            assertThat(this.extractor.getIntValue(null,
                    this.bean)).isEqualTo(LongClassFieldExtractorTest.VALUE);
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    @Test
    public void testGetLongValue() {
        try {
            assertThat(this.extractor.getLongValue(null,
                    this.bean)).isEqualTo(LongClassFieldExtractorTest.VALUE);
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    @Test
    public void testGetShortValue() {
        try {
            assertThat(this.extractor.getShortValue(null,
                    this.bean)).isEqualTo((short)LongClassFieldExtractorTest.VALUE);
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    @Test
    public void testGetValue() {
        try {
            assertThat(this.extractor.getValue(null,
                    this.bean)).isEqualTo(new Long( (short) LongClassFieldExtractorTest.VALUE ));
            assertThat(this.extractor.getValue(null,
                    this.bean) instanceof Long).isTrue();
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }

    @Test
    public void testIsNullValue() {
        try {
            assertThat(this.extractor.isNullValue(null,
                    this.bean)).isFalse();
        } catch ( final Exception e ) {
            fail( "Should not throw an exception" );
        }
    }
}
