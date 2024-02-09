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
package org.drools.mvel.asm;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.core.base.TestBean;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.core.util.asm.BeanInherit;
import org.drools.core.util.asm.TestAbstract;
import org.drools.core.util.asm.TestAbstractImpl;
import org.drools.core.util.asm.TestInterface;
import org.drools.core.util.asm.TestInterfaceImpl;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseClassFieldAccessorFactoryTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    @Test
    public void testIt() throws Exception {
        ClassFieldAccessorFactory factory = new ClassFieldAccessorFactory();
        
        ClassFieldAccessorCache.CacheEntry cachEntry = new ClassFieldAccessorCache.CacheEntry( Thread.currentThread().getContextClassLoader()  );

        ReadAccessor ex = factory.getClassFieldReader( TestBean.class,
                                                               "name",
                                                               cachEntry );
        assertThat(ex.getValue(null,
                new TestBean())).isEqualTo("michael");
        ex = factory.getClassFieldReader( TestBean.class,
                                          "age",
                                          cachEntry );
        assertThat(((Number) ex.getValue(null,
                new TestBean())).intValue()).isEqualTo(42);

    }

    @Test
    public void testInterface() throws Exception {
        final ReadAccessor ex = store.getReader( TestInterface.class,
                                                         "something" );
        assertThat(ex.getIndex()).isEqualTo(2);
        assertThat(ex.getValue(null,
                new TestInterfaceImpl())).isEqualTo("foo");
    }

    @Test
    public void testAbstract() throws Exception {
        final ReadAccessor ex = store.getReader( TestAbstract.class,
                                                         "something" );
        assertThat(ex.getIndex()).isEqualTo(3);
        assertThat(ex.getValue(null,
                new TestAbstractImpl())).isEqualTo("foo");
    }

    @Test
    public void testInherited() throws Exception {
        final ReadAccessor ex = store.getReader( BeanInherit.class,
                                                         "text" );
        assertThat(ex.getValue(null,
                new BeanInherit())).isEqualTo("hola");
    }

    @Test
    public void testSelfReference() throws Exception {
        final ReadAccessor ex = store.getReader( BeanInherit.class,
                                                         "this" );
        final TestBean bean = new TestBean();
        assertThat(ex.getValue(null,
                bean)).isEqualTo(bean);
    }

}
