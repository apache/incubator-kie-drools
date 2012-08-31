/**
 */
package org.jboss.drools.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.jboss.drools.DroolsPackage;
import org.jboss.drools.UserDistributionDataPointType;
import org.jboss.drools.UserDistributionType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>User Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.drools.impl.UserDistributionTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link org.jboss.drools.impl.UserDistributionTypeImpl#getUserDistributionDataPoint <em>User Distribution Data Point</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UserDistributionTypeImpl extends DistributionParameterImpl implements UserDistributionType {
	/**
	 * The cached value of the '{@link #getGroup() <em>Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroup()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap group;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UserDistributionTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DroolsPackage.Literals.USER_DISTRIBUTION_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup() {
		if (group == null) {
			group = new BasicFeatureMap(this, DroolsPackage.USER_DISTRIBUTION_TYPE__GROUP);
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<UserDistributionDataPointType> getUserDistributionDataPoint() {
		return getGroup().list(DroolsPackage.Literals.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DroolsPackage.USER_DISTRIBUTION_TYPE__GROUP:
				return ((InternalEList<?>)getGroup()).basicRemove(otherEnd, msgs);
			case DroolsPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
				return ((InternalEList<?>)getUserDistributionDataPoint()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DroolsPackage.USER_DISTRIBUTION_TYPE__GROUP:
				if (coreType) return getGroup();
				return ((FeatureMap.Internal)getGroup()).getWrapper();
			case DroolsPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
				return getUserDistributionDataPoint();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DroolsPackage.USER_DISTRIBUTION_TYPE__GROUP:
				((FeatureMap.Internal)getGroup()).set(newValue);
				return;
			case DroolsPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
				getUserDistributionDataPoint().clear();
				getUserDistributionDataPoint().addAll((Collection<? extends UserDistributionDataPointType>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case DroolsPackage.USER_DISTRIBUTION_TYPE__GROUP:
				getGroup().clear();
				return;
			case DroolsPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
				getUserDistributionDataPoint().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DroolsPackage.USER_DISTRIBUTION_TYPE__GROUP:
				return group != null && !group.isEmpty();
			case DroolsPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
				return !getUserDistributionDataPoint().isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (group: ");
		result.append(group);
		result.append(')');
		return result.toString();
	}

} //UserDistributionTypeImpl
