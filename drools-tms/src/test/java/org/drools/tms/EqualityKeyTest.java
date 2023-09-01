package org.drools.tms;

import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.test.model.Cheese;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EqualityKeyTest {
    @Test
    public void test1() {
        ReteooFactHandleFactory factory = new ReteooFactHandleFactory();
        
        InternalFactHandle ch1 = factory.newFactHandle( new Cheese ("c", 10), null, null, null );
        EqualityKey key = new TruthMaintenanceSystemEqualityKey( ch1 );

        assertThat(key.getFactHandle()).isSameAs(ch1);
        assertThat(key.size()).isEqualTo(1);
        
        InternalFactHandle ch2 = factory.newFactHandle( new Cheese ("c", 10), null, null, null );
        key.addFactHandle( ch2 );

        assertThat(key.size()).isEqualTo(2);
        assertThat(key.get(1)).isEqualTo(ch2);
        
        key.removeFactHandle( ch1 );
        assertThat(key.getFactHandle()).isSameAs(ch2);
        assertThat(key.size()).isEqualTo(1);
        
        key.removeFactHandle( ch2 );
        assertThat(key.getFactHandle()).isNull();
        assertThat(key.size()).isEqualTo(0);
        
        key = new TruthMaintenanceSystemEqualityKey( ch2 );
        key.addFactHandle( ch1 );
        assertThat(key.getFactHandle()).isSameAs(ch2);
        assertThat(key.size()).isEqualTo(2);
        assertThat(key.get(1)).isEqualTo(ch1);
        
        key.removeFactHandle( ch1 );
        assertThat(key.getFactHandle()).isSameAs(ch2);
        assertThat(key.size()).isEqualTo(1);
        
        key.removeFactHandle( ch2 );
        assertThat(key.getFactHandle()).isNull();
        assertThat(key.size()).isEqualTo(0);
    }
}
