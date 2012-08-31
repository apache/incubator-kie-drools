/**
 */
package org.jboss.drools;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User Distribution Data Point Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jboss.drools.UserDistributionDataPointType#getParameterValueGroup <em>Parameter Value Group</em>}</li>
 *   <li>{@link org.jboss.drools.UserDistributionDataPointType#getParameterValue <em>Parameter Value</em>}</li>
 *   <li>{@link org.jboss.drools.UserDistributionDataPointType#getProbability <em>Probability</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getUserDistributionDataPointType()
 * @model extendedMetaData="name='UserDistributionDataPoint_._type' kind='elementOnly'"
 * @generated
 */
public interface UserDistributionDataPointType extends EObject {
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
	 * @see org.jboss.drools.DroolsPackage#getUserDistributionDataPointType_ParameterValueGroup()
	 * @model dataType="org.eclipse.emf.ecore.EFeatureMapEntry" required="true" many="false"
	 *        extendedMetaData="kind='group' name='ParameterValue:group' namespace='##targetNamespace'"
	 * @generated
	 */
	FeatureMap getParameterValueGroup();

	/**
	 * Returns the value of the '<em><b>Parameter Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Value</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Value</em>' containment reference.
	 * @see #setParameterValue(ParameterValue)
	 * @see org.jboss.drools.DroolsPackage#getUserDistributionDataPointType_ParameterValue()
	 * @model containment="true" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ParameterValue' namespace='##targetNamespace' group='ParameterValue:group'"
	 * @generated
	 */
	ParameterValue getParameterValue();

	/**
	 * Sets the value of the '{@link org.jboss.drools.UserDistributionDataPointType#getParameterValue <em>Parameter Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameter Value</em>' containment reference.
	 * @see #getParameterValue()
	 * @generated
	 */
	void setParameterValue(ParameterValue value);

	/**
	 * Returns the value of the '<em><b>Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Probability</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Probability</em>' attribute.
	 * @see #isSetProbability()
	 * @see #unsetProbability()
	 * @see #setProbability(float)
	 * @see org.jboss.drools.DroolsPackage#getUserDistributionDataPointType_Probability()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Float"
	 *        extendedMetaData="kind='attribute' name='probability'"
	 * @generated
	 */
	float getProbability();

	/**
	 * Sets the value of the '{@link org.jboss.drools.UserDistributionDataPointType#getProbability <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Probability</em>' attribute.
	 * @see #isSetProbability()
	 * @see #unsetProbability()
	 * @see #getProbability()
	 * @generated
	 */
	void setProbability(float value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.UserDistributionDataPointType#getProbability <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetProbability()
	 * @see #getProbability()
	 * @see #setProbability(float)
	 * @generated
	 */
	void unsetProbability();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.UserDistributionDataPointType#getProbability <em>Probability</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Probability</em>' attribute is set.
	 * @see #unsetProbability()
	 * @see #getProbability()
	 * @see #setProbability(float)
	 * @generated
	 */
	boolean isSetProbability();

} // UserDistributionDataPointType
