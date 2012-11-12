package org.jbpm.form.builder.services.model;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.form.builder.services.model.forms.FormEncodingException;


public class ScriptData implements Mappable {

	private Map<String, String> parameters = new HashMap<String, String>();

	public String get(Object key) {
		return parameters.get(key);
	}

	public String put(String key, String value) {
		return parameters.put(key, value);
	}
	
	@Override
	public void setDataMap(Map<String, Object> dataMap) throws FormEncodingException {
		parameters.clear();
		for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
			put(entry.getKey(), String.valueOf(entry.getValue()));
		}
	}
	
	@Override
	public Map<String, Object> getDataMap() {
		Map<String, Object> retval = new HashMap<String, Object>();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			retval.put(entry.getKey(), entry.getValue());
		}
		return retval;
	}

	@Override
	public int hashCode() {
		return 31 + ((parameters == null) ? 0 : parameters.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ScriptData other = (ScriptData) obj;
		if (parameters == null) {
			if (other.parameters != null) return false;
		} else if (!parameters.equals(other.parameters)) return false;
		return true;
	}
}
