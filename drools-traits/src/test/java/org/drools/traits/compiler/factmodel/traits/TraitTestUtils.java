package org.drools.traits.compiler.factmodel.traits;

import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.drools.traits.core.util.StandaloneTraitFactory;

public class TraitTestUtils {

    public static StandaloneTraitFactory createStandaloneTraitFactory() {
        return new StandaloneTraitFactory(ProjectClassLoader.createProjectClassLoader());
    }
}
