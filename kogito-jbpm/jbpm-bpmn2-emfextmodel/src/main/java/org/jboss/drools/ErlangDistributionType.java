/**
 */
package org.jboss.drools;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Erlang Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jboss.drools.ErlangDistributionType#getK <em>K</em>}</li>
 *   <li>{@link org.jboss.drools.ErlangDistributionType#getMean <em>Mean</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getErlangDistributionType()
 * @model extendedMetaData="name='ErlangDistribution_._type' kind='empty'"
 * @generated
 */
public interface ErlangDistributionType extends DistributionParameter {
	/**
	 * Returns the value of the '<em><b>K</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>K</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>K</em>' attribute.
	 * @see #isSetK()
	 * @see #unsetK()
	 * @see #setK(double)
	 * @see org.jboss.drools.DroolsPackage#getErlangDistributionType_K()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='k'"
	 * @generated
	 */
	double getK();

	/**
	 * Sets the value of the '{@link org.jboss.drools.ErlangDistributionType#getK <em>K</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>K</em>' attribute.
	 * @see #isSetK()
	 * @see #unsetK()
	 * @see #getK()
	 * @generated
	 */
	void setK(double value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.ErlangDistributionType#getK <em>K</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetK()
	 * @see #getK()
	 * @see #setK(double)
	 * @generated
	 */
	void unsetK();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.ErlangDistributionType#getK <em>K</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>K</em>' attribute is set.
	 * @see #unsetK()
	 * @see #getK()
	 * @see #setK(double)
	 * @generated
	 */
	boolean isSetK();

	/**
	 * Returns the value of the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mean</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mean</em>' attribute.
	 * @see #isSetMean()
	 * @see #unsetMean()
	 * @see #setMean(double)
	 * @see org.jboss.drools.DroolsPackage#getErlangDistributionType_Mean()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='mean'"
	 * @generated
	 */
	double getMean();

	/**
	 * Sets the value of the '{@link org.jboss.drools.ErlangDistributionType#getMean <em>Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mean</em>' attribute.
	 * @see #isSetMean()
	 * @see #unsetMean()
	 * @see #getMean()
	 * @generated
	 */
	void setMean(double value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.ErlangDistributionType#getMean <em>Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMean()
	 * @see #getMean()
	 * @see #setMean(double)
	 * @generated
	 */
	void unsetMean();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.ErlangDistributionType#getMean <em>Mean</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Mean</em>' attribute is set.
	 * @see #unsetMean()
	 * @see #getMean()
	 * @see #setMean(double)
	 * @generated
	 */
	boolean isSetMean();

} // ErlangDistributionType
