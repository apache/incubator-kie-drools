package org.drools.games.adventures.model;

public class Item extends Thing {

    public Item(String name) {
        this( name, true);
    }

    public Item(String name, boolean portable) {
        super( name, portable );
    }

    public Item(long id, String name, boolean portable) {
        super(name, portable );
    }

    @Override
    public String toString() {
        return "Item{id=" + getId() +", name=" + getName() + "} ";
    }
}
