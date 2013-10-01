package org.drools.workbench.models.datamodel.imports;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.kie.commons.validation.PortablePreconditions;

/**
 * An event signalling removal of an import
 */
public class ImportRemovedEvent {

    private final Import item;
    private final PackageDataModelOracle oracle;

    public ImportRemovedEvent( final PackageDataModelOracle oracle,
                               final Import item ) {
        this.oracle = PortablePreconditions.checkNotNull( "oracle",
                                                          oracle );
        this.item = PortablePreconditions.checkNotNull( "item",
                                                        item );
    }

    public Import getImport() {
        return this.item;
    }

    public PackageDataModelOracle getDataModelOracle() {
        return this.oracle;
    }

}
