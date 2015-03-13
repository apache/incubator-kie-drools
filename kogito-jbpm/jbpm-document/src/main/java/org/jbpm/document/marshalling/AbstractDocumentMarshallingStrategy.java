package org.jbpm.document.marshalling;

import java.util.Date;
import java.util.Map;

import org.jbpm.document.Document;
import org.kie.api.marshalling.ObjectMarshallingStrategy;

/**
 * Marshalling strategy definition to Marshal Document.
 */
public abstract class AbstractDocumentMarshallingStrategy implements ObjectMarshallingStrategy {

    /**
     * Creates a valid Document instance with the data received.
     * This method is called when a document is uploaded to create the Document instance <b>before</b>
     * marshalling the document content.
     *
     * @param name              The document name.
     * @param size              The size of the document content.
     * @param lastModified      The last modified date of the document.
     * @param params            A Map that contain params for the document creation.
     * @return                  A Document instance containing all the document info (including identifier and download
     *                          link) except the Document content.
     */
    public abstract Document buildDocument( String name, long size, Date lastModified, Map<String, String> params );

    @Override
    public boolean accept(Object o) {
        return o instanceof Document;
    }
}
