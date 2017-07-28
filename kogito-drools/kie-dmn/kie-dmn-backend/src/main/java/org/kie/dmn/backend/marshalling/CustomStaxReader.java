package org.kie.dmn.backend.marshalling;

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxReader;


public class CustomStaxReader extends StaxReader {
    /** 
     * ATTENTION this is intercepted during XStream StaxDriver creation as there is no proper API to inherit.
     * Do not mutate reference - mutating this reference would not sort any effect on the actual underlying StaxReader
     */
    private XMLStreamReader in;

    public CustomStaxReader(QNameMap qnameMap, XMLStreamReader in) {
        // Please note that the super() internally calls moveDown(). If one day this need to be extended in this class,
        // remind to defer inside the overrided moveDown() in this class with a simple return if this.in is null
        // and make an explicit moveDown() call as part of THIS constructor.
        super(qnameMap, in);
        this.in = in;
        moveDown(); //needed because this class overrides pullNextEvent, moveDown.
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
    
    @Override
    public String getAttribute(String name) {
        // REFEDINES default XStream behavior, by expliciting the namespaceURI to use, instead of generic `null` 
        // which is problematic in case of:
        //  - multiple attribute with the same name and different namespace, not supported by XStream
        //  - if using IBM JDK, because the XML infra is not respecting the JDK API javadoc contract.
        // ref: DROOLS-1622

        // Also note.
        // To avoid semantic ambiguities as per example in W3C https://www.w3.org/TR/REC-xml-names/#uniqAttrs
        // <!-- http://www.w3.org is bound to n1 and is the default -->
        // <x xmlns:n1="http://www.w3.org" 
        //    xmlns="http://www.w3.org" >
        //   <good a="1"     b="2" />
        //   <good a="1"     n1:a="2" />     <<-- this has semantic ambiguity in our case because if `a` was `id` which value to bind?
        // </x>
        // hence we do not support for attributes an "explicit" prefix for the namespace.
        // In other words, a similar example:
        // <dmn:inputData dmn:id="_3d560678-a126-4654-a686-bc6d941fe40b" dmn:name="MyInput">
        // is not supported, and is expected as standard XML:
        // <dmn:inputData id="_3d560678-a126-4654-a686-bc6d941fe40b" name="MyInput">
                
        return getAttribute( XMLConstants.DEFAULT_NS_PREFIX, this.encodeAttribute(name) );
    }
    
    public String getAttribute(String namespaceURI, String name) {
        return this.in.getAttributeValue( namespaceURI, this.encodeAttribute(name) );
    }
    
    public Map<QName, String> getAdditionalAttributes() {
        Map<QName, String> result = new HashMap<>();
        for (int aIndex = 0; aIndex < in.getAttributeCount(); aIndex++) {
            String attributePrefix = in.getAttributePrefix(aIndex);
            if ( !XMLConstants.DEFAULT_NS_PREFIX.equals(attributePrefix) ) {
                result.put( new QName(in.getAttributeNamespace(aIndex), in.getAttributeLocalName(aIndex), attributePrefix), in.getAttributeValue(aIndex) );
            }
        }
        return result;
    }
    
    @Override
    public void moveDown() {
        if ( in == null ) {
            return; // hack for this extension: defer the moveDown until this constructor is fully completed.
        }
        super.moveDown();
    }
    @Override
    protected int pullNextEvent() {
        try {
            switch(in.next()) {
                case XMLStreamConstants.START_DOCUMENT:
                case XMLStreamConstants.START_ELEMENT:
                    return START_NODE;
                case XMLStreamConstants.END_DOCUMENT:
                case XMLStreamConstants.END_ELEMENT:
                    return END_NODE;
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.CDATA:          // <<-- the StAX api when on IBM JDK reports event as CDATA explicitly.
                    return TEXT;
                case XMLStreamConstants.COMMENT:
                    return COMMENT;
                default:
                    return OTHER;
            }
        } catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }
}
