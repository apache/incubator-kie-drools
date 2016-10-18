package org.kie.dmn.backend.marshalling;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxWriter;


public class CustomStaxWriter extends StaxWriter {
    /** 
     * ATTENTION use read-only methods do not mutate
     */
    private XMLStreamWriter out;

    public CustomStaxWriter(QNameMap qnameMap, XMLStreamWriter out, boolean writeStartEndDocument, boolean repairingNamespace, NameCoder nameCoder) throws XMLStreamException {
        super(qnameMap, out, writeStartEndDocument, repairingNamespace, nameCoder);
        this.out = out;
    }

    public void writeNamespace(String prefix, String uri) throws XMLStreamException {
        out.writeNamespace(prefix, uri);
    }
    
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        out.setDefaultNamespace(uri);
    }
}
