/**
 */
package org.jboss.drools.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.jboss.drools.CostParameters;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.Parameter;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Cost Parameters</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.drools.impl.CostParametersImpl#getFixedCost <em>Fixed Cost</em>}</li>
 *   <li>{@link org.jboss.drools.impl.CostParametersImpl#getUnitCost <em>Unit Cost</em>}</li>
 *   <li>{@link org.jboss.drools.impl.CostParametersImpl#getCurrencyUnit <em>Currency Unit</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CostParametersImpl extends EObjectImpl implements CostParameters {
	/**
	 * The cached value of the '{@link #getFixedCost() <em>Fixed Cost</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFixedCost()
	 * @generated
	 * @ordered
	 */
	protected Parameter fixedCost;

	/**
	 * The cached value of the '{@link #getUnitCost() <em>Unit Cost</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnitCost()
	 * @generated
	 * @ordered
	 */
	protected Parameter unitCost;

	/**
	 * The default value of the '{@link #getCurrencyUnit() <em>Currency Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrencyUnit()
	 * @generated
	 * @ordered
	 */
	protected static final String CURRENCY_UNIT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCurrencyUnit() <em>Currency Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrencyUnit()
	 * @generated
	 * @ordered
	 */
	protected String currencyUnit = CURRENCY_UNIT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CostParametersImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DroolsPackage.Literals.COST_PARAMETERS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getFixedCost() {
		return fixedCost;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFixedCost(Parameter newFixedCost, NotificationChain msgs) {
		Parameter oldFixedCost = fixedCost;
		fixedCost = newFixedCost;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DroolsPackage.COST_PARAMETERS__FIXED_COST, oldFixedCost, newFixedCost);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFixedCost(Parameter newFixedCost) {
		if (newFixedCost != fixedCost) {
			NotificationChain msgs = null;
			if (fixedCost != null)
				msgs = ((InternalEObject)fixedCost).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.COST_PARAMETERS__FIXED_COST, null, msgs);
			if (newFixedCost != null)
				msgs = ((InternalEObject)newFixedCost).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.COST_PARAMETERS__FIXED_COST, null, msgs);
			msgs = basicSetFixedCost(newFixedCost, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.COST_PARAMETERS__FIXED_COST, newFixedCost, newFixedCost));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getUnitCost() {
		return unitCost;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUnitCost(Parameter newUnitCost, NotificationChain msgs) {
		Parameter oldUnitCost = unitCost;
		unitCost = newUnitCost;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, DroolsPackage.COST_PARAMETERS__UNIT_COST, oldUnitCost, newUnitCost);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUnitCost(Parameter newUnitCost) {
		if (newUnitCost != unitCost) {
			NotificationChain msgs = null;
			if (unitCost != null)
				msgs = ((InternalEObject)unitCost).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.COST_PARAMETERS__UNIT_COST, null, msgs);
			if (newUnitCost != null)
				msgs = ((InternalEObject)newUnitCost).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - DroolsPackage.COST_PARAMETERS__UNIT_COST, null, msgs);
			msgs = basicSetUnitCost(newUnitCost, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.COST_PARAMETERS__UNIT_COST, newUnitCost, newUnitCost));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCurrencyUnit() {
		return currencyUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCurrencyUnit(String newCurrencyUnit) {
		String oldCurrencyUnit = currencyUnit;
		currencyUnit = newCurrencyUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.COST_PARAMETERS__CURRENCY_UNIT, oldCurrencyUnit, currencyUnit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DroolsPackage.COST_PARAMETERS__FIXED_COST:
				return basicSetFixedCost(null, msgs);
			case DroolsPackage.COST_PARAMETERS__UNIT_COST:
				return basicSetUnitCost(null, msgs);
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
			case DroolsPackage.COST_PARAMETERS__FIXED_COST:
				return getFixedCost();
			case DroolsPackage.COST_PARAMETERS__UNIT_COST:
				return getUnitCost();
			case DroolsPackage.COST_PARAMETERS__CURRENCY_UNIT:
				return getCurrencyUnit();
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
			case DroolsPackage.COST_PARAMETERS__FIXED_COST:
				setFixedCost((Parameter)newValue);
				return;
			case DroolsPackage.COST_PARAMETERS__UNIT_COST:
				setUnitCost((Parameter)newValue);
				return;
			case DroolsPackage.COST_PARAMETERS__CURRENCY_UNIT:
				setCurrencyUnit((String)newValue);
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
			case DroolsPackage.COST_PARAMETERS__FIXED_COST:
				setFixedCost((Parameter)null);
				return;
			case DroolsPackage.COST_PARAMETERS__UNIT_COST:
				setUnitCost((Parameter)null);
				return;
			case DroolsPackage.COST_PARAMETERS__CURRENCY_UNIT:
				setCurrencyUnit(CURRENCY_UNIT_EDEFAULT);
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
			case DroolsPackage.COST_PARAMETERS__FIXED_COST:
				return fixedCost != null;
			case DroolsPackage.COST_PARAMETERS__UNIT_COST:
				return unitCost != null;
			case DroolsPackage.COST_PARAMETERS__CURRENCY_UNIT:
				return CURRENCY_UNIT_EDEFAULT == null ? currencyUnit != null : !CURRENCY_UNIT_EDEFAULT.equals(currencyUnit);
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
		result.append(" (currencyUnit: ");
		result.append(currencyUnit);
		result.append(')');
		return result.toString();
	}

} //CostParametersImpl
