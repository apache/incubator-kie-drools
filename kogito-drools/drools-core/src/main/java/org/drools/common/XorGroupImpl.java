package org.drools.common;

import java.util.Iterator;

import org.drools.spi.Activation;
import org.drools.spi.XorGroup;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListObjectWrapper;

public class XorGroupImpl implements XorGroup {
    private String name;
    
    private final LinkedList list; 

    public XorGroupImpl(String name) {
        this.name = name;
        this.list = new LinkedList();
    }

    public String getName() {
        return this.name;
    }
    
    public void addActivation(Activation activation) {
        XorGroupNode node = new XorGroupNode(activation, this);
        activation.setXorGroupNode( node );     
        this.list.add( node );
    }
    
    public void removeActivation(Activation activation) {
        XorGroupNode node = activation.getXorGroupNode( );     
        this.list.remove( node );        
        activation.setXorGroupNode( null );
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
