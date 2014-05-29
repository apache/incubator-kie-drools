package org.jbpm.services.task.impl.model.xml.adapter;

import static org.jbpm.services.task.impl.model.xml.adapter.StringObjectMapXmlAdapter.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * This implements {@link Map} in order to fool JSON.. 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbStringObjectMap implements Map<String, Object> {

    @XmlElement(name="entry")
    public List<JaxbStringObjectMapEntry> entries = new ArrayList<JaxbStringObjectMapEntry>();
    
    public void addEntry(JaxbStringObjectMapEntry newEntry) { 
       this.entries.add(newEntry);
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if( key == null ) { 
            return false;
        }
        for( JaxbStringObjectMapEntry entry : entries ) { 
            if( key.equals(entry.getKey())) { 
                return true;
            } 
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if( value == null ) { 
            return false;
        }
        for( JaxbStringObjectMapEntry entry : entries ) { 
            Object entryVal = deserializeObject(entry.getBytes(), entry.getClassName(), entry.getKey());
            if( value.equals(entryVal) ) { 
               return true; 
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        if( key == null ) { 
            return null;
        }
        for( JaxbStringObjectMapEntry entry : entries ) { 
            if( key != null && key.equals(entry.getKey())) { 
                return deserializeObject(entry.getBytes(), entry.getClassName(), key.toString());
            } 
        }
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        Object oldVal = get(key);
        JaxbStringObjectMapEntry newEntry = createJaxbStringObjectMapEntry(value, key);
        entries.add(newEntry);
        return oldVal;
    }

    @Override
    public Object remove(Object key) {
        Iterator<JaxbStringObjectMapEntry> iter = entries.iterator();
        while( iter.hasNext() ) { 
            JaxbStringObjectMapEntry entry = iter.next();
            String entryKey = entry.getKey();
            if( key.equals(entryKey) ) { 
                iter.remove();
                return deserializeObject(entry.getBytes(), entry.getClassName(), entry.getKey());
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for( Entry<?, ?> entry : m.entrySet() ) { 
           JaxbStringObjectMapEntry newEntry = createJaxbStringObjectMapEntry(entry.getValue(), entry.getKey().toString());
           entries.add(newEntry);
        }
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @Override
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<String>();
        for( JaxbStringObjectMapEntry entry : entries ) { 
            keySet.add(entry.getKey());
        }
        return keySet;
    }

    @Override
    public Collection<Object> values() {
        List<Object> values = new ArrayList<Object>();
        for( JaxbStringObjectMapEntry entry : entries ) { 
           Object newVal = deserializeObject(entry.getBytes(), entry.getClassName(), entry.getKey());
           values.add(newVal);
        }
        return values;
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        Set<java.util.Map.Entry<String, Object>> entrySet = new HashSet<Map.Entry<String,Object>>();
        for( JaxbStringObjectMapEntry entry : entries ) { 
           Object newVal = deserializeObject(entry.getBytes(), entry.getClassName(), entry.getKey());
           String key = entry.getKey();
           Entry<String, Object> newEntry = new EntryImpl(key, newVal);
           entrySet.add(newEntry);
        } 
        return entrySet;
    }

    private class EntryImpl implements Entry<String, Object> {

        private final String key;
        private Object val;
        
        public EntryImpl( String key, Object val) { 
            this.key = key;
            this.val = val;
        }
        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return val;
        }

        @Override
        public Object setValue(Object value) {
            Object oldVal = this.val;
            this.val = value;
            return oldVal;
        } 
        
    }
}
