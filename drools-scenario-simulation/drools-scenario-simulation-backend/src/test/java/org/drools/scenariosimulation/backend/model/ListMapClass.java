package org.drools.scenariosimulation.backend.model;

import java.util.List;
import java.util.Map;

public class ListMapClass {

    private String name;
    private List<String> names;
    private List<ListMapClass> siblings;
    private Map<String, Integer> phones;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<ListMapClass> getSiblings() {
        return siblings;
    }

    public void setSiblings(List<ListMapClass> siblings) {
        this.siblings = siblings;
    }

    public Map<String, Integer> getPhones() {
        return phones;
    }

    public void setPhones(Map<String, Integer> phones) {
        this.phones = phones;
    }
}
