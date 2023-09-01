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

public class FloatClassFieldExtractorTest extends BaseClassFieldExtractorsTest {
    private static final float VALUE     = 6;

    ReadAccessor reader;;
    TestBean                   bean      = new TestBean();

    @Before
    public void setUp() throws Exception {
        ClassFieldAccessorStore store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
        this.reader = store.getReader( TestBean.class,
                                          "floatAttr" );
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
                this.bean)).isEqualTo((byte) FloatClassFieldExtractorTest.VALUE);
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
                this.bean)).isCloseTo(FloatClassFieldExtractorTest.VALUE, within(0.01));
    }

    @Test
    public void testGetFloatValue() {
        assertThat(this.reader.getFloatValue(null,
                this.bean)).isCloseTo(FloatClassFieldExtractorTest.VALUE, within(0.01f));
    }

    @Test
    public void testGetIntValue() {
        assertThat(this.reader.getIntValue(null,
                this.bean)).isEqualTo((int) FloatClassFieldExtractorTest.VALUE);
    }

    @Test
    public void testGetLongValue() {
        assertThat(this.reader.getLongValue(null,
                this.bean)).isEqualTo((long) FloatClassFieldExtractorTest.VALUE);
    }

    @Test
    public void testGetShortValue() {
        assertThat(this.reader.getShortValue(null,
                this.bean)).isEqualTo((short) FloatClassFieldExtractorTest.VALUE);
    }

    @Test
    public void testGetValue() {
        assertThat(this.reader.getValue(null,
                this.bean)).isEqualTo(new Float(FloatClassFieldExtractorTest.VALUE));
        assertThat(this.reader.getValue(null,
                this.bean) instanceof Float).isTrue();
    }

    @Test
    public void testIsNullValue() {
        assertThat(this.reader.isNullValue(null,
                this.bean)).isFalse();
    }
}
