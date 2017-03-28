package org.kie.dmn.backend.marshalling;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxWriter;


public class CustomStaxWriter extends StaxWriter {
    /** 
     * ATTENTION this is intercepted during XStream StaxDriver creation as there is no proper API to inherit.
     * Do not mutate reference - mutating this reference would not sort any effect on the actual underlying StaxWriter
     */
    private XMLStreamWriter out;
    private int tagDepth = 0;
    private static enum Op {
        START_NODE, END_NODE, VALUE;
    }
    private Op lastOp = null;
    
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

    @Override
    public void endNode() {
        if ( this.lastOp == Op.END_NODE ) {
            try {
                out.writeCharacters( System.lineSeparator() );
                for ( int i = 0; i < (tagDepth-1); i++ ) { out.writeCharacters("  "); }
            } catch (XMLStreamException e) {
                throw new StreamException(e);
            }
        }
        super.endNode();
        --this.tagDepth;
        
        this.lastOp = Op.END_NODE;
        
        if ( this.tagDepth == 0 ) {
            // closed last element before EOF
            try {
                out.writeCharacters( System.lineSeparator() );
            } catch (XMLStreamException e) {
                throw new StreamException(e);
            }
        }
    }

    @Override
    public void startNode(String arg0) {
        try {
            out.writeCharacters( System.lineSeparator() );
            for ( int i = 0; i < tagDepth; i++ ) { out.writeCharacters("  "); }
        } catch (XMLStreamException e) {
            throw new StreamException(e);
        }
        super.startNode(arg0);
        ++this.tagDepth;
        
        this.lastOp = Op.START_NODE;
    }

    @Override
    public void setValue(String text) {
        super.setValue(text);
        
        this.lastOp = Op.VALUE;
    }
}
