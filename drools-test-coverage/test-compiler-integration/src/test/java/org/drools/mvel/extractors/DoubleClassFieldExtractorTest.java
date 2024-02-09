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

public class DoubleClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    private static final double VALUE = 7;

    ReadAccessor reader;
    TestBean                    bean  = new TestBean();

    @Before
    public void setUp() throws Exception {
        ClassFieldAccessorStore store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.reader = store.getReader( TestBean.class,
                                          "doubleAttr" );
    }

    @Test
    public void testGetBooleanValue() {
        try {
            this.reader.getBooleanValue( null,
                                            this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetByteValue() {
        assertThat(this.reader.getByteValue(null,
                this.bean)).isEqualTo((byte) DoubleClassFieldExtractorTest.VALUE);
    }

    @Test
    public void testGetCharValue() {
        try {
            this.reader.getCharValue( null,
                                         this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetDoubleValue() {
        assertThat(this.reader.getDoubleValue(null,
                this.bean)).isCloseTo(DoubleClassFieldExtractorTest.VALUE, within(0.01));
    }

    @Test
    public void testGetFloatValue() {
        assertThat(this.reader.getFloatValue(null,
                this.bean)).isCloseTo((float)DoubleClassFieldExtractorTest.VALUE, within(0.01f));
    }

    @Test
    public void testGetIntValue() {
        assertThat(this.reader.getIntValue(null,
                this.bean)).isEqualTo((int) DoubleClassFieldExtractorTest.VALUE);
    }

    @Test
    public void testGetLongValue() {
        assertThat(this.reader.getLongValue(null,
                this.bean)).isEqualTo((long) DoubleClassFieldExtractorTest.VALUE);
    }

    @Test
    public void testGetShortValue() {
        assertThat(this.reader.getShortValue(null,
                this.bean)).isEqualTo((short) DoubleClassFieldExtractorTest.VALUE);
    }

    @Test
    public void testGetValue() {
        assertThat(this.reader.getValue(null,
                this.bean)).isEqualTo(new Double(DoubleClassFieldExtractorTest.VALUE));
        assertThat(this.reader.getValue(null,
                this.bean) instanceof Double).isTrue();
    }

    @Test
    public void testIsNullValue() {
        assertThat(this.reader.isNullValue(null,
                this.bean)).isFalse();
    }
}
