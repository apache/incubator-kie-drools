/**
 */
package org.jboss.drools;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Control Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jboss.drools.ControlParameters#getProbability <em>Probability</em>}</li>
 *   <li>{@link org.jboss.drools.ControlParameters#getInterTriggerTimer <em>Inter Trigger Timer</em>}</li>
 *   <li>{@link org.jboss.drools.ControlParameters#getMaxTriggerCount <em>Max Trigger Count</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getControlParameters()
 * @model extendedMetaData="name='ControlParameters' kind='elementOnly'"
 * @generated
 */
public interface ControlParameters extends EObject {
	/**
	 * Returns the value of the '<em><b>Probability</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Probability</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Probability</em>' containment reference.
	 * @see #setProbability(Parameter)
	 * @see org.jboss.drools.DroolsPackage#getControlParameters_Probability()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Probability' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getProbability();

	/**
	 * Sets the value of the '{@link org.jboss.drools.ControlParameters#getProbability <em>Probability</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Probability</em>' containment reference.
	 * @see #getProbability()
	 * @generated
	 */
	void setProbability(Parameter value);

	/**
	 * Returns the value of the '<em><b>Inter Trigger Timer</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Inter Trigger Timer</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inter Trigger Timer</em>' containment reference.
	 * @see #setInterTriggerTimer(Parameter)
	 * @see org.jboss.drools.DroolsPackage#getControlParameters_InterTriggerTimer()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='InterTriggerTimer' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getInterTriggerTimer();

	/**
	 * Sets the value of the '{@link org.jboss.drools.ControlParameters#getInterTriggerTimer <em>Inter Trigger Timer</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Inter Trigger Timer</em>' containment reference.
	 * @see #getInterTriggerTimer()
	 * @generated
	 */
	void setInterTriggerTimer(Parameter value);

	/**
	 * Returns the value of the '<em><b>Max Trigger Count</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Max Trigger Count</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max Trigger Count</em>' containment reference.
	 * @see #setMaxTriggerCount(Parameter)
	 * @see org.jboss.drools.DroolsPackage#getControlParameters_MaxTriggerCount()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='MaxTriggerCount' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getMaxTriggerCount();

	/**
	 * Sets the value of the '{@link org.jboss.drools.ControlParameters#getMaxTriggerCount <em>Max Trigger Count</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Trigger Count</em>' containment reference.
	 * @see #getMaxTriggerCount()
	 * @generated
	 */
	void setMaxTriggerCount(Parameter value);

} // ControlParameters
