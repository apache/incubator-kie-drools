package org.drools.traits.compiler.factmodel.traits;

import org.drools.base.factmodel.traits.Trait;

@Trait
public interface IRole {

    public String getRoleName();
    public void setRoleName( String name );
}
