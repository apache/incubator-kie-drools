/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.marshalling;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface ObjectMarshallingStrategy {

    /**
     * Override this method if you want multiple marshalling strategies of the same implementation in environment
     * @return the unique name in runtime environment of the ObjectMarshallingStrategy
     */
    default public String getName( ) {
        return getClass().getName();
    }
    
    boolean accept(Object object);

    void write(ObjectOutputStream os,
                      Object object) throws IOException;

    Object read(ObjectInputStream os) throws IOException, ClassNotFoundException;

    /**
     * This method is analogous to the write() method, but instead
     * of writing the object into an output stream, it returns
     * the marshalled object as a byte[].
     *
     * @param context the context for this strategy created by the method #createContext()
     * @param object the object to be marshalled
     *
     * @return the marshalled byte[] of the input object
     */
    byte[] marshal( Context context,
                    ObjectOutputStream os,
                    Object object ) throws IOException;

    /**
     * This method is analogous to the read method, but instead of reading it from an
     * input stream, it reads it from a byte[]
     *
     * @param context the context for this strategy created by the method #createContext()
     * @param object the marshalled object in a byte[]
     *
     * @return the unmarshalled Object
     */
    Object unmarshal( String dataType,
                      Context context,
                      ObjectInputStream is,
                      byte[] object,
                      ClassLoader classloader ) throws IOException, ClassNotFoundException;

    default Object unmarshal( Context context,
                              ObjectInputStream is,
                              byte[] object,
                              ClassLoader classloader ) throws IOException, ClassNotFoundException {
        return unmarshal( null, context, is, object, classloader );
    }

    /**
     * Creates a new marshalling context
     */
    Context createContext();
    
    default String getType(Class<?> clazz) {
        return clazz.getCanonicalName();
    }

    interface Context {
        /**
         * Loads the context from the given object input stream
         */
        void read(ObjectInputStream ois) throws IOException, ClassNotFoundException;

        /**
         * Writes the context to the given object output stream
         */
        void write(ObjectOutputStream oos) throws IOException;
    }

}
