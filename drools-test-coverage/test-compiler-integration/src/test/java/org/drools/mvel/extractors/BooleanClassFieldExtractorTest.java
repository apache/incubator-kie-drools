package org.drools.mvel.extractors;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.TestBean;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class BooleanClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    ReadAccessor reader;
    TestBean  bean      = new TestBean();

    @Before
    public void setUp() throws Exception {
        ClassFieldAccessorStore store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.reader = store.getReader( TestBean.class,
                                              "booleanAttr" );
    }

    @Test
    public void testGetBooleanValue() {
        assertThat(this.reader.getBooleanValue(null,
                this.bean)).isTrue();
    }

    @Test
    public void testGetByteValue() {
        try {
            this.reader.getByteValue( null,
                                         this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
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
        try {
            this.reader.getDoubleValue( null,
                                           this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetFloatValue() {
        try {
            this.reader.getFloatValue( null,
                                          this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetIntValue() {
        try {
            this.reader.getIntValue( null,
                                        this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetLongValue() {
        try {
            this.reader.getLongValue( null,
                                         this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetShortValue() {
        try {
            this.reader.getShortValue( null,
                                          this.bean );
            fail( "Should have throw an exception" );
        } catch ( final Exception e ) {
            // success
        }
    }

    @Test
    public void testGetValue() {
        assertThat(this.reader.getValue(null,
                this.bean)).isSameAs(Boolean.TRUE);
    }

    @Test
    public void testIsNullValue() {
        assertThat(this.reader.isNullValue(null,
                this.bean)).isFalse();
    }
}
