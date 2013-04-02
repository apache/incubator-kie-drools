package org.drools.core.factmodel.traits;

import java.util.BitSet;

public interface TraitType {

    public static final String traitNameField = "__$$trait_Name";

    public String getTraitName();

    public BitSet getTypeCode();

    public boolean isVirtual();

}