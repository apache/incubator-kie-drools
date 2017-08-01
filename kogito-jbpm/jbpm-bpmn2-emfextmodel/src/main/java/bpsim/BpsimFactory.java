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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see bpsim.BpsimPackage
 * @generated
 */
public interface BpsimFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	BpsimFactory eINSTANCE = bpsim.impl.BpsimFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Beta Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Beta Distribution Type</em>'.
	 * @generated
	 */
	BetaDistributionType createBetaDistributionType();

	/**
	 * Returns a new object of class '<em>Binomial Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Binomial Distribution Type</em>'.
	 * @generated
	 */
	BinomialDistributionType createBinomialDistributionType();

	/**
	 * Returns a new object of class '<em>Boolean Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Boolean Parameter Type</em>'.
	 * @generated
	 */
	BooleanParameterType createBooleanParameterType();

	/**
	 * Returns a new object of class '<em>BP Sim Data Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>BP Sim Data Type</em>'.
	 * @generated
	 */
	BPSimDataType createBPSimDataType();

	/**
	 * Returns a new object of class '<em>Calendar</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Calendar</em>'.
	 * @generated
	 */
	Calendar createCalendar();

	/**
	 * Returns a new object of class '<em>Constant Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Constant Parameter</em>'.
	 * @generated
	 */
	ConstantParameter createConstantParameter();

	/**
	 * Returns a new object of class '<em>Control Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Control Parameters</em>'.
	 * @generated
	 */
	ControlParameters createControlParameters();

	/**
	 * Returns a new object of class '<em>Cost Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Cost Parameters</em>'.
	 * @generated
	 */
	CostParameters createCostParameters();

	/**
	 * Returns a new object of class '<em>Date Time Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Date Time Parameter Type</em>'.
	 * @generated
	 */
	DateTimeParameterType createDateTimeParameterType();

	/**
	 * Returns a new object of class '<em>Distribution Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Distribution Parameter</em>'.
	 * @generated
	 */
	DistributionParameter createDistributionParameter();

	/**
	 * Returns a new object of class '<em>Document Root</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Document Root</em>'.
	 * @generated
	 */
	DocumentRoot createDocumentRoot();

	/**
	 * Returns a new object of class '<em>Duration Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Duration Parameter Type</em>'.
	 * @generated
	 */
	DurationParameterType createDurationParameterType();

	/**
	 * Returns a new object of class '<em>Element Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Element Parameters</em>'.
	 * @generated
	 */
	ElementParameters createElementParameters();

	/**
	 * Returns a new object of class '<em>Element Parameters Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Element Parameters Type</em>'.
	 * @generated
	 */
	ElementParametersType createElementParametersType();

	/**
	 * Returns a new object of class '<em>Enum Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Enum Parameter Type</em>'.
	 * @generated
	 */
	EnumParameterType createEnumParameterType();

	/**
	 * Returns a new object of class '<em>Erlang Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Erlang Distribution Type</em>'.
	 * @generated
	 */
	ErlangDistributionType createErlangDistributionType();

	/**
	 * Returns a new object of class '<em>Expression Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Expression Parameter Type</em>'.
	 * @generated
	 */
	ExpressionParameterType createExpressionParameterType();

	/**
	 * Returns a new object of class '<em>Floating Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Floating Parameter Type</em>'.
	 * @generated
	 */
	FloatingParameterType createFloatingParameterType();

	/**
	 * Returns a new object of class '<em>Gamma Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Gamma Distribution Type</em>'.
	 * @generated
	 */
	GammaDistributionType createGammaDistributionType();

	/**
	 * Returns a new object of class '<em>Log Normal Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Log Normal Distribution Type</em>'.
	 * @generated
	 */
	LogNormalDistributionType createLogNormalDistributionType();

	/**
	 * Returns a new object of class '<em>Negative Exponential Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Negative Exponential Distribution Type</em>'.
	 * @generated
	 */
	NegativeExponentialDistributionType createNegativeExponentialDistributionType();

	/**
	 * Returns a new object of class '<em>Normal Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Normal Distribution Type</em>'.
	 * @generated
	 */
	NormalDistributionType createNormalDistributionType();

	/**
	 * Returns a new object of class '<em>Numeric Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Numeric Parameter Type</em>'.
	 * @generated
	 */
	NumericParameterType createNumericParameterType();

	/**
	 * Returns a new object of class '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter</em>'.
	 * @generated
	 */
	Parameter createParameter();

	/**
	 * Returns a new object of class '<em>Parameter Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameter Value</em>'.
	 * @generated
	 */
	ParameterValue createParameterValue();

	/**
	 * Returns a new object of class '<em>Poisson Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Poisson Distribution Type</em>'.
	 * @generated
	 */
	PoissonDistributionType createPoissonDistributionType();

	/**
	 * Returns a new object of class '<em>Priority Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Priority Parameters</em>'.
	 * @generated
	 */
	PriorityParameters createPriorityParameters();

	/**
	 * Returns a new object of class '<em>Property Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Property Parameters</em>'.
	 * @generated
	 */
	PropertyParameters createPropertyParameters();

	/**
	 * Returns a new object of class '<em>Property Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Property Type</em>'.
	 * @generated
	 */
	PropertyType createPropertyType();

	/**
	 * Returns a new object of class '<em>Resource Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Resource Parameters</em>'.
	 * @generated
	 */
	ResourceParameters createResourceParameters();

	/**
	 * Returns a new object of class '<em>Scenario</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Scenario</em>'.
	 * @generated
	 */
	Scenario createScenario();

	/**
	 * Returns a new object of class '<em>Scenario Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Scenario Parameters</em>'.
	 * @generated
	 */
	ScenarioParameters createScenarioParameters();

	/**
	 * Returns a new object of class '<em>Scenario Parameters Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Scenario Parameters Type</em>'.
	 * @generated
	 */
	ScenarioParametersType createScenarioParametersType();

	/**
	 * Returns a new object of class '<em>String Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>String Parameter Type</em>'.
	 * @generated
	 */
	StringParameterType createStringParameterType();

	/**
	 * Returns a new object of class '<em>Time Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Time Parameters</em>'.
	 * @generated
	 */
	TimeParameters createTimeParameters();

	/**
	 * Returns a new object of class '<em>Triangular Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Triangular Distribution Type</em>'.
	 * @generated
	 */
	TriangularDistributionType createTriangularDistributionType();

	/**
	 * Returns a new object of class '<em>Truncated Normal Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Truncated Normal Distribution Type</em>'.
	 * @generated
	 */
	TruncatedNormalDistributionType createTruncatedNormalDistributionType();

	/**
	 * Returns a new object of class '<em>Uniform Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Uniform Distribution Type</em>'.
	 * @generated
	 */
	UniformDistributionType createUniformDistributionType();

	/**
	 * Returns a new object of class '<em>User Distribution Data Point Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>User Distribution Data Point Type</em>'.
	 * @generated
	 */
	UserDistributionDataPointType createUserDistributionDataPointType();

	/**
	 * Returns a new object of class '<em>User Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>User Distribution Type</em>'.
	 * @generated
	 */
	UserDistributionType createUserDistributionType();

	/**
	 * Returns a new object of class '<em>Vendor Extension</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Vendor Extension</em>'.
	 * @generated
	 */
	VendorExtension createVendorExtension();

	/**
	 * Returns a new object of class '<em>Weibull Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Weibull Distribution Type</em>'.
	 * @generated
	 */
	WeibullDistributionType createWeibullDistributionType();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	BpsimPackage getBpsimPackage();

} //BpsimFactory
