package org.drools.core.reteoo;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DisconnectedFactHandle;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FactHandleTest {
    /*
     * Class under test for void FactHandleImpl(long)
     */
    @Test
    public void testFactHandleImpllong() {
        final DefaultFactHandle f0 = new DefaultFactHandle( 134,
                                                            "cheese" );
        assertThat(f0.getId()).isEqualTo(134);
        assertThat(f0.getRecency()).isEqualTo(134);
    }

    /*
     * Class under test for boolean equals(Object)
     */
    @Test
    public void testEqualsObject() {
        final DefaultFactHandle f0 = new DefaultFactHandle( 134,
                                                            "cheese" );
        final DefaultFactHandle f1 = new DefaultFactHandle( 96,
                                                            "cheese" );
        final DefaultFactHandle f3 = new DefaultFactHandle( 96,
                                                            "cheese" );

        assertThat(f0).as("f0 should not equal f1").isNotEqualTo(f1);
        assertThat(f3).isEqualTo(f1);
        assertThat(f3).isNotSameAs(f1);
    }

    @Test
    public void testHashCode() {
        final DefaultFactHandle f0 = new DefaultFactHandle( 234,
                                                            "cheese" );
        assertThat(f0.getObjectHashCode()).isEqualTo("cheese".hashCode());

        assertThat(f0).hasSameHashCodeAs(234);
    }

    @Test 
    public void testInvalidate() {
        final DefaultFactHandle f0 = new DefaultFactHandle( 134,
                                                            "cheese" );
        assertThat(f0.getId()).isEqualTo(134);

        f0.invalidate();
        // invalidate no longer sets the id to -1
        assertThat(f0.getId()).isEqualTo(134);
    }

    @Test
    public void testDefaultFactHandleCreateFromExternalFormat() {
        // DROOLS-7076
        String externalFormat = "0:2147483648:171497379:-1361525545:2147483648:null:NON_TRAIT:java.lang.String";
        final DefaultFactHandle f0 = DefaultFactHandle.createFromExternalFormat(externalFormat);
        assertThat(f0.getId()).isEqualTo(2147483648L);
    }

    @Test
    public void testDisconnectedFactHandleCreateFromExternalFormat() {
        // DROOLS-7076
        String externalFormat = "0:2147483648:171497379:-1361525545:2147483648:null:NON_TRAIT:java.lang.String";
        final DisconnectedFactHandle f0 = new DisconnectedFactHandle(externalFormat);
        assertThat(f0.getId()).isEqualTo(2147483648L);
    }
}
