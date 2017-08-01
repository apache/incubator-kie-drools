/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 */
package bpsim;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Enum Parameter Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.EnumParameterType#getGroup <em>Group</em>}</li>
 *   <li>{@link bpsim.EnumParameterType#getParameterValueGroup <em>Parameter Value Group</em>}</li>
 *   <li>{@link bpsim.EnumParameterType#getParameterValue <em>Parameter Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpsim.BpsimPackage#getEnumParameterType()
 * @model extendedMetaData="name='EnumParameter_._type' kind='elementOnly'"
 * @generated
 */
public interface EnumParameterType extends ParameterValue {
	/**
	 * Returns the value of the '<em><b>Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Even if here we reference a list of Parameter Values, only Constant Parameters are valid here. There is just no real way of expressing it in xsd.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Group</em>' attribute list.
	 * @see bpsim.BpsimPackage#getEnumParameterType_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:3'"
	 * @generated
	 */
	FeatureMap getGroup();

	/**
	 * Returns the value of the '<em><b>Parameter Value Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Value Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Value Group</em>' attribute list.
	 * @see bpsim.BpsimPackage#getEnumParameterType_ParameterValueGroup()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" required="true" many="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='group' name='ParameterValue:group' namespace='##targetNamespace' group='#group:3'"
	 * @generated
	 */
	FeatureMap getParameterValueGroup();

	/**
	 * Returns the value of the '<em><b>Parameter Value</b></em>' containment reference list.
	 * The list contents are of type {@link bpsim.ParameterValue}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Value</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Value</em>' containment reference list.
	 * @see bpsim.BpsimPackage#getEnumParameterType_ParameterValue()
	 * @model containment="true" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ParameterValue' namespace='##targetNamespace' group='ParameterValue:group'"
	 * @generated
	 */
	EList<ParameterValue> getParameterValue();

} // EnumParameterType
