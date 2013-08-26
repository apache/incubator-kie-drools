package org.jbpm.process.workitem.rest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person {

    private String name;
    private Integer age;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    
    
}
