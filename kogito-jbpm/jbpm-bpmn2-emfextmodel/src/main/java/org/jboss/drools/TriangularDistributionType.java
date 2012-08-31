/**
 */
package org.jboss.drools;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Triangular Distribution Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jboss.drools.TriangularDistributionType#getMax <em>Max</em>}</li>
 *   <li>{@link org.jboss.drools.TriangularDistributionType#getMin <em>Min</em>}</li>
 *   <li>{@link org.jboss.drools.TriangularDistributionType#getMostLikely <em>Most Likely</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getTriangularDistributionType()
 * @model extendedMetaData="name='TriangularDistribution_._type' kind='empty'"
 * @generated
 */
public interface TriangularDistributionType extends DistributionParameter {
	/**
	 * Returns the value of the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Max</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Max</em>' attribute.
	 * @see #isSetMax()
	 * @see #unsetMax()
	 * @see #setMax(double)
	 * @see org.jboss.drools.DroolsPackage#getTriangularDistributionType_Max()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='max'"
	 * @generated
	 */
	double getMax();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TriangularDistributionType#getMax <em>Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max</em>' attribute.
	 * @see #isSetMax()
	 * @see #unsetMax()
	 * @see #getMax()
	 * @generated
	 */
	void setMax(double value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.TriangularDistributionType#getMax <em>Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMax()
	 * @see #getMax()
	 * @see #setMax(double)
	 * @generated
	 */
	void unsetMax();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.TriangularDistributionType#getMax <em>Max</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Max</em>' attribute is set.
	 * @see #unsetMax()
	 * @see #getMax()
	 * @see #setMax(double)
	 * @generated
	 */
	boolean isSetMax();

	/**
	 * Returns the value of the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Min</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min</em>' attribute.
	 * @see #isSetMin()
	 * @see #unsetMin()
	 * @see #setMin(double)
	 * @see org.jboss.drools.DroolsPackage#getTriangularDistributionType_Min()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='min'"
	 * @generated
	 */
	double getMin();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TriangularDistributionType#getMin <em>Min</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min</em>' attribute.
	 * @see #isSetMin()
	 * @see #unsetMin()
	 * @see #getMin()
	 * @generated
	 */
	void setMin(double value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.TriangularDistributionType#getMin <em>Min</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMin()
	 * @see #getMin()
	 * @see #setMin(double)
	 * @generated
	 */
	void unsetMin();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.TriangularDistributionType#getMin <em>Min</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Min</em>' attribute is set.
	 * @see #unsetMin()
	 * @see #getMin()
	 * @see #setMin(double)
	 * @generated
	 */
	boolean isSetMin();

	/**
	 * Returns the value of the '<em><b>Most Likely</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Most Likely</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Most Likely</em>' attribute.
	 * @see #isSetMostLikely()
	 * @see #unsetMostLikely()
	 * @see #setMostLikely(double)
	 * @see org.jboss.drools.DroolsPackage#getTriangularDistributionType_MostLikely()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double"
	 *        extendedMetaData="kind='attribute' name='mostLikely'"
	 * @generated
	 */
	double getMostLikely();

	/**
	 * Sets the value of the '{@link org.jboss.drools.TriangularDistributionType#getMostLikely <em>Most Likely</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Most Likely</em>' attribute.
	 * @see #isSetMostLikely()
	 * @see #unsetMostLikely()
	 * @see #getMostLikely()
	 * @generated
	 */
	void setMostLikely(double value);

	/**
	 * Unsets the value of the '{@link org.jboss.drools.TriangularDistributionType#getMostLikely <em>Most Likely</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMostLikely()
	 * @see #getMostLikely()
	 * @see #setMostLikely(double)
	 * @generated
	 */
	void unsetMostLikely();

	/**
	 * Returns whether the value of the '{@link org.jboss.drools.TriangularDistributionType#getMostLikely <em>Most Likely</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Most Likely</em>' attribute is set.
	 * @see #unsetMostLikely()
	 * @see #getMostLikely()
	 * @see #setMostLikely(double)
	 * @generated
	 */
	boolean isSetMostLikely();

} // TriangularDistributionType
