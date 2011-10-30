package ruleml.translator.drl2ruleml;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage the bound vars in a drools source for the translation.
 * 
 * @author jabarski
 */

public class VariableBindingsManager {
	private Map<String, PropertyInfo> boundVarsOnFieldName = new HashMap<String, PropertyInfo>();
	private Map<String, PropertyInfo> boundVarsOnDeclaration = new HashMap<String, PropertyInfo>();

	public static class PropertyInfo {
		private String name;
		private String value;
		private String var;
		private ValueType type = ValueType.VAR;
		private boolean active = true;;
		private String clazz;

		@Override
		public boolean equals(Object obj) {
			try {
				PropertyInfo propertyInfo = (PropertyInfo) obj;

				if (var != null && propertyInfo.getVar() != null
						&& var.equals(propertyInfo.getVar())) {
					return true;
				}

				if (name != null && clazz != null
						&& propertyInfo.getName() != null
						&& propertyInfo.getClazz() != null
						&& name.equals(propertyInfo.getName())
						&& clazz.equals(propertyInfo.getClazz())) {
					return true;
				}
			} finally {
			}
			return false;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			if (value != null) {
				setType(ValueType.IND);
			} else {
				setType(ValueType.VAR);
			}
			this.value = value;
		}

		public String getClazz() {
			return clazz;
		}

		public void setClazz(String clazz) {
			this.clazz = clazz;
		}

		public void setVar(String var) {
			this.var = var;
		}

		public String getVar() {
			return var;
		}

		private void setType(ValueType type) {
			this.type = type;
		}

		public ValueType getType() {
			return type;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public boolean isActive() {
			return active;
		}

		public enum ValueType {
			VAR, IND, DATA
		}
	}

	public PropertyInfo get(String fieldName, String clazz) {
		return boundVarsOnFieldName.get(clazz + "_" + fieldName);
	}

	public PropertyInfo get(String declaration) {
		return boundVarsOnDeclaration.get(declaration);
	}

	public boolean containsKey(String key) {
		if (boundVarsOnDeclaration.containsKey(key)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean containsKey(String fieldName, String clazz) {
		if (boundVarsOnFieldName.containsKey(clazz + "_" + fieldName)) {
			return true;
		} else {
			return false;
		}
	}

	public void put(PropertyInfo propertyInfo) {

		if (propertyInfo.getName() != null) {
			boundVarsOnFieldName.put(propertyInfo.getClazz() + "_"
					+ propertyInfo.getName(), propertyInfo);
		}

		if (propertyInfo.getVar() != null) {
			boundVarsOnDeclaration.put(propertyInfo.getVar(), propertyInfo);
		}
	}
}
