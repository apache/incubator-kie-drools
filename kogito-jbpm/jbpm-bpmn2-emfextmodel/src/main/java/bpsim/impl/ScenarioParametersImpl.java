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
import bpsim.PropertyParameters;
import bpsim.ScenarioParameters;
import bpsim.TimeUnit;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Scenario Parameters</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.ScenarioParametersImpl#getStart <em>Start</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioParametersImpl#getDuration <em>Duration</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioParametersImpl#getPropertyParameters <em>Property Parameters</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioParametersImpl#getBaseCurrencyUnit <em>Base Currency Unit</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioParametersImpl#getBaseTimeUnit <em>Base Time Unit</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioParametersImpl#getReplication <em>Replication</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioParametersImpl#getSeed <em>Seed</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ScenarioParametersImpl extends EObjectImpl implements ScenarioParameters {
	/**
	 * The cached value of the '{@link #getStart() <em>Start</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStart()
	 * @generated
	 * @ordered
	 */
	protected Parameter start;

	/**
	 * The cached value of the '{@link #getDuration() <em>Duration</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected Parameter duration;

	/**
	 * The cached value of the '{@link #getPropertyParameters() <em>Property Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertyParameters()
	 * @generated
	 * @ordered
	 */
	protected PropertyParameters propertyParameters;

	/**
	 * The default value of the '{@link #getBaseCurrencyUnit() <em>Base Currency Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBaseCurrencyUnit()
	 * @generated
	 * @ordered
	 */
	protected static final String BASE_CURRENCY_UNIT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBaseCurrencyUnit() <em>Base Currency Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBaseCurrencyUnit()
	 * @generated
	 * @ordered
	 */
	protected String baseCurrencyUnit = BASE_CURRENCY_UNIT_EDEFAULT;

	/**
	 * The default value of the '{@link #getBaseTimeUnit() <em>Base Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBaseTimeUnit()
	 * @generated
	 * @ordered
	 */
	protected static final TimeUnit BASE_TIME_UNIT_EDEFAULT = TimeUnit.MS;

	/**
	 * The cached value of the '{@link #getBaseTimeUnit() <em>Base Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBaseTimeUnit()
	 * @generated
	 * @ordered
	 */
	protected TimeUnit baseTimeUnit = BASE_TIME_UNIT_EDEFAULT;

	/**
	 * This is true if the Base Time Unit attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean baseTimeUnitESet;

	/**
	 * The default value of the '{@link #getReplication() <em>Replication</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReplication()
	 * @generated
	 * @ordered
	 */
	protected static final int REPLICATION_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getReplication() <em>Replication</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReplication()
	 * @generated
	 * @ordered
	 */
	protected int replication = REPLICATION_EDEFAULT;

	/**
	 * This is true if the Replication attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean replicationESet;

	/**
	 * The default value of the '{@link #getSeed() <em>Seed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSeed()
	 * @generated
	 * @ordered
	 */
	protected static final long SEED_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getSeed() <em>Seed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSeed()
	 * @generated
	 * @ordered
	 */
	protected long seed = SEED_EDEFAULT;

	/**
	 * This is true if the Seed attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean seedESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ScenarioParametersImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BpsimPackage.Literals.SCENARIO_PARAMETERS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getStart() {
		return start;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetStart(Parameter newStart, NotificationChain msgs) {
		Parameter oldStart = start;
		start = newStart;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__START, oldStart, newStart);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStart(Parameter newStart) {
		if (newStart != start) {
			NotificationChain msgs = null;
			if (start != null)
				msgs = ((InternalEObject)start).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.SCENARIO_PARAMETERS__START, null, msgs);
			if (newStart != null)
				msgs = ((InternalEObject)newStart).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.SCENARIO_PARAMETERS__START, null, msgs);
			msgs = basicSetStart(newStart, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__START, newStart, newStart));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter getDuration() {
		return duration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDuration(Parameter newDuration, NotificationChain msgs) {
		Parameter oldDuration = duration;
		duration = newDuration;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__DURATION, oldDuration, newDuration);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDuration(Parameter newDuration) {
		if (newDuration != duration) {
			NotificationChain msgs = null;
			if (duration != null)
				msgs = ((InternalEObject)duration).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.SCENARIO_PARAMETERS__DURATION, null, msgs);
			if (newDuration != null)
				msgs = ((InternalEObject)newDuration).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.SCENARIO_PARAMETERS__DURATION, null, msgs);
			msgs = basicSetDuration(newDuration, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__DURATION, newDuration, newDuration));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PropertyParameters getPropertyParameters() {
		return propertyParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPropertyParameters(PropertyParameters newPropertyParameters, NotificationChain msgs) {
		PropertyParameters oldPropertyParameters = propertyParameters;
		propertyParameters = newPropertyParameters;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__PROPERTY_PARAMETERS, oldPropertyParameters, newPropertyParameters);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPropertyParameters(PropertyParameters newPropertyParameters) {
		if (newPropertyParameters != propertyParameters) {
			NotificationChain msgs = null;
			if (propertyParameters != null)
				msgs = ((InternalEObject)propertyParameters).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.SCENARIO_PARAMETERS__PROPERTY_PARAMETERS, null, msgs);
			if (newPropertyParameters != null)
				msgs = ((InternalEObject)newPropertyParameters).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.SCENARIO_PARAMETERS__PROPERTY_PARAMETERS, null, msgs);
			msgs = basicSetPropertyParameters(newPropertyParameters, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__PROPERTY_PARAMETERS, newPropertyParameters, newPropertyParameters));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBaseCurrencyUnit() {
		return baseCurrencyUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBaseCurrencyUnit(String newBaseCurrencyUnit) {
		String oldBaseCurrencyUnit = baseCurrencyUnit;
		baseCurrencyUnit = newBaseCurrencyUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__BASE_CURRENCY_UNIT, oldBaseCurrencyUnit, baseCurrencyUnit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimeUnit getBaseTimeUnit() {
		return baseTimeUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBaseTimeUnit(TimeUnit newBaseTimeUnit) {
		TimeUnit oldBaseTimeUnit = baseTimeUnit;
		baseTimeUnit = newBaseTimeUnit == null ? BASE_TIME_UNIT_EDEFAULT : newBaseTimeUnit;
		boolean oldBaseTimeUnitESet = baseTimeUnitESet;
		baseTimeUnitESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__BASE_TIME_UNIT, oldBaseTimeUnit, baseTimeUnit, !oldBaseTimeUnitESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetBaseTimeUnit() {
		TimeUnit oldBaseTimeUnit = baseTimeUnit;
		boolean oldBaseTimeUnitESet = baseTimeUnitESet;
		baseTimeUnit = BASE_TIME_UNIT_EDEFAULT;
		baseTimeUnitESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.SCENARIO_PARAMETERS__BASE_TIME_UNIT, oldBaseTimeUnit, BASE_TIME_UNIT_EDEFAULT, oldBaseTimeUnitESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetBaseTimeUnit() {
		return baseTimeUnitESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getReplication() {
		return replication;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReplication(int newReplication) {
		int oldReplication = replication;
		replication = newReplication;
		boolean oldReplicationESet = replicationESet;
		replicationESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__REPLICATION, oldReplication, replication, !oldReplicationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetReplication() {
		int oldReplication = replication;
		boolean oldReplicationESet = replicationESet;
		replication = REPLICATION_EDEFAULT;
		replicationESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.SCENARIO_PARAMETERS__REPLICATION, oldReplication, REPLICATION_EDEFAULT, oldReplicationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetReplication() {
		return replicationESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getSeed() {
		return seed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSeed(long newSeed) {
		long oldSeed = seed;
		seed = newSeed;
		boolean oldSeedESet = seedESet;
		seedESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO_PARAMETERS__SEED, oldSeed, seed, !oldSeedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetSeed() {
		long oldSeed = seed;
		boolean oldSeedESet = seedESet;
		seed = SEED_EDEFAULT;
		seedESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, BpsimPackage.SCENARIO_PARAMETERS__SEED, oldSeed, SEED_EDEFAULT, oldSeedESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetSeed() {
		return seedESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BpsimPackage.SCENARIO_PARAMETERS__START:
				return basicSetStart(null, msgs);
			case BpsimPackage.SCENARIO_PARAMETERS__DURATION:
				return basicSetDuration(null, msgs);
			case BpsimPackage.SCENARIO_PARAMETERS__PROPERTY_PARAMETERS:
				return basicSetPropertyParameters(null, msgs);
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
			case BpsimPackage.SCENARIO_PARAMETERS__START:
				return getStart();
			case BpsimPackage.SCENARIO_PARAMETERS__DURATION:
				return getDuration();
			case BpsimPackage.SCENARIO_PARAMETERS__PROPERTY_PARAMETERS:
				return getPropertyParameters();
			case BpsimPackage.SCENARIO_PARAMETERS__BASE_CURRENCY_UNIT:
				return getBaseCurrencyUnit();
			case BpsimPackage.SCENARIO_PARAMETERS__BASE_TIME_UNIT:
				return getBaseTimeUnit();
			case BpsimPackage.SCENARIO_PARAMETERS__REPLICATION:
				return getReplication();
			case BpsimPackage.SCENARIO_PARAMETERS__SEED:
				return getSeed();
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
			case BpsimPackage.SCENARIO_PARAMETERS__START:
				setStart((Parameter)newValue);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__DURATION:
				setDuration((Parameter)newValue);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__PROPERTY_PARAMETERS:
				setPropertyParameters((PropertyParameters)newValue);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__BASE_CURRENCY_UNIT:
				setBaseCurrencyUnit((String)newValue);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__BASE_TIME_UNIT:
				setBaseTimeUnit((TimeUnit)newValue);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__REPLICATION:
				setReplication((Integer)newValue);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__SEED:
				setSeed((Long)newValue);
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
			case BpsimPackage.SCENARIO_PARAMETERS__START:
				setStart((Parameter)null);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__DURATION:
				setDuration((Parameter)null);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__PROPERTY_PARAMETERS:
				setPropertyParameters((PropertyParameters)null);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__BASE_CURRENCY_UNIT:
				setBaseCurrencyUnit(BASE_CURRENCY_UNIT_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__BASE_TIME_UNIT:
				unsetBaseTimeUnit();
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__REPLICATION:
				unsetReplication();
				return;
			case BpsimPackage.SCENARIO_PARAMETERS__SEED:
				unsetSeed();
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
			case BpsimPackage.SCENARIO_PARAMETERS__START:
				return start != null;
			case BpsimPackage.SCENARIO_PARAMETERS__DURATION:
				return duration != null;
			case BpsimPackage.SCENARIO_PARAMETERS__PROPERTY_PARAMETERS:
				return propertyParameters != null;
			case BpsimPackage.SCENARIO_PARAMETERS__BASE_CURRENCY_UNIT:
				return BASE_CURRENCY_UNIT_EDEFAULT == null ? baseCurrencyUnit != null : !BASE_CURRENCY_UNIT_EDEFAULT.equals(baseCurrencyUnit);
			case BpsimPackage.SCENARIO_PARAMETERS__BASE_TIME_UNIT:
				return isSetBaseTimeUnit();
			case BpsimPackage.SCENARIO_PARAMETERS__REPLICATION:
				return isSetReplication();
			case BpsimPackage.SCENARIO_PARAMETERS__SEED:
				return isSetSeed();
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
		result.append(" (baseCurrencyUnit: ");
		result.append(baseCurrencyUnit);
		result.append(", baseTimeUnit: ");
		if (baseTimeUnitESet) result.append(baseTimeUnit); else result.append("<unset>");
		result.append(", replication: ");
		if (replicationESet) result.append(replication); else result.append("<unset>");
		result.append(", seed: ");
		if (seedESet) result.append(seed); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //ScenarioParametersImpl
