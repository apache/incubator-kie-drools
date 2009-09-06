package org.drools.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;

/**
 * <p>
 * Generic interface to provide a Reader or InputStream for the underlying IO resource.
 * </p>
 */
public interface Resource extends Serializable {
    /**
     * Open an InputStream to the resource, the user must close this when finished.
     * 
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Opens a Reader to the resource, the user most close this when finished.
     * @return
     * @throws IOException
     */
    public Reader getReader() throws IOException;

}
