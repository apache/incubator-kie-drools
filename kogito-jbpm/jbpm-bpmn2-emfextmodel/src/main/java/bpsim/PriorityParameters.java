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
 * A representation of the model object '<em><b>Priority Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.PriorityParameters#getInterruptible <em>Interruptible</em>}</li>
 *   <li>{@link bpsim.PriorityParameters#getPriority <em>Priority</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpsim.BpsimPackage#getPriorityParameters()
 * @model extendedMetaData="name='PriorityParameters' kind='elementOnly'"
 * @generated
 */
public interface PriorityParameters extends EObject {
	/**
	 * Returns the value of the '<em><b>Interruptible</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Interruptible</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interruptible</em>' containment reference.
	 * @see #setInterruptible(Parameter)
	 * @see bpsim.BpsimPackage#getPriorityParameters_Interruptible()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Interruptible' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getInterruptible();

	/**
	 * Sets the value of the '{@link bpsim.PriorityParameters#getInterruptible <em>Interruptible</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Interruptible</em>' containment reference.
	 * @see #getInterruptible()
	 * @generated
	 */
	void setInterruptible(Parameter value);

	/**
	 * Returns the value of the '<em><b>Priority</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Priority</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Priority</em>' containment reference.
	 * @see #setPriority(Parameter)
	 * @see bpsim.BpsimPackage#getPriorityParameters_Priority()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Priority' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getPriority();

	/**
	 * Sets the value of the '{@link bpsim.PriorityParameters#getPriority <em>Priority</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Priority</em>' containment reference.
	 * @see #getPriority()
	 * @generated
	 */
	void setPriority(Parameter value);

} // PriorityParameters
