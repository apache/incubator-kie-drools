/**
 */
package org.jboss.drools.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.jboss.drools.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.jboss.drools.DroolsPackage
 * @generated
 */
public class DroolsAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static DroolsPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DroolsAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = DroolsPackage.eINSTANCE;
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
	protected DroolsSwitch<Adapter> modelSwitch =
		new DroolsSwitch<Adapter>() {
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
			public Adapter caseDecimalParameterType(DecimalParameterType object) {
				return createDecimalParameterTypeAdapter();
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
			public Adapter caseGlobalType(GlobalType object) {
				return createGlobalTypeAdapter();
			}
			@Override
			public Adapter caseImportType(ImportType object) {
				return createImportTypeAdapter();
			}
			@Override
			public Adapter caseLogNormalDistributionType(LogNormalDistributionType object) {
				return createLogNormalDistributionTypeAdapter();
			}
			@Override
			public Adapter caseMetadataType(MetadataType object) {
				return createMetadataTypeAdapter();
			}
			@Override
			public Adapter caseMetaentryType(MetaentryType object) {
				return createMetaentryTypeAdapter();
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
			public Adapter caseOnEntryScriptType(OnEntryScriptType object) {
				return createOnEntryScriptTypeAdapter();
			}
			@Override
			public Adapter caseOnExitScriptType(OnExitScriptType object) {
				return createOnExitScriptTypeAdapter();
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
			public Adapter caseProcessAnalysisDataType(ProcessAnalysisDataType object) {
				return createProcessAnalysisDataTypeAdapter();
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
			public Adapter caseRandomDistributionType(RandomDistributionType object) {
				return createRandomDistributionTypeAdapter();
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
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.BetaDistributionType <em>Beta Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.BetaDistributionType
	 * @generated
	 */
	public Adapter createBetaDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.BinomialDistributionType <em>Binomial Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.BinomialDistributionType
	 * @generated
	 */
	public Adapter createBinomialDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.BooleanParameterType <em>Boolean Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.BooleanParameterType
	 * @generated
	 */
	public Adapter createBooleanParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.Calendar <em>Calendar</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.Calendar
	 * @generated
	 */
	public Adapter createCalendarAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ConstantParameter <em>Constant Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ConstantParameter
	 * @generated
	 */
	public Adapter createConstantParameterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ControlParameters <em>Control Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ControlParameters
	 * @generated
	 */
	public Adapter createControlParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.CostParameters <em>Cost Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.CostParameters
	 * @generated
	 */
	public Adapter createCostParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.DateTimeParameterType <em>Date Time Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.DateTimeParameterType
	 * @generated
	 */
	public Adapter createDateTimeParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.DecimalParameterType <em>Decimal Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.DecimalParameterType
	 * @generated
	 */
	public Adapter createDecimalParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.DistributionParameter <em>Distribution Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.DistributionParameter
	 * @generated
	 */
	public Adapter createDistributionParameterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.DocumentRoot <em>Document Root</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.DocumentRoot
	 * @generated
	 */
	public Adapter createDocumentRootAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.DurationParameterType <em>Duration Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.DurationParameterType
	 * @generated
	 */
	public Adapter createDurationParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ElementParameters <em>Element Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ElementParameters
	 * @generated
	 */
	public Adapter createElementParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ElementParametersType <em>Element Parameters Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ElementParametersType
	 * @generated
	 */
	public Adapter createElementParametersTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.EnumParameterType <em>Enum Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.EnumParameterType
	 * @generated
	 */
	public Adapter createEnumParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ErlangDistributionType <em>Erlang Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ErlangDistributionType
	 * @generated
	 */
	public Adapter createErlangDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ExpressionParameterType <em>Expression Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ExpressionParameterType
	 * @generated
	 */
	public Adapter createExpressionParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.FloatingParameterType <em>Floating Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.FloatingParameterType
	 * @generated
	 */
	public Adapter createFloatingParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.GammaDistributionType <em>Gamma Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.GammaDistributionType
	 * @generated
	 */
	public Adapter createGammaDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.GlobalType <em>Global Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.GlobalType
	 * @generated
	 */
	public Adapter createGlobalTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ImportType <em>Import Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ImportType
	 * @generated
	 */
	public Adapter createImportTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.LogNormalDistributionType <em>Log Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.LogNormalDistributionType
	 * @generated
	 */
	public Adapter createLogNormalDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.MetadataType <em>Metadata Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.MetadataType
	 * @generated
	 */
	public Adapter createMetadataTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.MetaentryType <em>Metaentry Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.MetaentryType
	 * @generated
	 */
	public Adapter createMetaentryTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.NegativeExponentialDistributionType <em>Negative Exponential Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.NegativeExponentialDistributionType
	 * @generated
	 */
	public Adapter createNegativeExponentialDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.NormalDistributionType <em>Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.NormalDistributionType
	 * @generated
	 */
	public Adapter createNormalDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.NumericParameterType <em>Numeric Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.NumericParameterType
	 * @generated
	 */
	public Adapter createNumericParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.OnEntryScriptType <em>On Entry Script Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.OnEntryScriptType
	 * @generated
	 */
	public Adapter createOnEntryScriptTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.OnExitScriptType <em>On Exit Script Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.OnExitScriptType
	 * @generated
	 */
	public Adapter createOnExitScriptTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.Parameter
	 * @generated
	 */
	public Adapter createParameterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ParameterValue
	 * @generated
	 */
	public Adapter createParameterValueAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.PoissonDistributionType <em>Poisson Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.PoissonDistributionType
	 * @generated
	 */
	public Adapter createPoissonDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.PriorityParameters <em>Priority Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.PriorityParameters
	 * @generated
	 */
	public Adapter createPriorityParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ProcessAnalysisDataType <em>Process Analysis Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ProcessAnalysisDataType
	 * @generated
	 */
	public Adapter createProcessAnalysisDataTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.PropertyParameters <em>Property Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.PropertyParameters
	 * @generated
	 */
	public Adapter createPropertyParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.PropertyType <em>Property Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.PropertyType
	 * @generated
	 */
	public Adapter createPropertyTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.RandomDistributionType <em>Random Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.RandomDistributionType
	 * @generated
	 */
	public Adapter createRandomDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ResourceParameters <em>Resource Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ResourceParameters
	 * @generated
	 */
	public Adapter createResourceParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.Scenario <em>Scenario</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.Scenario
	 * @generated
	 */
	public Adapter createScenarioAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ScenarioParameters <em>Scenario Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ScenarioParameters
	 * @generated
	 */
	public Adapter createScenarioParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.ScenarioParametersType <em>Scenario Parameters Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.ScenarioParametersType
	 * @generated
	 */
	public Adapter createScenarioParametersTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.StringParameterType <em>String Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.StringParameterType
	 * @generated
	 */
	public Adapter createStringParameterTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.TimeParameters <em>Time Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.TimeParameters
	 * @generated
	 */
	public Adapter createTimeParametersAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.TriangularDistributionType <em>Triangular Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.TriangularDistributionType
	 * @generated
	 */
	public Adapter createTriangularDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.TruncatedNormalDistributionType <em>Truncated Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.TruncatedNormalDistributionType
	 * @generated
	 */
	public Adapter createTruncatedNormalDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.UniformDistributionType <em>Uniform Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.UniformDistributionType
	 * @generated
	 */
	public Adapter createUniformDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.UserDistributionDataPointType <em>User Distribution Data Point Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.UserDistributionDataPointType
	 * @generated
	 */
	public Adapter createUserDistributionDataPointTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.UserDistributionType <em>User Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.UserDistributionType
	 * @generated
	 */
	public Adapter createUserDistributionTypeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.VendorExtension <em>Vendor Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.VendorExtension
	 * @generated
	 */
	public Adapter createVendorExtensionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.jboss.drools.WeibullDistributionType <em>Weibull Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.jboss.drools.WeibullDistributionType
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

} //DroolsAdapterFactory
