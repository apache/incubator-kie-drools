package org.drools.examples.traits;


import org.drools.factmodel.traits.Trait;

@Trait( impl = ScholarImpl.class )
public interface Scholar<K>  {

    public void learn(String subject);


}
