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
package org.drools.traits.core.meta.org;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.traits.compiler.Person;
import org.drools.traits.core.factmodel.Entity;
import org.drools.traits.core.meta.org.test.AnotherKlass;
import org.drools.traits.core.meta.org.test.AnotherKlassImpl;
import org.drools.traits.core.meta.org.test.AnotherKlass_;
import org.drools.traits.core.meta.org.test.Klass;
import org.drools.traits.core.meta.org.test.KlassImpl;
import org.drools.traits.core.meta.org.test.Klass_;
import org.drools.traits.core.meta.org.test.SubKlass;
import org.drools.traits.core.meta.org.test.SubKlassImpl;
import org.drools.traits.core.meta.org.test.SubKlass_;
import org.drools.traits.core.metadata.Identifiable;
import org.drools.traits.core.metadata.Lit;
import org.drools.traits.core.metadata.MetadataContainer;
import org.drools.traits.core.metadata.With;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.traits.compiler.factmodel.traits.TraitTestUtils.createStandaloneTraitFactory;
import static org.assertj.core.api.Assertions.assertThat;

public class MetadataTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataTest.class);

    @Test
    public void testKlassAndSubKlassWithImpl() {
        SubKlass ski = new SubKlassImpl();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = new SubKlass_(ski );

        assertThat((int) sk.subProp.get(ski)).isEqualTo(42);
        assertThat(sk.prop.get(ski)).isEqualTo("hello");

        sk.modify().prop( "bye" ).subProp( -99 ).call();

        assertThat((int) sk.subProp.get(ski)).isEqualTo(-99);
        assertThat(sk.prop.get(ski)).isEqualTo("bye");
    }

    @Test
    public void testKlassAndSubKlassWithHolderImpl() {
        SubKlassImpl ski = new SubKlassImpl();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = ski.get_();

        assertThat((int) sk.subProp.get(ski)).isEqualTo(42);
        assertThat(sk.prop.get(ski)).isEqualTo("hello");

        sk.modify().prop( "bye" ).subProp( -99 ).call();

        assertThat((int) sk.subProp.get(ski)).isEqualTo(-99);
        assertThat(sk.prop.get(ski)).isEqualTo("bye");
    }


    @Test
    public void testKlassAndSubKlassWithInterfaces() {
        Foo ski = new Foo();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = new SubKlass_( ski );

        assertThat((int) sk.subProp.get(ski)).isEqualTo(42);
        assertThat(sk.prop.get(ski)).isEqualTo("hello");

        sk.modify().subProp( -99 ).prop( "bye" ).call();

        assertThat((int) sk.subProp.get(ski)).isEqualTo(-99);
        assertThat(sk.prop.get(ski)).isEqualTo("bye");

        LOGGER.debug( ski.map.toString());
        Map tgt = new HashMap();
        tgt.put( "prop", "bye" );
        tgt.put( "subProp", -99 );
        assertThat(ski.map).isEqualTo(tgt);
    }


    @Test
    public void testMetaPropertiesWithManyKlasses() {
        SubKlass ski = new Foo();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = new SubKlass_( ski );

        AnotherKlass aki = new AnotherKlassImpl();
        aki.setNum( 1 );

        AnotherKlass_ ak = new AnotherKlass_(aki );

        sk.modify().subProp( -99 ).prop( "bye" ).call();
        ak.modify().num( -5 ).call();

        assertThat(aki.getNum()).isEqualTo(-5);
        assertThat((int) ski.getSubProp()).isEqualTo(-99);
    }

    @Test
    public void testMetadataInternals() {
        SubKlass_<SubKlass> sk = new SubKlass_( new SubKlassImpl() );
        Klass_<Klass> k = new Klass_( new KlassImpl() );
        AnotherKlass_<AnotherKlass> ak = new AnotherKlass_( new AnotherKlassImpl() );

        assertThat(ak.getMetaClassInfo() .getProperties().length).isEqualTo(4);
        assertThat(sk.getMetaClassInfo() .getProperties().length).isEqualTo(4);
        assertThat(k.getMetaClassInfo().getProperties().length).isEqualTo(4);

        assertThat(sk.getMetaClassInfo().getProperties()[2].getName()).isEqualTo("subProp");
    }

    @Test
    public void testMetadataModifyStyle() {
        SubKlassImpl ski = new SubKlassImpl();
        SubKlass_.modify( ski ).prop( "hello" ).subProp( 42 ).call();

        assertThat(ski.getProp()).isEqualTo("hello");
        assertThat((int) ski.getSubProp()).isEqualTo(42);
    }


    @Test
    public void testModificationMask() {
        SubKlassImpl ski = new SubKlassImpl();
        SubKlass_.SubKlass_Modify task = SubKlass_.modify( ski ).prop( "hello" ).subProp( 42 );
        task.call();
        assertThat(task.getModificationMask().toString()).isEqualTo("288");

        SubKlass_.SubKlass_Modify task2 = SubKlass_.modify( ski ).prop( "hello" );
        task2.call();
        assertThat(task2.getModificationMask().toString()).isEqualTo("32");

        SubKlass_.SubKlass_Modify task3 = SubKlass_.modify( ski ).subProp( 42 );
        task3.call();
        assertThat(task3.getModificationMask().toString()).isEqualTo("256");
    }


    @Test
    public void testURIs() {
        AnotherKlassImpl aki = new AnotherKlassImpl();
        assertThat(aki.get_().getMetaClassInfo().getUri()).isEqualTo(URI.create("http://www.test.org#AnotherKlass"));
        assertThat(aki.get_().num.getUri()).isEqualTo(URI.create("http://www.test.org#AnotherKlass?num"));

        URI uri = AnotherKlass_.getIdentifier( aki );
        assertThat(uri).isEqualTo(URI.create("http://www.test.org#AnotherKlass/AnotherKlassImpl/" +
                System.identityHashCode(aki)));

        assertThat(AnotherKlass_.modify(aki).num(33).getUri()).isEqualTo(URI.create(uri.toString() + "/modify?num"));


        assertThat(uri.toString().startsWith(aki.get_().getMetaClassInfo().getUri().toString())).isTrue();

        assertThat(SubKlass_.newSubKlass(URI.create("http://www.test.org#SubKlass/123")).getUri()).isEqualTo(URI.create("http://www.test.org#SubKlass/123?create"));

        assertThat(aki.get_().donAnotherKlass(new Foo()).getUri()).isEqualTo(URI.create("123?don=org.drools.traits.core.meta.org.test.AnotherKlass"));

    }

    @Test
    public void testNewInstance() {
        Klass klass = Klass_.newKlass( URI.create( "test" ) ).call();
        assertThat(klass).isNotNull();
        assertThat(klass instanceof KlassImpl).isTrue();

        SubKlass klass2 = SubKlass_.newSubKlass( URI.create( "test2" ) ).subProp( 42 ).prop( "hello" ).call();

        assertThat(klass2.getProp()).isEqualTo("hello");
        assertThat((int) klass2.getSubProp()).isEqualTo(42);
    }


    @Test
    public void testURIsOnLegacyClasses() {
        Person p = new Person();
        URI uri = MetadataContainer.getIdentifier( p );

        assertThat(uri).isEqualTo(URI.create("urn:" + p.getClass().getPackage().getName() +  "/" + p.getClass().getSimpleName() + "/" + System.identityHashCode(p)));
    }

    @Test
    public void testDon() {
        Entity entity = new Entity( "123" );
        entity._setDynamicProperties( new HashMap(  ) );
        entity._getDynamicProperties().put( "prop", "hello" );

        Klass klass = Klass_.donKlass( entity )
                .setTraitFactory(createStandaloneTraitFactory())
                .call();

        assertThat(klass.getProp()).isEqualTo("hello");
    }


    @Test
    public void testDonWithAttributes() {
        Entity entity = new Entity( "123" );
        entity._setDynamicProperties( new HashMap() );

        SubKlass klass = SubKlass_.donSubKlass(entity )
                .setTraitFactory(createStandaloneTraitFactory())
                .prop( "hello" ).subProp( 32 )
                .call();

        assertThat(klass.getProp()).isEqualTo("hello");
        assertThat((int) klass.getSubProp()).isEqualTo(32);
    }

    @Test
    public void testInitWithModifyArgs() {
        AnotherKlass aki = AnotherKlass_.newAnotherKlass( "000" ).call();
        SubKlass ski = SubKlass_.newSubKlass( URI.create( "123" ), With.with( aki ) ).prop( "hello" ).subProp( 42 ).another( aki ).call();
        Klass ki = Klass_.newKlass( "1421" ).call();

        assertThat(ski.getProp()).isEqualTo("hello");
        assertThat((int) ski.getSubProp()).isEqualTo(42);
        assertThat(ski.getAnother()).isEqualTo(aki);
    }

    @Test
    public void testCollectionOrientedProperties() {
        AnotherKlass aki0 = AnotherKlass_.newAnotherKlass( "000" ).call();
        AnotherKlass aki1 = AnotherKlass_.newAnotherKlass( "001" ).call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass( "002" ).call();
        AnotherKlass aki3 = AnotherKlass_.newAnotherKlass( "003" ).call();
        AnotherKlass aki4 = AnotherKlass_.newAnotherKlass( "004" ).call();

        ArrayList<AnotherKlass> initial = new ArrayList( Arrays.asList( aki0, aki1 ) );
        SubKlass ski = SubKlass_.newSubKlass( URI.create( "123" ) )
                .links( initial, Lit.SET )
                .links( aki1, Lit.REMOVE )
                .links( aki2, Lit.ADD )
                .links( Arrays.asList( aki3, aki4 ), Lit.REMOVE )
                .call();

        assertThat(ski.getLinks()).isEqualTo(Arrays.asList(aki0, aki2));
    }

    @Test
    public void testOneToOneProperty() {
        AnotherKlass aki0 = AnotherKlass_.newAnotherKlass( "000" ).call();
        Klass klass = Klass_.newKlass( "001" ).call();

        Klass_.modify( klass, With.with( aki0 ) ).another( aki0 ).call();

        assertThat(aki0).isSameAs(klass.getAnother());
        assertThat(aki0.getTheKlass()).isSameAs(klass);

        Klass klass1 = Klass_.newKlass( "002" ).call();
        AnotherKlass_.modify( aki0 ).theKlass( klass1 ).call();

        assertThat(klass1.getAnother()).isSameAs(aki0);
        assertThat(aki0.getTheKlass()).isSameAs(klass1);

        Klass_.modify( klass ).another( null ).call();
        assertThat(klass.getAnother()).isNull();
        assertThat(aki0.getTheKlass()).isNull();

    }


    @Test
    public void testOneToManyProperty() {

        AnotherKlass aki = AnotherKlass_.newAnotherKlass( "000" ).call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass( "999" ).call();
        Klass klass1 = Klass_.newKlass( "001" ).call();
        Klass klass2 = Klass_.newKlass( "002" ).call();

        AnotherKlass_.modify( aki, With.with( klass1, klass2 ) ).manyKlasses( new ArrayList( Arrays.asList( klass1, klass2 ) ), Lit.SET ).call();

        assertThat(klass1.getOneAnother()).isSameAs(aki);
        assertThat(klass2.getOneAnother()).isSameAs(aki);

        AnotherKlass_.modify( aki2 ).manyKlasses( klass1, Lit.ADD ).call();

        assertThat(klass1.getOneAnother()).isSameAs(aki2);
        assertThat(klass2.getOneAnother()).isSameAs(aki);

        assertThat(aki.getManyKlasses().contains(klass1)).isFalse();
        assertThat(aki2.getManyKlasses().contains(klass1)).isTrue();
        assertThat(aki.getManyKlasses().contains(klass2)).isTrue();

        AnotherKlass_.modify( aki2 ).manyKlasses( klass1, Lit.REMOVE ).call();

        assertThat(klass1.getOneAnother()).isNull();
        assertThat(aki2.getManyKlasses().contains(klass1)).isFalse();

    }


    @Test
    public void testManyToOneProperty() {

        AnotherKlass aki = AnotherKlass_.newAnotherKlass( "000" ).call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass( "999" ).call();
        Klass klass1 = Klass_.newKlass( "001" ).call();
        Klass klass2 = Klass_.newKlass( "002" ).call();

        Klass_.modify( klass1 ).oneAnother( aki ).call();
        Klass_.modify( klass2 ).oneAnother( aki ).call();

        assertThat(klass1.getOneAnother()).isSameAs(aki);
        assertThat(klass2.getOneAnother()).isSameAs(aki);

        assertThat(aki.getManyKlasses()).isEqualTo(Arrays.asList(klass1, klass2));

        Klass_.modify( klass1 ).oneAnother( aki2 ).call();

        assertThat(klass1.getOneAnother()).isSameAs(aki2);
        assertThat(aki2.getManyKlasses()).isEqualTo(List.of(klass1));
        assertThat(aki.getManyKlasses()).isEqualTo(List.of(klass2));

    }

    @Test
    public void testManyToManyProperty() {

        AnotherKlass aki1 = AnotherKlass_.newAnotherKlass( "000" ).call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass( "999" ).call();
        Klass klass1 = Klass_.newKlass( "001" ).call();
        Klass klass2 = Klass_.newKlass( "002" ).call();


        Klass_.modify( klass1 ).manyOthers( aki1, Lit.ADD ).call();
        Klass_.modify( klass1 ).manyOthers( aki2, Lit.ADD ).call();

        AnotherKlass_.modify( aki2 ).manyMoreKlasses( klass2, Lit.ADD ).call();
        AnotherKlass_.modify( aki1 ).manyMoreKlasses( klass2, Lit.ADD ).call();

        assertThat(klass1.getManyAnothers().contains(aki1)).isTrue();
        assertThat(klass1.getManyAnothers().contains(aki2)).isTrue();
        assertThat(klass2.getManyAnothers().contains(aki1)).isTrue();
        assertThat(klass2.getManyAnothers().contains(aki2)).isTrue();

        assertThat(aki1.getManyMoreKlasses().contains(klass1)).isTrue();
        assertThat(aki1.getManyMoreKlasses().contains(klass2)).isTrue();
        assertThat(aki2.getManyMoreKlasses().contains(klass1)).isTrue();
        assertThat(aki2.getManyMoreKlasses().contains(klass2)).isTrue();

        AnotherKlass_.modify( aki2 ).manyMoreKlasses( klass2, Lit.REMOVE ).call();

        assertThat(klass1.getManyAnothers().contains(aki1)).isTrue();
        assertThat(klass1.getManyAnothers().contains(aki2)).isTrue();
        assertThat(klass2.getManyAnothers().contains(aki1)).isTrue();
        assertThat(klass2.getManyAnothers().contains(aki2)).isFalse();

        assertThat(aki1.getManyMoreKlasses().contains(klass1)).isTrue();
        assertThat(aki1.getManyMoreKlasses().contains(klass2)).isTrue();
        assertThat(aki2.getManyMoreKlasses().contains(klass1)).isTrue();
        assertThat(aki2.getManyMoreKlasses().contains(klass2)).isFalse();

        AnotherKlass_.modify( aki2 ).manyMoreKlasses( klass2, Lit.ADD ).call();

        assertThat(klass1.getManyAnothers().contains(aki1)).isTrue();
        assertThat(klass1.getManyAnothers().contains(aki2)).isTrue();
        assertThat(klass2.getManyAnothers().contains(aki1)).isTrue();
        assertThat(klass2.getManyAnothers().contains(aki2)).isTrue();

        assertThat(aki1.getManyMoreKlasses().contains(klass1)).isTrue();
        assertThat(aki1.getManyMoreKlasses().contains(klass2)).isTrue();
        assertThat(aki2.getManyMoreKlasses().contains(klass1)).isTrue();
        assertThat(aki2.getManyMoreKlasses().contains(klass2)).isTrue();

        AnotherKlass_.modify( aki2 ).manyMoreKlasses( klass2, Lit.SET ).call();

        assertThat(klass1.getManyAnothers().contains(aki1)).isTrue();
        assertThat(klass1.getManyAnothers().contains(aki2)).isFalse();
        assertThat(klass2.getManyAnothers().contains(aki1)).isTrue();
        assertThat(klass2.getManyAnothers().contains(aki2)).isTrue();

        assertThat(aki1.getManyMoreKlasses().contains(klass1)).isTrue();
        assertThat(aki1.getManyMoreKlasses().contains(klass2)).isTrue();
        assertThat(aki2.getManyMoreKlasses().contains(klass1)).isFalse();
        assertThat(aki2.getManyMoreKlasses().contains(klass2)).isTrue();

    }




    public static class Foo implements SubKlass, Identifiable {

        private URI uri;

        public Foo() {
            this( "123" );
        }

        public Foo( String uri ) {
            this.uri = URI.create( uri );
        }

        public Map<String,Object> map = new HashMap<String,Object>();

        @Override
        public String getProp() {
            return (String) map.get( "prop" );
        }

        @Override
        public void setProp( String value ) {
            map.put( "prop", value );
        }

        @Override
        public AnotherKlass getAnother() {
            return (AnotherKlass) map.get( "another" );
        }

        @Override
        public void setAnother( AnotherKlass another ) {
            map.put( "another", another );
        }

        @Override
        public AnotherKlass getOneAnother() {
            return null;
        }

        @Override
        public void setOneAnother( AnotherKlass another ) {

        }

        @Override
        public List<AnotherKlass> getManyAnothers() {
            return null;
        }

        @Override
        public void setManyAnothers( List<AnotherKlass> anothers ) {

        }

        @Override
        public Integer getSubProp() {
            return (Integer) map.get( "subProp" );
        }

        @Override
        public void setSubProp( Integer value ) {
            map.put( "subProp", value );
        }

        @Override
        public List<AnotherKlass> getLinks() {
            return null;
        }

        @Override
        public void setLinks( List<AnotherKlass> links ) {

        }

        @Override
        public URI getUri() {
            return uri;
        }

        @Override
        public Object getId() {
            return uri;
        }
    }


}
