/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.model.datatypes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.pmml.pmml_4_2.model.ParameterInfo;

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
	
	public PMML4DataType newPMML4Data( String correlationId, ParameterInfo parameterInfo) {
		return newPMML4Data( correlationId, 
				parameterInfo.getName(), 
				null, 
				parameterInfo.getName(), 
				parameterInfo.getType(), 
				parameterInfo.getValue(), 
				defaultWeight, 
				validFlag, 
				missingFlag );
	}
	
	public PMML4DataType newPMML4Data( String correlationId, ParameterInfo parameterInfo, Double weight) {
		return newPMML4Data( correlationId, 
				parameterInfo.getName(), 
				null, 
				parameterInfo.getName(), 
				parameterInfo.getType(), 
				parameterInfo.getValue(), 
				weight, 
				validFlag, 
				missingFlag );
	}
	
	public PMML4DataType newPMML4Data( String correlationId, ParameterInfo parameterInfo, Boolean valid, Boolean missing) {
		return newPMML4Data( correlationId, 
				parameterInfo.getName(),
				null,
				parameterInfo.getName(),
				parameterInfo.getType(),
				parameterInfo.getValue(),
				defaultWeight,
				valid,
				missing );
	}
	
	public PMML4DataType newPMML4Data( String correlationId, ParameterInfo parameterInfo, Double weight, Boolean valid, Boolean missing) {
		return newPMML4Data( correlationId, 
				parameterInfo.getName(),
				null,
				parameterInfo.getName(),
				parameterInfo.getType(),
				parameterInfo.getValue(),
				weight,
				valid,
				missing );
	}
	
	public <T> PMML4DataType newPMML4Data( String correlationId, String name,
											String context,
											String displayName,
											Class<T> clazz,
											T value,
											Double weight,
											Boolean valid,
											Boolean missing) {
		PMML4Data data = null;
		String parmTypeName = clazz.getName();
		if (parmTypeName == null || parmTypeName.trim().isEmpty()) {
			String errMsg = "PMML4DataFactory::Parameter: "+name+" - type is null or blank";
			throw new IllegalArgumentException(errMsg);
		}
		if (!mapOfKnownTypes.containsKey(parmTypeName)) {
			String errMsg = "PMML4DataFactory::Parameter: "+name+" - is of unregistered type: "+parmTypeName;
			throw new RuntimeException(errMsg);
		}
		Class<? extends PMML4Data> pmmlDataClass = mapOfKnownTypes.get(parmTypeName);
		if (pmmlDataClass != null) {
			try {
				data = pmmlDataClass.getDeclaredConstructor(String.class, String.class, String.class, String.class, clazz, Double.class, Boolean.class, Boolean.class)
						.newInstance(correlationId, name,context,name,value,weight,valid,missing);
			} catch (Exception rx) {
				String errMsg = "PMML4DataFactory::Unable create data object from ParameterInfo::Parameter: "+name;
				throw new RuntimeException(errMsg,rx);
			}
		}
		return data;
	}
	
	public PMML4DataType copy( PMML4Data source) {
		PMML4Data data = null;
		Class<?> srcValueClass = source.getValueClass();
		if (srcValueClass == null) {
			String errMsg = "PMML4DataFactory::Copying "+source.getName()+" - Unable to determine the class of the source's value";
			throw new RuntimeException(errMsg);
		}
		String className = source.getValueClass().getName();
		if (!mapOfKnownTypes.containsKey(className)) {
			String errMsg = "PMML4DataFactory::Copying "+source.getName()+" - value is of unregistered type: "+className;
			throw new RuntimeException(errMsg);
		}
		Class<? extends PMML4Data> pmmlDataClass = mapOfKnownTypes.get(className);
		if (pmmlDataClass != null) {
			try {
				data = pmmlDataClass.getDeclaredConstructor(String.class, String.class, String.class, String.class, srcValueClass, Double.class, Boolean.class, Boolean.class)
						.newInstance(source.getCorrelationId(), source.getName(), source.getContext(), source.getDisplayValue(), source.getValue(), source.getWeight(), source.isValid(), source.isMissing());
			} catch (Exception rx) {
				String errMsg = "PMML4DataFactory::Copying - Unable to create copy from source "+source.getName();
				throw new RuntimeException(errMsg, rx);
			}
		}
		return data;
	}
	
	public PMML4Placeholder getPlaceholder( String correlationId, String name, String context) {
		return new PMML4Placeholder(correlationId, name, context);
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
