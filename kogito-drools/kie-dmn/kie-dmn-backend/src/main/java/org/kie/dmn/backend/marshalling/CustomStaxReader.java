package org.kie.dmn.backend.marshalling;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamReader;

import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxReader;


public class CustomStaxReader extends StaxReader {

    /** 
     * ATTENTION use read-only methods do not mutate
     */
    private XMLStreamReader in;

    public CustomStaxReader(QNameMap qnameMap, XMLStreamReader in) {
        super(qnameMap, in);
        this.in = in;
    }
    
    public Map<String, String> getNSContext() {
        Map<String, String> nsContext = new HashMap<>();
        for (int nsIndex = 0; nsIndex < in.getNamespaceCount(); nsIndex++) {
            String nsPrefix = in.getNamespacePrefix(nsIndex);
            String nsId = in.getNamespaceURI(nsIndex);
            nsContext.put(in.getNamespacePrefix(nsIndex)!=null?in.getNamespacePrefix(nsIndex):XMLConstants.DEFAULT_NS_PREFIX, in.getNamespaceURI(nsIndex) );
          }
        return nsContext;
    }
}
