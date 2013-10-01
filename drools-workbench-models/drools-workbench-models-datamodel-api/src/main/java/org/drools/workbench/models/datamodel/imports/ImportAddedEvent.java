package org.drools.workbench.models.datamodel.imports;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.kie.commons.validation.PortablePreconditions;

/**
 * An event signalling adding an import
 */
public class ImportAddedEvent {

    private final Import item;
    private final PackageDataModelOracle oracle;

    public ImportAddedEvent( final PackageDataModelOracle oracle,
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
