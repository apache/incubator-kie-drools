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
 * A representation of the model object '<em><b>Time Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.TimeParameters#getTransferTime <em>Transfer Time</em>}</li>
 *   <li>{@link bpsim.TimeParameters#getQueueTime <em>Queue Time</em>}</li>
 *   <li>{@link bpsim.TimeParameters#getWaitTime <em>Wait Time</em>}</li>
 *   <li>{@link bpsim.TimeParameters#getSetUpTime <em>Set Up Time</em>}</li>
 *   <li>{@link bpsim.TimeParameters#getProcessingTime <em>Processing Time</em>}</li>
 *   <li>{@link bpsim.TimeParameters#getValidationTime <em>Validation Time</em>}</li>
 *   <li>{@link bpsim.TimeParameters#getReworkTime <em>Rework Time</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpsim.BpsimPackage#getTimeParameters()
 * @model extendedMetaData="name='TimeParameters' kind='elementOnly'"
 * @generated
 */
public interface TimeParameters extends EObject {
	/**
	 * Returns the value of the '<em><b>Transfer Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Transfer Time</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transfer Time</em>' containment reference.
	 * @see #setTransferTime(Parameter)
	 * @see bpsim.BpsimPackage#getTimeParameters_TransferTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='TransferTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getTransferTime();

	/**
	 * Sets the value of the '{@link bpsim.TimeParameters#getTransferTime <em>Transfer Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transfer Time</em>' containment reference.
	 * @see #getTransferTime()
	 * @generated
	 */
	void setTransferTime(Parameter value);

	/**
	 * Returns the value of the '<em><b>Queue Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Queue Time</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Queue Time</em>' containment reference.
	 * @see #setQueueTime(Parameter)
	 * @see bpsim.BpsimPackage#getTimeParameters_QueueTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='QueueTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getQueueTime();

	/**
	 * Sets the value of the '{@link bpsim.TimeParameters#getQueueTime <em>Queue Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Queue Time</em>' containment reference.
	 * @see #getQueueTime()
	 * @generated
	 */
	void setQueueTime(Parameter value);

	/**
	 * Returns the value of the '<em><b>Wait Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Wait Time</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wait Time</em>' containment reference.
	 * @see #setWaitTime(Parameter)
	 * @see bpsim.BpsimPackage#getTimeParameters_WaitTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='WaitTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getWaitTime();

	/**
	 * Sets the value of the '{@link bpsim.TimeParameters#getWaitTime <em>Wait Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wait Time</em>' containment reference.
	 * @see #getWaitTime()
	 * @generated
	 */
	void setWaitTime(Parameter value);

	/**
	 * Returns the value of the '<em><b>Set Up Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Set Up Time</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Set Up Time</em>' containment reference.
	 * @see #setSetUpTime(Parameter)
	 * @see bpsim.BpsimPackage#getTimeParameters_SetUpTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='SetUpTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getSetUpTime();

	/**
	 * Sets the value of the '{@link bpsim.TimeParameters#getSetUpTime <em>Set Up Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Set Up Time</em>' containment reference.
	 * @see #getSetUpTime()
	 * @generated
	 */
	void setSetUpTime(Parameter value);

	/**
	 * Returns the value of the '<em><b>Processing Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Processing Time</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Processing Time</em>' containment reference.
	 * @see #setProcessingTime(Parameter)
	 * @see bpsim.BpsimPackage#getTimeParameters_ProcessingTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ProcessingTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getProcessingTime();

	/**
	 * Sets the value of the '{@link bpsim.TimeParameters#getProcessingTime <em>Processing Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Processing Time</em>' containment reference.
	 * @see #getProcessingTime()
	 * @generated
	 */
	void setProcessingTime(Parameter value);

	/**
	 * Returns the value of the '<em><b>Validation Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validation Time</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validation Time</em>' containment reference.
	 * @see #setValidationTime(Parameter)
	 * @see bpsim.BpsimPackage#getTimeParameters_ValidationTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ValidationTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getValidationTime();

	/**
	 * Sets the value of the '{@link bpsim.TimeParameters#getValidationTime <em>Validation Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Validation Time</em>' containment reference.
	 * @see #getValidationTime()
	 * @generated
	 */
	void setValidationTime(Parameter value);

	/**
	 * Returns the value of the '<em><b>Rework Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rework Time</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rework Time</em>' containment reference.
	 * @see #setReworkTime(Parameter)
	 * @see bpsim.BpsimPackage#getTimeParameters_ReworkTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ReworkTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getReworkTime();

	/**
	 * Sets the value of the '{@link bpsim.TimeParameters#getReworkTime <em>Rework Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rework Time</em>' containment reference.
	 * @see #getReworkTime()
	 * @generated
	 */
	void setReworkTime(Parameter value);

} // TimeParameters
