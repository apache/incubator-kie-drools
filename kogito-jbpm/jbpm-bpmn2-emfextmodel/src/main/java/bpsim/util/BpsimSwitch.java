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
package bpsim.util;

import bpsim.*;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see bpsim.BpsimPackage
 * @generated
 */
public class BpsimSwitch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static BpsimPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BpsimSwitch() {
		if (modelPackage == null) {
			modelPackage = BpsimPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case BpsimPackage.BETA_DISTRIBUTION_TYPE: {
				BetaDistributionType betaDistributionType = (BetaDistributionType)theEObject;
				T result = caseBetaDistributionType(betaDistributionType);
				if (result == null) result = caseDistributionParameter(betaDistributionType);
				if (result == null) result = caseParameterValue(betaDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.BINOMIAL_DISTRIBUTION_TYPE: {
				BinomialDistributionType binomialDistributionType = (BinomialDistributionType)theEObject;
				T result = caseBinomialDistributionType(binomialDistributionType);
				if (result == null) result = caseDistributionParameter(binomialDistributionType);
				if (result == null) result = caseParameterValue(binomialDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.BOOLEAN_PARAMETER_TYPE: {
				BooleanParameterType booleanParameterType = (BooleanParameterType)theEObject;
				T result = caseBooleanParameterType(booleanParameterType);
				if (result == null) result = caseConstantParameter(booleanParameterType);
				if (result == null) result = caseParameterValue(booleanParameterType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.BP_SIM_DATA_TYPE: {
				BPSimDataType bpSimDataType = (BPSimDataType)theEObject;
				T result = caseBPSimDataType(bpSimDataType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.CALENDAR: {
				Calendar calendar = (Calendar)theEObject;
				T result = caseCalendar(calendar);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.CONSTANT_PARAMETER: {
				ConstantParameter constantParameter = (ConstantParameter)theEObject;
				T result = caseConstantParameter(constantParameter);
				if (result == null) result = caseParameterValue(constantParameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.CONTROL_PARAMETERS: {
				ControlParameters controlParameters = (ControlParameters)theEObject;
				T result = caseControlParameters(controlParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.COST_PARAMETERS: {
				CostParameters costParameters = (CostParameters)theEObject;
				T result = caseCostParameters(costParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.DATE_TIME_PARAMETER_TYPE: {
				DateTimeParameterType dateTimeParameterType = (DateTimeParameterType)theEObject;
				T result = caseDateTimeParameterType(dateTimeParameterType);
				if (result == null) result = caseConstantParameter(dateTimeParameterType);
				if (result == null) result = caseParameterValue(dateTimeParameterType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.DISTRIBUTION_PARAMETER: {
				DistributionParameter distributionParameter = (DistributionParameter)theEObject;
				T result = caseDistributionParameter(distributionParameter);
				if (result == null) result = caseParameterValue(distributionParameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.DOCUMENT_ROOT: {
				DocumentRoot documentRoot = (DocumentRoot)theEObject;
				T result = caseDocumentRoot(documentRoot);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.DURATION_PARAMETER_TYPE: {
				DurationParameterType durationParameterType = (DurationParameterType)theEObject;
				T result = caseDurationParameterType(durationParameterType);
				if (result == null) result = caseConstantParameter(durationParameterType);
				if (result == null) result = caseParameterValue(durationParameterType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.ELEMENT_PARAMETERS: {
				ElementParameters elementParameters = (ElementParameters)theEObject;
				T result = caseElementParameters(elementParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.ELEMENT_PARAMETERS_TYPE: {
				ElementParametersType elementParametersType = (ElementParametersType)theEObject;
				T result = caseElementParametersType(elementParametersType);
				if (result == null) result = caseElementParameters(elementParametersType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.ENUM_PARAMETER_TYPE: {
				EnumParameterType enumParameterType = (EnumParameterType)theEObject;
				T result = caseEnumParameterType(enumParameterType);
				if (result == null) result = caseParameterValue(enumParameterType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.ERLANG_DISTRIBUTION_TYPE: {
				ErlangDistributionType erlangDistributionType = (ErlangDistributionType)theEObject;
				T result = caseErlangDistributionType(erlangDistributionType);
				if (result == null) result = caseDistributionParameter(erlangDistributionType);
				if (result == null) result = caseParameterValue(erlangDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.EXPRESSION_PARAMETER_TYPE: {
				ExpressionParameterType expressionParameterType = (ExpressionParameterType)theEObject;
				T result = caseExpressionParameterType(expressionParameterType);
				if (result == null) result = caseParameterValue(expressionParameterType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.FLOATING_PARAMETER_TYPE: {
				FloatingParameterType floatingParameterType = (FloatingParameterType)theEObject;
				T result = caseFloatingParameterType(floatingParameterType);
				if (result == null) result = caseConstantParameter(floatingParameterType);
				if (result == null) result = caseParameterValue(floatingParameterType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.GAMMA_DISTRIBUTION_TYPE: {
				GammaDistributionType gammaDistributionType = (GammaDistributionType)theEObject;
				T result = caseGammaDistributionType(gammaDistributionType);
				if (result == null) result = caseDistributionParameter(gammaDistributionType);
				if (result == null) result = caseParameterValue(gammaDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.LOG_NORMAL_DISTRIBUTION_TYPE: {
				LogNormalDistributionType logNormalDistributionType = (LogNormalDistributionType)theEObject;
				T result = caseLogNormalDistributionType(logNormalDistributionType);
				if (result == null) result = caseDistributionParameter(logNormalDistributionType);
				if (result == null) result = caseParameterValue(logNormalDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE: {
				NegativeExponentialDistributionType negativeExponentialDistributionType = (NegativeExponentialDistributionType)theEObject;
				T result = caseNegativeExponentialDistributionType(negativeExponentialDistributionType);
				if (result == null) result = caseDistributionParameter(negativeExponentialDistributionType);
				if (result == null) result = caseParameterValue(negativeExponentialDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.NORMAL_DISTRIBUTION_TYPE: {
				NormalDistributionType normalDistributionType = (NormalDistributionType)theEObject;
				T result = caseNormalDistributionType(normalDistributionType);
				if (result == null) result = caseDistributionParameter(normalDistributionType);
				if (result == null) result = caseParameterValue(normalDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.NUMERIC_PARAMETER_TYPE: {
				NumericParameterType numericParameterType = (NumericParameterType)theEObject;
				T result = caseNumericParameterType(numericParameterType);
				if (result == null) result = caseConstantParameter(numericParameterType);
				if (result == null) result = caseParameterValue(numericParameterType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.PARAMETER: {
				Parameter parameter = (Parameter)theEObject;
				T result = caseParameter(parameter);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.PARAMETER_VALUE: {
				ParameterValue parameterValue = (ParameterValue)theEObject;
				T result = caseParameterValue(parameterValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.POISSON_DISTRIBUTION_TYPE: {
				PoissonDistributionType poissonDistributionType = (PoissonDistributionType)theEObject;
				T result = casePoissonDistributionType(poissonDistributionType);
				if (result == null) result = caseDistributionParameter(poissonDistributionType);
				if (result == null) result = caseParameterValue(poissonDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.PRIORITY_PARAMETERS: {
				PriorityParameters priorityParameters = (PriorityParameters)theEObject;
				T result = casePriorityParameters(priorityParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.PROPERTY_PARAMETERS: {
				PropertyParameters propertyParameters = (PropertyParameters)theEObject;
				T result = casePropertyParameters(propertyParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.PROPERTY_TYPE: {
				PropertyType propertyType = (PropertyType)theEObject;
				T result = casePropertyType(propertyType);
				if (result == null) result = caseParameter(propertyType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.RESOURCE_PARAMETERS: {
				ResourceParameters resourceParameters = (ResourceParameters)theEObject;
				T result = caseResourceParameters(resourceParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.SCENARIO: {
				Scenario scenario = (Scenario)theEObject;
				T result = caseScenario(scenario);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.SCENARIO_PARAMETERS: {
				ScenarioParameters scenarioParameters = (ScenarioParameters)theEObject;
				T result = caseScenarioParameters(scenarioParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.SCENARIO_PARAMETERS_TYPE: {
				ScenarioParametersType scenarioParametersType = (ScenarioParametersType)theEObject;
				T result = caseScenarioParametersType(scenarioParametersType);
				if (result == null) result = caseScenarioParameters(scenarioParametersType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.STRING_PARAMETER_TYPE: {
				StringParameterType stringParameterType = (StringParameterType)theEObject;
				T result = caseStringParameterType(stringParameterType);
				if (result == null) result = caseConstantParameter(stringParameterType);
				if (result == null) result = caseParameterValue(stringParameterType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.TIME_PARAMETERS: {
				TimeParameters timeParameters = (TimeParameters)theEObject;
				T result = caseTimeParameters(timeParameters);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.TRIANGULAR_DISTRIBUTION_TYPE: {
				TriangularDistributionType triangularDistributionType = (TriangularDistributionType)theEObject;
				T result = caseTriangularDistributionType(triangularDistributionType);
				if (result == null) result = caseDistributionParameter(triangularDistributionType);
				if (result == null) result = caseParameterValue(triangularDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.TRUNCATED_NORMAL_DISTRIBUTION_TYPE: {
				TruncatedNormalDistributionType truncatedNormalDistributionType = (TruncatedNormalDistributionType)theEObject;
				T result = caseTruncatedNormalDistributionType(truncatedNormalDistributionType);
				if (result == null) result = caseDistributionParameter(truncatedNormalDistributionType);
				if (result == null) result = caseParameterValue(truncatedNormalDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.UNIFORM_DISTRIBUTION_TYPE: {
				UniformDistributionType uniformDistributionType = (UniformDistributionType)theEObject;
				T result = caseUniformDistributionType(uniformDistributionType);
				if (result == null) result = caseDistributionParameter(uniformDistributionType);
				if (result == null) result = caseParameterValue(uniformDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.USER_DISTRIBUTION_DATA_POINT_TYPE: {
				UserDistributionDataPointType userDistributionDataPointType = (UserDistributionDataPointType)theEObject;
				T result = caseUserDistributionDataPointType(userDistributionDataPointType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.USER_DISTRIBUTION_TYPE: {
				UserDistributionType userDistributionType = (UserDistributionType)theEObject;
				T result = caseUserDistributionType(userDistributionType);
				if (result == null) result = caseDistributionParameter(userDistributionType);
				if (result == null) result = caseParameterValue(userDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.VENDOR_EXTENSION: {
				VendorExtension vendorExtension = (VendorExtension)theEObject;
				T result = caseVendorExtension(vendorExtension);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case BpsimPackage.WEIBULL_DISTRIBUTION_TYPE: {
				WeibullDistributionType weibullDistributionType = (WeibullDistributionType)theEObject;
				T result = caseWeibullDistributionType(weibullDistributionType);
				if (result == null) result = caseDistributionParameter(weibullDistributionType);
				if (result == null) result = caseParameterValue(weibullDistributionType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Beta Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Beta Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBetaDistributionType(BetaDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Binomial Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Binomial Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBinomialDistributionType(BinomialDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Boolean Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Boolean Parameter Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBooleanParameterType(BooleanParameterType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>BP Sim Data Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>BP Sim Data Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBPSimDataType(BPSimDataType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Calendar</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Calendar</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCalendar(Calendar object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Constant Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Constant Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConstantParameter(ConstantParameter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Control Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Control Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseControlParameters(ControlParameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Cost Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Cost Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCostParameters(CostParameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Date Time Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Date Time Parameter Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDateTimeParameterType(DateTimeParameterType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Distribution Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Distribution Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDistributionParameter(DistributionParameter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Document Root</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Document Root</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDocumentRoot(DocumentRoot object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Duration Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Duration Parameter Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDurationParameterType(DurationParameterType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Element Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Element Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseElementParameters(ElementParameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Element Parameters Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Element Parameters Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseElementParametersType(ElementParametersType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Enum Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Enum Parameter Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEnumParameterType(EnumParameterType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Erlang Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Erlang Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseErlangDistributionType(ErlangDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Expression Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Expression Parameter Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExpressionParameterType(ExpressionParameterType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Floating Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Floating Parameter Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFloatingParameterType(FloatingParameterType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Gamma Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Gamma Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGammaDistributionType(GammaDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Log Normal Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Log Normal Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLogNormalDistributionType(LogNormalDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Negative Exponential Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Negative Exponential Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNegativeExponentialDistributionType(NegativeExponentialDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Normal Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Normal Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNormalDistributionType(NormalDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Numeric Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Numeric Parameter Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNumericParameterType(NumericParameterType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameter(Parameter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Parameter Value</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Parameter Value</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseParameterValue(ParameterValue object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Poisson Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Poisson Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePoissonDistributionType(PoissonDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Priority Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Priority Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePriorityParameters(PriorityParameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Property Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Property Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePropertyParameters(PropertyParameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Property Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Property Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePropertyType(PropertyType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Resource Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Resource Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseResourceParameters(ResourceParameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Scenario</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Scenario</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseScenario(Scenario object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Scenario Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Scenario Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseScenarioParameters(ScenarioParameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Scenario Parameters Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Scenario Parameters Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseScenarioParametersType(ScenarioParametersType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String Parameter Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String Parameter Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringParameterType(StringParameterType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Time Parameters</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Time Parameters</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTimeParameters(TimeParameters object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Triangular Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Triangular Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTriangularDistributionType(TriangularDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Truncated Normal Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Truncated Normal Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTruncatedNormalDistributionType(TruncatedNormalDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Uniform Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Uniform Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUniformDistributionType(UniformDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>User Distribution Data Point Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>User Distribution Data Point Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUserDistributionDataPointType(UserDistributionDataPointType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>User Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>User Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUserDistributionType(UserDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Vendor Extension</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Vendor Extension</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVendorExtension(VendorExtension object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Weibull Distribution Type</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Weibull Distribution Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseWeibullDistributionType(WeibullDistributionType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T defaultCase(EObject object) {
		return null;
	}

} //BpsimSwitch
