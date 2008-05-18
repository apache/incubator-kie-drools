package org.drools.marshalling;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public interface PlaceholderResolverStrategy {
    
    public void setIndex(int id);
    
    public int getIndex();
    
    public boolean accept(Object object);

    public void write(ObjectOutputStream os,
                      Object object) throws IOException;

    public ObjectPlaceholder read(ObjectInputStream os) throws IOException, ClassNotFoundException;
}
