package org.drools.base.factmodel.traits;


import java.util.BitSet;

public interface TraitType {

    BitSet _getTypeCode();

    boolean _isVirtual();

    String traitNameField = "__$$trait_Name";

    String _getTraitName();

    boolean _hasTypeCode( BitSet typeCode );
}
