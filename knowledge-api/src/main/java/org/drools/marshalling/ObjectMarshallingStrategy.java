/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.marshalling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface ObjectMarshallingStrategy {
    
    public boolean accept(Object object);

    public void write(ObjectOutputStream os,
                      Object object) throws IOException;
    
    public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException;
    
    /**
     * This method is analogous to the write() method, but instead
     * of writing the object into an output stream, it returns
     * the marshalled object as a byte[].
     * 
     * @param object the object to be marshalled
     * 
     * @return the marshalled byte[] of the input object
     */
    public byte[] marshal( Object object ) throws IOException;
    
    /**
     * This method is analogous to the read method, but instead of reading it from an 
     * input stream, it reads it from a byte[]
     * 
     * @param object the marshalled object in a byte[]
     * 
     * @return the unmarshalled Object
     */
    public Object unmarshal( byte[] object, ClassLoader classloader ) throws IOException, ClassNotFoundException;

}
