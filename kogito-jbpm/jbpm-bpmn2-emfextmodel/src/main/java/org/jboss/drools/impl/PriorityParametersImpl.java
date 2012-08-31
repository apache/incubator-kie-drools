/**
 */
package org.jboss.drools.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.jboss.drools.DroolsPackage;
import org.jboss.drools.Parameter;
import org.jboss.drools.PriorityParameters;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Priority Parameters</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.drools.impl.PriorityParametersImpl#getInterruptible <em>Interruptible</em>}</li>
 *   <li>{@link org.jboss.drools.impl.PriorityParametersImpl#getPriority <em>Priority</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PriorityParametersImpl extends EObjectImpl implements PriorityParameters {
	/**
	 * The cached value of the '{@link #getInterruptible() <em>Interruptible</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterruptible()
	 * @generated
	 * @ordered
	 */
	protected Parameter interruptible;

	/**
	 * The cached value of the '{@link #getPriority() <em>Priority</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPriority()
	 * @generated
	 * @ordered
	 */
	protected Parameter priority;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PriorityParametersImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DroolsPackage.Literals.PRIORITY_PARAMETERS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getInterruptible() {
		return interruptible;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetInterruptible(Parameter newInterruptible, NotificationChain msgs) {
		Parameter oldInterruptible = interruptible;
		interruptible = newInterruptible;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DroolsPackage.PRIORITY_PARAMETERS__INTERRUPTIBLE, oldInterruptible, newInterruptible);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInterruptible(Parameter newInterruptible) {
		if (newInterruptible != interruptible) {
			NotificationChain msgs = null;
			if (interruptible != null)
				msgs = ((InternalEObject)interruptible).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.PRIORITY_PARAMETERS__INTERRUPTIBLE, null, msgs);
			if (newInterruptible != null)
				msgs = ((InternalEObject)newInterruptible).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.PRIORITY_PARAMETERS__INTERRUPTIBLE, null, msgs);
			msgs = basicSetInterruptible(newInterruptible, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.PRIORITY_PARAMETERS__INTERRUPTIBLE, newInterruptible, newInterruptible));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getPriority() {
		return priority;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPriority(Parameter newPriority, NotificationChain msgs) {
		Parameter oldPriority = priority;
		priority = newPriority;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DroolsPackage.PRIORITY_PARAMETERS__PRIORITY, oldPriority, newPriority);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPriority(Parameter newPriority) {
		if (newPriority != priority) {
			NotificationChain msgs = null;
			if (priority != null)
				msgs = ((InternalEObject)priority).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.PRIORITY_PARAMETERS__PRIORITY, null, msgs);
			if (newPriority != null)
				msgs = ((InternalEObject)newPriority).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.PRIORITY_PARAMETERS__PRIORITY, null, msgs);
			msgs = basicSetPriority(newPriority, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.PRIORITY_PARAMETERS__PRIORITY, newPriority, newPriority));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DroolsPackage.PRIORITY_PARAMETERS__INTERRUPTIBLE:
				return basicSetInterruptible(null, msgs);
			case DroolsPackage.PRIORITY_PARAMETERS__PRIORITY:
				return basicSetPriority(null, msgs);
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
			case DroolsPackage.PRIORITY_PARAMETERS__INTERRUPTIBLE:
				return getInterruptible();
			case DroolsPackage.PRIORITY_PARAMETERS__PRIORITY:
				return getPriority();
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
			case DroolsPackage.PRIORITY_PARAMETERS__INTERRUPTIBLE:
				setInterruptible((Parameter)newValue);
				return;
			case DroolsPackage.PRIORITY_PARAMETERS__PRIORITY:
				setPriority((Parameter)newValue);
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
			case DroolsPackage.PRIORITY_PARAMETERS__INTERRUPTIBLE:
				setInterruptible((Parameter)null);
				return;
			case DroolsPackage.PRIORITY_PARAMETERS__PRIORITY:
				setPriority((Parameter)null);
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
			case DroolsPackage.PRIORITY_PARAMETERS__INTERRUPTIBLE:
				return interruptible != null;
			case DroolsPackage.PRIORITY_PARAMETERS__PRIORITY:
				return priority != null;
		}
		return super.eIsSet(featureID);
	}

} //PriorityParametersImpl
