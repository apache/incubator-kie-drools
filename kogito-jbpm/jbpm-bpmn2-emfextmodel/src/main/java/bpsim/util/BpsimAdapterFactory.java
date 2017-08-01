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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see bpsim.BpsimPackage
 * @generated
 */
public class BpsimAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static BpsimPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BpsimAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = BpsimPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BpsimSwitch<Adapter> modelSwitch =
		new BpsimSwitch<Adapter>() {
			@Override
			public Adapter caseBetaDistributionType(BetaDistributionType object) {
				return createBetaDistributionTypeAdapter();
			}
			@Override
			public Adapter caseBinomialDistributionType(BinomialDistributionType object) {
				return createBinomialDistributionTypeAdapter();
			}
			@Override
			public Adapter caseBooleanParameterType(BooleanParameterType object) {
				return createBooleanParameterTypeAdapter();
			}
			@Override
			public Adapter caseBPSimDataType(BPSimDataType object) {
				return createBPSimDataTypeAdapter();
			}
			@Override
			public Adapter caseCalendar(Calendar object) {
				return createCalendarAdapter();
			}
			@Override
			public Adapter caseConstantParameter(ConstantParameter object) {
				return createConstantParameterAdapter();
			}
			@Override
			public Adapter caseControlParameters(ControlParameters object) {
				return createControlParametersAdapter();
			}
			@Override
			public Adapter caseCostParameters(CostParameters object) {
				return createCostParametersAdapter();
			}
			@Override
			public Adapter caseDateTimeParameterType(DateTimeParameterType object) {
				return createDateTimeParameterTypeAdapter();
			}
			@Override
			public Adapter caseDistributionParameter(DistributionParameter object) {
				return createDistributionParameterAdapter();
			}
			@Override
			public Adapter caseDocumentRoot(DocumentRoot object) {
				return createDocumentRootAdapter();
			}
			@Override
			public Adapter caseDurationParameterType(DurationParameterType object) {
				return createDurationParameterTypeAdapter();
			}
			@Override
			public Adapter caseElementParameters(ElementParameters object) {
				return createElementParametersAdapter();
			}
			@Override
			public Adapter caseElementParametersType(ElementParametersType object) {
				return createElementParametersTypeAdapter();
			}
			@Override
			public Adapter caseEnumParameterType(EnumParameterType object) {
				return createEnumParameterTypeAdapter();
			}
			@Override
			public Adapter caseErlangDistributionType(ErlangDistributionType object) {
				return createErlangDistributionTypeAdapter();
			}
			@Override
			public Adapter caseExpressionParameterType(ExpressionParameterType object) {
				return createExpressionParameterTypeAdapter();
			}
			@Override
			public Adapter caseFloatingParameterType(FloatingParameterType object) {
				return createFloatingParameterTypeAdapter();
			}
			@Override
			public Adapter caseGammaDistributionType(GammaDistributionType object) {
				return createGammaDistributionTypeAdapter();
			}
			@Override
			public Adapter caseLogNormalDistributionType(LogNormalDistributionType object) {
				return createLogNormalDistributionTypeAdapter();
			}
			@Override
			public Adapter caseNegativeExponentialDistributionType(NegativeExponentialDistributionType object) {
				return createNegativeExponentialDistributionTypeAdapter();
			}
			@Override
			public Adapter caseNormalDistributionType(NormalDistributionType object) {
				return createNormalDistributionTypeAdapter();
			}
			@Override
			public Adapter caseNumericParameterType(NumericParameterType object) {
				return createNumericParameterTypeAdapter();
			}
			@Override
			public Adapter caseParameter(Parameter object) {
				return createParameterAdapter();
			}
			@Override
			public Adapter caseParameterValue(ParameterValue object) {
				return createParameterValueAdapter();
			}
			@Override
			public Adapter casePoissonDistributionType(PoissonDistributionType object) {
				return createPoissonDistributionTypeAdapter();
			}
			@Override
			public Adapter casePriorityParameters(PriorityParameters object) {
				return createPriorityParametersAdapter();
			}
			@Override
			public Adapter casePropertyParameters(PropertyParameters object) {
				return createPropertyParametersAdapter();
			}
			@Override
			public Adapter casePropertyType(PropertyType object) {
				return createPropertyTypeAdapter();
			}
			@Override
			public Adapter caseResourceParameters(ResourceParameters object) {
				return createResourceParametersAdapter();
			}
			@Override
			public Adapter caseScenario(Scenario object) {
				return createScenarioAdapter();
			}
			@Override
			public Adapter caseScenarioParameters(ScenarioParameters object) {
				return createScenarioParametersAdapter();
			}
			@Override
			public Adapter caseScenarioParametersType(ScenarioParametersType object) {
				return createScenarioParametersTypeAdapter();
			}
			@Override
			public Adapter caseStringParameterType(StringParameterType object) {
				return createStringParameterTypeAdapter();
			}
			@Override
			public Adapter caseTimeParameters(TimeParameters object) {
				return createTimeParametersAdapter();
			}
			@Override
			public Adapter caseTriangularDistributionType(TriangularDistributionType object) {
				return createTriangularDistributionTypeAdapter();
			}
			@Override
			public Adapter caseTruncatedNormalDistributionType(TruncatedNormalDistributionType object) {
				return createTruncatedNormalDistributionTypeAdapter();
			}
			@Override
			public Adapter caseUniformDistributionType(UniformDistributionType object) {
				return createUniformDistributionTypeAdapter();
			}
			@Override
			public Adapter caseUserDistributionDataPointType(UserDistributionDataPointType object) {
				return createUserDistributionDataPointTypeAdapter();
			}
			@Override
			public Adapter caseUserDistributionType(UserDistributionType object) {
				return createUserDistributionTypeAdapter();
			}
			@Override
			public Adapter caseVendorExtension(VendorExtension object) {
				return createVendorExtensionAdapter();
			}
			@Override
			public Adapter caseWeibullDistributionType(WeibullDistributionType object) {
				return createWeibullDistributionTypeAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link bpsim.BetaDistributionType <em>Beta Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.BetaDistributionType
	 * @generated
	 */
	public Adapter createBetaDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.BinomialDistributionType <em>Binomial Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.BinomialDistributionType
	 * @generated
	 */
	public Adapter createBinomialDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.BooleanParameterType <em>Boolean Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.BooleanParameterType
	 * @generated
	 */
	public Adapter createBooleanParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.BPSimDataType <em>BP Sim Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.BPSimDataType
	 * @generated
	 */
	public Adapter createBPSimDataTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.Calendar <em>Calendar</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.Calendar
	 * @generated
	 */
	public Adapter createCalendarAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ConstantParameter <em>Constant Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ConstantParameter
	 * @generated
	 */
	public Adapter createConstantParameterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ControlParameters <em>Control Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ControlParameters
	 * @generated
	 */
	public Adapter createControlParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.CostParameters <em>Cost Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.CostParameters
	 * @generated
	 */
	public Adapter createCostParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.DateTimeParameterType <em>Date Time Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.DateTimeParameterType
	 * @generated
	 */
	public Adapter createDateTimeParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.DistributionParameter <em>Distribution Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.DistributionParameter
	 * @generated
	 */
	public Adapter createDistributionParameterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.DocumentRoot <em>Document Root</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.DocumentRoot
	 * @generated
	 */
	public Adapter createDocumentRootAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.DurationParameterType <em>Duration Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.DurationParameterType
	 * @generated
	 */
	public Adapter createDurationParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ElementParameters <em>Element Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ElementParameters
	 * @generated
	 */
	public Adapter createElementParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ElementParametersType <em>Element Parameters Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ElementParametersType
	 * @generated
	 */
	public Adapter createElementParametersTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.EnumParameterType <em>Enum Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.EnumParameterType
	 * @generated
	 */
	public Adapter createEnumParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ErlangDistributionType <em>Erlang Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ErlangDistributionType
	 * @generated
	 */
	public Adapter createErlangDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ExpressionParameterType <em>Expression Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ExpressionParameterType
	 * @generated
	 */
	public Adapter createExpressionParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.FloatingParameterType <em>Floating Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.FloatingParameterType
	 * @generated
	 */
	public Adapter createFloatingParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.GammaDistributionType <em>Gamma Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.GammaDistributionType
	 * @generated
	 */
	public Adapter createGammaDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.LogNormalDistributionType <em>Log Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.LogNormalDistributionType
	 * @generated
	 */
	public Adapter createLogNormalDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.NegativeExponentialDistributionType <em>Negative Exponential Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.NegativeExponentialDistributionType
	 * @generated
	 */
	public Adapter createNegativeExponentialDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.NormalDistributionType <em>Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.NormalDistributionType
	 * @generated
	 */
	public Adapter createNormalDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.NumericParameterType <em>Numeric Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.NumericParameterType
	 * @generated
	 */
	public Adapter createNumericParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.Parameter
	 * @generated
	 */
	public Adapter createParameterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ParameterValue
	 * @generated
	 */
	public Adapter createParameterValueAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.PoissonDistributionType <em>Poisson Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.PoissonDistributionType
	 * @generated
	 */
	public Adapter createPoissonDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.PriorityParameters <em>Priority Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.PriorityParameters
	 * @generated
	 */
	public Adapter createPriorityParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.PropertyParameters <em>Property Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.PropertyParameters
	 * @generated
	 */
	public Adapter createPropertyParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.PropertyType <em>Property Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.PropertyType
	 * @generated
	 */
	public Adapter createPropertyTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ResourceParameters <em>Resource Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ResourceParameters
	 * @generated
	 */
	public Adapter createResourceParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.Scenario <em>Scenario</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.Scenario
	 * @generated
	 */
	public Adapter createScenarioAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ScenarioParameters <em>Scenario Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ScenarioParameters
	 * @generated
	 */
	public Adapter createScenarioParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.ScenarioParametersType <em>Scenario Parameters Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.ScenarioParametersType
	 * @generated
	 */
	public Adapter createScenarioParametersTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.StringParameterType <em>String Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.StringParameterType
	 * @generated
	 */
	public Adapter createStringParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.TimeParameters <em>Time Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.TimeParameters
	 * @generated
	 */
	public Adapter createTimeParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.TriangularDistributionType <em>Triangular Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.TriangularDistributionType
	 * @generated
	 */
	public Adapter createTriangularDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.TruncatedNormalDistributionType <em>Truncated Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.TruncatedNormalDistributionType
	 * @generated
	 */
	public Adapter createTruncatedNormalDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.UniformDistributionType <em>Uniform Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.UniformDistributionType
	 * @generated
	 */
	public Adapter createUniformDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.UserDistributionDataPointType <em>User Distribution Data Point Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.UserDistributionDataPointType
	 * @generated
	 */
	public Adapter createUserDistributionDataPointTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.UserDistributionType <em>User Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.UserDistributionType
	 * @generated
	 */
	public Adapter createUserDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.VendorExtension <em>Vendor Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.VendorExtension
	 * @generated
	 */
	public Adapter createVendorExtensionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link bpsim.WeibullDistributionType <em>Weibull Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see bpsim.WeibullDistributionType
	 * @generated
	 */
	public Adapter createWeibullDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //BpsimAdapterFactory
