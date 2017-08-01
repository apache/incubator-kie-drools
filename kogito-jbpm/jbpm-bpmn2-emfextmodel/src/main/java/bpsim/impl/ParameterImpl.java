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
import bpsim.Parameter;
import bpsim.ParameterValue;
import bpsim.ResultType;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.ParameterImpl#getResultRequest <em>Result Request</em>}</li>
 *   <li>{@link bpsim.impl.ParameterImpl#getParameterValueGroup <em>Parameter Value Group</em>}</li>
 *   <li>{@link bpsim.impl.ParameterImpl#getParameterValue <em>Parameter Value</em>}</li>
 *   <li>{@link bpsim.impl.ParameterImpl#isKpi <em>Kpi</em>}</li>
 *   <li>{@link bpsim.impl.ParameterImpl#isSla <em>Sla</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ParameterImpl extends EObjectImpl implements Parameter {
	/**
	 * The cached value of the '{@link #getResultRequest() <em>Result Request</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResultRequest()
	 * @generated
	 * @ordered
	 */
	protected EList<ResultType> resultRequest;

	/**
	 * The cached value of the '{@link #getParameterValueGroup() <em>Parameter Value Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameterValueGroup()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap parameterValueGroup;

	/**
	 * The default value of the '{@link #isKpi() <em>Kpi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isKpi()
	 * @generated
	 * @ordered
	 */
	protected static final boolean KPI_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isKpi() <em>Kpi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isKpi()
	 * @generated
	 * @ordered
	 */
	protected boolean kpi = KPI_EDEFAULT;

	/**
	 * This is true if the Kpi attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean kpiESet;

	/**
	 * The default value of the '{@link #isSla() <em>Sla</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSla()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SLA_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSla() <em>Sla</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSla()
	 * @generated
	 * @ordered
	 */
	protected boolean sla = SLA_EDEFAULT;

	/**
	 * This is true if the Sla attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean slaESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ParameterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BpsimPackage.Literals.PARAMETER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ResultType> getResultRequest() {
		if (resultRequest == null) {
			resultRequest = new EDataTypeEList<ResultType>(ResultType.class, this, BpsimPackage.PARAMETER__RESULT_REQUEST);
		}
		return resultRequest;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getParameterValueGroup() {
		if (parameterValueGroup == null) {
			parameterValueGroup = new BasicFeatureMap(this, BpsimPackage.PARAMETER__PARAMETER_VALUE_GROUP);
		}
		return parameterValueGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ParameterValue> getParameterValue() {
		return getParameterValueGroup().list(BpsimPackage.Literals.PARAMETER__PARAMETER_VALUE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isKpi() {
		return kpi;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setKpi(boolean newKpi) {
		boolean oldKpi = kpi;
		kpi = newKpi;
		boolean oldKpiESet = kpiESet;
		kpiESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.PARAMETER__KPI, oldKpi, kpi, !oldKpiESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetKpi() {
		boolean oldKpi = kpi;
		boolean oldKpiESet = kpiESet;
		kpi = KPI_EDEFAULT;
		kpiESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.PARAMETER__KPI, oldKpi, KPI_EDEFAULT, oldKpiESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetKpi() {
		return kpiESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSla() {
		return sla;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSla(boolean newSla) {
		boolean oldSla = sla;
		sla = newSla;
		boolean oldSlaESet = slaESet;
		slaESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.PARAMETER__SLA, oldSla, sla, !oldSlaESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSla() {
		boolean oldSla = sla;
		boolean oldSlaESet = slaESet;
		sla = SLA_EDEFAULT;
		slaESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.PARAMETER__SLA, oldSla, SLA_EDEFAULT, oldSlaESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSla() {
		return slaESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BpsimPackage.PARAMETER__PARAMETER_VALUE_GROUP:
				return ((InternalEList<?>)getParameterValueGroup()).basicRemove(otherEnd, msgs);
			case BpsimPackage.PARAMETER__PARAMETER_VALUE:
				return ((InternalEList<?>)getParameterValue()).basicRemove(otherEnd, msgs);
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
			case BpsimPackage.PARAMETER__RESULT_REQUEST:
				return getResultRequest();
			case BpsimPackage.PARAMETER__PARAMETER_VALUE_GROUP:
				if (coreType) return getParameterValueGroup();
				return ((FeatureMap.Internal)getParameterValueGroup()).getWrapper();
			case BpsimPackage.PARAMETER__PARAMETER_VALUE:
				return getParameterValue();
			case BpsimPackage.PARAMETER__KPI:
				return isKpi();
			case BpsimPackage.PARAMETER__SLA:
				return isSla();
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
			case BpsimPackage.PARAMETER__RESULT_REQUEST:
				getResultRequest().clear();
				getResultRequest().addAll((Collection<? extends ResultType>)newValue);
				return;
			case BpsimPackage.PARAMETER__PARAMETER_VALUE_GROUP:
				((FeatureMap.Internal)getParameterValueGroup()).set(newValue);
				return;
			case BpsimPackage.PARAMETER__PARAMETER_VALUE:
				getParameterValue().clear();
				getParameterValue().addAll((Collection<? extends ParameterValue>)newValue);
				return;
			case BpsimPackage.PARAMETER__KPI:
				setKpi((Boolean)newValue);
				return;
			case BpsimPackage.PARAMETER__SLA:
				setSla((Boolean)newValue);
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
			case BpsimPackage.PARAMETER__RESULT_REQUEST:
				getResultRequest().clear();
				return;
			case BpsimPackage.PARAMETER__PARAMETER_VALUE_GROUP:
				getParameterValueGroup().clear();
				return;
			case BpsimPackage.PARAMETER__PARAMETER_VALUE:
				getParameterValue().clear();
				return;
			case BpsimPackage.PARAMETER__KPI:
				unsetKpi();
				return;
			case BpsimPackage.PARAMETER__SLA:
				unsetSla();
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
			case BpsimPackage.PARAMETER__RESULT_REQUEST:
				return resultRequest != null && !resultRequest.isEmpty();
			case BpsimPackage.PARAMETER__PARAMETER_VALUE_GROUP:
				return parameterValueGroup != null && !parameterValueGroup.isEmpty();
			case BpsimPackage.PARAMETER__PARAMETER_VALUE:
				return !getParameterValue().isEmpty();
			case BpsimPackage.PARAMETER__KPI:
				return isSetKpi();
			case BpsimPackage.PARAMETER__SLA:
				return isSetSla();
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
		result.append(" (resultRequest: ");
		result.append(resultRequest);
		result.append(", parameterValueGroup: ");
		result.append(parameterValueGroup);
		result.append(", kpi: ");
		if (kpiESet) result.append(kpi); else result.append("<unset>");
		result.append(", sla: ");
		if (slaESet) result.append(sla); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //ParameterImpl
