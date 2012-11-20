/**
 */
package org.jboss.drools.impl;

import java.math.BigInteger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.jboss.drools.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DroolsFactoryImpl extends EFactoryImpl implements DroolsFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DroolsFactory init() {
		try {
			DroolsFactory theDroolsFactory = (DroolsFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.jboss.org/drools"); 
			if (theDroolsFactory != null) {
				return theDroolsFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new DroolsFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DroolsFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case DroolsPackage.BETA_DISTRIBUTION_TYPE: return createBetaDistributionType();
			case DroolsPackage.BINOMIAL_DISTRIBUTION_TYPE: return createBinomialDistributionType();
			case DroolsPackage.BOOLEAN_PARAMETER_TYPE: return createBooleanParameterType();
			case DroolsPackage.CALENDAR: return createCalendar();
			case DroolsPackage.CONSTANT_PARAMETER: return createConstantParameter();
			case DroolsPackage.CONTROL_PARAMETERS: return createControlParameters();
			case DroolsPackage.COST_PARAMETERS: return createCostParameters();
			case DroolsPackage.DATE_TIME_PARAMETER_TYPE: return createDateTimeParameterType();
			case DroolsPackage.DECIMAL_PARAMETER_TYPE: return createDecimalParameterType();
			case DroolsPackage.DISTRIBUTION_PARAMETER: return createDistributionParameter();
			case DroolsPackage.DOCUMENT_ROOT: return createDocumentRoot();
			case DroolsPackage.DURATION_PARAMETER_TYPE: return createDurationParameterType();
			case DroolsPackage.ELEMENT_PARAMETERS: return createElementParameters();
			case DroolsPackage.ENUM_PARAMETER_TYPE: return createEnumParameterType();
			case DroolsPackage.ERLANG_DISTRIBUTION_TYPE: return createErlangDistributionType();
			case DroolsPackage.EXPRESSION_PARAMETER_TYPE: return createExpressionParameterType();
			case DroolsPackage.FLOATING_PARAMETER_TYPE: return createFloatingParameterType();
			case DroolsPackage.GAMMA_DISTRIBUTION_TYPE: return createGammaDistributionType();
			case DroolsPackage.GLOBAL_TYPE: return createGlobalType();
			case DroolsPackage.IMPORT_TYPE: return createImportType();
			case DroolsPackage.LOG_NORMAL_DISTRIBUTION_TYPE: return createLogNormalDistributionType();
			case DroolsPackage.METADATA_TYPE: return createMetadataType();
			case DroolsPackage.METAENTRY_TYPE: return createMetaentryType();
			case DroolsPackage.NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE: return createNegativeExponentialDistributionType();
			case DroolsPackage.NORMAL_DISTRIBUTION_TYPE: return createNormalDistributionType();
			case DroolsPackage.NUMERIC_PARAMETER_TYPE: return createNumericParameterType();
			case DroolsPackage.ON_ENTRY_SCRIPT_TYPE: return createOnEntryScriptType();
			case DroolsPackage.ON_EXIT_SCRIPT_TYPE: return createOnExitScriptType();
			case DroolsPackage.PARAMETER: return createParameter();
			case DroolsPackage.PARAMETER_VALUE: return createParameterValue();
			case DroolsPackage.POISSON_DISTRIBUTION_TYPE: return createPoissonDistributionType();
			case DroolsPackage.PRIORITY_PARAMETERS: return createPriorityParameters();
			case DroolsPackage.PROCESS_ANALYSIS_DATA_TYPE: return createProcessAnalysisDataType();
			case DroolsPackage.PROPERTY_PARAMETERS: return createPropertyParameters();
			case DroolsPackage.PROPERTY_TYPE: return createPropertyType();
			case DroolsPackage.RANDOM_DISTRIBUTION_TYPE: return createRandomDistributionType();
			case DroolsPackage.RESOURCE_PARAMETERS: return createResourceParameters();
			case DroolsPackage.SCENARIO: return createScenario();
			case DroolsPackage.SCENARIO_PARAMETERS: return createScenarioParameters();
			case DroolsPackage.STRING_PARAMETER_TYPE: return createStringParameterType();
			case DroolsPackage.TIME_PARAMETERS: return createTimeParameters();
			case DroolsPackage.TRIANGULAR_DISTRIBUTION_TYPE: return createTriangularDistributionType();
			case DroolsPackage.TRUNCATED_NORMAL_DISTRIBUTION_TYPE: return createTruncatedNormalDistributionType();
			case DroolsPackage.UNIFORM_DISTRIBUTION_TYPE: return createUniformDistributionType();
			case DroolsPackage.USER_DISTRIBUTION_DATA_POINT_TYPE: return createUserDistributionDataPointType();
			case DroolsPackage.USER_DISTRIBUTION_TYPE: return createUserDistributionType();
			case DroolsPackage.VENDOR_EXTENSION: return createVendorExtension();
			case DroolsPackage.WEIBULL_DISTRIBUTION_TYPE: return createWeibullDistributionType();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case DroolsPackage.RESULT_TYPE:
				return createResultTypeFromString(eDataType, initialValue);
			case DroolsPackage.TIME_UNIT:
				return createTimeUnitFromString(eDataType, initialValue);
			case DroolsPackage.PACKAGE_NAME_TYPE:
				return createPackageNameTypeFromString(eDataType, initialValue);
			case DroolsPackage.PRIORITY_TYPE:
				return createPriorityTypeFromString(eDataType, initialValue);
			case DroolsPackage.RESULT_TYPE_OBJECT:
				return createResultTypeObjectFromString(eDataType, initialValue);
			case DroolsPackage.RULE_FLOW_GROUP_TYPE:
				return createRuleFlowGroupTypeFromString(eDataType, initialValue);
			case DroolsPackage.TASK_NAME_TYPE:
				return createTaskNameTypeFromString(eDataType, initialValue);
			case DroolsPackage.TIME_UNIT_OBJECT:
				return createTimeUnitObjectFromString(eDataType, initialValue);
			case DroolsPackage.VERSION_TYPE:
				return createVersionTypeFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case DroolsPackage.RESULT_TYPE:
				return convertResultTypeToString(eDataType, instanceValue);
			case DroolsPackage.TIME_UNIT:
				return convertTimeUnitToString(eDataType, instanceValue);
			case DroolsPackage.PACKAGE_NAME_TYPE:
				return convertPackageNameTypeToString(eDataType, instanceValue);
			case DroolsPackage.PRIORITY_TYPE:
				return convertPriorityTypeToString(eDataType, instanceValue);
			case DroolsPackage.RESULT_TYPE_OBJECT:
				return convertResultTypeObjectToString(eDataType, instanceValue);
			case DroolsPackage.RULE_FLOW_GROUP_TYPE:
				return convertRuleFlowGroupTypeToString(eDataType, instanceValue);
			case DroolsPackage.TASK_NAME_TYPE:
				return convertTaskNameTypeToString(eDataType, instanceValue);
			case DroolsPackage.TIME_UNIT_OBJECT:
				return convertTimeUnitObjectToString(eDataType, instanceValue);
			case DroolsPackage.VERSION_TYPE:
				return convertVersionTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BetaDistributionType createBetaDistributionType() {
		BetaDistributionTypeImpl betaDistributionType = new BetaDistributionTypeImpl();
		return betaDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BinomialDistributionType createBinomialDistributionType() {
		BinomialDistributionTypeImpl binomialDistributionType = new BinomialDistributionTypeImpl();
		return binomialDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BooleanParameterType createBooleanParameterType() {
		BooleanParameterTypeImpl booleanParameterType = new BooleanParameterTypeImpl();
		return booleanParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Calendar createCalendar() {
		CalendarImpl calendar = new CalendarImpl();
		return calendar;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConstantParameter createConstantParameter() {
		ConstantParameterImpl constantParameter = new ConstantParameterImpl();
		return constantParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ControlParameters createControlParameters() {
		ControlParametersImpl controlParameters = new ControlParametersImpl();
		return controlParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CostParameters createCostParameters() {
		CostParametersImpl costParameters = new CostParametersImpl();
		return costParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DateTimeParameterType createDateTimeParameterType() {
		DateTimeParameterTypeImpl dateTimeParameterType = new DateTimeParameterTypeImpl();
		return dateTimeParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DecimalParameterType createDecimalParameterType() {
		DecimalParameterTypeImpl decimalParameterType = new DecimalParameterTypeImpl();
		return decimalParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DistributionParameter createDistributionParameter() {
		DistributionParameterImpl distributionParameter = new DistributionParameterImpl();
		return distributionParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DocumentRoot createDocumentRoot() {
		DocumentRootImpl documentRoot = new DocumentRootImpl();
		return documentRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DurationParameterType createDurationParameterType() {
		DurationParameterTypeImpl durationParameterType = new DurationParameterTypeImpl();
		return durationParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ElementParameters createElementParameters() {
		ElementParametersImpl elementParameters = new ElementParametersImpl();
		return elementParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnumParameterType createEnumParameterType() {
		EnumParameterTypeImpl enumParameterType = new EnumParameterTypeImpl();
		return enumParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ErlangDistributionType createErlangDistributionType() {
		ErlangDistributionTypeImpl erlangDistributionType = new ErlangDistributionTypeImpl();
		return erlangDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExpressionParameterType createExpressionParameterType() {
		ExpressionParameterTypeImpl expressionParameterType = new ExpressionParameterTypeImpl();
		return expressionParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FloatingParameterType createFloatingParameterType() {
		FloatingParameterTypeImpl floatingParameterType = new FloatingParameterTypeImpl();
		return floatingParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GammaDistributionType createGammaDistributionType() {
		GammaDistributionTypeImpl gammaDistributionType = new GammaDistributionTypeImpl();
		return gammaDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GlobalType createGlobalType() {
		GlobalTypeImpl globalType = new GlobalTypeImpl();
		return globalType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImportType createImportType() {
		ImportTypeImpl importType = new ImportTypeImpl();
		return importType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LogNormalDistributionType createLogNormalDistributionType() {
		LogNormalDistributionTypeImpl logNormalDistributionType = new LogNormalDistributionTypeImpl();
		return logNormalDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetadataType createMetadataType() {
		MetadataTypeImpl metadataType = new MetadataTypeImpl();
		return metadataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetaentryType createMetaentryType() {
		MetaentryTypeImpl metaentryType = new MetaentryTypeImpl();
		return metaentryType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NegativeExponentialDistributionType createNegativeExponentialDistributionType() {
		NegativeExponentialDistributionTypeImpl negativeExponentialDistributionType = new NegativeExponentialDistributionTypeImpl();
		return negativeExponentialDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NormalDistributionType createNormalDistributionType() {
		NormalDistributionTypeImpl normalDistributionType = new NormalDistributionTypeImpl();
		return normalDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NumericParameterType createNumericParameterType() {
		NumericParameterTypeImpl numericParameterType = new NumericParameterTypeImpl();
		return numericParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OnEntryScriptType createOnEntryScriptType() {
		OnEntryScriptTypeImpl onEntryScriptType = new OnEntryScriptTypeImpl();
		return onEntryScriptType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OnExitScriptType createOnExitScriptType() {
		OnExitScriptTypeImpl onExitScriptType = new OnExitScriptTypeImpl();
		return onExitScriptType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Parameter createParameter() {
		ParameterImpl parameter = new ParameterImpl();
		return parameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterValue createParameterValue() {
		ParameterValueImpl parameterValue = new ParameterValueImpl();
		return parameterValue;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PoissonDistributionType createPoissonDistributionType() {
		PoissonDistributionTypeImpl poissonDistributionType = new PoissonDistributionTypeImpl();
		return poissonDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PriorityParameters createPriorityParameters() {
		PriorityParametersImpl priorityParameters = new PriorityParametersImpl();
		return priorityParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProcessAnalysisDataType createProcessAnalysisDataType() {
		ProcessAnalysisDataTypeImpl processAnalysisDataType = new ProcessAnalysisDataTypeImpl();
		return processAnalysisDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PropertyParameters createPropertyParameters() {
		PropertyParametersImpl propertyParameters = new PropertyParametersImpl();
		return propertyParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PropertyType createPropertyType() {
		PropertyTypeImpl propertyType = new PropertyTypeImpl();
		return propertyType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RandomDistributionType createRandomDistributionType() {
		RandomDistributionTypeImpl randomDistributionType = new RandomDistributionTypeImpl();
		return randomDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResourceParameters createResourceParameters() {
		ResourceParametersImpl resourceParameters = new ResourceParametersImpl();
		return resourceParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Scenario createScenario() {
		ScenarioImpl scenario = new ScenarioImpl();
		return scenario;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScenarioParameters createScenarioParameters() {
		ScenarioParametersImpl scenarioParameters = new ScenarioParametersImpl();
		return scenarioParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StringParameterType createStringParameterType() {
		StringParameterTypeImpl stringParameterType = new StringParameterTypeImpl();
		return stringParameterType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimeParameters createTimeParameters() {
		TimeParametersImpl timeParameters = new TimeParametersImpl();
		return timeParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TriangularDistributionType createTriangularDistributionType() {
		TriangularDistributionTypeImpl triangularDistributionType = new TriangularDistributionTypeImpl();
		return triangularDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TruncatedNormalDistributionType createTruncatedNormalDistributionType() {
		TruncatedNormalDistributionTypeImpl truncatedNormalDistributionType = new TruncatedNormalDistributionTypeImpl();
		return truncatedNormalDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UniformDistributionType createUniformDistributionType() {
		UniformDistributionTypeImpl uniformDistributionType = new UniformDistributionTypeImpl();
		return uniformDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UserDistributionDataPointType createUserDistributionDataPointType() {
		UserDistributionDataPointTypeImpl userDistributionDataPointType = new UserDistributionDataPointTypeImpl();
		return userDistributionDataPointType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UserDistributionType createUserDistributionType() {
		UserDistributionTypeImpl userDistributionType = new UserDistributionTypeImpl();
		return userDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VendorExtension createVendorExtension() {
		VendorExtensionImpl vendorExtension = new VendorExtensionImpl();
		return vendorExtension;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WeibullDistributionType createWeibullDistributionType() {
		WeibullDistributionTypeImpl weibullDistributionType = new WeibullDistributionTypeImpl();
		return weibullDistributionType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResultType createResultTypeFromString(EDataType eDataType, String initialValue) {
		ResultType result = ResultType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertResultTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimeUnit createTimeUnitFromString(EDataType eDataType, String initialValue) {
		TimeUnit result = TimeUnit.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTimeUnitToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createPackageNameTypeFromString(EDataType eDataType, String initialValue) {
		return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPackageNameTypeToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BigInteger createPriorityTypeFromString(EDataType eDataType, String initialValue) {
		return (BigInteger)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.INTEGER, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPriorityTypeToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.INTEGER, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ResultType createResultTypeObjectFromString(EDataType eDataType, String initialValue) {
		return createResultTypeFromString(DroolsPackage.Literals.RESULT_TYPE, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertResultTypeObjectToString(EDataType eDataType, Object instanceValue) {
		return convertResultTypeToString(DroolsPackage.Literals.RESULT_TYPE, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createRuleFlowGroupTypeFromString(EDataType eDataType, String initialValue) {
		return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertRuleFlowGroupTypeToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createTaskNameTypeFromString(EDataType eDataType, String initialValue) {
		return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTaskNameTypeToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimeUnit createTimeUnitObjectFromString(EDataType eDataType, String initialValue) {
		return createTimeUnitFromString(DroolsPackage.Literals.TIME_UNIT, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTimeUnitObjectToString(EDataType eDataType, Object instanceValue) {
		return convertTimeUnitToString(DroolsPackage.Literals.TIME_UNIT, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String createVersionTypeFromString(EDataType eDataType, String initialValue) {
		return (String)XMLTypeFactory.eINSTANCE.createFromString(XMLTypePackage.Literals.STRING, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertVersionTypeToString(EDataType eDataType, Object instanceValue) {
		return XMLTypeFactory.eINSTANCE.convertToString(XMLTypePackage.Literals.STRING, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DroolsPackage getDroolsPackage() {
		return (DroolsPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static DroolsPackage getPackage() {
		return DroolsPackage.eINSTANCE;
	}

} //DroolsFactoryImpl
