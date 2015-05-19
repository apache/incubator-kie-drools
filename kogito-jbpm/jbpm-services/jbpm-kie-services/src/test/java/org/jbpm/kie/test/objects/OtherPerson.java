package org.jbpm.kie.test.objects;

public class OtherPerson extends Person {

    public OtherPerson() { 
       // default constructor 
    }
    
    public OtherPerson(Person person) { 
        this.id = person.id;
        this.log = person.log;
        this.name = person.name;
        this.time = person.time;
    }
}
