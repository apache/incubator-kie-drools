/**
 */
package org.jboss.drools;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Boolean Parameter Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jboss.drools.BooleanParameterType#isValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getBooleanParameterType()
 * @model extendedMetaData="name='BooleanParameter_._type' kind='empty'"
 * @generated
 */
public interface BooleanParameterType extends ConstantParameter {
	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #isSetValue()
	 * @see #unsetValue()
	 * @see #setValue(boolean)
	 * @see org.jboss.drools.DroolsPackage#getBooleanParameterType_Value()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='value'"
	 * @generated
	 */
	boolean isValue();

	/**
	 * Sets the value of the '{@link org.jboss.drools.BooleanParameterType#isValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #isSetValue()
	 * @see #unsetValue()
	 * @see #isValue()
	 * @generated
	 */
	void setValue(boolean value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.BooleanParameterType#isValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetValue()
	 * @see #isValue()
	 * @see #setValue(boolean)
	 * @generated
	 */
	void unsetValue();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.BooleanParameterType#isValue <em>Value</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Value</em>' attribute is set.
	 * @see #unsetValue()
	 * @see #isValue()
	 * @see #setValue(boolean)
	 * @generated
	 */
	boolean isSetValue();

} // BooleanParameterType
