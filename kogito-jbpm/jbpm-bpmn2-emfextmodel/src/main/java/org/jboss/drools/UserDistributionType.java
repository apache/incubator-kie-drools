/**
 */
package org.jboss.drools;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jboss.drools.UserDistributionType#getGroup <em>Group</em>}</li>
 *   <li>{@link org.jboss.drools.UserDistributionType#getUserDistributionDataPoint <em>User Distribution Data Point</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getUserDistributionType()
 * @model extendedMetaData="name='UserDistribution_._type' kind='elementOnly'"
 * @generated
 */
public interface UserDistributionType extends DistributionParameter {
	/**
	 * Returns the value of the '<em><b>Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group</em>' attribute list.
	 * @see org.jboss.drools.DroolsPackage#getUserDistributionType_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:4'"
	 * @generated
	 */
	FeatureMap getGroup();

	/**
	 * Returns the value of the '<em><b>User Distribution Data Point</b></em>' containment reference list.
	 * The list contents are of type {@link org.jboss.drools.UserDistributionDataPointType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>User Distribution Data Point</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>User Distribution Data Point</em>' containment reference list.
	 * @see org.jboss.drools.DroolsPackage#getUserDistributionType_UserDistributionDataPoint()
	 * @model containment="true" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='UserDistributionDataPoint' namespace='##targetNamespace' group='#group:4'"
	 * @generated
	 */
	EList<UserDistributionDataPointType> getUserDistributionDataPoint();

} // UserDistributionType
