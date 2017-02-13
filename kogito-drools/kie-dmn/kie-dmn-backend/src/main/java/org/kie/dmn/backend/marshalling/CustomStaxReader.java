package org.kie.dmn.backend.marshalling;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;

import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxReader;


public class CustomStaxReader extends StaxReader {
    /** 
     * ATTENTION this is intercepted during XStream StaxDriver creation as there is no proper API to inherit.
     * Do not mutate reference - mutating this reference would not sort any effect on the actual underlying StaxReader
     */
    private XMLStreamReader in;

    public CustomStaxReader(QNameMap qnameMap, XMLStreamReader in) {
        super(qnameMap, in);
        this.in = in;
    }
    
    public Map<String, String> getNsContext() {
        Map<String, String> nsContext = new HashMap<>();
        for (int nsIndex = 0; nsIndex < in.getNamespaceCount(); nsIndex++) {
            String nsPrefix = in.getNamespacePrefix(nsIndex);
            String nsId = in.getNamespaceURI(nsIndex);
            nsContext.put(nsPrefix!=null?nsPrefix:XMLConstants.DEFAULT_NS_PREFIX, nsId );
        }
        return nsContext;
    }
    
    public Location getLocation() {
        return in.getLocation();
    }
}
