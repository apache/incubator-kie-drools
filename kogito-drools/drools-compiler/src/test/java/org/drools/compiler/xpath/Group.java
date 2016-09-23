package org.drools.compiler.xpath;

import java.util.List;

import org.drools.core.phreak.AbstractReactiveObject;
import org.drools.core.phreak.ReactiveList;

public class Group extends AbstractReactiveObject {
    private final String name;
    private final List<Person> members = new ReactiveList<Person>();

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Person> getMembers() {
        return members;
    }

    public void addPerson(Person p) {
        members.add(p);
    }
    
    public void removePerson(Person p) {
        members.remove(p);
    }
}
