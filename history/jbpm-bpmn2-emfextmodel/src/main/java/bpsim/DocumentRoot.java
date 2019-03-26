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

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.DocumentRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getBetaDistribution <em>Beta Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getParameterValue <em>Parameter Value</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getBinomialDistribution <em>Binomial Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getBooleanParameter <em>Boolean Parameter</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getBPSimData <em>BP Sim Data</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getDateTimeParameter <em>Date Time Parameter</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getDurationParameter <em>Duration Parameter</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getEnumParameter <em>Enum Parameter</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getErlangDistribution <em>Erlang Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getExpressionParameter <em>Expression Parameter</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getFloatingParameter <em>Floating Parameter</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getGammaDistribution <em>Gamma Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getLogNormalDistribution <em>Log Normal Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getNegativeExponentialDistribution <em>Negative Exponential Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getNormalDistribution <em>Normal Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getNumericParameter <em>Numeric Parameter</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getPoissonDistribution <em>Poisson Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getStringParameter <em>String Parameter</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getTriangularDistribution <em>Triangular Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getTruncatedNormalDistribution <em>Truncated Normal Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getUniformDistribution <em>Uniform Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getUserDistribution <em>User Distribution</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getUserDistributionDataPoint <em>User Distribution Data Point</em>}</li>
 *   <li>{@link bpsim.DocumentRoot#getWeibullDistribution <em>Weibull Distribution</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpsim.BpsimPackage#getDocumentRoot()
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 */
public interface DocumentRoot extends EObject {
	/**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see bpsim.BpsimPackage#getDocumentRoot_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
	FeatureMap getMixed();

	/**
	 * Returns the value of the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>XMLNS Prefix Map</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>XMLNS Prefix Map</em>' map.
	 * @see bpsim.BpsimPackage#getDocumentRoot_XMLNSPrefixMap()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>" transient="true"
	 *        extendedMetaData="kind='attribute' name='xmlns:prefix'"
	 * @generated
	 */
	EMap<String, String> getXMLNSPrefixMap();

	/**
	 * Returns the value of the '<em><b>XSI Schema Location</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>XSI Schema Location</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>XSI Schema Location</em>' map.
	 * @see bpsim.BpsimPackage#getDocumentRoot_XSISchemaLocation()
	 * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>" transient="true"
	 *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
	 * @generated
	 */
	EMap<String, String> getXSISchemaLocation();

	/**
	 * Returns the value of the '<em><b>Beta Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Beta Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Beta Distribution</em>' containment reference.
	 * @see #setBetaDistribution(BetaDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_BetaDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='BetaDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	BetaDistributionType getBetaDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getBetaDistribution <em>Beta Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Beta Distribution</em>' containment reference.
	 * @see #getBetaDistribution()
	 * @generated
	 */
	void setBetaDistribution(BetaDistributionType value);

	/**
	 * Returns the value of the '<em><b>Parameter Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameter Value</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameter Value</em>' containment reference.
	 * @see #setParameterValue(ParameterValue)
	 * @see bpsim.BpsimPackage#getDocumentRoot_ParameterValue()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ParameterValue' namespace='##targetNamespace'"
	 * @generated
	 */
	ParameterValue getParameterValue();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getParameterValue <em>Parameter Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parameter Value</em>' containment reference.
	 * @see #getParameterValue()
	 * @generated
	 */
	void setParameterValue(ParameterValue value);

	/**
	 * Returns the value of the '<em><b>Binomial Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binomial Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Binomial Distribution</em>' containment reference.
	 * @see #setBinomialDistribution(BinomialDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_BinomialDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='BinomialDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	BinomialDistributionType getBinomialDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getBinomialDistribution <em>Binomial Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Binomial Distribution</em>' containment reference.
	 * @see #getBinomialDistribution()
	 * @generated
	 */
	void setBinomialDistribution(BinomialDistributionType value);

	/**
	 * Returns the value of the '<em><b>Boolean Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Boolean Parameter</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Boolean Parameter</em>' containment reference.
	 * @see #setBooleanParameter(BooleanParameterType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_BooleanParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='BooleanParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	BooleanParameterType getBooleanParameter();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getBooleanParameter <em>Boolean Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Boolean Parameter</em>' containment reference.
	 * @see #getBooleanParameter()
	 * @generated
	 */
	void setBooleanParameter(BooleanParameterType value);

	/**
	 * Returns the value of the '<em><b>BP Sim Data</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>BP Sim Data</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>BP Sim Data</em>' containment reference.
	 * @see #setBPSimData(BPSimDataType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_BPSimData()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='BPSimData' namespace='##targetNamespace'"
	 * @generated
	 */
	BPSimDataType getBPSimData();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getBPSimData <em>BP Sim Data</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>BP Sim Data</em>' containment reference.
	 * @see #getBPSimData()
	 * @generated
	 */
	void setBPSimData(BPSimDataType value);

	/**
	 * Returns the value of the '<em><b>Date Time Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Date Time Parameter</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Date Time Parameter</em>' containment reference.
	 * @see #setDateTimeParameter(DateTimeParameterType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_DateTimeParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='DateTimeParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	DateTimeParameterType getDateTimeParameter();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getDateTimeParameter <em>Date Time Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Date Time Parameter</em>' containment reference.
	 * @see #getDateTimeParameter()
	 * @generated
	 */
	void setDateTimeParameter(DateTimeParameterType value);

	/**
	 * Returns the value of the '<em><b>Duration Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Duration Parameter</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Duration Parameter</em>' containment reference.
	 * @see #setDurationParameter(DurationParameterType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_DurationParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='DurationParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	DurationParameterType getDurationParameter();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getDurationParameter <em>Duration Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Duration Parameter</em>' containment reference.
	 * @see #getDurationParameter()
	 * @generated
	 */
	void setDurationParameter(DurationParameterType value);

	/**
	 * Returns the value of the '<em><b>Enum Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Enum Parameter</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enum Parameter</em>' containment reference.
	 * @see #setEnumParameter(EnumParameterType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_EnumParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='EnumParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	EnumParameterType getEnumParameter();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getEnumParameter <em>Enum Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Enum Parameter</em>' containment reference.
	 * @see #getEnumParameter()
	 * @generated
	 */
	void setEnumParameter(EnumParameterType value);

	/**
	 * Returns the value of the '<em><b>Erlang Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Erlang Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Erlang Distribution</em>' containment reference.
	 * @see #setErlangDistribution(ErlangDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_ErlangDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ErlangDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	ErlangDistributionType getErlangDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getErlangDistribution <em>Erlang Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Erlang Distribution</em>' containment reference.
	 * @see #getErlangDistribution()
	 * @generated
	 */
	void setErlangDistribution(ErlangDistributionType value);

	/**
	 * Returns the value of the '<em><b>Expression Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Expression Parameter</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expression Parameter</em>' containment reference.
	 * @see #setExpressionParameter(ExpressionParameterType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_ExpressionParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ExpressionParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	ExpressionParameterType getExpressionParameter();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getExpressionParameter <em>Expression Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expression Parameter</em>' containment reference.
	 * @see #getExpressionParameter()
	 * @generated
	 */
	void setExpressionParameter(ExpressionParameterType value);

	/**
	 * Returns the value of the '<em><b>Floating Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Floating Parameter</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Floating Parameter</em>' containment reference.
	 * @see #setFloatingParameter(FloatingParameterType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_FloatingParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='FloatingParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	FloatingParameterType getFloatingParameter();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getFloatingParameter <em>Floating Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Floating Parameter</em>' containment reference.
	 * @see #getFloatingParameter()
	 * @generated
	 */
	void setFloatingParameter(FloatingParameterType value);

	/**
	 * Returns the value of the '<em><b>Gamma Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gamma Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gamma Distribution</em>' containment reference.
	 * @see #setGammaDistribution(GammaDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_GammaDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='GammaDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	GammaDistributionType getGammaDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getGammaDistribution <em>Gamma Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gamma Distribution</em>' containment reference.
	 * @see #getGammaDistribution()
	 * @generated
	 */
	void setGammaDistribution(GammaDistributionType value);

	/**
	 * Returns the value of the '<em><b>Log Normal Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Log Normal Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Log Normal Distribution</em>' containment reference.
	 * @see #setLogNormalDistribution(LogNormalDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_LogNormalDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='LogNormalDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	LogNormalDistributionType getLogNormalDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getLogNormalDistribution <em>Log Normal Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Log Normal Distribution</em>' containment reference.
	 * @see #getLogNormalDistribution()
	 * @generated
	 */
	void setLogNormalDistribution(LogNormalDistributionType value);

	/**
	 * Returns the value of the '<em><b>Negative Exponential Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Negative Exponential Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Negative Exponential Distribution</em>' containment reference.
	 * @see #setNegativeExponentialDistribution(NegativeExponentialDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_NegativeExponentialDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='NegativeExponentialDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	NegativeExponentialDistributionType getNegativeExponentialDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getNegativeExponentialDistribution <em>Negative Exponential Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Negative Exponential Distribution</em>' containment reference.
	 * @see #getNegativeExponentialDistribution()
	 * @generated
	 */
	void setNegativeExponentialDistribution(NegativeExponentialDistributionType value);

	/**
	 * Returns the value of the '<em><b>Normal Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Normal Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Normal Distribution</em>' containment reference.
	 * @see #setNormalDistribution(NormalDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_NormalDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='NormalDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	NormalDistributionType getNormalDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getNormalDistribution <em>Normal Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Normal Distribution</em>' containment reference.
	 * @see #getNormalDistribution()
	 * @generated
	 */
	void setNormalDistribution(NormalDistributionType value);

	/**
	 * Returns the value of the '<em><b>Numeric Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Numeric Parameter</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Numeric Parameter</em>' containment reference.
	 * @see #setNumericParameter(NumericParameterType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_NumericParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='NumericParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	NumericParameterType getNumericParameter();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getNumericParameter <em>Numeric Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Numeric Parameter</em>' containment reference.
	 * @see #getNumericParameter()
	 * @generated
	 */
	void setNumericParameter(NumericParameterType value);

	/**
	 * Returns the value of the '<em><b>Poisson Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Poisson Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Poisson Distribution</em>' containment reference.
	 * @see #setPoissonDistribution(PoissonDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_PoissonDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='PoissonDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	PoissonDistributionType getPoissonDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getPoissonDistribution <em>Poisson Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Poisson Distribution</em>' containment reference.
	 * @see #getPoissonDistribution()
	 * @generated
	 */
	void setPoissonDistribution(PoissonDistributionType value);

	/**
	 * Returns the value of the '<em><b>String Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>String Parameter</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>String Parameter</em>' containment reference.
	 * @see #setStringParameter(StringParameterType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_StringParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='StringParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	StringParameterType getStringParameter();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getStringParameter <em>String Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>String Parameter</em>' containment reference.
	 * @see #getStringParameter()
	 * @generated
	 */
	void setStringParameter(StringParameterType value);

	/**
	 * Returns the value of the '<em><b>Triangular Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Triangular Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Triangular Distribution</em>' containment reference.
	 * @see #setTriangularDistribution(TriangularDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_TriangularDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='TriangularDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	TriangularDistributionType getTriangularDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getTriangularDistribution <em>Triangular Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Triangular Distribution</em>' containment reference.
	 * @see #getTriangularDistribution()
	 * @generated
	 */
	void setTriangularDistribution(TriangularDistributionType value);

	/**
	 * Returns the value of the '<em><b>Truncated Normal Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Truncated Normal Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Truncated Normal Distribution</em>' containment reference.
	 * @see #setTruncatedNormalDistribution(TruncatedNormalDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_TruncatedNormalDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='TruncatedNormalDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	TruncatedNormalDistributionType getTruncatedNormalDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getTruncatedNormalDistribution <em>Truncated Normal Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Truncated Normal Distribution</em>' containment reference.
	 * @see #getTruncatedNormalDistribution()
	 * @generated
	 */
	void setTruncatedNormalDistribution(TruncatedNormalDistributionType value);

	/**
	 * Returns the value of the '<em><b>Uniform Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Uniform Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uniform Distribution</em>' containment reference.
	 * @see #setUniformDistribution(UniformDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_UniformDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='UniformDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	UniformDistributionType getUniformDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getUniformDistribution <em>Uniform Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uniform Distribution</em>' containment reference.
	 * @see #getUniformDistribution()
	 * @generated
	 */
	void setUniformDistribution(UniformDistributionType value);

	/**
	 * Returns the value of the '<em><b>User Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>User Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>User Distribution</em>' containment reference.
	 * @see #setUserDistribution(UserDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_UserDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='UserDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	UserDistributionType getUserDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getUserDistribution <em>User Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>User Distribution</em>' containment reference.
	 * @see #getUserDistribution()
	 * @generated
	 */
	void setUserDistribution(UserDistributionType value);

	/**
	 * Returns the value of the '<em><b>User Distribution Data Point</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>User Distribution Data Point</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>User Distribution Data Point</em>' containment reference.
	 * @see #setUserDistributionDataPoint(UserDistributionDataPointType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_UserDistributionDataPoint()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='UserDistributionDataPoint' namespace='##targetNamespace'"
	 * @generated
	 */
	UserDistributionDataPointType getUserDistributionDataPoint();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getUserDistributionDataPoint <em>User Distribution Data Point</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>User Distribution Data Point</em>' containment reference.
	 * @see #getUserDistributionDataPoint()
	 * @generated
	 */
	void setUserDistributionDataPoint(UserDistributionDataPointType value);

	/**
	 * Returns the value of the '<em><b>Weibull Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Weibull Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Weibull Distribution</em>' containment reference.
	 * @see #setWeibullDistribution(WeibullDistributionType)
	 * @see bpsim.BpsimPackage#getDocumentRoot_WeibullDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='WeibullDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	WeibullDistributionType getWeibullDistribution();

	/**
	 * Sets the value of the '{@link bpsim.DocumentRoot#getWeibullDistribution <em>Weibull Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Weibull Distribution</em>' containment reference.
	 * @see #getWeibullDistribution()
	 * @generated
	 */
	void setWeibullDistribution(WeibullDistributionType value);

} // DocumentRoot
