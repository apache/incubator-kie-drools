/*
 * Copyright 2013 JBoss Inc
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

package org.drools.compiler.factmodel.traits;

import org.drools.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.factmodel.traits.Thing;
import org.drools.factmodel.traits.Traitable;
import org.drools.factmodel.traits.TraitableBean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Traitable
public class StudentImpl implements IStudent<StudentImpl>, TraitableBean<StudentImpl,StudentImpl> {

    private String school;
    private String name;
    private int age;


    private Map<String,Thing<StudentImpl>> traitMap = new HashMap<String, Thing<StudentImpl>>();

    public StudentImpl() {
    }

    public StudentImpl(String school, String name, int age) {
        this.school = school;
        this.name = name;
        this.age = age;

    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Map<String, Object> getFields() {
        return null;
    }

    public void setFields(Map<String, Object> fields) {

    }

    public StudentImpl getCore() {
        return this;
    }

    @Override
    public String toString() {
        return "StudentImpl{" +
                "school='" + school + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public Map<String, Object> getDynamicProperties() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDynamicProperties(Map<String, Object> map) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map<String, Thing<StudentImpl>> getTraitMap() {
        return traitMap;
    }

    public void setTraitMap(Map<String, Thing<StudentImpl>> map) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addTrait(String type, Thing<StudentImpl> proxy) throws LogicalTypeInconsistencyException {
        traitMap.put( type, proxy );
    }

    public Thing<StudentImpl> getTrait(String type) {
        return traitMap.get( type );
    }

    public boolean hasTrait(String type) {
        return traitMap.containsKey( type );
    }

    public Thing removeTrait(String type) {
        return traitMap.remove( type );
    }

    public Collection<String> getTraits() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void denyTrait(Class trait) throws LogicalTypeInconsistencyException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void allowTrait(Class trait) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
