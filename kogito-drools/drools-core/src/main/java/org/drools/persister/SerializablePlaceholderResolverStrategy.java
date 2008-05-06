package org.drools.persister;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializablePlaceholderResolverStrategy
    implements
    PlaceholderResolverStrategy {
    private int id;

//    public SerializablePlaceholderResolverStrategy(int id) {
//        this.id = id;
//    }

    public void setId(int id) {
        this.id = id;       
    }
    
    public int getId() {
        return id;
    }

    public ObjectPlaceholder read(ObjectInputStream os) throws IOException,
                                                       ClassNotFoundException {
        return new SerializablePlaceholder( os.readObject() );
    }

    public void write(ObjectOutputStream os,
                      Object object) throws IOException {
        os.writeObject( object );
    }

}
