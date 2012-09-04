/**
 */
package org.jboss.drools;

import java.math.BigInteger;

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
 *   <li>{@link org.jboss.drools.DocumentRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getBetaDistribution <em>Beta Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getParameterValue <em>Parameter Value</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getBinomialDistribution <em>Binomial Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getBooleanParameter <em>Boolean Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getDateTimeParameter <em>Date Time Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getDurationParameter <em>Duration Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getEnumParameter <em>Enum Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getErlangDistribution <em>Erlang Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getExpressionParameter <em>Expression Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getFloatingParameter <em>Floating Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getGammaDistribution <em>Gamma Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getGlobal <em>Global</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getImport <em>Import</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getLogNormalDistribution <em>Log Normal Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getMetadata <em>Metadata</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getMetaentry <em>Metaentry</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getNegativeExponentialDistribution <em>Negative Exponential Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getNormalDistribution <em>Normal Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getNumericParameter <em>Numeric Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getOnEntryScript <em>On Entry Script</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getOnExitScript <em>On Exit Script</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getPoissonDistribution <em>Poisson Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getProcessAnalysisData <em>Process Analysis Data</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getRandomDistribution <em>Random Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getStringParameter <em>String Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getTriangularDistribution <em>Triangular Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getTruncatedNormalDistribution <em>Truncated Normal Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getUniformDistribution <em>Uniform Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getUserDistribution <em>User Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getUserDistributionDataPoint <em>User Distribution Data Point</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getWeibullDistribution <em>Weibull Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getPackageName <em>Package Name</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getPriority <em>Priority</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getRuleFlowGroup <em>Rule Flow Group</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getTaskName <em>Task Name</em>}</li>
 *   <li>{@link org.jboss.drools.DocumentRoot#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jboss.drools.DroolsPackage#getDocumentRoot()
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_Mixed()
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_XMLNSPrefixMap()
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_XSISchemaLocation()
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_BetaDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='BetaDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	BetaDistributionType getBetaDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getBetaDistribution <em>Beta Distribution</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_ParameterValue()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ParameterValue' namespace='##targetNamespace'"
	 * @generated
	 */
	ParameterValue getParameterValue();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getParameterValue <em>Parameter Value</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_BinomialDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='BinomialDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	BinomialDistributionType getBinomialDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getBinomialDistribution <em>Binomial Distribution</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_BooleanParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='BooleanParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	BooleanParameterType getBooleanParameter();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getBooleanParameter <em>Boolean Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Boolean Parameter</em>' containment reference.
	 * @see #getBooleanParameter()
	 * @generated
	 */
	void setBooleanParameter(BooleanParameterType value);

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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_DateTimeParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='DateTimeParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	DateTimeParameterType getDateTimeParameter();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getDateTimeParameter <em>Date Time Parameter</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_DurationParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='DurationParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	DurationParameterType getDurationParameter();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getDurationParameter <em>Duration Parameter</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_EnumParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='EnumParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	EnumParameterType getEnumParameter();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getEnumParameter <em>Enum Parameter</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_ErlangDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ErlangDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	ErlangDistributionType getErlangDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getErlangDistribution <em>Erlang Distribution</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_ExpressionParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ExpressionParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	ExpressionParameterType getExpressionParameter();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getExpressionParameter <em>Expression Parameter</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_FloatingParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='FloatingParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	FloatingParameterType getFloatingParameter();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getFloatingParameter <em>Floating Parameter</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_GammaDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='GammaDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	GammaDistributionType getGammaDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getGammaDistribution <em>Gamma Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gamma Distribution</em>' containment reference.
	 * @see #getGammaDistribution()
	 * @generated
	 */
	void setGammaDistribution(GammaDistributionType value);

	/**
	 * Returns the value of the '<em><b>Global</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Global</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Global</em>' containment reference.
	 * @see #setGlobal(GlobalType)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_Global()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='global' namespace='##targetNamespace'"
	 * @generated
	 */
	GlobalType getGlobal();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getGlobal <em>Global</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Global</em>' containment reference.
	 * @see #getGlobal()
	 * @generated
	 */
	void setGlobal(GlobalType value);

	/**
	 * Returns the value of the '<em><b>Import</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Import</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Import</em>' containment reference.
	 * @see #setImport(ImportType)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_Import()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='import' namespace='##targetNamespace'"
	 * @generated
	 */
	ImportType getImport();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getImport <em>Import</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Import</em>' containment reference.
	 * @see #getImport()
	 * @generated
	 */
	void setImport(ImportType value);

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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_LogNormalDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='LogNormalDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	LogNormalDistributionType getLogNormalDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getLogNormalDistribution <em>Log Normal Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Log Normal Distribution</em>' containment reference.
	 * @see #getLogNormalDistribution()
	 * @generated
	 */
	void setLogNormalDistribution(LogNormalDistributionType value);

	/**
	 * Returns the value of the '<em><b>Metadata</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Metadata</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metadata</em>' containment reference.
	 * @see #setMetadata(MetadataType)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_Metadata()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='metadata' namespace='##targetNamespace'"
	 * @generated
	 */
	MetadataType getMetadata();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getMetadata <em>Metadata</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Metadata</em>' containment reference.
	 * @see #getMetadata()
	 * @generated
	 */
	void setMetadata(MetadataType value);

	/**
	 * Returns the value of the '<em><b>Metaentry</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Metaentry</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metaentry</em>' containment reference.
	 * @see #setMetaentry(MetaentryType)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_Metaentry()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='metaentry' namespace='##targetNamespace'"
	 * @generated
	 */
	MetaentryType getMetaentry();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getMetaentry <em>Metaentry</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Metaentry</em>' containment reference.
	 * @see #getMetaentry()
	 * @generated
	 */
	void setMetaentry(MetaentryType value);

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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_NegativeExponentialDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='NegativeExponentialDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	NegativeExponentialDistributionType getNegativeExponentialDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getNegativeExponentialDistribution <em>Negative Exponential Distribution</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_NormalDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='NormalDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	NormalDistributionType getNormalDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getNormalDistribution <em>Normal Distribution</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_NumericParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='NumericParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	NumericParameterType getNumericParameter();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getNumericParameter <em>Numeric Parameter</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Numeric Parameter</em>' containment reference.
	 * @see #getNumericParameter()
	 * @generated
	 */
	void setNumericParameter(NumericParameterType value);

	/**
	 * Returns the value of the '<em><b>On Entry Script</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>On Entry Script</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On Entry Script</em>' containment reference.
	 * @see #setOnEntryScript(OnEntryScriptType)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_OnEntryScript()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='onEntry-script' namespace='##targetNamespace'"
	 * @generated
	 */
	OnEntryScriptType getOnEntryScript();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getOnEntryScript <em>On Entry Script</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>On Entry Script</em>' containment reference.
	 * @see #getOnEntryScript()
	 * @generated
	 */
	void setOnEntryScript(OnEntryScriptType value);

	/**
	 * Returns the value of the '<em><b>On Exit Script</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>On Exit Script</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>On Exit Script</em>' containment reference.
	 * @see #setOnExitScript(OnExitScriptType)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_OnExitScript()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='onExit-script' namespace='##targetNamespace'"
	 * @generated
	 */
	OnExitScriptType getOnExitScript();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getOnExitScript <em>On Exit Script</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>On Exit Script</em>' containment reference.
	 * @see #getOnExitScript()
	 * @generated
	 */
	void setOnExitScript(OnExitScriptType value);

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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_PoissonDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='PoissonDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	PoissonDistributionType getPoissonDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getPoissonDistribution <em>Poisson Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Poisson Distribution</em>' containment reference.
	 * @see #getPoissonDistribution()
	 * @generated
	 */
	void setPoissonDistribution(PoissonDistributionType value);

	/**
	 * Returns the value of the '<em><b>Process Analysis Data</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Process Analysis Data</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Process Analysis Data</em>' containment reference.
	 * @see #setProcessAnalysisData(ProcessAnalysisDataType)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_ProcessAnalysisData()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ProcessAnalysisData' namespace='##targetNamespace'"
	 * @generated
	 */
	ProcessAnalysisDataType getProcessAnalysisData();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getProcessAnalysisData <em>Process Analysis Data</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Process Analysis Data</em>' containment reference.
	 * @see #getProcessAnalysisData()
	 * @generated
	 */
	void setProcessAnalysisData(ProcessAnalysisDataType value);

	/**
	 * Returns the value of the '<em><b>Random Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Random Distribution</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Random Distribution</em>' containment reference.
	 * @see #setRandomDistribution(RandomDistributionType)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_RandomDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='RandomDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	RandomDistributionType getRandomDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getRandomDistribution <em>Random Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Random Distribution</em>' containment reference.
	 * @see #getRandomDistribution()
	 * @generated
	 */
	void setRandomDistribution(RandomDistributionType value);

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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_StringParameter()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='StringParameter' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	StringParameterType getStringParameter();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getStringParameter <em>String Parameter</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_TriangularDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='TriangularDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	TriangularDistributionType getTriangularDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getTriangularDistribution <em>Triangular Distribution</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_TruncatedNormalDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='TruncatedNormalDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	TruncatedNormalDistributionType getTruncatedNormalDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getTruncatedNormalDistribution <em>Truncated Normal Distribution</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_UniformDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='UniformDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	UniformDistributionType getUniformDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getUniformDistribution <em>Uniform Distribution</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_UserDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='UserDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	UserDistributionType getUserDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getUserDistribution <em>User Distribution</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_UserDistributionDataPoint()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='UserDistributionDataPoint' namespace='##targetNamespace'"
	 * @generated
	 */
	UserDistributionDataPointType getUserDistributionDataPoint();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getUserDistributionDataPoint <em>User Distribution Data Point</em>}' containment reference.
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
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_WeibullDistribution()
	 * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='WeibullDistribution' namespace='##targetNamespace' affiliation='ParameterValue'"
	 * @generated
	 */
	WeibullDistributionType getWeibullDistribution();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getWeibullDistribution <em>Weibull Distribution</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Weibull Distribution</em>' containment reference.
	 * @see #getWeibullDistribution()
	 * @generated
	 */
	void setWeibullDistribution(WeibullDistributionType value);

	/**
	 * Returns the value of the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Package Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Package Name</em>' attribute.
	 * @see #setPackageName(String)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_PackageName()
	 * @model dataType="org.jboss.drools.PackageNameType"
	 *        extendedMetaData="kind='attribute' name='packageName' namespace='##targetNamespace'"
	 * @generated
	 */
	String getPackageName();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getPackageName <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Package Name</em>' attribute.
	 * @see #getPackageName()
	 * @generated
	 */
	void setPackageName(String value);

	/**
	 * Returns the value of the '<em><b>Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Priority</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Priority</em>' attribute.
	 * @see #setPriority(BigInteger)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_Priority()
	 * @model dataType="org.jboss.drools.PriorityType"
	 *        extendedMetaData="kind='attribute' name='priority' namespace='##targetNamespace'"
	 * @generated
	 */
	BigInteger getPriority();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getPriority <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Priority</em>' attribute.
	 * @see #getPriority()
	 * @generated
	 */
	void setPriority(BigInteger value);

	/**
	 * Returns the value of the '<em><b>Rule Flow Group</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rule Flow Group</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rule Flow Group</em>' attribute.
	 * @see #setRuleFlowGroup(String)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_RuleFlowGroup()
	 * @model dataType="org.jboss.drools.RuleFlowGroupType"
	 *        extendedMetaData="kind='attribute' name='ruleFlowGroup' namespace='##targetNamespace'"
	 * @generated
	 */
	String getRuleFlowGroup();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getRuleFlowGroup <em>Rule Flow Group</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rule Flow Group</em>' attribute.
	 * @see #getRuleFlowGroup()
	 * @generated
	 */
	void setRuleFlowGroup(String value);

	/**
	 * Returns the value of the '<em><b>Task Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Task Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Task Name</em>' attribute.
	 * @see #setTaskName(String)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_TaskName()
	 * @model dataType="org.jboss.drools.TaskNameType"
	 *        extendedMetaData="kind='attribute' name='taskName' namespace='##targetNamespace'"
	 * @generated
	 */
	String getTaskName();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getTaskName <em>Task Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Task Name</em>' attribute.
	 * @see #getTaskName()
	 * @generated
	 */
	void setTaskName(String value);

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #setVersion(String)
	 * @see org.jboss.drools.DroolsPackage#getDocumentRoot_Version()
	 * @model dataType="org.jboss.drools.VersionType"
	 *        extendedMetaData="kind='attribute' name='version' namespace='##targetNamespace'"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link org.jboss.drools.DocumentRoot#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

} // DocumentRoot
