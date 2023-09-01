package org.drools.core.impl;

import java.util.Collections;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;
import org.junit.Test;
import org.kie.internal.conf.CompositeBaseConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeBaseImplTest {

    @Test
    public void testStaticImports() {

        KnowledgeBaseImpl base = new KnowledgeBaseImpl("default", (CompositeBaseConfiguration)  RuleBaseFactory.newKnowledgeBaseConfiguration());

        // assume empty knowledge base
        assertThat(base.getPackages()).isEmpty();

        // add package with function static import into knowledge base
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.drools.test" );
        pkg.addStaticImport( "org.drools.function.myFunction" );
        base.addPackage( pkg );

        // verify package has been added
        assertThat(base.getPackages()).hasSize(1);

        // retrieve copied and merged package from the base
        InternalKnowledgePackage copy = base.getPackage( "org.drools.test" );
        assertThat(copy.getStaticImports()).isEqualTo(Collections.singleton("org.drools.function.myFunction"));
    }
}
