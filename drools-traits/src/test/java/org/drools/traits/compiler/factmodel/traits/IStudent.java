package org.drools.traits.compiler.factmodel.traits;

import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.Trait;
import org.drools.base.factmodel.traits.TraitType;
import org.drools.base.factmodel.traits.TraitableBean;

@Trait
public interface IStudent<K extends TraitableBean> extends IPerson<K>,Thing<K>, TraitType {

    public String getSchool();
    public void setSchool( String school );
}
