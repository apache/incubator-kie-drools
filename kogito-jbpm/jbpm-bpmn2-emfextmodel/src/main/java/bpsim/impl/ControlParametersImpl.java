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
import bpsim.ControlParameters;
import bpsim.Parameter;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Control Parameters</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.ControlParametersImpl#getProbability <em>Probability</em>}</li>
 *   <li>{@link bpsim.impl.ControlParametersImpl#getCondition <em>Condition</em>}</li>
 *   <li>{@link bpsim.impl.ControlParametersImpl#getInterTriggerTimer <em>Inter Trigger Timer</em>}</li>
 *   <li>{@link bpsim.impl.ControlParametersImpl#getTriggerCount <em>Trigger Count</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ControlParametersImpl extends EObjectImpl implements ControlParameters {
	/**
	 * The cached value of the '{@link #getProbability() <em>Probability</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProbability()
	 * @generated
	 * @ordered
	 */
	protected Parameter probability;

	/**
	 * The cached value of the '{@link #getCondition() <em>Condition</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCondition()
	 * @generated
	 * @ordered
	 */
	protected Parameter condition;

	/**
	 * The cached value of the '{@link #getInterTriggerTimer() <em>Inter Trigger Timer</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterTriggerTimer()
	 * @generated
	 * @ordered
	 */
	protected Parameter interTriggerTimer;

	/**
	 * The cached value of the '{@link #getTriggerCount() <em>Trigger Count</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTriggerCount()
	 * @generated
	 * @ordered
	 */
	protected Parameter triggerCount;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ControlParametersImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BpsimPackage.Literals.CONTROL_PARAMETERS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getProbability() {
		return probability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProbability(Parameter newProbability, NotificationChain msgs) {
		Parameter oldProbability = probability;
		probability = newProbability;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BpsimPackage.CONTROL_PARAMETERS__PROBABILITY, oldProbability, newProbability);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProbability(Parameter newProbability) {
		if (newProbability != probability) {
			NotificationChain msgs = null;
			if (probability != null)
				msgs = ((InternalEObject)probability).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.CONTROL_PARAMETERS__PROBABILITY, null, msgs);
			if (newProbability != null)
				msgs = ((InternalEObject)newProbability).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.CONTROL_PARAMETERS__PROBABILITY, null, msgs);
			msgs = basicSetProbability(newProbability, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.CONTROL_PARAMETERS__PROBABILITY, newProbability, newProbability));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getCondition() {
		return condition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetCondition(Parameter newCondition, NotificationChain msgs) {
		Parameter oldCondition = condition;
		condition = newCondition;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BpsimPackage.CONTROL_PARAMETERS__CONDITION, oldCondition, newCondition);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCondition(Parameter newCondition) {
		if (newCondition != condition) {
			NotificationChain msgs = null;
			if (condition != null)
				msgs = ((InternalEObject)condition).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.CONTROL_PARAMETERS__CONDITION, null, msgs);
			if (newCondition != null)
				msgs = ((InternalEObject)newCondition).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.CONTROL_PARAMETERS__CONDITION, null, msgs);
			msgs = basicSetCondition(newCondition, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.CONTROL_PARAMETERS__CONDITION, newCondition, newCondition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getInterTriggerTimer() {
		return interTriggerTimer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetInterTriggerTimer(Parameter newInterTriggerTimer, NotificationChain msgs) {
		Parameter oldInterTriggerTimer = interTriggerTimer;
		interTriggerTimer = newInterTriggerTimer;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BpsimPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER, oldInterTriggerTimer, newInterTriggerTimer);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInterTriggerTimer(Parameter newInterTriggerTimer) {
		if (newInterTriggerTimer != interTriggerTimer) {
			NotificationChain msgs = null;
			if (interTriggerTimer != null)
				msgs = ((InternalEObject)interTriggerTimer).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER, null, msgs);
			if (newInterTriggerTimer != null)
				msgs = ((InternalEObject)newInterTriggerTimer).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER, null, msgs);
			msgs = basicSetInterTriggerTimer(newInterTriggerTimer, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER, newInterTriggerTimer, newInterTriggerTimer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getTriggerCount() {
		return triggerCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTriggerCount(Parameter newTriggerCount, NotificationChain msgs) {
		Parameter oldTriggerCount = triggerCount;
		triggerCount = newTriggerCount;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BpsimPackage.CONTROL_PARAMETERS__TRIGGER_COUNT, oldTriggerCount, newTriggerCount);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTriggerCount(Parameter newTriggerCount) {
		if (newTriggerCount != triggerCount) {
			NotificationChain msgs = null;
			if (triggerCount != null)
				msgs = ((InternalEObject)triggerCount).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.CONTROL_PARAMETERS__TRIGGER_COUNT, null, msgs);
			if (newTriggerCount != null)
				msgs = ((InternalEObject)newTriggerCount).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.CONTROL_PARAMETERS__TRIGGER_COUNT, null, msgs);
			msgs = basicSetTriggerCount(newTriggerCount, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.CONTROL_PARAMETERS__TRIGGER_COUNT, newTriggerCount, newTriggerCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BpsimPackage.CONTROL_PARAMETERS__PROBABILITY:
				return basicSetProbability(null, msgs);
			case BpsimPackage.CONTROL_PARAMETERS__CONDITION:
				return basicSetCondition(null, msgs);
			case BpsimPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				return basicSetInterTriggerTimer(null, msgs);
			case BpsimPackage.CONTROL_PARAMETERS__TRIGGER_COUNT:
				return basicSetTriggerCount(null, msgs);
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
			case BpsimPackage.CONTROL_PARAMETERS__PROBABILITY:
				return getProbability();
			case BpsimPackage.CONTROL_PARAMETERS__CONDITION:
				return getCondition();
			case BpsimPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				return getInterTriggerTimer();
			case BpsimPackage.CONTROL_PARAMETERS__TRIGGER_COUNT:
				return getTriggerCount();
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
			case BpsimPackage.CONTROL_PARAMETERS__PROBABILITY:
				setProbability((Parameter)newValue);
				return;
			case BpsimPackage.CONTROL_PARAMETERS__CONDITION:
				setCondition((Parameter)newValue);
				return;
			case BpsimPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				setInterTriggerTimer((Parameter)newValue);
				return;
			case BpsimPackage.CONTROL_PARAMETERS__TRIGGER_COUNT:
				setTriggerCount((Parameter)newValue);
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
			case BpsimPackage.CONTROL_PARAMETERS__PROBABILITY:
				setProbability((Parameter)null);
				return;
			case BpsimPackage.CONTROL_PARAMETERS__CONDITION:
				setCondition((Parameter)null);
				return;
			case BpsimPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				setInterTriggerTimer((Parameter)null);
				return;
			case BpsimPackage.CONTROL_PARAMETERS__TRIGGER_COUNT:
				setTriggerCount((Parameter)null);
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
			case BpsimPackage.CONTROL_PARAMETERS__PROBABILITY:
				return probability != null;
			case BpsimPackage.CONTROL_PARAMETERS__CONDITION:
				return condition != null;
			case BpsimPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				return interTriggerTimer != null;
			case BpsimPackage.CONTROL_PARAMETERS__TRIGGER_COUNT:
				return triggerCount != null;
		}
		return super.eIsSet(featureID);
	}

} //ControlParametersImpl
