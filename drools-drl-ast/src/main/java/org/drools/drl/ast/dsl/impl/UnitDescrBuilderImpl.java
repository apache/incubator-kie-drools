package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.UnitDescrBuilder;
import org.drools.drl.ast.descr.UnitDescr;

public class UnitDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, UnitDescr>
    implements
    UnitDescrBuilder {

    protected UnitDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent, new UnitDescr() );
    }

    public UnitDescrBuilder target( String target ) {
        descr.setTarget( target );
        return this;
    }

}
