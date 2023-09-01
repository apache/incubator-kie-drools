package org.drools.core.reteoo;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectTypeConfTest {

    @Test
    public void testGetPackageName() {
        assertThat(ClassObjectTypeConf.getPackageName(this.getClass(), null)).isEqualTo("org.drools.core.reteoo");
        Package thispkg = this.getClass().getPackage();
        assertThat(thispkg).isNotNull();
        assertThat(ClassObjectTypeConf.getPackageName(this.getClass(), thispkg)).isEqualTo("org.drools.core.reteoo");
    }

}
