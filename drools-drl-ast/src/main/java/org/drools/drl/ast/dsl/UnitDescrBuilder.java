package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.UnitDescr;

public interface UnitDescrBuilder
        extends
        DescrBuilder<PackageDescrBuilder, UnitDescr> {

    /**
     * Sets the unit target
     *
     * @param target the unit class
     * @return itself
     */
    UnitDescrBuilder target( String target );
}
