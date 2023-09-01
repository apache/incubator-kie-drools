package org.kie.efesto.common.api.identifiers;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.componentroots.ComponentFoo;
import org.kie.efesto.common.api.identifiers.componentroots.ComponentRootA;
import org.kie.efesto.common.api.identifiers.componentroots.ComponentRootB;
import org.kie.efesto.common.api.identifiers.componentroots.EfestoComponentRootBar;
import org.kie.efesto.common.api.identifiers.componentroots.LocalComponentIdA;
import org.kie.efesto.common.api.identifiers.componentroots.LocalComponentIdB;
import org.kie.efesto.common.api.identifiers.componentroots.LocalComponentIdFoo;

import static org.assertj.core.api.Assertions.assertThat;

public class AppRootTest {

    @Test
    public void testAppRoot_withComponentRoot() {
        LocalComponentIdFoo retrieved = new ReflectiveAppRoot()
                .get(ComponentFoo.class)
                .get("fileName", "name", "secondName");
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void testAppRoot_withEfestoAppRootAsComponentRoot() {
        LocalComponentIdA retrievedA = new ReflectiveAppRoot()
                .get(EfestoAppRoot.class)
                .get(EfestoComponentRootBar.class)
                .get(ComponentRootA.class)
                .get("fileName", "name");
        assertThat(retrievedA).isNotNull();
        LocalComponentIdB retrievedB = new ReflectiveAppRoot()
                .get(EfestoAppRoot.class)
                .get(EfestoComponentRootBar.class)
                .get(ComponentRootB.class)
                .get("fileName", "name", "secondName");
        assertThat(retrievedB).isNotNull();
        LocalComponentIdFoo retrievedFoo = new ReflectiveAppRoot()
                .get(EfestoAppRoot.class)
                .get(EfestoComponentRootBar.class)
                .get(ComponentFoo.class)
                .get("fileName", "name", "secondName");
        assertThat(retrievedFoo).isNotNull();
    }
}
