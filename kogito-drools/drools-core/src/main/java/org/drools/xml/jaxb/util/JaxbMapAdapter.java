package org.drools.xml.jaxb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class JaxbMapAdapter extends XmlAdapter<JaxbPair[], Map<String,Object>> {

	@Override
	public JaxbPair[] marshal(Map<String, Object> value) throws Exception {
		if (value == null || value.isEmpty()) {
			return new JaxbPair[0];
		}
		List<JaxbPair> ret = new ArrayList<JaxbPair>(value.size());
		for (Map.Entry<String, Object> entry : value.entrySet()) {
			ret.add(new JaxbPair(entry.getKey(), entry.getValue()));
		}
		
		return ret.toArray(new JaxbPair[value.size()]);
	}

	@Override
	public Map<String, Object> unmarshal(JaxbPair[] value) throws Exception {
		Map<String, Object> r = new HashMap<String, Object>();
		for( JaxbPair p : value ) {
			r.put(p.getKey(), p.getValue());
		}
		return r;
	}

}
