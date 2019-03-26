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

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.Parameter#getResultRequest <em>Result Request</em>}</li>
 *   <li>{@link bpsim.Parameter#getParameterValueGroup <em>Parameter Value Group</em>}</li>
 *   <li>{@link bpsim.Parameter#getParameterValue <em>Parameter Value</em>}</li>
 *   <li>{@link bpsim.Parameter#isKpi <em>Kpi</em>}</li>
 *   <li>{@link bpsim.Parameter#isSla <em>Sla</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpsim.BpsimPackage#getParameter()
 * @model extendedMetaData="name='Parameter' kind='elementOnly'"
 * @generated
 */
public interface Parameter extends EObject {
	/**
	 * Returns the value of the '<em><b>Result Request</b></em>' attribute list.
	 * The list contents are of type {@link bpsim.ResultType}.
	 * The literals are from the enumeration {@link bpsim.ResultType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Result Request</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Result Request</em>' attribute list.
	 * @see bpsim.ResultType
	 * @see bpsim.BpsimPackage#getParameter_ResultRequest()
	 * @model unique="false"
	 *        extendedMetaData="kind='element' name='ResultRequest' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<ResultType> getResultRequest();

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
	 * @see bpsim.BpsimPackage#getParameter_ParameterValueGroup()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='ParameterValue:group' namespace='##targetNamespace'"
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
	 * @see bpsim.BpsimPackage#getParameter_ParameterValue()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ParameterValue' namespace='##targetNamespace' group='ParameterValue:group'"
	 * @generated
	 */
	EList<ParameterValue> getParameterValue();

	/**
	 * Returns the value of the '<em><b>Kpi</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kpi</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kpi</em>' attribute.
	 * @see #isSetKpi()
	 * @see #unsetKpi()
	 * @see #setKpi(boolean)
	 * @see bpsim.BpsimPackage#getParameter_Kpi()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='kpi'"
	 * @generated
	 */
	boolean isKpi();

	/**
	 * Sets the value of the '{@link bpsim.Parameter#isKpi <em>Kpi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Kpi</em>' attribute.
	 * @see #isSetKpi()
	 * @see #unsetKpi()
	 * @see #isKpi()
	 * @generated
	 */
	void setKpi(boolean value);

	/**
	 * Unsets the value of the '{@link bpsim.Parameter#isKpi <em>Kpi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetKpi()
	 * @see #isKpi()
	 * @see #setKpi(boolean)
	 * @generated
	 */
	void unsetKpi();

	/**
	 * Returns whether the value of the '{@link bpsim.Parameter#isKpi <em>Kpi</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Kpi</em>' attribute is set.
	 * @see #unsetKpi()
	 * @see #isKpi()
	 * @see #setKpi(boolean)
	 * @generated
	 */
	boolean isSetKpi();

	/**
	 * Returns the value of the '<em><b>Sla</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sla</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sla</em>' attribute.
	 * @see #isSetSla()
	 * @see #unsetSla()
	 * @see #setSla(boolean)
	 * @see bpsim.BpsimPackage#getParameter_Sla()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='sla'"
	 * @generated
	 */
	boolean isSla();

	/**
	 * Sets the value of the '{@link bpsim.Parameter#isSla <em>Sla</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sla</em>' attribute.
	 * @see #isSetSla()
	 * @see #unsetSla()
	 * @see #isSla()
	 * @generated
	 */
	void setSla(boolean value);

	/**
	 * Unsets the value of the '{@link bpsim.Parameter#isSla <em>Sla</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSla()
	 * @see #isSla()
	 * @see #setSla(boolean)
	 * @generated
	 */
	void unsetSla();

	/**
	 * Returns whether the value of the '{@link bpsim.Parameter#isSla <em>Sla</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Sla</em>' attribute is set.
	 * @see #unsetSla()
	 * @see #isSla()
	 * @see #setSla(boolean)
	 * @generated
	 */
	boolean isSetSla();

} // Parameter
