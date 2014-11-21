package org.kie.internal.jaxb;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This is a {@link XmlAdapter} for mapping Map<String, String> instances 
 * to classes/instances that <b>both</b> JAXB/XML and JSON can deal with. 
 * <p>
 * The most important reason for the existence of this class is that it works well
 * with jackson JSON! JaxbMapAdapter, on the other hand, does <b>not</b>!
 */
public class StringKeyStringValueMapXmlAdapter extends XmlAdapter<StringKeyStringValueMap, Map<String, String>> {

    @Override
    public StringKeyStringValueMap marshal(Map<String, String> map) throws Exception {
        if( map == null ) { 
            return null;
        }
        StringKeyStringValueMap xmlMap = new StringKeyStringValueMap();
        for(Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            StringKeyStringValueEntry xmlEntry = new StringKeyStringValueEntry(key, value);
            xmlMap.addEntry(xmlEntry);
        }
        return xmlMap;
    }

    
    @Override
    public Map<String, String> unmarshal(StringKeyStringValueMap xmlMap) {
        if( xmlMap == null ) { 
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        for( StringKeyStringValueEntry xmlEntry : xmlMap.entries ) { 
            String key = xmlEntry.getKey();
            String value = xmlEntry.getValue();
            map.put(key, value);
        }
        return map;
    }
}