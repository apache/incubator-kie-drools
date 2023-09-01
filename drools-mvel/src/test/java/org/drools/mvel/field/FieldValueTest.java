package org.drools.mvel.field;

import org.drools.base.rule.accessor.FieldValue;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldValueTest {
    FieldValue field1;
    FieldValue field2;
    FieldValue field3;
    FieldValue field4;
    FieldValue field5;

    @Before
    public void setUp() throws Exception {
        this.field1 = FieldFactory.getInstance().getFieldValue( null );
        this.field2 = FieldFactory.getInstance().getFieldValue( null );
        this.field3 = FieldFactory.getInstance().getFieldValue( "A" );
        this.field4 = FieldFactory.getInstance().getFieldValue( "A" );
        this.field5 = FieldFactory.getInstance().getFieldValue( "B" );
    }

    /*
     * Test method for 'org.kie.base.FieldValue.hashCode()'
     */
    @Test
    public void testHashCode() {
        assertThat(this.field1.hashCode()).isEqualTo(this.field1.hashCode());
        assertThat(this.field2.hashCode()).isEqualTo(this.field1.hashCode());
        assertThat(this.field3.hashCode()).isEqualTo(this.field3.hashCode());
        assertThat(this.field4.hashCode()).isEqualTo(this.field3.hashCode());
        assertThat(this.field1.hashCode() == this.field3.hashCode()).isFalse();
        assertThat(this.field3.hashCode() == this.field1.hashCode()).isFalse();
        assertThat(this.field3.hashCode() == this.field5.hashCode()).isFalse();
    }

    /*
     * Test method for 'org.kie.base.FieldValue.equals(Object)'
     */
    @Test
    public void testEqualsObject() {
        assertThat(this.field1).isEqualTo(this.field1);
        assertThat(this.field2).isEqualTo(this.field1);
        assertThat(this.field3).isEqualTo(this.field3);
        assertThat(this.field4).isEqualTo(this.field3);
        assertThat(this.field1.equals(this.field3)).isFalse();
        assertThat(this.field3.equals(this.field1)).isFalse();
        assertThat(this.field3.equals(this.field5)).isFalse();
    }

}
