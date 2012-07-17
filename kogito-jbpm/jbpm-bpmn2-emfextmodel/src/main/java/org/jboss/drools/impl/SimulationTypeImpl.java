/**
 */
package org.jboss.drools.impl;

import java.math.BigInteger;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.jboss.drools.DroolsPackage;
import org.jboss.drools.SimulationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Simulation Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.drools.impl.SimulationTypeImpl#getCostpertimeunit <em>Costpertimeunit</em>}</li>
 *   <li>{@link org.jboss.drools.impl.SimulationTypeImpl#getDistributiontype <em>Distributiontype</em>}</li>
 *   <li>{@link org.jboss.drools.impl.SimulationTypeImpl#getDuration <em>Duration</em>}</li>
 *   <li>{@link org.jboss.drools.impl.SimulationTypeImpl#getProbability <em>Probability</em>}</li>
 *   <li>{@link org.jboss.drools.impl.SimulationTypeImpl#getRange <em>Range</em>}</li>
 *   <li>{@link org.jboss.drools.impl.SimulationTypeImpl#getStaffavailability <em>Staffavailability</em>}</li>
 *   <li>{@link org.jboss.drools.impl.SimulationTypeImpl#getStandarddeviation <em>Standarddeviation</em>}</li>
 *   <li>{@link org.jboss.drools.impl.SimulationTypeImpl#getTimeunit <em>Timeunit</em>}</li>
 *   <li>{@link org.jboss.drools.impl.SimulationTypeImpl#getWorkinghours <em>Workinghours</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SimulationTypeImpl extends EObjectImpl implements SimulationType {
	/**
	 * The default value of the '{@link #getCostpertimeunit() <em>Costpertimeunit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCostpertimeunit()
	 * @generated
	 * @ordered
	 */
	protected static final double COSTPERTIMEUNIT_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getCostpertimeunit() <em>Costpertimeunit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCostpertimeunit()
	 * @generated
	 * @ordered
	 */
	protected double costpertimeunit = COSTPERTIMEUNIT_EDEFAULT;

	/**
	 * This is true if the Costpertimeunit attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean costpertimeunitESet;

	/**
	 * The default value of the '{@link #getDistributiontype() <em>Distributiontype</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDistributiontype()
	 * @generated
	 * @ordered
	 */
	protected static final String DISTRIBUTIONTYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDistributiontype() <em>Distributiontype</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDistributiontype()
	 * @generated
	 * @ordered
	 */
	protected String distributiontype = DISTRIBUTIONTYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected static final BigInteger DURATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected BigInteger duration = DURATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getProbability() <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProbability()
	 * @generated
	 * @ordered
	 */
	protected static final BigInteger PROBABILITY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProbability() <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProbability()
	 * @generated
	 * @ordered
	 */
	protected BigInteger probability = PROBABILITY_EDEFAULT;

	/**
	 * The default value of the '{@link #getRange() <em>Range</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRange()
	 * @generated
	 * @ordered
	 */
	protected static final BigInteger RANGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRange() <em>Range</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRange()
	 * @generated
	 * @ordered
	 */
	protected BigInteger range = RANGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getStaffavailability() <em>Staffavailability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStaffavailability()
	 * @generated
	 * @ordered
	 */
	protected static final double STAFFAVAILABILITY_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getStaffavailability() <em>Staffavailability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStaffavailability()
	 * @generated
	 * @ordered
	 */
	protected double staffavailability = STAFFAVAILABILITY_EDEFAULT;

	/**
	 * This is true if the Staffavailability attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean staffavailabilityESet;

	/**
	 * The default value of the '{@link #getStandarddeviation() <em>Standarddeviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStandarddeviation()
	 * @generated
	 * @ordered
	 */
	protected static final double STANDARDDEVIATION_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getStandarddeviation() <em>Standarddeviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStandarddeviation()
	 * @generated
	 * @ordered
	 */
	protected double standarddeviation = STANDARDDEVIATION_EDEFAULT;

	/**
	 * This is true if the Standarddeviation attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean standarddeviationESet;

	/**
	 * The default value of the '{@link #getTimeunit() <em>Timeunit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimeunit()
	 * @generated
	 * @ordered
	 */
	protected static final String TIMEUNIT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTimeunit() <em>Timeunit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimeunit()
	 * @generated
	 * @ordered
	 */
	protected String timeunit = TIMEUNIT_EDEFAULT;

	/**
	 * The default value of the '{@link #getWorkinghours() <em>Workinghours</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWorkinghours()
	 * @generated
	 * @ordered
	 */
	protected static final double WORKINGHOURS_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getWorkinghours() <em>Workinghours</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWorkinghours()
	 * @generated
	 * @ordered
	 */
	protected double workinghours = WORKINGHOURS_EDEFAULT;

	/**
	 * This is true if the Workinghours attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean workinghoursESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SimulationTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return DroolsPackage.Literals.SIMULATION_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getCostpertimeunit() {
		return costpertimeunit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCostpertimeunit(double newCostpertimeunit) {
		double oldCostpertimeunit = costpertimeunit;
		costpertimeunit = newCostpertimeunit;
		boolean oldCostpertimeunitESet = costpertimeunitESet;
		costpertimeunitESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.SIMULATION_TYPE__COSTPERTIMEUNIT, oldCostpertimeunit, costpertimeunit, !oldCostpertimeunitESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetCostpertimeunit() {
		double oldCostpertimeunit = costpertimeunit;
		boolean oldCostpertimeunitESet = costpertimeunitESet;
		costpertimeunit = COSTPERTIMEUNIT_EDEFAULT;
		costpertimeunitESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DroolsPackage.SIMULATION_TYPE__COSTPERTIMEUNIT, oldCostpertimeunit, COSTPERTIMEUNIT_EDEFAULT, oldCostpertimeunitESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetCostpertimeunit() {
		return costpertimeunitESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDistributiontype() {
		return distributiontype;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDistributiontype(String newDistributiontype) {
		String oldDistributiontype = distributiontype;
		distributiontype = newDistributiontype;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.SIMULATION_TYPE__DISTRIBUTIONTYPE, oldDistributiontype, distributiontype));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BigInteger getDuration() {
		return duration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDuration(BigInteger newDuration) {
		BigInteger oldDuration = duration;
		duration = newDuration;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.SIMULATION_TYPE__DURATION, oldDuration, duration));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BigInteger getProbability() {
		return probability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProbability(BigInteger newProbability) {
		BigInteger oldProbability = probability;
		probability = newProbability;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.SIMULATION_TYPE__PROBABILITY, oldProbability, probability));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BigInteger getRange() {
		return range;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRange(BigInteger newRange) {
		BigInteger oldRange = range;
		range = newRange;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.SIMULATION_TYPE__RANGE, oldRange, range));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getStaffavailability() {
		return staffavailability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStaffavailability(double newStaffavailability) {
		double oldStaffavailability = staffavailability;
		staffavailability = newStaffavailability;
		boolean oldStaffavailabilityESet = staffavailabilityESet;
		staffavailabilityESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.SIMULATION_TYPE__STAFFAVAILABILITY, oldStaffavailability, staffavailability, !oldStaffavailabilityESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetStaffavailability() {
		double oldStaffavailability = staffavailability;
		boolean oldStaffavailabilityESet = staffavailabilityESet;
		staffavailability = STAFFAVAILABILITY_EDEFAULT;
		staffavailabilityESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DroolsPackage.SIMULATION_TYPE__STAFFAVAILABILITY, oldStaffavailability, STAFFAVAILABILITY_EDEFAULT, oldStaffavailabilityESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetStaffavailability() {
		return staffavailabilityESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getStandarddeviation() {
		return standarddeviation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStandarddeviation(double newStandarddeviation) {
		double oldStandarddeviation = standarddeviation;
		standarddeviation = newStandarddeviation;
		boolean oldStandarddeviationESet = standarddeviationESet;
		standarddeviationESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.SIMULATION_TYPE__STANDARDDEVIATION, oldStandarddeviation, standarddeviation, !oldStandarddeviationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetStandarddeviation() {
		double oldStandarddeviation = standarddeviation;
		boolean oldStandarddeviationESet = standarddeviationESet;
		standarddeviation = STANDARDDEVIATION_EDEFAULT;
		standarddeviationESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DroolsPackage.SIMULATION_TYPE__STANDARDDEVIATION, oldStandarddeviation, STANDARDDEVIATION_EDEFAULT, oldStandarddeviationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetStandarddeviation() {
		return standarddeviationESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTimeunit() {
		return timeunit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTimeunit(String newTimeunit) {
		String oldTimeunit = timeunit;
		timeunit = newTimeunit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.SIMULATION_TYPE__TIMEUNIT, oldTimeunit, timeunit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getWorkinghours() {
		return workinghours;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWorkinghours(double newWorkinghours) {
		double oldWorkinghours = workinghours;
		workinghours = newWorkinghours;
		boolean oldWorkinghoursESet = workinghoursESet;
		workinghoursESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.SIMULATION_TYPE__WORKINGHOURS, oldWorkinghours, workinghours, !oldWorkinghoursESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetWorkinghours() {
		double oldWorkinghours = workinghours;
		boolean oldWorkinghoursESet = workinghoursESet;
		workinghours = WORKINGHOURS_EDEFAULT;
		workinghoursESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, DroolsPackage.SIMULATION_TYPE__WORKINGHOURS, oldWorkinghours, WORKINGHOURS_EDEFAULT, oldWorkinghoursESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetWorkinghours() {
		return workinghoursESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DroolsPackage.SIMULATION_TYPE__COSTPERTIMEUNIT:
				return new Double(getCostpertimeunit());
			case DroolsPackage.SIMULATION_TYPE__DISTRIBUTIONTYPE:
				return getDistributiontype();
			case DroolsPackage.SIMULATION_TYPE__DURATION:
				return getDuration();
			case DroolsPackage.SIMULATION_TYPE__PROBABILITY:
				return getProbability();
			case DroolsPackage.SIMULATION_TYPE__RANGE:
				return getRange();
			case DroolsPackage.SIMULATION_TYPE__STAFFAVAILABILITY:
				return new Double(getStaffavailability());
			case DroolsPackage.SIMULATION_TYPE__STANDARDDEVIATION:
				return new Double(getStandarddeviation());
			case DroolsPackage.SIMULATION_TYPE__TIMEUNIT:
				return getTimeunit();
			case DroolsPackage.SIMULATION_TYPE__WORKINGHOURS:
				return new Double(getWorkinghours());
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DroolsPackage.SIMULATION_TYPE__COSTPERTIMEUNIT:
				setCostpertimeunit(((Double)newValue).doubleValue());
				return;
			case DroolsPackage.SIMULATION_TYPE__DISTRIBUTIONTYPE:
				setDistributiontype((String)newValue);
				return;
			case DroolsPackage.SIMULATION_TYPE__DURATION:
				setDuration((BigInteger)newValue);
				return;
			case DroolsPackage.SIMULATION_TYPE__PROBABILITY:
				setProbability((BigInteger)newValue);
				return;
			case DroolsPackage.SIMULATION_TYPE__RANGE:
				setRange((BigInteger)newValue);
				return;
			case DroolsPackage.SIMULATION_TYPE__STAFFAVAILABILITY:
				setStaffavailability(((Double)newValue).doubleValue());
				return;
			case DroolsPackage.SIMULATION_TYPE__STANDARDDEVIATION:
				setStandarddeviation(((Double)newValue).doubleValue());
				return;
			case DroolsPackage.SIMULATION_TYPE__TIMEUNIT:
				setTimeunit((String)newValue);
				return;
			case DroolsPackage.SIMULATION_TYPE__WORKINGHOURS:
				setWorkinghours(((Double)newValue).doubleValue());
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case DroolsPackage.SIMULATION_TYPE__COSTPERTIMEUNIT:
				unsetCostpertimeunit();
				return;
			case DroolsPackage.SIMULATION_TYPE__DISTRIBUTIONTYPE:
				setDistributiontype(DISTRIBUTIONTYPE_EDEFAULT);
				return;
			case DroolsPackage.SIMULATION_TYPE__DURATION:
				setDuration(DURATION_EDEFAULT);
				return;
			case DroolsPackage.SIMULATION_TYPE__PROBABILITY:
				setProbability(PROBABILITY_EDEFAULT);
				return;
			case DroolsPackage.SIMULATION_TYPE__RANGE:
				setRange(RANGE_EDEFAULT);
				return;
			case DroolsPackage.SIMULATION_TYPE__STAFFAVAILABILITY:
				unsetStaffavailability();
				return;
			case DroolsPackage.SIMULATION_TYPE__STANDARDDEVIATION:
				unsetStandarddeviation();
				return;
			case DroolsPackage.SIMULATION_TYPE__TIMEUNIT:
				setTimeunit(TIMEUNIT_EDEFAULT);
				return;
			case DroolsPackage.SIMULATION_TYPE__WORKINGHOURS:
				unsetWorkinghours();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DroolsPackage.SIMULATION_TYPE__COSTPERTIMEUNIT:
				return isSetCostpertimeunit();
			case DroolsPackage.SIMULATION_TYPE__DISTRIBUTIONTYPE:
				return DISTRIBUTIONTYPE_EDEFAULT == null ? distributiontype != null : !DISTRIBUTIONTYPE_EDEFAULT.equals(distributiontype);
			case DroolsPackage.SIMULATION_TYPE__DURATION:
				return DURATION_EDEFAULT == null ? duration != null : !DURATION_EDEFAULT.equals(duration);
			case DroolsPackage.SIMULATION_TYPE__PROBABILITY:
				return PROBABILITY_EDEFAULT == null ? probability != null : !PROBABILITY_EDEFAULT.equals(probability);
			case DroolsPackage.SIMULATION_TYPE__RANGE:
				return RANGE_EDEFAULT == null ? range != null : !RANGE_EDEFAULT.equals(range);
			case DroolsPackage.SIMULATION_TYPE__STAFFAVAILABILITY:
				return isSetStaffavailability();
			case DroolsPackage.SIMULATION_TYPE__STANDARDDEVIATION:
				return isSetStandarddeviation();
			case DroolsPackage.SIMULATION_TYPE__TIMEUNIT:
				return TIMEUNIT_EDEFAULT == null ? timeunit != null : !TIMEUNIT_EDEFAULT.equals(timeunit);
			case DroolsPackage.SIMULATION_TYPE__WORKINGHOURS:
				return isSetWorkinghours();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (costpertimeunit: ");
		if (costpertimeunitESet) result.append(costpertimeunit); else result.append("<unset>");
		result.append(", distributiontype: ");
		result.append(distributiontype);
		result.append(", duration: ");
		result.append(duration);
		result.append(", probability: ");
		result.append(probability);
		result.append(", range: ");
		result.append(range);
		result.append(", staffavailability: ");
		if (staffavailabilityESet) result.append(staffavailability); else result.append("<unset>");
		result.append(", standarddeviation: ");
		if (standarddeviationESet) result.append(standarddeviation); else result.append("<unset>");
		result.append(", timeunit: ");
		result.append(timeunit);
		result.append(", workinghours: ");
		if (workinghoursESet) result.append(workinghours); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}

} //SimulationTypeImpl
