package org.drools.marshalling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.IdentityHashMap;
import java.util.Map;

public class IdentityPlaceholderResolverStrategy
    implements
    PlaceholderResolverStrategy {
    
    private int index;
    
    private Map<Integer, Object> map;

    private PlaceholderResolverStrategyAcceptor acceptor;
    
    public IdentityPlaceholderResolverStrategy(PlaceholderResolverStrategyAcceptor acceptor) {
        this.acceptor = acceptor;
        this.map = new IdentityHashMap<Integer, Object>();
    }
    
    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }    

    public ObjectPlaceholder read(ObjectInputStream os) throws IOException,
                                                       ClassNotFoundException {
        int id = os.readInt();
        return new SerializablePlaceholder( map.get( id ));
    }

    public void write(ObjectOutputStream os,
                      Object object) throws IOException {
        Integer id = map.size();
        map.put( id, object );
        os.writeInt( id );
    }

    public boolean accept(Object object) {
        return this.acceptor.accept( object );
    }
}
