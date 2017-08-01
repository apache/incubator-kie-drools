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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cost Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.CostParameters#getFixedCost <em>Fixed Cost</em>}</li>
 *   <li>{@link bpsim.CostParameters#getUnitCost <em>Unit Cost</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpsim.BpsimPackage#getCostParameters()
 * @model extendedMetaData="name='CostParameters' kind='elementOnly'"
 * @generated
 */
public interface CostParameters extends EObject {
	/**
	 * Returns the value of the '<em><b>Fixed Cost</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fixed Cost</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fixed Cost</em>' containment reference.
	 * @see #setFixedCost(Parameter)
	 * @see bpsim.BpsimPackage#getCostParameters_FixedCost()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='FixedCost' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getFixedCost();

	/**
	 * Sets the value of the '{@link bpsim.CostParameters#getFixedCost <em>Fixed Cost</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fixed Cost</em>' containment reference.
	 * @see #getFixedCost()
	 * @generated
	 */
	void setFixedCost(Parameter value);

	/**
	 * Returns the value of the '<em><b>Unit Cost</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Unit Cost</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unit Cost</em>' containment reference.
	 * @see #setUnitCost(Parameter)
	 * @see bpsim.BpsimPackage#getCostParameters_UnitCost()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='UnitCost' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getUnitCost();

	/**
	 * Sets the value of the '{@link bpsim.CostParameters#getUnitCost <em>Unit Cost</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Unit Cost</em>' containment reference.
	 * @see #getUnitCost()
	 * @generated
	 */
	void setUnitCost(Parameter value);

} // CostParameters
