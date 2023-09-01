package org.drools.traits.compiler.factmodel.traits;

import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.Trait;
import org.drools.base.factmodel.traits.TraitableBean;

@Trait
public interface IPerson<K extends TraitableBean> extends Thing<K> {

    public String getName();
    public void setName( String name );

    public int getAge();
    public void setAge( int age );

}
