package org.optaplanner.core.impl.io.jaxb;

import java.io.Reader;
import java.io.Writer;

public interface JaxbIO<T> {

    T read(Reader reader);

    void write(T root, Writer writer);
}
