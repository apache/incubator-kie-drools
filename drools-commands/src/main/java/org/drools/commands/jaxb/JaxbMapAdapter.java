package org.drools.commands.jaxb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JaxbMapAdapter extends XmlAdapter<JaxbStringObjectPair[], Map<String,? extends Object>> {

    protected static Logger logger = LoggerFactory.getLogger(JaxbMapAdapter.class);

    @Override
    public JaxbStringObjectPair[] marshal(Map<String, ? extends Object> map) throws Exception {
        try {
            if (map == null || map.isEmpty()) {
                return new JaxbStringObjectPair[0];
            }

            List<JaxbStringObjectPair> ret = new ArrayList<>(map.size());
            for (Map.Entry<String, ? extends Object> entry : map.entrySet()) {
                Object obj = entry.getValue();
                // There's already a @XmlJavaTypeAdapter(JaxbUnknownAdapter.class) anno on the JaxbStringObjectPair.value field
                ret.add(new JaxbStringObjectPair(entry.getKey(), obj));
            }

            return ret.toArray(new JaxbStringObjectPair[map.size()]);
        } catch( Exception e ) {
            // because exceptions are always swallowed by JAXB
            logger.error("Unable to marshall " + map.getClass().getName() + " instance: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Map<String, Object> unmarshal(JaxbStringObjectPair[] value) throws Exception {
        try {
            Map<String, Object> r = new LinkedHashMap<>();
            for( JaxbStringObjectPair p : value ) {
                r.put(p.getKey(), p.getValue());
            }
            return r;
        } catch( Exception e ) {
            // because exceptions are always swallowed by JAXB
            logger.error("Unable to *un*marshal " + value.getClass().getName() + " instance: " + e.getMessage(), e);
            throw e;
        }
    }

}
