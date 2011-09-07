/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.factmodel.traits;

import java.util.Map;

public class DoSomethingProxy<K,T> extends TraitProxy implements ISomethingWithBehaviour<K> {

    private int age;

    private K core;

    private SomethingImpl<K> somethingImpl;
    private Object object;
    private Map<String, Object> map;

    public DoSomethingProxy( Imp2 obj, Map<String, Object> m ) {
        this.object = obj;
        this.map = m;

        fields = new StudentProxyWrapper2( obj, m );

        somethingImpl = new SomethingImpl<K>( this );
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
    public Object getObject() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
