/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.meta;

import org.drools.core.common.ProjectClassLoader;
import org.drools.core.factmodel.traits.Entity;
import org.drools.core.meta.org.test.AnotherKlass;
import org.drools.core.meta.org.test.AnotherKlassImpl;
import org.drools.core.meta.org.test.AnotherKlass_;
import org.drools.core.meta.org.test.Klass;
import org.drools.core.meta.org.test.KlassImpl;
import org.drools.core.meta.org.test.Klass_;
import org.drools.core.meta.org.test.SubKlass;
import org.drools.core.meta.org.test.SubKlassImpl;
import org.drools.core.meta.org.test.SubKlass_;
import org.drools.core.metadata.Identifiable;
import org.drools.core.metadata.Lit;
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.metadata.With;
import org.drools.core.test.model.Person;
import org.drools.core.util.StandaloneTraitFactory;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MetadataTest {


    @Test
    public void testKlassAndSubKlassWithImpl() {
        SubKlass ski = new SubKlassImpl();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = new SubKlass_( ski );

        assertEquals( 42, (int) sk.subProp.get( ski ) );
        assertEquals( "hello", sk.prop.get( ski ) );

        sk.modify().prop( "bye" ).subProp( -99 ).call();

        assertEquals( -99, (int) sk.subProp.get( ski ) );
        assertEquals( "bye", sk.prop.get( ski ) );
    }

    @Test
    public void testKlassAndSubKlassWithHolderImpl() {
        SubKlassImpl ski = new SubKlassImpl();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = ski.get_();

        assertEquals( 42, (int) sk.subProp.get( ski ) );
        assertEquals( "hello", sk.prop.get( ski ) );

        sk.modify().prop( "bye" ).subProp( -99 ).call();

        assertEquals( -99, (int) sk.subProp.get( ski ) );
        assertEquals( "bye", sk.prop.get( ski ) );
    }


    @Test
    public void testKlassAndSubKlassWithInterfaces() {
        SubKlass ski = new Foo();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = new SubKlass_( ski );

        assertEquals( 42, (int) sk.subProp.get( ski ) );
        assertEquals( "hello", sk.prop.get( ski ) );

        sk.modify().subProp( -99 ).prop( "bye" ).call();

        assertEquals( -99, (int) sk.subProp.get( ski ) );
        assertEquals( "bye", sk.prop.get( ski ) );

        System.out.println( ((Foo) ski).map );
        Map tgt = new HashMap();
        tgt.put( "prop", "bye" );
        tgt.put( "subProp", -99 );
        assertEquals( tgt, ((Foo) ski).map );
    }


    @Test
    public void testMetaPropertiesWithManyKlasses() {
        SubKlass ski = new Foo();
        ski.setSubProp( 42 );
        ski.setProp( "hello" );

        SubKlass_ sk = new SubKlass_( ski );

        AnotherKlass aki = new AnotherKlassImpl();
        aki.setNum( 1 );

        AnotherKlass_ ak = new AnotherKlass_( aki );

        sk.modify().subProp( -99 ).prop( "bye" ).call();
        ak.modify().num( -5 ).call();

        assertEquals( -5, aki.getNum() );
        assertEquals( -99, (int) ski.getSubProp() );
    }

    @Test
    public void testMetadataInternals() {
        SubKlass_<SubKlass> sk = new SubKlass_( new SubKlassImpl() );
        Klass_<Klass> k = new Klass_( new KlassImpl() );
        AnotherKlass_<AnotherKlass> ak = new AnotherKlass_( new AnotherKlassImpl() );

        assertEquals( 4, ak.getMetaClassInfo() .getProperties().length );
        assertEquals( 4, sk.getMetaClassInfo() .getProperties().length );
        assertEquals( 4, k.getMetaClassInfo().getProperties().length );

        assertEquals( "subProp", sk.getMetaClassInfo().getProperties()[2].getName() );
    }

    @Test
    public void testMetadataModifyStyle() {
        SubKlassImpl ski = new SubKlassImpl();
        SubKlass_.modify( ski ).prop( "hello" ).subProp( 42 ).call();

        assertEquals( "hello", ski.getProp() );
        assertEquals( 42, (int) ski.getSubProp() );
    }


    @Test
    public void testModificationMask() {
        SubKlassImpl ski = new SubKlassImpl();
        SubKlass_.SubKlass_Modify task = SubKlass_.modify( ski ).prop( "hello" ).subProp( 42 );
        task.call();
        assertEquals( "80", task.getModificationMask().toString() );

        SubKlass_.SubKlass_Modify task2 = SubKlass_.modify( ski ).prop( "hello" );
        task2.call();
        assertEquals( "16", task2.getModificationMask().toString() );

        SubKlass_.SubKlass_Modify task3 = SubKlass_.modify( ski ).subProp( 42 );
        task3.call();
        assertEquals( "64", task3.getModificationMask().toString() );
    }


    @Test
    public void testURIs() {
        AnotherKlassImpl aki = new AnotherKlassImpl();
        assertEquals( URI.create( "http://www.test.org#AnotherKlass" ), aki.get_().getMetaClassInfo().getUri() );
        assertEquals( URI.create( "http://www.test.org#AnotherKlass?num" ), aki.get_().num.getUri() );

        URI uri = AnotherKlass_.getIdentifier( aki );
        assertEquals( URI.create( "http://www.test.org#AnotherKlass/AnotherKlassImpl/" +
                                  System.identityHashCode( aki ) ),
                      uri );

        assertEquals( URI.create( uri.toString() + "/modify?num" ),
                      AnotherKlass_.modify( aki ).num( 33 ).getUri() );


        assertTrue( uri.toString().startsWith( aki.get_().getMetaClassInfo().getUri().toString() ) );

        assertEquals( URI.create( "http://www.test.org#SubKlass/123?create" ),
                      SubKlass_.newSubKlass( URI.create( "http://www.test.org#SubKlass/123" ) ).getUri() );

        assertEquals( URI.create( "123?don=org.drools.core.meta.org.test.AnotherKlass" ),
                      aki.get_().donAnotherKlass( new Foo() ).getUri() );

    }

    @Test
    public void testNewInstance() {
        Klass klass = Klass_.newKlass( URI.create( "test" ) ).call();
        assertNotNull( klass );
        assertTrue( klass instanceof KlassImpl );

        SubKlass klass2 = SubKlass_.newSubKlass( URI.create( "test2" ) ).subProp( 42 ).prop( "hello" ).call();

        assertEquals( "hello", klass2.getProp() );
        assertEquals( 42, (int) klass2.getSubProp() );
    }

    @Test
    public void testURIsOnLegacyClasses() {
        Person p = new Person();
        URI uri = MetadataContainer.getIdentifier( p );

        assertEquals( URI.create( "urn:" + p.getClass().getPackage().getName() +  "/" + p.getClass().getSimpleName() + "/" + System.identityHashCode( p ) ), uri );
    }

    @Test
    public void testDon() {
        Entity entity = new Entity( "123" );
        entity._setDynamicProperties( new HashMap(  ) );
        entity._getDynamicProperties().put( "prop", "hello" );

        Klass klass = Klass_.donKlass( entity )
                .setTraitFactory( new StandaloneTraitFactory( ProjectClassLoader.createProjectClassLoader() ) )
                .call();

        assertEquals( "hello", klass.getProp() );
    }


    @Test
    public void testDonWithAttributes() {
        Entity entity = new Entity( "123" );
        entity._setDynamicProperties( new HashMap() );

        SubKlass klass = SubKlass_.donSubKlass( entity )
                .setTraitFactory( new StandaloneTraitFactory( ProjectClassLoader.createProjectClassLoader() ) )
                .prop( "hello" ).subProp( 32 )
                .call();

        assertEquals( "hello", klass.getProp() );
        assertEquals( 32, (int) klass.getSubProp() );
    }

    @Test
    public void testInitWithModifyArgs() {
        AnotherKlass aki = AnotherKlass_.newAnotherKlass( "000" ).call();
        SubKlass ski = SubKlass_.newSubKlass( URI.create( "123" ), With.with( aki ) ).prop( "hello" ).subProp( 42 ).another( aki ).call();
        Klass ki = Klass_.newKlass( "1421" ).call();

        assertEquals( "hello", ski.getProp() );
        assertEquals( 42, (int) ski.getSubProp() );
        assertEquals( aki, ski.getAnother() );
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

        assertEquals( Arrays.asList( aki0, aki2 ), ski.getLinks() );
    }

    @Test
    public void testOneToOneProperty() {
        AnotherKlass aki0 = AnotherKlass_.newAnotherKlass( "000" ).call();
        Klass klass = Klass_.newKlass( "001" ).call();

        Klass_.modify( klass, With.with( aki0 ) ).another( aki0 ).call();

        assertSame( klass.getAnother(), aki0 );
        assertSame( klass, aki0.getTheKlass() );

        Klass klass1 = Klass_.newKlass( "002" ).call();
        AnotherKlass_.modify( aki0 ).theKlass( klass1 ).call();

        assertSame( aki0, klass1.getAnother() );
        assertSame( klass1, aki0.getTheKlass() );

        Klass_.modify( klass ).another( null ).call();
        assertNull( klass.getAnother() );
        assertNull( aki0.getTheKlass() );

    }


    @Test
    public void testOneToManyProperty() {

        AnotherKlass aki = AnotherKlass_.newAnotherKlass( "000" ).call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass( "999" ).call();
        Klass klass1 = Klass_.newKlass( "001" ).call();
        Klass klass2 = Klass_.newKlass( "002" ).call();

        AnotherKlass_.modify( aki, With.with( klass1, klass2 ) ).manyKlasses( new ArrayList( Arrays.asList( klass1, klass2 ) ), Lit.SET ).call();

        assertSame( aki, klass1.getOneAnother() );
        assertSame( aki, klass2.getOneAnother() );

        AnotherKlass_.modify( aki2 ).manyKlasses( klass1, Lit.ADD ).call();

        assertSame( aki2, klass1.getOneAnother() );
        assertSame( aki, klass2.getOneAnother() );

        assertFalse( aki.getManyKlasses().contains( klass1 ) );
        assertTrue( aki2.getManyKlasses().contains( klass1 ) );
        assertTrue( aki.getManyKlasses().contains( klass2 ) );

        AnotherKlass_.modify( aki2 ).manyKlasses( klass1, Lit.REMOVE ).call();

        assertNull( klass1.getOneAnother() );
        assertFalse( aki2.getManyKlasses().contains( klass1 ) );

    }


    @Test
    public void testManyToOneProperty() {

        AnotherKlass aki = AnotherKlass_.newAnotherKlass( "000" ).call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass( "999" ).call();
        Klass klass1 = Klass_.newKlass( "001" ).call();
        Klass klass2 = Klass_.newKlass( "002" ).call();

        Klass_.modify( klass1 ).oneAnother( aki ).call();
        Klass_.modify( klass2 ).oneAnother( aki ).call();

        assertSame( aki, klass1.getOneAnother() );
        assertSame( aki, klass2.getOneAnother() );

        assertEquals( Arrays.asList( klass1, klass2 ), aki.getManyKlasses() );

        Klass_.modify( klass1 ).oneAnother( aki2 ).call();

        assertSame( aki2, klass1.getOneAnother() );
        assertEquals( Arrays.asList( klass1 ), aki2.getManyKlasses() );
        assertEquals( Arrays.asList( klass2 ), aki.getManyKlasses() );

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

        assertTrue( klass1.getManyAnothers().contains( aki1 ) );
        assertTrue( klass1.getManyAnothers().contains( aki2 ) );
        assertTrue( klass2.getManyAnothers().contains( aki1 ) );
        assertTrue( klass2.getManyAnothers().contains( aki2 ) );

        assertTrue( aki1.getManyMoreKlasses().contains( klass1 ) );
        assertTrue( aki1.getManyMoreKlasses().contains( klass2 ) );
        assertTrue( aki2.getManyMoreKlasses().contains( klass1 ) );
        assertTrue( aki2.getManyMoreKlasses().contains( klass2 ) );

        AnotherKlass_.modify( aki2 ).manyMoreKlasses( klass2, Lit.REMOVE ).call();

        assertTrue( klass1.getManyAnothers().contains( aki1 ) );
        assertTrue( klass1.getManyAnothers().contains( aki2 ) );
        assertTrue( klass2.getManyAnothers().contains( aki1 ) );
        assertFalse( klass2.getManyAnothers().contains( aki2 ) );

        assertTrue( aki1.getManyMoreKlasses().contains( klass1 ) );
        assertTrue( aki1.getManyMoreKlasses().contains( klass2 ) );
        assertTrue( aki2.getManyMoreKlasses().contains( klass1 ) );
        assertFalse( aki2.getManyMoreKlasses().contains( klass2 ) );

        AnotherKlass_.modify( aki2 ).manyMoreKlasses( klass2, Lit.ADD ).call();

        assertTrue( klass1.getManyAnothers().contains( aki1 ) );
        assertTrue( klass1.getManyAnothers().contains( aki2 ) );
        assertTrue( klass2.getManyAnothers().contains( aki1 ) );
        assertTrue( klass2.getManyAnothers().contains( aki2 ) );

        assertTrue( aki1.getManyMoreKlasses().contains( klass1 ) );
        assertTrue( aki1.getManyMoreKlasses().contains( klass2 ) );
        assertTrue( aki2.getManyMoreKlasses().contains( klass1 ) );
        assertTrue( aki2.getManyMoreKlasses().contains( klass2 ) );

        AnotherKlass_.modify( aki2 ).manyMoreKlasses( klass2, Lit.SET ).call();

        assertTrue( klass1.getManyAnothers().contains( aki1 ) );
        assertFalse( klass1.getManyAnothers().contains( aki2 ) );
        assertTrue( klass2.getManyAnothers().contains( aki1 ) );
        assertTrue( klass2.getManyAnothers().contains( aki2 ) );

        assertTrue( aki1.getManyMoreKlasses().contains( klass1 ) );
        assertTrue( aki1.getManyMoreKlasses().contains( klass2 ) );
        assertFalse( aki2.getManyMoreKlasses().contains( klass1 ) );
        assertTrue( aki2.getManyMoreKlasses().contains( klass2 ) );

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
