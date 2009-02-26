package org.drools.marshalling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface ObjectMarshallingStrategy {
    
    public boolean accept(Object object);

    public void write(ObjectOutputStream os,
                      Object object) throws IOException;

    public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException;
}
