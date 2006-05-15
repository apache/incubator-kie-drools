package org.drools.common;

import java.util.Iterator;

import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListObjectWrapper;

public class ActivationGroupImpl implements ActivationGroup {
    private String name;
    
    private final LinkedList list; 

    public ActivationGroupImpl(String name) {
        this.name = name;
        this.list = new LinkedList();
    }

    public String getName() {
        return this.name;
    }
    
    public void addActivation(Activation activation) {
        ActivationGroupNode node = new ActivationGroupNode(activation, this);
        activation.setActivationGroupNode( node );     
        this.list.add( node );
    }
    
    public void removeActivation(Activation activation) {
        ActivationGroupNode node = activation.getActivationGroupNode( );     
        this.list.remove( node );        
        activation.setActivationGroupNode( null );
    }
    
    public Iterator iterator() {
        return this.list.iterator();
    }
    
    public boolean isEmpty() {
        return this.list.isEmpty();
    }
    
    public int size() {
        return this.list.size();
    }
    
    public void clear() {
        this.list.clear();
    }

}
