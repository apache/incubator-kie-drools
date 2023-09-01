package org.drools.traits.compiler.factmodel.traits;

import org.drools.traits.core.factmodel.TraitProxyImpl;
import org.drools.base.factmodel.traits.TraitableBean;

import java.util.BitSet;
import java.util.Map;

public class DoSomethingProxyImpl<K,T> extends TraitProxyImpl implements ISomethingWithBehaviour<K> {

    private static final String traitType = ISomethingWithBehaviour.class.getName();

    private int age;

    private K core;

    private SomethingImpl<K> somethingImpl;
    private Object object;
    private Map<String, Object> map;

    public DoSomethingProxyImpl(Imp2 obj, Map<String, Object> m ) {
        this.object = obj;
        this.map = m;

        fields = new StudentProxyWrapper2( obj, m );

        somethingImpl = new SomethingImpl<K>( this );

        setTypeCode( new BitSet(  ) );
    }


    public String getName() {
        return somethingImpl.getName();
    }

    public void setName(String name) {
        somethingImpl.setName( name );
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String doSomething(  int j ) {
        return somethingImpl.doSomething( j );
    }

    public void doAnotherTask() {
        somethingImpl.doAnotherTask();
    }

    @Override
    public TraitableBean getObject() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String _getTraitName() {
        return traitType;
    }
}
