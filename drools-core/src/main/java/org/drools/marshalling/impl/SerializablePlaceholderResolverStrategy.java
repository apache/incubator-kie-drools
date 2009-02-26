package org.drools.marshalling.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;

public class SerializablePlaceholderResolverStrategy
    implements
    ObjectMarshallingStrategy {

    private int index;
    
    private ObjectMarshallingStrategyAcceptor acceptor;
    
    public SerializablePlaceholderResolverStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        this.acceptor = acceptor;
    }
    
    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }    

    public Object read(ObjectInputStream os) throws IOException,
                                                       ClassNotFoundException {
        return  os.readObject();
    }

    public void write(ObjectOutputStream os,
                      Object object) throws IOException {
        os.writeObject( object );
    }

    public boolean accept(Object object) {
        return acceptor.accept( object );
    }

}
