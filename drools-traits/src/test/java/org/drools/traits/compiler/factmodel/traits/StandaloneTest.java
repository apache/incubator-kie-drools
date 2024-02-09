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
package org.drools.traits.compiler.factmodel.traits;

import org.drools.base.factmodel.traits.CoreWrapper;
import org.drools.base.factmodel.traits.Trait;
import org.drools.traits.core.factmodel.Entity;
import org.drools.traits.core.factmodel.LogicalTypeInconsistencyException;
import org.drools.base.factmodel.traits.Thing;
import org.drools.traits.core.util.StandaloneTraitFactory;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.traits.compiler.factmodel.traits.TraitTestUtils.createStandaloneTraitFactory;

public class StandaloneTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneTest.class);

    private StandaloneTraitFactory factory;

    @Before
    public void init() {
        ProjectClassLoader loader = ProjectClassLoader.createProjectClassLoader();
        factory = createStandaloneTraitFactory();
    }

    @Test
    public void testThing() throws LogicalTypeInconsistencyException {
        // Entity is @Traitable and implements TraitableBean natively
        // Thing is a Trait
        // --> just call getProxy
        Entity core = new Entity( "x" );
        Thing thing = factory.don( core, Thing.class );
        assertThat(thing).isNotNull();
    }


    @Test
    public void testHierarchy() throws LogicalTypeInconsistencyException {
        Imp imp = new Imp();
        imp.setName( "john doe" );

        // Imp is not a TraitableBean, so we need to wrap it first
        // IStudent is a Thing, so it will work directly
        CoreWrapper<Imp> core = factory.makeTraitable(imp, Imp.class );
        IStudent student = (IStudent) factory.don( core, IStudent.class );

        LOGGER.debug( student.getName() );
        LOGGER.debug( student.getSchool() );
        assertThat(student.getName()).isEqualTo("john doe");
        assertThat(student.getSchool()).isNull();

        IPerson p = (IPerson) factory.don( core, IPerson.class );

        student.setName( "alan ford" );

        LOGGER.debug( p.getName() );
        assertThat(p.getName()).isEqualTo("alan ford");
    }


    @Trait
    public static interface IFoo {
        public String getName();
        public void setName( String n );
    }

    @Test
    public void testLegacy() throws LogicalTypeInconsistencyException {
        Imp imp = new Imp();
        imp.setName( "john doe" );

        // Imp is not a TraitableBean, so we need to wrap it first
        // IFoo is not a Thing, but it will be extended internally
        CoreWrapper<Imp> core = factory.makeTraitable( imp, Imp.class );
        IFoo foo = (IFoo) factory.don( core, IFoo.class );

        LOGGER.debug( foo.getName() );
        LOGGER.debug( "Is foo instance of Thing? : " + (foo instanceof Thing) );

        assertThat(foo.getName()).isEqualTo("john doe");
        assertThat(foo instanceof Thing).isTrue();
    }




}
