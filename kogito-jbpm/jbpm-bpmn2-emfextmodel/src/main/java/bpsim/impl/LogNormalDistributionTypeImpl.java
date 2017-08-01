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
import bpsim.LogNormalDistributionType;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Log Normal Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.LogNormalDistributionTypeImpl#getMean <em>Mean</em>}</li>
 *   <li>{@link bpsim.impl.LogNormalDistributionTypeImpl#getStandardDeviation <em>Standard Deviation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LogNormalDistributionTypeImpl extends DistributionParameterImpl implements LogNormalDistributionType {
	/**
	 * The default value of the '{@link #getMean() <em>Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMean()
	 * @generated
	 * @ordered
	 */
	protected static final double MEAN_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getMean() <em>Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMean()
	 * @generated
	 * @ordered
	 */
	protected double mean = MEAN_EDEFAULT;

	/**
	 * This is true if the Mean attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean meanESet;

	/**
	 * The default value of the '{@link #getStandardDeviation() <em>Standard Deviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStandardDeviation()
	 * @generated
	 * @ordered
	 */
	protected static final double STANDARD_DEVIATION_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getStandardDeviation() <em>Standard Deviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStandardDeviation()
	 * @generated
	 * @ordered
	 */
	protected double standardDeviation = STANDARD_DEVIATION_EDEFAULT;

	/**
	 * This is true if the Standard Deviation attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean standardDeviationESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LogNormalDistributionTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BpsimPackage.Literals.LOG_NORMAL_DISTRIBUTION_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMean(double newMean) {
		double oldMean = mean;
		mean = newMean;
		boolean oldMeanESet = meanESet;
		meanESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__MEAN, oldMean, mean, !oldMeanESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetMean() {
		double oldMean = mean;
		boolean oldMeanESet = meanESet;
		mean = MEAN_EDEFAULT;
		meanESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__MEAN, oldMean, MEAN_EDEFAULT, oldMeanESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetMean() {
		return meanESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getStandardDeviation() {
		return standardDeviation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStandardDeviation(double newStandardDeviation) {
		double oldStandardDeviation = standardDeviation;
		standardDeviation = newStandardDeviation;
		boolean oldStandardDeviationESet = standardDeviationESet;
		standardDeviationESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION, oldStandardDeviation, standardDeviation, !oldStandardDeviationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetStandardDeviation() {
		double oldStandardDeviation = standardDeviation;
		boolean oldStandardDeviationESet = standardDeviationESet;
		standardDeviation = STANDARD_DEVIATION_EDEFAULT;
		standardDeviationESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION, oldStandardDeviation, STANDARD_DEVIATION_EDEFAULT, oldStandardDeviationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetStandardDeviation() {
		return standardDeviationESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__MEAN:
				return getMean();
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION:
				return getStandardDeviation();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__MEAN:
				setMean((Double)newValue);
				return;
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION:
				setStandardDeviation((Double)newValue);
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
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__MEAN:
				unsetMean();
				return;
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION:
				unsetStandardDeviation();
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
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__MEAN:
				return isSetMean();
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION:
				return isSetStandardDeviation();
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
		result.append(" (mean: ");
		if (meanESet) result.append(mean); else result.append("<unset>");
		result.append(", standardDeviation: ");
		if (standardDeviationESet) result.append(standardDeviation); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //LogNormalDistributionTypeImpl
