package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;

/**
 * Instantiates a {@link CompilationPhase} that operates on
 * a single pair of {@link PackageRegistry} and {@link CompositePackageDescr}
 */
public interface SinglePackagePhaseFactory {
    CompilationPhase create(PackageRegistry pkgRegistry, CompositePackageDescr packageDescr);
}
