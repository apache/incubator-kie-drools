package org.drools.workbench.models.commons.shared.imports;

/**
 * Models marked with this interface support Imports.
 */
public interface HasImports {

    Imports getImports();

    void setImports( final Imports imports );

}
