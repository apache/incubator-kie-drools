package org.drools.traits.compiler.factmodel.traits;
import org.drools.base.factmodel.traits.Trait;

@Trait( impl = SomethingImpl.class )
public interface ISomethingWithBehaviour<K> extends IDoSomething<K> {

    public String getName();
    public void setName( String name );

    public int getAge();
    public void setAge( int age );



}
