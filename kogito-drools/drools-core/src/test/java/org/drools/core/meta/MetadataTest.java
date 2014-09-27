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
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.test.model.Person;
import org.drools.core.util.StandaloneTraitFactory;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

        assertEquals( 1, ak.getMetaClassInfo() .getProperties().length );
        assertEquals( 2, sk.getMetaClassInfo() .getProperties().length );
        assertEquals( 1, k.getMetaClassInfo().getProperties().length );

        assertEquals( "subProp", sk.getMetaClassInfo().getProperties()[1].getName() );
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
        assertEquals( 3, task.getModificationMask() );

        SubKlass_.SubKlass_Modify task2 = SubKlass_.modify( ski ).prop( "hello" );
        task2.call();
        assertEquals( 1, task2.getModificationMask() );

        SubKlass_.SubKlass_Modify task3 = SubKlass_.modify( ski ).subProp( 42 );
        task3.call();
        assertEquals( 2, task3.getModificationMask() );
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
        public Integer getSubProp() {
            return (Integer) map.get( "subProp" );
        }

        @Override
        public void setSubProp( Integer value ) {
            map.put( "subProp", value );
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
