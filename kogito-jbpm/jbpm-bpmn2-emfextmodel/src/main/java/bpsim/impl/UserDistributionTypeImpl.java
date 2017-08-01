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
package bpsim.impl;

import bpsim.BpsimPackage;
import bpsim.UserDistributionDataPointType;
import bpsim.UserDistributionType;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>User Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.UserDistributionTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link bpsim.impl.UserDistributionTypeImpl#getUserDistributionDataPoint <em>User Distribution Data Point</em>}</li>
 *   <li>{@link bpsim.impl.UserDistributionTypeImpl#isDiscrete <em>Discrete</em>}</li>
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
	 * The default value of the '{@link #isDiscrete() <em>Discrete</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDiscrete()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DISCRETE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDiscrete() <em>Discrete</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDiscrete()
	 * @generated
	 * @ordered
	 */
	protected boolean discrete = DISCRETE_EDEFAULT;

	/**
	 * This is true if the Discrete attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean discreteESet;

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
		return BpsimPackage.Literals.USER_DISTRIBUTION_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup() {
		if (group == null) {
			group = new BasicFeatureMap(this, BpsimPackage.USER_DISTRIBUTION_TYPE__GROUP);
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<UserDistributionDataPointType> getUserDistributionDataPoint() {
		return getGroup().list(BpsimPackage.Literals.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isDiscrete() {
		return discrete;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDiscrete(boolean newDiscrete) {
		boolean oldDiscrete = discrete;
		discrete = newDiscrete;
		boolean oldDiscreteESet = discreteESet;
		discreteESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.USER_DISTRIBUTION_TYPE__DISCRETE, oldDiscrete, discrete, !oldDiscreteESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDiscrete() {
		boolean oldDiscrete = discrete;
		boolean oldDiscreteESet = discreteESet;
		discrete = DISCRETE_EDEFAULT;
		discreteESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.USER_DISTRIBUTION_TYPE__DISCRETE, oldDiscrete, DISCRETE_EDEFAULT, oldDiscreteESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetDiscrete() {
		return discreteESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BpsimPackage.USER_DISTRIBUTION_TYPE__GROUP:
				return ((InternalEList<?>)getGroup()).basicRemove(otherEnd, msgs);
			case BpsimPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
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
			case BpsimPackage.USER_DISTRIBUTION_TYPE__GROUP:
				if (coreType) return getGroup();
				return ((FeatureMap.Internal)getGroup()).getWrapper();
			case BpsimPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
				return getUserDistributionDataPoint();
			case BpsimPackage.USER_DISTRIBUTION_TYPE__DISCRETE:
				return isDiscrete();
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
			case BpsimPackage.USER_DISTRIBUTION_TYPE__GROUP:
				((FeatureMap.Internal)getGroup()).set(newValue);
				return;
			case BpsimPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
				getUserDistributionDataPoint().clear();
				getUserDistributionDataPoint().addAll((Collection<? extends UserDistributionDataPointType>)newValue);
				return;
			case BpsimPackage.USER_DISTRIBUTION_TYPE__DISCRETE:
				setDiscrete((Boolean)newValue);
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
			case BpsimPackage.USER_DISTRIBUTION_TYPE__GROUP:
				getGroup().clear();
				return;
			case BpsimPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
				getUserDistributionDataPoint().clear();
				return;
			case BpsimPackage.USER_DISTRIBUTION_TYPE__DISCRETE:
				unsetDiscrete();
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
			case BpsimPackage.USER_DISTRIBUTION_TYPE__GROUP:
				return group != null && !group.isEmpty();
			case BpsimPackage.USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT:
				return !getUserDistributionDataPoint().isEmpty();
			case BpsimPackage.USER_DISTRIBUTION_TYPE__DISCRETE:
				return isSetDiscrete();
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
		result.append(", discrete: ");
		if (discreteESet) result.append(discrete); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //UserDistributionTypeImpl
