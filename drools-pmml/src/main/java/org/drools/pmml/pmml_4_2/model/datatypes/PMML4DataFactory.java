package org.drools.pmml.pmml_4_2.model.datatypes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.pmml.pmml_4_2.model.ParameterInfo;

public class PMML4DataFactory {
	private static PMML4DataFactory INSTANCE = new PMML4DataFactory();
	private Boolean validFlag = Boolean.FALSE;
	private Boolean missingFlag = Boolean.FALSE;
	private Double defaultWeight = 1.0;
	private static Map<String, Class<? extends PMML4Data>> mapOfKnownTypes;
	
	public static PMML4DataFactory get() {
		return INSTANCE;
	}
	
	public static void registerDataType(String valueType, Class<? extends PMML4Data> clazz) {
		mapOfKnownTypes.put(valueType, clazz);
	}
	
	private PMML4DataFactory() {
		if (mapOfKnownTypes == null) {
			mapOfKnownTypes = new HashMap<>();
		}
		// pre-set types
		registerDataType(Boolean.class.getName(), PMML4Boolean.class);
		registerDataType(String.class.getName(), PMML4String.class);
		registerDataType(Date.class.getName(), PMML4Date.class);
		registerDataType(Integer.class.getName(), PMML4Integer.class);
		registerDataType(Double.class.getName(), PMML4Double.class);
	}
	
	public PMML4DataType newPMML4Data(ParameterInfo parameterInfo) {
		PMML4Data data = null;
		Class<?> parmType = parameterInfo.getType();
		String parmTypeName = parmType.getName();
		String parmName = parameterInfo.getName();
		if (parmTypeName == null || parmTypeName.trim().isEmpty()) {
			String errMsg = "PMML4DataFactory::Parameter: "+parmName+" - type is null or blank";
			throw new IllegalArgumentException(errMsg);
		}
		if (!mapOfKnownTypes.containsKey(parmTypeName)) {
			String errMsg = "PMML4DataFactory::Parameter: "+parmName+" - is of unregistered type: "+parmTypeName;
			throw new RuntimeException(errMsg);
		}
		Class<? extends PMML4Data> clazz = mapOfKnownTypes.get(parmTypeName);
		if (clazz != null) {
			try {
				data = clazz.getDeclaredConstructor(String.class, String.class, String.class, parmType, Double.class, Boolean.class, Boolean.class)
						.newInstance(parmName,null,parmName,parameterInfo.getValue(),defaultWeight,validFlag,missingFlag);
			} catch (Exception rx) {
				String errMsg = "PMML4DataFactory::Unable create data object from ParameterInfo::Parameter: "+parmName;
				throw new RuntimeException(errMsg,rx);
			}
		}
		return data;
	}
	
	public Boolean getValidFlag() {
		return this.validFlag;
	}
	
	public void setValidFlag(Boolean validFlag) {
		this.validFlag = Boolean.valueOf(validFlag != null && validFlag.booleanValue());
	}
	
	public Boolean getMissingFlag() {
		return this.missingFlag;
	}
	
	public void setMissingFlag(Boolean missingFlag) {
		this.missingFlag = Boolean.valueOf(missingFlag != null && missingFlag.booleanValue());
	}
	
	public Double getDefaultWeight() {
		return this.defaultWeight;
	}
	
	public void setDefaultWeight(Double weight) {
		this.defaultWeight = (weight != null) ? weight : 1.0;
	}
}
