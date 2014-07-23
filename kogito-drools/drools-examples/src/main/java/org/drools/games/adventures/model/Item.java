package org.drools.games.adventures.model;

import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class Item extends Thing {
    @Position(2)
    private boolean fixed;

    public Item(String name) {
        this( name, false);
    }

    public Item(String name, boolean fixed) {
        super( name );
        this.fixed = fixed;
    }

    public Item(long id, String name, boolean fixed) {
        super(name );
        this.fixed = fixed;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }



    @Override
    public String toString() {
        return "Item{id=" + getId() +", name=" + getName() + "} ";
    }
}
