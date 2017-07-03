package org.kie.dmn.feel.marshaller;

import org.kie.dmn.feel.lang.Type;

/**
 * A generic marshaller interface for FEEL values
 */
public interface FEELMarshaller<T> {

    /**
     * Marshalls the given FEEL value into an object of type T
     *
     * @param value the FEEL value to be marshalled
     *
     * @return the marshalled T value
     */
    T marshall( Object value );

    /**
     * Unmarshals the marshalled T value into a FEEL object
     *
     * @param value the marshalled value to unmarshall
     *
     * @return the unmarshalled FEEL object
     */
    Object unmarshall(Type feelType, T value );

}
