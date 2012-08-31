/**
 */
package org.jboss.drools.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.jboss.drools.ControlParameters;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.Parameter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Control Parameters</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.drools.impl.ControlParametersImpl#getProbability <em>Probability</em>}</li>
 *   <li>{@link org.jboss.drools.impl.ControlParametersImpl#getInterTriggerTimer <em>Inter Trigger Timer</em>}</li>
 *   <li>{@link org.jboss.drools.impl.ControlParametersImpl#getMaxTriggerCount <em>Max Trigger Count</em>}</li>
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
	 * The cached value of the '{@link #getInterTriggerTimer() <em>Inter Trigger Timer</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterTriggerTimer()
	 * @generated
	 * @ordered
	 */
	protected Parameter interTriggerTimer;

	/**
	 * The cached value of the '{@link #getMaxTriggerCount() <em>Max Trigger Count</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxTriggerCount()
	 * @generated
	 * @ordered
	 */
	protected Parameter maxTriggerCount;

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
		return DroolsPackage.Literals.CONTROL_PARAMETERS;
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DroolsPackage.CONTROL_PARAMETERS__PROBABILITY, oldProbability, newProbability);
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
				msgs = ((InternalEObject)probability).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.CONTROL_PARAMETERS__PROBABILITY, null, msgs);
			if (newProbability != null)
				msgs = ((InternalEObject)newProbability).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.CONTROL_PARAMETERS__PROBABILITY, null, msgs);
			msgs = basicSetProbability(newProbability, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.CONTROL_PARAMETERS__PROBABILITY, newProbability, newProbability));
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DroolsPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER, oldInterTriggerTimer, newInterTriggerTimer);
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
				msgs = ((InternalEObject)interTriggerTimer).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER, null, msgs);
			if (newInterTriggerTimer != null)
				msgs = ((InternalEObject)newInterTriggerTimer).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER, null, msgs);
			msgs = basicSetInterTriggerTimer(newInterTriggerTimer, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER, newInterTriggerTimer, newInterTriggerTimer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getMaxTriggerCount() {
		return maxTriggerCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMaxTriggerCount(Parameter newMaxTriggerCount, NotificationChain msgs) {
		Parameter oldMaxTriggerCount = maxTriggerCount;
		maxTriggerCount = newMaxTriggerCount;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DroolsPackage.CONTROL_PARAMETERS__MAX_TRIGGER_COUNT, oldMaxTriggerCount, newMaxTriggerCount);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaxTriggerCount(Parameter newMaxTriggerCount) {
		if (newMaxTriggerCount != maxTriggerCount) {
			NotificationChain msgs = null;
			if (maxTriggerCount != null)
				msgs = ((InternalEObject)maxTriggerCount).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.CONTROL_PARAMETERS__MAX_TRIGGER_COUNT, null, msgs);
			if (newMaxTriggerCount != null)
				msgs = ((InternalEObject)newMaxTriggerCount).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.CONTROL_PARAMETERS__MAX_TRIGGER_COUNT, null, msgs);
			msgs = basicSetMaxTriggerCount(newMaxTriggerCount, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.CONTROL_PARAMETERS__MAX_TRIGGER_COUNT, newMaxTriggerCount, newMaxTriggerCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DroolsPackage.CONTROL_PARAMETERS__PROBABILITY:
				return basicSetProbability(null, msgs);
			case DroolsPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				return basicSetInterTriggerTimer(null, msgs);
			case DroolsPackage.CONTROL_PARAMETERS__MAX_TRIGGER_COUNT:
				return basicSetMaxTriggerCount(null, msgs);
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
			case DroolsPackage.CONTROL_PARAMETERS__PROBABILITY:
				return getProbability();
			case DroolsPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				return getInterTriggerTimer();
			case DroolsPackage.CONTROL_PARAMETERS__MAX_TRIGGER_COUNT:
				return getMaxTriggerCount();
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
			case DroolsPackage.CONTROL_PARAMETERS__PROBABILITY:
				setProbability((Parameter)newValue);
				return;
			case DroolsPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				setInterTriggerTimer((Parameter)newValue);
				return;
			case DroolsPackage.CONTROL_PARAMETERS__MAX_TRIGGER_COUNT:
				setMaxTriggerCount((Parameter)newValue);
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
			case DroolsPackage.CONTROL_PARAMETERS__PROBABILITY:
				setProbability((Parameter)null);
				return;
			case DroolsPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				setInterTriggerTimer((Parameter)null);
				return;
			case DroolsPackage.CONTROL_PARAMETERS__MAX_TRIGGER_COUNT:
				setMaxTriggerCount((Parameter)null);
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
			case DroolsPackage.CONTROL_PARAMETERS__PROBABILITY:
				return probability != null;
			case DroolsPackage.CONTROL_PARAMETERS__INTER_TRIGGER_TIMER:
				return interTriggerTimer != null;
			case DroolsPackage.CONTROL_PARAMETERS__MAX_TRIGGER_COUNT:
				return maxTriggerCount != null;
		}
		return super.eIsSet(featureID);
	}

} //ControlParametersImpl
