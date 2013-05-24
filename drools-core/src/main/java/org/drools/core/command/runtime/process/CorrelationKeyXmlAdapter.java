package org.drools.core.command.runtime.process;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationProperty;

public class CorrelationKeyXmlAdapter extends XmlAdapter<String, CorrelationKey> {

    @Override
    public CorrelationKey unmarshal(String key) throws Exception {
    	List<String> keys = new ArrayList<String>();
    	for (String k: key.split(",")) {
    		keys.add(key);
    	}
        return KieInternalServices.Factory.get()
    		.newCorrelationKeyFactory().newCorrelationKey(keys);
    }

    @Override
    public String marshal(CorrelationKey key) throws Exception {
    	String result = null;
        for (CorrelationProperty<?> p: key.getProperties()) {
        	if (result == null) {
        		result = (String) p.getValue();
        	} else {
        		result += "," + p.getValue();
        	}
        }
        return result;
    }

}
