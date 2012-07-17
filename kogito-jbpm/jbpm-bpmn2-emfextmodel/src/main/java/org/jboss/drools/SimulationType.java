/**
 */
package org.jboss.drools;

import java.math.BigInteger;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Simulation Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jboss.drools.SimulationType#getCostpertimeunit <em>Costpertimeunit</em>}</li>
 *   <li>{@link org.jboss.drools.SimulationType#getDistributiontype <em>Distributiontype</em>}</li>
 *   <li>{@link org.jboss.drools.SimulationType#getDuration <em>Duration</em>}</li>
 *   <li>{@link org.jboss.drools.SimulationType#getProbability <em>Probability</em>}</li>
 *   <li>{@link org.jboss.drools.SimulationType#getRange <em>Range</em>}</li>
 *   <li>{@link org.jboss.drools.SimulationType#getStaffavailability <em>Staffavailability</em>}</li>
 *   <li>{@link org.jboss.drools.SimulationType#getStandarddeviation <em>Standarddeviation</em>}</li>
 *   <li>{@link org.jboss.drools.SimulationType#getTimeunit <em>Timeunit</em>}</li>
 *   <li>{@link org.jboss.drools.SimulationType#getWorkinghours <em>Workinghours</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getSimulationType()
 * @model extendedMetaData="name='simulation_._type' kind='empty'"
 * @generated
 */
public interface SimulationType extends EObject {
	/**
	 * Returns the value of the '<em><b>Costpertimeunit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Costpertimeunit</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Costpertimeunit</em>' attribute.
	 * @see #isSetCostpertimeunit()
	 * @see #unsetCostpertimeunit()
	 * @see #setCostpertimeunit(double)
	 * @see org.jboss.drools.DroolsPackage#getSimulationType_Costpertimeunit()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='costpertimeunit'"
	 * @generated
	 */
	double getCostpertimeunit();

	/**
	 * Sets the value of the '{@link org.jboss.drools.SimulationType#getCostpertimeunit <em>Costpertimeunit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Costpertimeunit</em>' attribute.
	 * @see #isSetCostpertimeunit()
	 * @see #unsetCostpertimeunit()
	 * @see #getCostpertimeunit()
	 * @generated
	 */
	void setCostpertimeunit(double value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.SimulationType#getCostpertimeunit <em>Costpertimeunit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetCostpertimeunit()
	 * @see #getCostpertimeunit()
	 * @see #setCostpertimeunit(double)
	 * @generated
	 */
	void unsetCostpertimeunit();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.SimulationType#getCostpertimeunit <em>Costpertimeunit</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Costpertimeunit</em>' attribute is set.
	 * @see #unsetCostpertimeunit()
	 * @see #getCostpertimeunit()
	 * @see #setCostpertimeunit(double)
	 * @generated
	 */
	boolean isSetCostpertimeunit();

	/**
	 * Returns the value of the '<em><b>Distributiontype</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Distributiontype</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Distributiontype</em>' attribute.
	 * @see #setDistributiontype(String)
	 * @see org.jboss.drools.DroolsPackage#getSimulationType_Distributiontype()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='distributiontype'"
	 * @generated
	 */
	String getDistributiontype();

	/**
	 * Sets the value of the '{@link org.jboss.drools.SimulationType#getDistributiontype <em>Distributiontype</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Distributiontype</em>' attribute.
	 * @see #getDistributiontype()
	 * @generated
	 */
	void setDistributiontype(String value);

	/**
	 * Returns the value of the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Duration</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Duration</em>' attribute.
	 * @see #setDuration(BigInteger)
	 * @see org.jboss.drools.DroolsPackage#getSimulationType_Duration()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.Integer"
	 *        extendedMetaData="kind='attribute' name='duration'"
	 * @generated
	 */
	BigInteger getDuration();

	/**
	 * Sets the value of the '{@link org.jboss.drools.SimulationType#getDuration <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Duration</em>' attribute.
	 * @see #getDuration()
	 * @generated
	 */
	void setDuration(BigInteger value);

	/**
	 * Returns the value of the '<em><b>Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Probability</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Probability</em>' attribute.
	 * @see #setProbability(BigInteger)
	 * @see org.jboss.drools.DroolsPackage#getSimulationType_Probability()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.Integer"
	 *        extendedMetaData="kind='attribute' name='probability'"
	 * @generated
	 */
	BigInteger getProbability();

	/**
	 * Sets the value of the '{@link org.jboss.drools.SimulationType#getProbability <em>Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Probability</em>' attribute.
	 * @see #getProbability()
	 * @generated
	 */
	void setProbability(BigInteger value);

	/**
	 * Returns the value of the '<em><b>Range</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Range</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Range</em>' attribute.
	 * @see #setRange(BigInteger)
	 * @see org.jboss.drools.DroolsPackage#getSimulationType_Range()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.Integer"
	 *        extendedMetaData="kind='attribute' name='range'"
	 * @generated
	 */
	BigInteger getRange();

	/**
	 * Sets the value of the '{@link org.jboss.drools.SimulationType#getRange <em>Range</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Range</em>' attribute.
	 * @see #getRange()
	 * @generated
	 */
	void setRange(BigInteger value);

	/**
	 * Returns the value of the '<em><b>Staffavailability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Staffavailability</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Staffavailability</em>' attribute.
	 * @see #isSetStaffavailability()
	 * @see #unsetStaffavailability()
	 * @see #setStaffavailability(double)
	 * @see org.jboss.drools.DroolsPackage#getSimulationType_Staffavailability()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='staffavailability'"
	 * @generated
	 */
	double getStaffavailability();

	/**
	 * Sets the value of the '{@link org.jboss.drools.SimulationType#getStaffavailability <em>Staffavailability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Staffavailability</em>' attribute.
	 * @see #isSetStaffavailability()
	 * @see #unsetStaffavailability()
	 * @see #getStaffavailability()
	 * @generated
	 */
	void setStaffavailability(double value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.SimulationType#getStaffavailability <em>Staffavailability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetStaffavailability()
	 * @see #getStaffavailability()
	 * @see #setStaffavailability(double)
	 * @generated
	 */
	void unsetStaffavailability();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.SimulationType#getStaffavailability <em>Staffavailability</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Staffavailability</em>' attribute is set.
	 * @see #unsetStaffavailability()
	 * @see #getStaffavailability()
	 * @see #setStaffavailability(double)
	 * @generated
	 */
	boolean isSetStaffavailability();

	/**
	 * Returns the value of the '<em><b>Standarddeviation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Standarddeviation</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Standarddeviation</em>' attribute.
	 * @see #isSetStandarddeviation()
	 * @see #unsetStandarddeviation()
	 * @see #setStandarddeviation(double)
	 * @see org.jboss.drools.DroolsPackage#getSimulationType_Standarddeviation()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='standarddeviation'"
	 * @generated
	 */
	double getStandarddeviation();

	/**
	 * Sets the value of the '{@link org.jboss.drools.SimulationType#getStandarddeviation <em>Standarddeviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Standarddeviation</em>' attribute.
	 * @see #isSetStandarddeviation()
	 * @see #unsetStandarddeviation()
	 * @see #getStandarddeviation()
	 * @generated
	 */
	void setStandarddeviation(double value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.SimulationType#getStandarddeviation <em>Standarddeviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetStandarddeviation()
	 * @see #getStandarddeviation()
	 * @see #setStandarddeviation(double)
	 * @generated
	 */
	void unsetStandarddeviation();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.SimulationType#getStandarddeviation <em>Standarddeviation</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Standarddeviation</em>' attribute is set.
	 * @see #unsetStandarddeviation()
	 * @see #getStandarddeviation()
	 * @see #setStandarddeviation(double)
	 * @generated
	 */
	boolean isSetStandarddeviation();

	/**
	 * Returns the value of the '<em><b>Timeunit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Timeunit</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Timeunit</em>' attribute.
	 * @see #setTimeunit(String)
	 * @see org.jboss.drools.DroolsPackage#getSimulationType_Timeunit()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='timeunit'"
	 * @generated
	 */
	String getTimeunit();

	/**
	 * Sets the value of the '{@link org.jboss.drools.SimulationType#getTimeunit <em>Timeunit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timeunit</em>' attribute.
	 * @see #getTimeunit()
	 * @generated
	 */
	void setTimeunit(String value);

	/**
	 * Returns the value of the '<em><b>Workinghours</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Workinghours</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Workinghours</em>' attribute.
	 * @see #isSetWorkinghours()
	 * @see #unsetWorkinghours()
	 * @see #setWorkinghours(double)
	 * @see org.jboss.drools.DroolsPackage#getSimulationType_Workinghours()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='workinghours'"
	 * @generated
	 */
	double getWorkinghours();

	/**
	 * Sets the value of the '{@link org.jboss.drools.SimulationType#getWorkinghours <em>Workinghours</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Workinghours</em>' attribute.
	 * @see #isSetWorkinghours()
	 * @see #unsetWorkinghours()
	 * @see #getWorkinghours()
	 * @generated
	 */
	void setWorkinghours(double value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.SimulationType#getWorkinghours <em>Workinghours</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetWorkinghours()
	 * @see #getWorkinghours()
	 * @see #setWorkinghours(double)
	 * @generated
	 */
	void unsetWorkinghours();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.SimulationType#getWorkinghours <em>Workinghours</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Workinghours</em>' attribute is set.
	 * @see #unsetWorkinghours()
	 * @see #getWorkinghours()
	 * @see #setWorkinghours(double)
	 * @generated
	 */
	boolean isSetWorkinghours();

} // SimulationType
