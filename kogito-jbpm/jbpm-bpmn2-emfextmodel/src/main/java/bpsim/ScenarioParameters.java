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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Scenario Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.ScenarioParameters#getStart <em>Start</em>}</li>
 *   <li>{@link bpsim.ScenarioParameters#getDuration <em>Duration</em>}</li>
 *   <li>{@link bpsim.ScenarioParameters#getPropertyParameters <em>Property Parameters</em>}</li>
 *   <li>{@link bpsim.ScenarioParameters#getBaseCurrencyUnit <em>Base Currency Unit</em>}</li>
 *   <li>{@link bpsim.ScenarioParameters#getBaseTimeUnit <em>Base Time Unit</em>}</li>
 *   <li>{@link bpsim.ScenarioParameters#getReplication <em>Replication</em>}</li>
 *   <li>{@link bpsim.ScenarioParameters#getSeed <em>Seed</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpsim.BpsimPackage#getScenarioParameters()
 * @model extendedMetaData="name='ScenarioParameters' kind='elementOnly'"
 * @generated
 */
public interface ScenarioParameters extends EObject {
	/**
	 * Returns the value of the '<em><b>Start</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Start</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Start</em>' containment reference.
	 * @see #setStart(Parameter)
	 * @see bpsim.BpsimPackage#getScenarioParameters_Start()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Start' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getStart();

	/**
	 * Sets the value of the '{@link bpsim.ScenarioParameters#getStart <em>Start</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Start</em>' containment reference.
	 * @see #getStart()
	 * @generated
	 */
	void setStart(Parameter value);

	/**
	 * Returns the value of the '<em><b>Duration</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Duration</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Duration</em>' containment reference.
	 * @see #setDuration(Parameter)
	 * @see bpsim.BpsimPackage#getScenarioParameters_Duration()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='Duration' namespace='##targetNamespace'"
	 * @generated
	 */
	Parameter getDuration();

	/**
	 * Sets the value of the '{@link bpsim.ScenarioParameters#getDuration <em>Duration</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Duration</em>' containment reference.
	 * @see #getDuration()
	 * @generated
	 */
	void setDuration(Parameter value);

	/**
	 * Returns the value of the '<em><b>Property Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Property Parameters</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property Parameters</em>' containment reference.
	 * @see #setPropertyParameters(PropertyParameters)
	 * @see bpsim.BpsimPackage#getScenarioParameters_PropertyParameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='PropertyParameters' namespace='##targetNamespace'"
	 * @generated
	 */
	PropertyParameters getPropertyParameters();

	/**
	 * Sets the value of the '{@link bpsim.ScenarioParameters#getPropertyParameters <em>Property Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Property Parameters</em>' containment reference.
	 * @see #getPropertyParameters()
	 * @generated
	 */
	void setPropertyParameters(PropertyParameters value);

	/**
	 * Returns the value of the '<em><b>Base Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Base Currency Unit</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Base Currency Unit</em>' attribute.
	 * @see #setBaseCurrencyUnit(String)
	 * @see bpsim.BpsimPackage#getScenarioParameters_BaseCurrencyUnit()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='baseCurrencyUnit'"
	 * @generated
	 */
	String getBaseCurrencyUnit();

	/**
	 * Sets the value of the '{@link bpsim.ScenarioParameters#getBaseCurrencyUnit <em>Base Currency Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Base Currency Unit</em>' attribute.
	 * @see #getBaseCurrencyUnit()
	 * @generated
	 */
	void setBaseCurrencyUnit(String value);

	/**
	 * Returns the value of the '<em><b>Base Time Unit</b></em>' attribute.
	 * The literals are from the enumeration {@link bpsim.TimeUnit}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Base Time Unit</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Base Time Unit</em>' attribute.
	 * @see bpsim.TimeUnit
	 * @see #isSetBaseTimeUnit()
	 * @see #unsetBaseTimeUnit()
	 * @see #setBaseTimeUnit(TimeUnit)
	 * @see bpsim.BpsimPackage#getScenarioParameters_BaseTimeUnit()
	 * @model unsettable="true"
	 *        extendedMetaData="kind='attribute' name='baseTimeUnit'"
	 * @generated
	 */
	TimeUnit getBaseTimeUnit();

	/**
	 * Sets the value of the '{@link bpsim.ScenarioParameters#getBaseTimeUnit <em>Base Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Base Time Unit</em>' attribute.
	 * @see bpsim.TimeUnit
	 * @see #isSetBaseTimeUnit()
	 * @see #unsetBaseTimeUnit()
	 * @see #getBaseTimeUnit()
	 * @generated
	 */
	void setBaseTimeUnit(TimeUnit value);

	/**
	 * Unsets the value of the '{@link bpsim.ScenarioParameters#getBaseTimeUnit <em>Base Time Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetBaseTimeUnit()
	 * @see #getBaseTimeUnit()
	 * @see #setBaseTimeUnit(TimeUnit)
	 * @generated
	 */
	void unsetBaseTimeUnit();

	/**
	 * Returns whether the value of the '{@link bpsim.ScenarioParameters#getBaseTimeUnit <em>Base Time Unit</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Base Time Unit</em>' attribute is set.
	 * @see #unsetBaseTimeUnit()
	 * @see #getBaseTimeUnit()
	 * @see #setBaseTimeUnit(TimeUnit)
	 * @generated
	 */
	boolean isSetBaseTimeUnit();

	/**
	 * Returns the value of the '<em><b>Replication</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Replication</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Replication</em>' attribute.
	 * @see #isSetReplication()
	 * @see #unsetReplication()
	 * @see #setReplication(int)
	 * @see bpsim.BpsimPackage#getScenarioParameters_Replication()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='attribute' name='replication'"
	 * @generated
	 */
	int getReplication();

	/**
	 * Sets the value of the '{@link bpsim.ScenarioParameters#getReplication <em>Replication</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Replication</em>' attribute.
	 * @see #isSetReplication()
	 * @see #unsetReplication()
	 * @see #getReplication()
	 * @generated
	 */
	void setReplication(int value);

	/**
	 * Unsets the value of the '{@link bpsim.ScenarioParameters#getReplication <em>Replication</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetReplication()
	 * @see #getReplication()
	 * @see #setReplication(int)
	 * @generated
	 */
	void unsetReplication();

	/**
	 * Returns whether the value of the '{@link bpsim.ScenarioParameters#getReplication <em>Replication</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Replication</em>' attribute is set.
	 * @see #unsetReplication()
	 * @see #getReplication()
	 * @see #setReplication(int)
	 * @generated
	 */
	boolean isSetReplication();

	/**
	 * Returns the value of the '<em><b>Seed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Seed</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Seed</em>' attribute.
	 * @see #isSetSeed()
	 * @see #unsetSeed()
	 * @see #setSeed(long)
	 * @see bpsim.BpsimPackage#getScenarioParameters_Seed()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Long"
	 *        extendedMetaData="kind='attribute' name='seed'"
	 * @generated
	 */
	long getSeed();

	/**
	 * Sets the value of the '{@link bpsim.ScenarioParameters#getSeed <em>Seed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Seed</em>' attribute.
	 * @see #isSetSeed()
	 * @see #unsetSeed()
	 * @see #getSeed()
	 * @generated
	 */
	void setSeed(long value);

	/**
	 * Unsets the value of the '{@link bpsim.ScenarioParameters#getSeed <em>Seed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSeed()
	 * @see #getSeed()
	 * @see #setSeed(long)
	 * @generated
	 */
	void unsetSeed();

	/**
	 * Returns whether the value of the '{@link bpsim.ScenarioParameters#getSeed <em>Seed</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Seed</em>' attribute is set.
	 * @see #unsetSeed()
	 * @see #getSeed()
	 * @see #setSeed(long)
	 * @generated
	 */
	boolean isSetSeed();

} // ScenarioParameters
