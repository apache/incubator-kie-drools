package org.drools.marshalling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializablePlaceholderResolverStrategy
    implements
    PlaceholderResolverStrategy {

    private int index;
    
    private PlaceholderResolverStrategyAcceptor acceptor;
    
    public SerializablePlaceholderResolverStrategy(PlaceholderResolverStrategyAcceptor acceptor) {
        this.acceptor = acceptor;
    }
    
    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }    

    public ObjectPlaceholder read(ObjectInputStream os) throws IOException,
                                                       ClassNotFoundException {
        return new SerializablePlaceholder( os.readObject() );
    }

    public void write(ObjectOutputStream os,
                      Object object) throws IOException {
        os.writeObject( object );
    }

    public boolean accept(Object object) {
        return this.accept( object );
    }

}
