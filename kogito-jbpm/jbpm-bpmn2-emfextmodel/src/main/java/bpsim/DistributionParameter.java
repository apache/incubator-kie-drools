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
package bpsim;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Distribution Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.DistributionParameter#getCurrencyUnit <em>Currency Unit</em>}</li>
 *   <li>{@link bpsim.DistributionParameter#getTimeUnit <em>Time Unit</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpsim.BpsimPackage#getDistributionParameter()
 * @model extendedMetaData="name='DistributionParameter' kind='empty'"
 * @generated
 */
public interface DistributionParameter extends ParameterValue {
	/**
	 * Returns the value of the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Currency Unit</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Currency Unit</em>' attribute.
	 * @see #setCurrencyUnit(String)
	 * @see bpsim.BpsimPackage#getDistributionParameter_CurrencyUnit()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='currencyUnit'"
	 * @generated
	 */
	String getCurrencyUnit();

	/**
	 * Sets the value of the '{@link bpsim.DistributionParameter#getCurrencyUnit <em>Currency Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Currency Unit</em>' attribute.
	 * @see #getCurrencyUnit()
	 * @generated
	 */
	void setCurrencyUnit(String value);

	/**
	 * Returns the value of the '<em><b>Time Unit</b></em>' attribute.
	 * The literals are from the enumeration {@link bpsim.TimeUnit}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time Unit</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time Unit</em>' attribute.
	 * @see bpsim.TimeUnit
	 * @see #isSetTimeUnit()
	 * @see #unsetTimeUnit()
	 * @see #setTimeUnit(TimeUnit)
	 * @see bpsim.BpsimPackage#getDistributionParameter_TimeUnit()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='timeUnit'"
	 * @generated
	 */
	TimeUnit getTimeUnit();

	/**
	 * Sets the value of the '{@link bpsim.DistributionParameter#getTimeUnit <em>Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Time Unit</em>' attribute.
	 * @see bpsim.TimeUnit
	 * @see #isSetTimeUnit()
	 * @see #unsetTimeUnit()
	 * @see #getTimeUnit()
	 * @generated
	 */
	void setTimeUnit(TimeUnit value);

	/**
	 * Unsets the value of the '{@link bpsim.DistributionParameter#getTimeUnit <em>Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTimeUnit()
	 * @see #getTimeUnit()
	 * @see #setTimeUnit(TimeUnit)
	 * @generated
	 */
	void unsetTimeUnit();

	/**
	 * Returns whether the value of the '{@link bpsim.DistributionParameter#getTimeUnit <em>Time Unit</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Time Unit</em>' attribute is set.
	 * @see #unsetTimeUnit()
	 * @see #getTimeUnit()
	 * @see #setTimeUnit(TimeUnit)
	 * @generated
	 */
	boolean isSetTimeUnit();

} // DistributionParameter
