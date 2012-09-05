/**
 */
package org.jboss.drools;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Time Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jboss.drools.TimeParameters#getTransferTime <em>Transfer Time</em>}</li>
 *   <li>{@link org.jboss.drools.TimeParameters#getQueueTime <em>Queue Time</em>}</li>
 *   <li>{@link org.jboss.drools.TimeParameters#getWaitTime <em>Wait Time</em>}</li>
 *   <li>{@link org.jboss.drools.TimeParameters#getSetUpTime <em>Set Up Time</em>}</li>
 *   <li>{@link org.jboss.drools.TimeParameters#getProcessingTime <em>Processing Time</em>}</li>
 *   <li>{@link org.jboss.drools.TimeParameters#getValidationTime <em>Validation Time</em>}</li>
 *   <li>{@link org.jboss.drools.TimeParameters#getReworkTime <em>Rework Time</em>}</li>
 *   <li>{@link org.jboss.drools.TimeParameters#getTimeUnit <em>Time Unit</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getTimeParameters()
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
	 * @see org.jboss.drools.DroolsPackage#getTimeParameters_TransferTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='TransferTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getTransferTime();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TimeParameters#getTransferTime <em>Transfer Time</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getTimeParameters_QueueTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='QueueTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getQueueTime();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TimeParameters#getQueueTime <em>Queue Time</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getTimeParameters_WaitTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='WaitTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getWaitTime();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TimeParameters#getWaitTime <em>Wait Time</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getTimeParameters_SetUpTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='SetUpTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getSetUpTime();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TimeParameters#getSetUpTime <em>Set Up Time</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getTimeParameters_ProcessingTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ProcessingTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getProcessingTime();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TimeParameters#getProcessingTime <em>Processing Time</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getTimeParameters_ValidationTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ValidationTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getValidationTime();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TimeParameters#getValidationTime <em>Validation Time</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getTimeParameters_ReworkTime()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ReworkTime' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getReworkTime();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TimeParameters#getReworkTime <em>Rework Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rework Time</em>' containment reference.
	 * @see #getReworkTime()
	 * @generated
	 */
	void setReworkTime(Parameter value);

	/**
	 * Returns the value of the '<em><b>Time Unit</b></em>' attribute.
	 * The literals are from the enumeration {@link org.jboss.drools.TimeUnit}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time Unit</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time Unit</em>' attribute.
	 * @see org.jboss.drools.TimeUnit
	 * @see #isSetTimeUnit()
	 * @see #unsetTimeUnit()
	 * @see #setTimeUnit(TimeUnit)
	 * @see org.jboss.drools.DroolsPackage#getTimeParameters_TimeUnit()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='element' name='TimeUnit' namespace='##targetNamespace'"
	 * @generated
	 */
	TimeUnit getTimeUnit();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TimeParameters#getTimeUnit <em>Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Time Unit</em>' attribute.
	 * @see org.jboss.drools.TimeUnit
	 * @see #isSetTimeUnit()
	 * @see #unsetTimeUnit()
	 * @see #getTimeUnit()
	 * @generated
	 */
	void setTimeUnit(TimeUnit value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.TimeParameters#getTimeUnit <em>Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTimeUnit()
	 * @see #getTimeUnit()
	 * @see #setTimeUnit(TimeUnit)
	 * @generated
	 */
	void unsetTimeUnit();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.TimeParameters#getTimeUnit <em>Time Unit</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Time Unit</em>' attribute is set.
	 * @see #unsetTimeUnit()
	 * @see #getTimeUnit()
	 * @see #setTimeUnit(TimeUnit)
	 * @generated
	 */
	boolean isSetTimeUnit();

} // TimeParameters
