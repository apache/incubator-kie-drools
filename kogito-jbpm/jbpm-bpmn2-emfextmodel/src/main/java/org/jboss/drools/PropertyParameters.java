/**
 */
package org.jboss.drools;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jboss.drools.PropertyParameters#getProperty <em>Property</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getPropertyParameters()
 * @model extendedMetaData="name='PropertyParameters' kind='elementOnly'"
 * @generated
 */
public interface PropertyParameters extends EObject {
	/**
	 * Returns the value of the '<em><b>Property</b></em>' containment reference list.
	 * The list contents are of type {@link org.jboss.drools.PropertyType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Property</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property</em>' containment reference list.
	 * @see org.jboss.drools.DroolsPackage#getPropertyParameters_Property()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Property' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<PropertyType> getProperty();

} // PropertyParameters
