package org.drools.traits.compiler.factmodel.traits;

import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.TraitFieldTMS;
import org.drools.traits.core.factmodel.TraitTypeMapImpl;
import org.drools.base.factmodel.traits.Traitable;
import org.drools.base.factmodel.traits.TraitableBean;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;


@Traitable
public class StudentImpl implements IStudent<StudentImpl>, TraitableBean<StudentImpl,StudentImpl> {

    private String school;
    private String name;
    private int age;


    private Map<String,Thing<StudentImpl>> traitMap = new TraitTypeMapImpl<String, Thing<StudentImpl>, StudentImpl>(new HashMap() );;

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

    public boolean isTop() {
        return false;
    }

    @Override
    public String toString() {
        return "StudentImpl{" +
                "school='" + school + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public Map<String, Object> _getDynamicProperties() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void _setDynamicProperties(Map<String, Object> map) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map<String, Thing<StudentImpl>> _getTraitMap() {
        return traitMap;
    }

    public void _setTraitMap(Map<String, Thing<StudentImpl>> map) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public TraitFieldTMS _getFieldTMS() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void _setFieldTMS( TraitFieldTMS tms ) {}

    public BitSet getBottomTypeCode() {
        return new BitSet();
    }

    public BitSet getCurrentTypeCode() {
        return null;
    }

    public BitSet _getTypeCode() {
        return new BitSet();
    }

    public boolean _isVirtual() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String _getTraitName() {
        return IStudent.class.getName();
    }

    @Override
    public boolean _hasTypeCode( BitSet typeCode ) {
        return false;
    }
}
