/**
 */
package org.jboss.drools;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.jboss.drools.DroolsFactory
 * @model kind="package"
 * @generated
 */
public interface DroolsPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "drools";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.jboss.org/drools";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "drools";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DroolsPackage eINSTANCE = org.jboss.drools.impl.DroolsPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ParameterValueImpl <em>Parameter Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ParameterValueImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getParameterValue()
	 * @generated
	 */
	int PARAMETER_VALUE = 31;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_VALUE__INSTANCE = 0;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_VALUE__RESULT = 1;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_VALUE__VALID_FOR = 2;

	/**
	 * The number of structural features of the '<em>Parameter Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_VALUE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.DistributionParameterImpl <em>Distribution Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.DistributionParameterImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getDistributionParameter()
	 * @generated
	 */
	int DISTRIBUTION_PARAMETER = 8;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_PARAMETER__INSTANCE = PARAMETER_VALUE__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_PARAMETER__RESULT = PARAMETER_VALUE__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_PARAMETER__VALID_FOR = PARAMETER_VALUE__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_PARAMETER__DISCRETE = PARAMETER_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Distribution Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_PARAMETER_FEATURE_COUNT = PARAMETER_VALUE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.BetaDistributionTypeImpl <em>Beta Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.BetaDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getBetaDistributionType()
	 * @generated
	 */
	int BETA_DISTRIBUTION_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Scale</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_TYPE__SCALE = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_TYPE__SHAPE = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Beta Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.BinomialDistributionTypeImpl <em>Binomial Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.BinomialDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getBinomialDistributionType()
	 * @generated
	 */
	int BINOMIAL_DISTRIBUTION_TYPE = 1;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_TYPE__PROBABILITY = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Trials</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_TYPE__TRIALS = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Binomial Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ConstantParameterImpl <em>Constant Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ConstantParameterImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getConstantParameter()
	 * @generated
	 */
	int CONSTANT_PARAMETER = 4;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTANT_PARAMETER__INSTANCE = PARAMETER_VALUE__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTANT_PARAMETER__RESULT = PARAMETER_VALUE__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTANT_PARAMETER__VALID_FOR = PARAMETER_VALUE__VALID_FOR;

	/**
	 * The number of structural features of the '<em>Constant Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONSTANT_PARAMETER_FEATURE_COUNT = PARAMETER_VALUE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.BooleanParameterTypeImpl <em>Boolean Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.BooleanParameterTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getBooleanParameterType()
	 * @generated
	 */
	int BOOLEAN_PARAMETER_TYPE = 2;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_PARAMETER_TYPE__INSTANCE = CONSTANT_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_PARAMETER_TYPE__RESULT = CONSTANT_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_PARAMETER_TYPE__VALID_FOR = CONSTANT_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_PARAMETER_TYPE__VALUE = CONSTANT_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Boolean Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BOOLEAN_PARAMETER_TYPE_FEATURE_COUNT = CONSTANT_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.CalendarImpl <em>Calendar</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.CalendarImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getCalendar()
	 * @generated
	 */
	int CALENDAR = 3;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALENDAR__VALUE = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALENDAR__ID = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALENDAR__NAME = 2;

	/**
	 * The number of structural features of the '<em>Calendar</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CALENDAR_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ControlParametersImpl <em>Control Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ControlParametersImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getControlParameters()
	 * @generated
	 */
	int CONTROL_PARAMETERS = 5;

	/**
	 * The feature id for the '<em><b>Probability</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_PARAMETERS__PROBABILITY = 0;

	/**
	 * The feature id for the '<em><b>Inter Trigger Timer</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_PARAMETERS__INTER_TRIGGER_TIMER = 1;

	/**
	 * The feature id for the '<em><b>Max Trigger Count</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_PARAMETERS__MAX_TRIGGER_COUNT = 2;

	/**
	 * The number of structural features of the '<em>Control Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_PARAMETERS_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.CostParametersImpl <em>Cost Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.CostParametersImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getCostParameters()
	 * @generated
	 */
	int COST_PARAMETERS = 6;

	/**
	 * The feature id for the '<em><b>Fixed Cost</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COST_PARAMETERS__FIXED_COST = 0;

	/**
	 * The feature id for the '<em><b>Unit Cost</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COST_PARAMETERS__UNIT_COST = 1;

	/**
	 * The number of structural features of the '<em>Cost Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COST_PARAMETERS_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.DateTimeParameterTypeImpl <em>Date Time Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.DateTimeParameterTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getDateTimeParameterType()
	 * @generated
	 */
	int DATE_TIME_PARAMETER_TYPE = 7;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_TIME_PARAMETER_TYPE__INSTANCE = CONSTANT_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_TIME_PARAMETER_TYPE__RESULT = CONSTANT_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_TIME_PARAMETER_TYPE__VALID_FOR = CONSTANT_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_TIME_PARAMETER_TYPE__VALUE = CONSTANT_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Date Time Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATE_TIME_PARAMETER_TYPE_FEATURE_COUNT = CONSTANT_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.DocumentRootImpl <em>Document Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.DocumentRootImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getDocumentRoot()
	 * @generated
	 */
	int DOCUMENT_ROOT = 9;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__MIXED = 0;

	/**
	 * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

	/**
	 * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

	/**
	 * The feature id for the '<em><b>Beta Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BETA_DISTRIBUTION = 3;

	/**
	 * The feature id for the '<em><b>Parameter Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__PARAMETER_VALUE = 4;

	/**
	 * The feature id for the '<em><b>Binomial Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION = 5;

	/**
	 * The feature id for the '<em><b>Boolean Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BOOLEAN_PARAMETER = 6;

	/**
	 * The feature id for the '<em><b>Date Time Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__DATE_TIME_PARAMETER = 7;

	/**
	 * The feature id for the '<em><b>Duration Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__DURATION_PARAMETER = 8;

	/**
	 * The feature id for the '<em><b>Enum Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__ENUM_PARAMETER = 9;

	/**
	 * The feature id for the '<em><b>Erlang Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__ERLANG_DISTRIBUTION = 10;

	/**
	 * The feature id for the '<em><b>Expression Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__EXPRESSION_PARAMETER = 11;

	/**
	 * The feature id for the '<em><b>Floating Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__FLOATING_PARAMETER = 12;

	/**
	 * The feature id for the '<em><b>Gamma Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__GAMMA_DISTRIBUTION = 13;

	/**
	 * The feature id for the '<em><b>Global</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__GLOBAL = 14;

	/**
	 * The feature id for the '<em><b>Import</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__IMPORT = 15;

	/**
	 * The feature id for the '<em><b>Log Normal Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION = 16;

	/**
	 * The feature id for the '<em><b>Metadata</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__METADATA = 17;

	/**
	 * The feature id for the '<em><b>Metaentry</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__METAENTRY = 18;

	/**
	 * The feature id for the '<em><b>Negative Exponential Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION = 19;

	/**
	 * The feature id for the '<em><b>Normal Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__NORMAL_DISTRIBUTION = 20;

	/**
	 * The feature id for the '<em><b>Numeric Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__NUMERIC_PARAMETER = 21;

	/**
	 * The feature id for the '<em><b>On Entry Script</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__ON_ENTRY_SCRIPT = 22;

	/**
	 * The feature id for the '<em><b>On Exit Script</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__ON_EXIT_SCRIPT = 23;

	/**
	 * The feature id for the '<em><b>Poisson Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__POISSON_DISTRIBUTION = 24;

	/**
	 * The feature id for the '<em><b>Process Analysis Data</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA = 25;

	/**
	 * The feature id for the '<em><b>Random Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__RANDOM_DISTRIBUTION = 26;

	/**
	 * The feature id for the '<em><b>String Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__STRING_PARAMETER = 27;

	/**
	 * The feature id for the '<em><b>Triangular Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION = 28;

	/**
	 * The feature id for the '<em><b>Truncated Normal Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION = 29;

	/**
	 * The feature id for the '<em><b>Uniform Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__UNIFORM_DISTRIBUTION = 30;

	/**
	 * The feature id for the '<em><b>User Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__USER_DISTRIBUTION = 31;

	/**
	 * The feature id for the '<em><b>User Distribution Data Point</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT = 32;

	/**
	 * The feature id for the '<em><b>Weibull Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__WEIBULL_DISTRIBUTION = 33;

	/**
	 * The feature id for the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__PACKAGE_NAME = 34;

	/**
	 * The feature id for the '<em><b>Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__PRIORITY = 35;

	/**
	 * The feature id for the '<em><b>Rule Flow Group</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__RULE_FLOW_GROUP = 36;

	/**
	 * The feature id for the '<em><b>Task Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__TASK_NAME = 37;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__VERSION = 38;

	/**
	 * The number of structural features of the '<em>Document Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT_FEATURE_COUNT = 39;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.DurationParameterTypeImpl <em>Duration Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.DurationParameterTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getDurationParameterType()
	 * @generated
	 */
	int DURATION_PARAMETER_TYPE = 10;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DURATION_PARAMETER_TYPE__INSTANCE = CONSTANT_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DURATION_PARAMETER_TYPE__RESULT = CONSTANT_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DURATION_PARAMETER_TYPE__VALID_FOR = CONSTANT_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DURATION_PARAMETER_TYPE__VALUE = CONSTANT_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Duration Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DURATION_PARAMETER_TYPE_FEATURE_COUNT = CONSTANT_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ElementParametersImpl <em>Element Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ElementParametersImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getElementParameters()
	 * @generated
	 */
	int ELEMENT_PARAMETERS = 11;

	/**
	 * The feature id for the '<em><b>Time Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__TIME_PARAMETERS = 0;

	/**
	 * The feature id for the '<em><b>Control Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__CONTROL_PARAMETERS = 1;

	/**
	 * The feature id for the '<em><b>Resource Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__RESOURCE_PARAMETERS = 2;

	/**
	 * The feature id for the '<em><b>Priority Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__PRIORITY_PARAMETERS = 3;

	/**
	 * The feature id for the '<em><b>Cost Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__COST_PARAMETERS = 4;

	/**
	 * The feature id for the '<em><b>Instance Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__INSTANCE_PARAMETERS = 5;

	/**
	 * The feature id for the '<em><b>Vendor Extension</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__VENDOR_EXTENSION = 6;

	/**
	 * The feature id for the '<em><b>Element Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__ELEMENT_ID = 7;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__ID = 8;

	/**
	 * The number of structural features of the '<em>Element Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ElementParametersTypeImpl <em>Element Parameters Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ElementParametersTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getElementParametersType()
	 * @generated
	 */
	int ELEMENT_PARAMETERS_TYPE = 12;

	/**
	 * The feature id for the '<em><b>Time Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__TIME_PARAMETERS = ELEMENT_PARAMETERS__TIME_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Control Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__CONTROL_PARAMETERS = ELEMENT_PARAMETERS__CONTROL_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Resource Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__RESOURCE_PARAMETERS = ELEMENT_PARAMETERS__RESOURCE_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Priority Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__PRIORITY_PARAMETERS = ELEMENT_PARAMETERS__PRIORITY_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Cost Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__COST_PARAMETERS = ELEMENT_PARAMETERS__COST_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Instance Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__INSTANCE_PARAMETERS = ELEMENT_PARAMETERS__INSTANCE_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Vendor Extension</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__VENDOR_EXTENSION = ELEMENT_PARAMETERS__VENDOR_EXTENSION;

	/**
	 * The feature id for the '<em><b>Element Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__ELEMENT_ID = ELEMENT_PARAMETERS__ELEMENT_ID;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__ID = ELEMENT_PARAMETERS__ID;

	/**
	 * The number of structural features of the '<em>Element Parameters Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE_FEATURE_COUNT = ELEMENT_PARAMETERS_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.EnumParameterTypeImpl <em>Enum Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.EnumParameterTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getEnumParameterType()
	 * @generated
	 */
	int ENUM_PARAMETER_TYPE = 13;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_PARAMETER_TYPE__INSTANCE = PARAMETER_VALUE__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_PARAMETER_TYPE__RESULT = PARAMETER_VALUE__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_PARAMETER_TYPE__VALID_FOR = PARAMETER_VALUE__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_PARAMETER_TYPE__GROUP = PARAMETER_VALUE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Parameter Value Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_PARAMETER_TYPE__PARAMETER_VALUE_GROUP = PARAMETER_VALUE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parameter Value</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_PARAMETER_TYPE__PARAMETER_VALUE = PARAMETER_VALUE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Enum Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ENUM_PARAMETER_TYPE_FEATURE_COUNT = PARAMETER_VALUE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ErlangDistributionTypeImpl <em>Erlang Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ErlangDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getErlangDistributionType()
	 * @generated
	 */
	int ERLANG_DISTRIBUTION_TYPE = 14;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ERLANG_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ERLANG_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ERLANG_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ERLANG_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>K</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ERLANG_DISTRIBUTION_TYPE__K = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ERLANG_DISTRIBUTION_TYPE__MEAN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Erlang Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ERLANG_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ExpressionParameterTypeImpl <em>Expression Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ExpressionParameterTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getExpressionParameterType()
	 * @generated
	 */
	int EXPRESSION_PARAMETER_TYPE = 15;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION_PARAMETER_TYPE__INSTANCE = PARAMETER_VALUE__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION_PARAMETER_TYPE__RESULT = PARAMETER_VALUE__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION_PARAMETER_TYPE__VALID_FOR = PARAMETER_VALUE__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION_PARAMETER_TYPE__VALUE = PARAMETER_VALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Expression Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION_PARAMETER_TYPE_FEATURE_COUNT = PARAMETER_VALUE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.FloatingParameterTypeImpl <em>Floating Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.FloatingParameterTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getFloatingParameterType()
	 * @generated
	 */
	int FLOATING_PARAMETER_TYPE = 16;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOATING_PARAMETER_TYPE__INSTANCE = CONSTANT_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOATING_PARAMETER_TYPE__RESULT = CONSTANT_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOATING_PARAMETER_TYPE__VALID_FOR = CONSTANT_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOATING_PARAMETER_TYPE__VALUE = CONSTANT_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Floating Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOATING_PARAMETER_TYPE_FEATURE_COUNT = CONSTANT_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.GammaDistributionTypeImpl <em>Gamma Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.GammaDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getGammaDistributionType()
	 * @generated
	 */
	int GAMMA_DISTRIBUTION_TYPE = 17;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Scale</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_TYPE__SCALE = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_TYPE__SHAPE = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Gamma Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ParameterImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 30;

	/**
	 * The feature id for the '<em><b>Result Request</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__RESULT_REQUEST = 0;

	/**
	 * The feature id for the '<em><b>Parameter Value Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__PARAMETER_VALUE_GROUP = 1;

	/**
	 * The feature id for the '<em><b>Parameter Value</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__PARAMETER_VALUE = 2;

	/**
	 * The feature id for the '<em><b>Kpi</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__KPI = 3;

	/**
	 * The feature id for the '<em><b>Sla</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__SLA = 4;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.GlobalParameterTypeImpl <em>Global Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.GlobalParameterTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getGlobalParameterType()
	 * @generated
	 */
	int GLOBAL_PARAMETER_TYPE = 18;

	/**
	 * The feature id for the '<em><b>Result Request</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_PARAMETER_TYPE__RESULT_REQUEST = PARAMETER__RESULT_REQUEST;

	/**
	 * The feature id for the '<em><b>Parameter Value Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_PARAMETER_TYPE__PARAMETER_VALUE_GROUP = PARAMETER__PARAMETER_VALUE_GROUP;

	/**
	 * The feature id for the '<em><b>Parameter Value</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_PARAMETER_TYPE__PARAMETER_VALUE = PARAMETER__PARAMETER_VALUE;

	/**
	 * The feature id for the '<em><b>Kpi</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_PARAMETER_TYPE__KPI = PARAMETER__KPI;

	/**
	 * The feature id for the '<em><b>Sla</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_PARAMETER_TYPE__SLA = PARAMETER__SLA;

	/**
	 * The feature id for the '<em><b>Property</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_PARAMETER_TYPE__PROPERTY = PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Global Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_PARAMETER_TYPE_FEATURE_COUNT = PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.GlobalTypeImpl <em>Global Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.GlobalTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getGlobalType()
	 * @generated
	 */
	int GLOBAL_TYPE = 19;

	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_TYPE__IDENTIFIER = 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_TYPE__TYPE = 1;

	/**
	 * The number of structural features of the '<em>Global Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GLOBAL_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ImportTypeImpl <em>Import Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ImportTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getImportType()
	 * @generated
	 */
	int IMPORT_TYPE = 20;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_TYPE__NAME = 0;

	/**
	 * The number of structural features of the '<em>Import Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPORT_TYPE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.InstanceParametersImpl <em>Instance Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.InstanceParametersImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getInstanceParameters()
	 * @generated
	 */
	int INSTANCE_PARAMETERS = 21;

	/**
	 * The feature id for the '<em><b>Property</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE_PARAMETERS__PROPERTY = 0;

	/**
	 * The number of structural features of the '<em>Instance Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INSTANCE_PARAMETERS_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.LogNormalDistributionTypeImpl <em>Log Normal Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.LogNormalDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getLogNormalDistributionType()
	 * @generated
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE = 22;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE__MEAN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Standard Deviation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Log Normal Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.MetadataTypeImpl <em>Metadata Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.MetadataTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getMetadataType()
	 * @generated
	 */
	int METADATA_TYPE = 23;

	/**
	 * The feature id for the '<em><b>Metaentry</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_TYPE__METAENTRY = 0;

	/**
	 * The number of structural features of the '<em>Metadata Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_TYPE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.MetaentryTypeImpl <em>Metaentry Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.MetaentryTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getMetaentryType()
	 * @generated
	 */
	int METAENTRY_TYPE = 24;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METAENTRY_TYPE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METAENTRY_TYPE__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Metaentry Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METAENTRY_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.NegativeExponentialDistributionTypeImpl <em>Negative Exponential Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.NegativeExponentialDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getNegativeExponentialDistributionType()
	 * @generated
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE = 25;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE__MEAN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Negative Exponential Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.NormalDistributionTypeImpl <em>Normal Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.NormalDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getNormalDistributionType()
	 * @generated
	 */
	int NORMAL_DISTRIBUTION_TYPE = 26;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_TYPE__MEAN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Standard Deviation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Normal Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.NumericParameterTypeImpl <em>Numeric Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.NumericParameterTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getNumericParameterType()
	 * @generated
	 */
	int NUMERIC_PARAMETER_TYPE = 27;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PARAMETER_TYPE__INSTANCE = CONSTANT_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PARAMETER_TYPE__RESULT = CONSTANT_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PARAMETER_TYPE__VALID_FOR = CONSTANT_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PARAMETER_TYPE__VALUE = CONSTANT_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Numeric Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PARAMETER_TYPE_FEATURE_COUNT = CONSTANT_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.OnEntryScriptTypeImpl <em>On Entry Script Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.OnEntryScriptTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getOnEntryScriptType()
	 * @generated
	 */
	int ON_ENTRY_SCRIPT_TYPE = 28;

	/**
	 * The feature id for the '<em><b>Script</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ON_ENTRY_SCRIPT_TYPE__SCRIPT = 0;

	/**
	 * The feature id for the '<em><b>Script Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ON_ENTRY_SCRIPT_TYPE__SCRIPT_FORMAT = 1;

	/**
	 * The number of structural features of the '<em>On Entry Script Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ON_ENTRY_SCRIPT_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.OnExitScriptTypeImpl <em>On Exit Script Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.OnExitScriptTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getOnExitScriptType()
	 * @generated
	 */
	int ON_EXIT_SCRIPT_TYPE = 29;

	/**
	 * The feature id for the '<em><b>Script</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ON_EXIT_SCRIPT_TYPE__SCRIPT = 0;

	/**
	 * The feature id for the '<em><b>Script Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ON_EXIT_SCRIPT_TYPE__SCRIPT_FORMAT = 1;

	/**
	 * The number of structural features of the '<em>On Exit Script Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ON_EXIT_SCRIPT_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.PoissonDistributionTypeImpl <em>Poisson Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.PoissonDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getPoissonDistributionType()
	 * @generated
	 */
	int POISSON_DISTRIBUTION_TYPE = 32;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_TYPE__MEAN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Poisson Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.PriorityParametersImpl <em>Priority Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.PriorityParametersImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getPriorityParameters()
	 * @generated
	 */
	int PRIORITY_PARAMETERS = 33;

	/**
	 * The feature id for the '<em><b>Interruptible</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIORITY_PARAMETERS__INTERRUPTIBLE = 0;

	/**
	 * The feature id for the '<em><b>Priority</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIORITY_PARAMETERS__PRIORITY = 1;

	/**
	 * The number of structural features of the '<em>Priority Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRIORITY_PARAMETERS_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ProcessAnalysisDataTypeImpl <em>Process Analysis Data Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ProcessAnalysisDataTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getProcessAnalysisDataType()
	 * @generated
	 */
	int PROCESS_ANALYSIS_DATA_TYPE = 34;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_ANALYSIS_DATA_TYPE__GROUP = 0;

	/**
	 * The feature id for the '<em><b>Scenario</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_ANALYSIS_DATA_TYPE__SCENARIO = 1;

	/**
	 * The number of structural features of the '<em>Process Analysis Data Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROCESS_ANALYSIS_DATA_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.PropertyTypeImpl <em>Property Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.PropertyTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getPropertyType()
	 * @generated
	 */
	int PROPERTY_TYPE = 35;

	/**
	 * The feature id for the '<em><b>Result Request</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_TYPE__RESULT_REQUEST = PARAMETER__RESULT_REQUEST;

	/**
	 * The feature id for the '<em><b>Parameter Value Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_TYPE__PARAMETER_VALUE_GROUP = PARAMETER__PARAMETER_VALUE_GROUP;

	/**
	 * The feature id for the '<em><b>Parameter Value</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_TYPE__PARAMETER_VALUE = PARAMETER__PARAMETER_VALUE;

	/**
	 * The feature id for the '<em><b>Kpi</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_TYPE__KPI = PARAMETER__KPI;

	/**
	 * The feature id for the '<em><b>Sla</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_TYPE__SLA = PARAMETER__SLA;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_TYPE__NAME = PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Property Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_TYPE_FEATURE_COUNT = PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.RandomDistributionTypeImpl <em>Random Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.RandomDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getRandomDistributionType()
	 * @generated
	 */
	int RANDOM_DISTRIBUTION_TYPE = 36;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANDOM_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANDOM_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANDOM_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANDOM_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANDOM_DISTRIBUTION_TYPE__MAX = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANDOM_DISTRIBUTION_TYPE__MIN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Random Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RANDOM_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ResourceParametersImpl <em>Resource Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ResourceParametersImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getResourceParameters()
	 * @generated
	 */
	int RESOURCE_PARAMETERS = 37;

	/**
	 * The feature id for the '<em><b>Selection</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_PARAMETERS__SELECTION = 0;

	/**
	 * The feature id for the '<em><b>Availability</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_PARAMETERS__AVAILABILITY = 1;

	/**
	 * The feature id for the '<em><b>Quantity</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_PARAMETERS__QUANTITY = 2;

	/**
	 * The feature id for the '<em><b>Workinghours</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_PARAMETERS__WORKINGHOURS = 3;

	/**
	 * The feature id for the '<em><b>Role</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_PARAMETERS__ROLE = 4;

	/**
	 * The number of structural features of the '<em>Resource Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_PARAMETERS_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ScenarioImpl <em>Scenario</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ScenarioImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getScenario()
	 * @generated
	 */
	int SCENARIO = 38;

	/**
	 * The feature id for the '<em><b>Scenario Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__SCENARIO_PARAMETERS = 0;

	/**
	 * The feature id for the '<em><b>Element Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__ELEMENT_PARAMETERS = 1;

	/**
	 * The feature id for the '<em><b>Calendar</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__CALENDAR = 2;

	/**
	 * The feature id for the '<em><b>Vendor Extension</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__VENDOR_EXTENSION = 3;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__AUTHOR = 4;

	/**
	 * The feature id for the '<em><b>Created</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__CREATED = 5;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__DESCRIPTION = 6;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__ID = 7;

	/**
	 * The feature id for the '<em><b>Inherits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__INHERITS = 8;

	/**
	 * The feature id for the '<em><b>Modified</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__MODIFIED = 9;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__NAME = 10;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__RESULT = 11;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__VERSION = 12;

	/**
	 * The number of structural features of the '<em>Scenario</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_FEATURE_COUNT = 13;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ScenarioParametersImpl <em>Scenario Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ScenarioParametersImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getScenarioParameters()
	 * @generated
	 */
	int SCENARIO_PARAMETERS = 39;

	/**
	 * The feature id for the '<em><b>Start</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS__START = 0;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS__DURATION = 1;

	/**
	 * The feature id for the '<em><b>Global Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS__GLOBAL_PARAMETER = 2;

	/**
	 * The feature id for the '<em><b>Base Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS__BASE_CURRENCY_UNIT = 3;

	/**
	 * The feature id for the '<em><b>Base Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS__BASE_TIME_UNIT = 4;

	/**
	 * The feature id for the '<em><b>Replication</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS__REPLICATION = 5;

	/**
	 * The feature id for the '<em><b>Seed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS__SEED = 6;

	/**
	 * The number of structural features of the '<em>Scenario Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_FEATURE_COUNT = 7;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.ScenarioParametersTypeImpl <em>Scenario Parameters Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ScenarioParametersTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getScenarioParametersType()
	 * @generated
	 */
	int SCENARIO_PARAMETERS_TYPE = 40;

	/**
	 * The feature id for the '<em><b>Start</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_TYPE__START = SCENARIO_PARAMETERS__START;

	/**
	 * The feature id for the '<em><b>Duration</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_TYPE__DURATION = SCENARIO_PARAMETERS__DURATION;

	/**
	 * The feature id for the '<em><b>Global Parameter</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_TYPE__GLOBAL_PARAMETER = SCENARIO_PARAMETERS__GLOBAL_PARAMETER;

	/**
	 * The feature id for the '<em><b>Base Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_TYPE__BASE_CURRENCY_UNIT = SCENARIO_PARAMETERS__BASE_CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Base Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_TYPE__BASE_TIME_UNIT = SCENARIO_PARAMETERS__BASE_TIME_UNIT;

	/**
	 * The feature id for the '<em><b>Replication</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_TYPE__REPLICATION = SCENARIO_PARAMETERS__REPLICATION;

	/**
	 * The feature id for the '<em><b>Seed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_TYPE__SEED = SCENARIO_PARAMETERS__SEED;

	/**
	 * The number of structural features of the '<em>Scenario Parameters Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_TYPE_FEATURE_COUNT = SCENARIO_PARAMETERS_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.StringParameterTypeImpl <em>String Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.StringParameterTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getStringParameterType()
	 * @generated
	 */
	int STRING_PARAMETER_TYPE = 41;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_PARAMETER_TYPE__INSTANCE = CONSTANT_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_PARAMETER_TYPE__RESULT = CONSTANT_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_PARAMETER_TYPE__VALID_FOR = CONSTANT_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_PARAMETER_TYPE__VALUE = CONSTANT_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>String Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_PARAMETER_TYPE_FEATURE_COUNT = CONSTANT_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.TimeParametersImpl <em>Time Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.TimeParametersImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getTimeParameters()
	 * @generated
	 */
	int TIME_PARAMETERS = 42;

	/**
	 * The feature id for the '<em><b>Transfer Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_PARAMETERS__TRANSFER_TIME = 0;

	/**
	 * The feature id for the '<em><b>Queue Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_PARAMETERS__QUEUE_TIME = 1;

	/**
	 * The feature id for the '<em><b>Wait Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_PARAMETERS__WAIT_TIME = 2;

	/**
	 * The feature id for the '<em><b>Set Up Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_PARAMETERS__SET_UP_TIME = 3;

	/**
	 * The feature id for the '<em><b>Processing Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_PARAMETERS__PROCESSING_TIME = 4;

	/**
	 * The feature id for the '<em><b>Validation Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_PARAMETERS__VALIDATION_TIME = 5;

	/**
	 * The feature id for the '<em><b>Rework Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_PARAMETERS__REWORK_TIME = 6;

	/**
	 * The number of structural features of the '<em>Time Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_PARAMETERS_FEATURE_COUNT = 7;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.TriangularDistributionTypeImpl <em>Triangular Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.TriangularDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getTriangularDistributionType()
	 * @generated
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE = 43;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__MAX = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__MIN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Most Likely</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__MOST_LIKELY = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Triangular Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.TruncatedNormalDistributionTypeImpl <em>Truncated Normal Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.TruncatedNormalDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getTruncatedNormalDistributionType()
	 * @generated
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE = 44;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__MAX = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__MEAN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__MIN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Standard Deviation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Truncated Normal Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.UniformDistributionTypeImpl <em>Uniform Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.UniformDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getUniformDistributionType()
	 * @generated
	 */
	int UNIFORM_DISTRIBUTION_TYPE = 45;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_TYPE__MAX = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_TYPE__MIN = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Uniform Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.UserDistributionDataPointTypeImpl <em>User Distribution Data Point Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.UserDistributionDataPointTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getUserDistributionDataPointType()
	 * @generated
	 */
	int USER_DISTRIBUTION_DATA_POINT_TYPE = 46;

	/**
	 * The feature id for the '<em><b>Parameter Value Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE_GROUP = 0;

	/**
	 * The feature id for the '<em><b>Parameter Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE = 1;

	/**
	 * The feature id for the '<em><b>Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_DATA_POINT_TYPE__PROBABILITY = 2;

	/**
	 * The number of structural features of the '<em>User Distribution Data Point Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_DATA_POINT_TYPE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.UserDistributionTypeImpl <em>User Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.UserDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getUserDistributionType()
	 * @generated
	 */
	int USER_DISTRIBUTION_TYPE = 47;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE__GROUP = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>User Distribution Data Point</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>User Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.VendorExtensionImpl <em>Vendor Extension</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.VendorExtensionImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getVendorExtension()
	 * @generated
	 */
	int VENDOR_EXTENSION = 48;

	/**
	 * The feature id for the '<em><b>Any</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VENDOR_EXTENSION__ANY = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VENDOR_EXTENSION__NAME = 1;

	/**
	 * The feature id for the '<em><b>Any Attribute</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VENDOR_EXTENSION__ANY_ATTRIBUTE = 2;

	/**
	 * The number of structural features of the '<em>Vendor Extension</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VENDOR_EXTENSION_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.WeibullDistributionTypeImpl <em>Weibull Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.WeibullDistributionTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getWeibullDistributionType()
	 * @generated
	 */
	int WEIBULL_DISTRIBUTION_TYPE = 49;

	/**
	 * The feature id for the '<em><b>Instance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEIBULL_DISTRIBUTION_TYPE__INSTANCE = DISTRIBUTION_PARAMETER__INSTANCE;

	/**
	 * The feature id for the '<em><b>Result</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEIBULL_DISTRIBUTION_TYPE__RESULT = DISTRIBUTION_PARAMETER__RESULT;

	/**
	 * The feature id for the '<em><b>Valid For</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEIBULL_DISTRIBUTION_TYPE__VALID_FOR = DISTRIBUTION_PARAMETER__VALID_FOR;

	/**
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEIBULL_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER__DISCRETE;

	/**
	 * The feature id for the '<em><b>Scale</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEIBULL_DISTRIBUTION_TYPE__SCALE = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEIBULL_DISTRIBUTION_TYPE__SHAPE = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Weibull Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEIBULL_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.ResultType <em>Result Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.ResultType
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getResultType()
	 * @generated
	 */
	int RESULT_TYPE = 50;

	/**
	 * The meta object id for the '{@link org.jboss.drools.TimeUnit <em>Time Unit</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.TimeUnit
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getTimeUnit()
	 * @generated
	 */
	int TIME_UNIT = 51;

	/**
	 * The meta object id for the '<em>Package Name Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getPackageNameType()
	 * @generated
	 */
	int PACKAGE_NAME_TYPE = 52;

	/**
	 * The meta object id for the '<em>Priority Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.math.BigInteger
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getPriorityType()
	 * @generated
	 */
	int PRIORITY_TYPE = 53;

	/**
	 * The meta object id for the '<em>Result Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.ResultType
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getResultTypeObject()
	 * @generated
	 */
	int RESULT_TYPE_OBJECT = 54;

	/**
	 * The meta object id for the '<em>Rule Flow Group Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getRuleFlowGroupType()
	 * @generated
	 */
	int RULE_FLOW_GROUP_TYPE = 55;

	/**
	 * The meta object id for the '<em>Task Name Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getTaskNameType()
	 * @generated
	 */
	int TASK_NAME_TYPE = 56;

	/**
	 * The meta object id for the '<em>Time Unit Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.TimeUnit
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getTimeUnitObject()
	 * @generated
	 */
	int TIME_UNIT_OBJECT = 57;

	/**
	 * The meta object id for the '<em>Version Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getVersionType()
	 * @generated
	 */
	int VERSION_TYPE = 58;


	/**
	 * Returns the meta object for class '{@link org.jboss.drools.BetaDistributionType <em>Beta Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Beta Distribution Type</em>'.
	 * @see org.jboss.drools.BetaDistributionType
	 * @generated
	 */
	EClass getBetaDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.BetaDistributionType#getScale <em>Scale</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scale</em>'.
	 * @see org.jboss.drools.BetaDistributionType#getScale()
	 * @see #getBetaDistributionType()
	 * @generated
	 */
	EAttribute getBetaDistributionType_Scale();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.BetaDistributionType#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape</em>'.
	 * @see org.jboss.drools.BetaDistributionType#getShape()
	 * @see #getBetaDistributionType()
	 * @generated
	 */
	EAttribute getBetaDistributionType_Shape();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.BinomialDistributionType <em>Binomial Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Binomial Distribution Type</em>'.
	 * @see org.jboss.drools.BinomialDistributionType
	 * @generated
	 */
	EClass getBinomialDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.BinomialDistributionType#getProbability <em>Probability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Probability</em>'.
	 * @see org.jboss.drools.BinomialDistributionType#getProbability()
	 * @see #getBinomialDistributionType()
	 * @generated
	 */
	EAttribute getBinomialDistributionType_Probability();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.BinomialDistributionType#getTrials <em>Trials</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Trials</em>'.
	 * @see org.jboss.drools.BinomialDistributionType#getTrials()
	 * @see #getBinomialDistributionType()
	 * @generated
	 */
	EAttribute getBinomialDistributionType_Trials();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.BooleanParameterType <em>Boolean Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Boolean Parameter Type</em>'.
	 * @see org.jboss.drools.BooleanParameterType
	 * @generated
	 */
	EClass getBooleanParameterType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.BooleanParameterType#isValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.drools.BooleanParameterType#isValue()
	 * @see #getBooleanParameterType()
	 * @generated
	 */
	EAttribute getBooleanParameterType_Value();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.Calendar <em>Calendar</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Calendar</em>'.
	 * @see org.jboss.drools.Calendar
	 * @generated
	 */
	EClass getCalendar();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Calendar#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.drools.Calendar#getValue()
	 * @see #getCalendar()
	 * @generated
	 */
	EAttribute getCalendar_Value();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Calendar#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.jboss.drools.Calendar#getId()
	 * @see #getCalendar()
	 * @generated
	 */
	EAttribute getCalendar_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Calendar#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.jboss.drools.Calendar#getName()
	 * @see #getCalendar()
	 * @generated
	 */
	EAttribute getCalendar_Name();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ConstantParameter <em>Constant Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Constant Parameter</em>'.
	 * @see org.jboss.drools.ConstantParameter
	 * @generated
	 */
	EClass getConstantParameter();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ControlParameters <em>Control Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Control Parameters</em>'.
	 * @see org.jboss.drools.ControlParameters
	 * @generated
	 */
	EClass getControlParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ControlParameters#getProbability <em>Probability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Probability</em>'.
	 * @see org.jboss.drools.ControlParameters#getProbability()
	 * @see #getControlParameters()
	 * @generated
	 */
	EReference getControlParameters_Probability();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ControlParameters#getInterTriggerTimer <em>Inter Trigger Timer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Inter Trigger Timer</em>'.
	 * @see org.jboss.drools.ControlParameters#getInterTriggerTimer()
	 * @see #getControlParameters()
	 * @generated
	 */
	EReference getControlParameters_InterTriggerTimer();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ControlParameters#getMaxTriggerCount <em>Max Trigger Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Max Trigger Count</em>'.
	 * @see org.jboss.drools.ControlParameters#getMaxTriggerCount()
	 * @see #getControlParameters()
	 * @generated
	 */
	EReference getControlParameters_MaxTriggerCount();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.CostParameters <em>Cost Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cost Parameters</em>'.
	 * @see org.jboss.drools.CostParameters
	 * @generated
	 */
	EClass getCostParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.CostParameters#getFixedCost <em>Fixed Cost</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Fixed Cost</em>'.
	 * @see org.jboss.drools.CostParameters#getFixedCost()
	 * @see #getCostParameters()
	 * @generated
	 */
	EReference getCostParameters_FixedCost();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.CostParameters#getUnitCost <em>Unit Cost</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Unit Cost</em>'.
	 * @see org.jboss.drools.CostParameters#getUnitCost()
	 * @see #getCostParameters()
	 * @generated
	 */
	EReference getCostParameters_UnitCost();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.DateTimeParameterType <em>Date Time Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Date Time Parameter Type</em>'.
	 * @see org.jboss.drools.DateTimeParameterType
	 * @generated
	 */
	EClass getDateTimeParameterType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.DateTimeParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.drools.DateTimeParameterType#getValue()
	 * @see #getDateTimeParameterType()
	 * @generated
	 */
	EAttribute getDateTimeParameterType_Value();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.DistributionParameter <em>Distribution Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Distribution Parameter</em>'.
	 * @see org.jboss.drools.DistributionParameter
	 * @generated
	 */
	EClass getDistributionParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.DistributionParameter#isDiscrete <em>Discrete</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Discrete</em>'.
	 * @see org.jboss.drools.DistributionParameter#isDiscrete()
	 * @see #getDistributionParameter()
	 * @generated
	 */
	EAttribute getDistributionParameter_Discrete();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.DocumentRoot <em>Document Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Document Root</em>'.
	 * @see org.jboss.drools.DocumentRoot
	 * @generated
	 */
	EClass getDocumentRoot();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.DocumentRoot#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see org.jboss.drools.DocumentRoot#getMixed()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_Mixed();

	/**
	 * Returns the meta object for the map '{@link org.jboss.drools.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
	 * @see org.jboss.drools.DocumentRoot#getXMLNSPrefixMap()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XMLNSPrefixMap();

	/**
	 * Returns the meta object for the map '{@link org.jboss.drools.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XSI Schema Location</em>'.
	 * @see org.jboss.drools.DocumentRoot#getXSISchemaLocation()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XSISchemaLocation();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getBetaDistribution <em>Beta Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Beta Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getBetaDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BetaDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameter Value</em>'.
	 * @see org.jboss.drools.DocumentRoot#getParameterValue()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_ParameterValue();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getBinomialDistribution <em>Binomial Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Binomial Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getBinomialDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BinomialDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getBooleanParameter <em>Boolean Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Boolean Parameter</em>'.
	 * @see org.jboss.drools.DocumentRoot#getBooleanParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BooleanParameter();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getDateTimeParameter <em>Date Time Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Date Time Parameter</em>'.
	 * @see org.jboss.drools.DocumentRoot#getDateTimeParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_DateTimeParameter();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getDurationParameter <em>Duration Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Duration Parameter</em>'.
	 * @see org.jboss.drools.DocumentRoot#getDurationParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_DurationParameter();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getEnumParameter <em>Enum Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Enum Parameter</em>'.
	 * @see org.jboss.drools.DocumentRoot#getEnumParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_EnumParameter();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getErlangDistribution <em>Erlang Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Erlang Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getErlangDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_ErlangDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getExpressionParameter <em>Expression Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Expression Parameter</em>'.
	 * @see org.jboss.drools.DocumentRoot#getExpressionParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_ExpressionParameter();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getFloatingParameter <em>Floating Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Floating Parameter</em>'.
	 * @see org.jboss.drools.DocumentRoot#getFloatingParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_FloatingParameter();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getGammaDistribution <em>Gamma Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Gamma Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getGammaDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_GammaDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getGlobal <em>Global</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Global</em>'.
	 * @see org.jboss.drools.DocumentRoot#getGlobal()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_Global();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getImport <em>Import</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Import</em>'.
	 * @see org.jboss.drools.DocumentRoot#getImport()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_Import();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getLogNormalDistribution <em>Log Normal Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Log Normal Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getLogNormalDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_LogNormalDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getMetadata <em>Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Metadata</em>'.
	 * @see org.jboss.drools.DocumentRoot#getMetadata()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_Metadata();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getMetaentry <em>Metaentry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Metaentry</em>'.
	 * @see org.jboss.drools.DocumentRoot#getMetaentry()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_Metaentry();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getNegativeExponentialDistribution <em>Negative Exponential Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Negative Exponential Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getNegativeExponentialDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_NegativeExponentialDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getNormalDistribution <em>Normal Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Normal Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getNormalDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_NormalDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getNumericParameter <em>Numeric Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Numeric Parameter</em>'.
	 * @see org.jboss.drools.DocumentRoot#getNumericParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_NumericParameter();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getOnEntryScript <em>On Entry Script</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>On Entry Script</em>'.
	 * @see org.jboss.drools.DocumentRoot#getOnEntryScript()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_OnEntryScript();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getOnExitScript <em>On Exit Script</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>On Exit Script</em>'.
	 * @see org.jboss.drools.DocumentRoot#getOnExitScript()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_OnExitScript();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getPoissonDistribution <em>Poisson Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Poisson Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getPoissonDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_PoissonDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getProcessAnalysisData <em>Process Analysis Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Process Analysis Data</em>'.
	 * @see org.jboss.drools.DocumentRoot#getProcessAnalysisData()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_ProcessAnalysisData();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getRandomDistribution <em>Random Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Random Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getRandomDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_RandomDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getStringParameter <em>String Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>String Parameter</em>'.
	 * @see org.jboss.drools.DocumentRoot#getStringParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_StringParameter();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getTriangularDistribution <em>Triangular Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Triangular Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getTriangularDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_TriangularDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getTruncatedNormalDistribution <em>Truncated Normal Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Truncated Normal Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getTruncatedNormalDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_TruncatedNormalDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getUniformDistribution <em>Uniform Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Uniform Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getUniformDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_UniformDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getUserDistribution <em>User Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>User Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getUserDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_UserDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getUserDistributionDataPoint <em>User Distribution Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>User Distribution Data Point</em>'.
	 * @see org.jboss.drools.DocumentRoot#getUserDistributionDataPoint()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_UserDistributionDataPoint();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getWeibullDistribution <em>Weibull Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Weibull Distribution</em>'.
	 * @see org.jboss.drools.DocumentRoot#getWeibullDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_WeibullDistribution();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.DocumentRoot#getPackageName <em>Package Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Package Name</em>'.
	 * @see org.jboss.drools.DocumentRoot#getPackageName()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_PackageName();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.DocumentRoot#getPriority <em>Priority</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Priority</em>'.
	 * @see org.jboss.drools.DocumentRoot#getPriority()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_Priority();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.DocumentRoot#getRuleFlowGroup <em>Rule Flow Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rule Flow Group</em>'.
	 * @see org.jboss.drools.DocumentRoot#getRuleFlowGroup()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_RuleFlowGroup();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.DocumentRoot#getTaskName <em>Task Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Task Name</em>'.
	 * @see org.jboss.drools.DocumentRoot#getTaskName()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_TaskName();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.DocumentRoot#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.jboss.drools.DocumentRoot#getVersion()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_Version();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.DurationParameterType <em>Duration Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Duration Parameter Type</em>'.
	 * @see org.jboss.drools.DurationParameterType
	 * @generated
	 */
	EClass getDurationParameterType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.DurationParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.drools.DurationParameterType#getValue()
	 * @see #getDurationParameterType()
	 * @generated
	 */
	EAttribute getDurationParameterType_Value();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ElementParameters <em>Element Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element Parameters</em>'.
	 * @see org.jboss.drools.ElementParameters
	 * @generated
	 */
	EClass getElementParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ElementParameters#getTimeParameters <em>Time Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Time Parameters</em>'.
	 * @see org.jboss.drools.ElementParameters#getTimeParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_TimeParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ElementParameters#getControlParameters <em>Control Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control Parameters</em>'.
	 * @see org.jboss.drools.ElementParameters#getControlParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_ControlParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ElementParameters#getResourceParameters <em>Resource Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Resource Parameters</em>'.
	 * @see org.jboss.drools.ElementParameters#getResourceParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_ResourceParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ElementParameters#getPriorityParameters <em>Priority Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Priority Parameters</em>'.
	 * @see org.jboss.drools.ElementParameters#getPriorityParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_PriorityParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ElementParameters#getCostParameters <em>Cost Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cost Parameters</em>'.
	 * @see org.jboss.drools.ElementParameters#getCostParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_CostParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ElementParameters#getInstanceParameters <em>Instance Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Instance Parameters</em>'.
	 * @see org.jboss.drools.ElementParameters#getInstanceParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_InstanceParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.ElementParameters#getVendorExtension <em>Vendor Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Vendor Extension</em>'.
	 * @see org.jboss.drools.ElementParameters#getVendorExtension()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_VendorExtension();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ElementParameters#getElementId <em>Element Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Element Id</em>'.
	 * @see org.jboss.drools.ElementParameters#getElementId()
	 * @see #getElementParameters()
	 * @generated
	 */
	EAttribute getElementParameters_ElementId();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ElementParameters#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.jboss.drools.ElementParameters#getId()
	 * @see #getElementParameters()
	 * @generated
	 */
	EAttribute getElementParameters_Id();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ElementParametersType <em>Element Parameters Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element Parameters Type</em>'.
	 * @see org.jboss.drools.ElementParametersType
	 * @generated
	 */
	EClass getElementParametersType();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.EnumParameterType <em>Enum Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Enum Parameter Type</em>'.
	 * @see org.jboss.drools.EnumParameterType
	 * @generated
	 */
	EClass getEnumParameterType();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.EnumParameterType#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see org.jboss.drools.EnumParameterType#getGroup()
	 * @see #getEnumParameterType()
	 * @generated
	 */
	EAttribute getEnumParameterType_Group();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.EnumParameterType#getParameterValueGroup <em>Parameter Value Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Parameter Value Group</em>'.
	 * @see org.jboss.drools.EnumParameterType#getParameterValueGroup()
	 * @see #getEnumParameterType()
	 * @generated
	 */
	EAttribute getEnumParameterType_ParameterValueGroup();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.EnumParameterType#getParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Value</em>'.
	 * @see org.jboss.drools.EnumParameterType#getParameterValue()
	 * @see #getEnumParameterType()
	 * @generated
	 */
	EReference getEnumParameterType_ParameterValue();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ErlangDistributionType <em>Erlang Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Erlang Distribution Type</em>'.
	 * @see org.jboss.drools.ErlangDistributionType
	 * @generated
	 */
	EClass getErlangDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ErlangDistributionType#getK <em>K</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>K</em>'.
	 * @see org.jboss.drools.ErlangDistributionType#getK()
	 * @see #getErlangDistributionType()
	 * @generated
	 */
	EAttribute getErlangDistributionType_K();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ErlangDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see org.jboss.drools.ErlangDistributionType#getMean()
	 * @see #getErlangDistributionType()
	 * @generated
	 */
	EAttribute getErlangDistributionType_Mean();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ExpressionParameterType <em>Expression Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Expression Parameter Type</em>'.
	 * @see org.jboss.drools.ExpressionParameterType
	 * @generated
	 */
	EClass getExpressionParameterType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ExpressionParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.drools.ExpressionParameterType#getValue()
	 * @see #getExpressionParameterType()
	 * @generated
	 */
	EAttribute getExpressionParameterType_Value();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.FloatingParameterType <em>Floating Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Floating Parameter Type</em>'.
	 * @see org.jboss.drools.FloatingParameterType
	 * @generated
	 */
	EClass getFloatingParameterType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.FloatingParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.drools.FloatingParameterType#getValue()
	 * @see #getFloatingParameterType()
	 * @generated
	 */
	EAttribute getFloatingParameterType_Value();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.GammaDistributionType <em>Gamma Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Gamma Distribution Type</em>'.
	 * @see org.jboss.drools.GammaDistributionType
	 * @generated
	 */
	EClass getGammaDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.GammaDistributionType#getScale <em>Scale</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scale</em>'.
	 * @see org.jboss.drools.GammaDistributionType#getScale()
	 * @see #getGammaDistributionType()
	 * @generated
	 */
	EAttribute getGammaDistributionType_Scale();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.GammaDistributionType#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape</em>'.
	 * @see org.jboss.drools.GammaDistributionType#getShape()
	 * @see #getGammaDistributionType()
	 * @generated
	 */
	EAttribute getGammaDistributionType_Shape();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.GlobalParameterType <em>Global Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Global Parameter Type</em>'.
	 * @see org.jboss.drools.GlobalParameterType
	 * @generated
	 */
	EClass getGlobalParameterType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.GlobalParameterType#getProperty <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Property</em>'.
	 * @see org.jboss.drools.GlobalParameterType#getProperty()
	 * @see #getGlobalParameterType()
	 * @generated
	 */
	EAttribute getGlobalParameterType_Property();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.GlobalType <em>Global Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Global Type</em>'.
	 * @see org.jboss.drools.GlobalType
	 * @generated
	 */
	EClass getGlobalType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.GlobalType#getIdentifier <em>Identifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Identifier</em>'.
	 * @see org.jboss.drools.GlobalType#getIdentifier()
	 * @see #getGlobalType()
	 * @generated
	 */
	EAttribute getGlobalType_Identifier();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.GlobalType#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.jboss.drools.GlobalType#getType()
	 * @see #getGlobalType()
	 * @generated
	 */
	EAttribute getGlobalType_Type();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ImportType <em>Import Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Import Type</em>'.
	 * @see org.jboss.drools.ImportType
	 * @generated
	 */
	EClass getImportType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ImportType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.jboss.drools.ImportType#getName()
	 * @see #getImportType()
	 * @generated
	 */
	EAttribute getImportType_Name();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.InstanceParameters <em>Instance Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Instance Parameters</em>'.
	 * @see org.jboss.drools.InstanceParameters
	 * @generated
	 */
	EClass getInstanceParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.InstanceParameters#getProperty <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Property</em>'.
	 * @see org.jboss.drools.InstanceParameters#getProperty()
	 * @see #getInstanceParameters()
	 * @generated
	 */
	EReference getInstanceParameters_Property();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.LogNormalDistributionType <em>Log Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Log Normal Distribution Type</em>'.
	 * @see org.jboss.drools.LogNormalDistributionType
	 * @generated
	 */
	EClass getLogNormalDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.LogNormalDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see org.jboss.drools.LogNormalDistributionType#getMean()
	 * @see #getLogNormalDistributionType()
	 * @generated
	 */
	EAttribute getLogNormalDistributionType_Mean();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.LogNormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Standard Deviation</em>'.
	 * @see org.jboss.drools.LogNormalDistributionType#getStandardDeviation()
	 * @see #getLogNormalDistributionType()
	 * @generated
	 */
	EAttribute getLogNormalDistributionType_StandardDeviation();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.MetadataType <em>Metadata Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata Type</em>'.
	 * @see org.jboss.drools.MetadataType
	 * @generated
	 */
	EClass getMetadataType();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.MetadataType#getMetaentry <em>Metaentry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Metaentry</em>'.
	 * @see org.jboss.drools.MetadataType#getMetaentry()
	 * @see #getMetadataType()
	 * @generated
	 */
	EReference getMetadataType_Metaentry();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.MetaentryType <em>Metaentry Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metaentry Type</em>'.
	 * @see org.jboss.drools.MetaentryType
	 * @generated
	 */
	EClass getMetaentryType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.MetaentryType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.jboss.drools.MetaentryType#getName()
	 * @see #getMetaentryType()
	 * @generated
	 */
	EAttribute getMetaentryType_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.MetaentryType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.drools.MetaentryType#getValue()
	 * @see #getMetaentryType()
	 * @generated
	 */
	EAttribute getMetaentryType_Value();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.NegativeExponentialDistributionType <em>Negative Exponential Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Negative Exponential Distribution Type</em>'.
	 * @see org.jboss.drools.NegativeExponentialDistributionType
	 * @generated
	 */
	EClass getNegativeExponentialDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.NegativeExponentialDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see org.jboss.drools.NegativeExponentialDistributionType#getMean()
	 * @see #getNegativeExponentialDistributionType()
	 * @generated
	 */
	EAttribute getNegativeExponentialDistributionType_Mean();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.NormalDistributionType <em>Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Normal Distribution Type</em>'.
	 * @see org.jboss.drools.NormalDistributionType
	 * @generated
	 */
	EClass getNormalDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.NormalDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see org.jboss.drools.NormalDistributionType#getMean()
	 * @see #getNormalDistributionType()
	 * @generated
	 */
	EAttribute getNormalDistributionType_Mean();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.NormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Standard Deviation</em>'.
	 * @see org.jboss.drools.NormalDistributionType#getStandardDeviation()
	 * @see #getNormalDistributionType()
	 * @generated
	 */
	EAttribute getNormalDistributionType_StandardDeviation();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.NumericParameterType <em>Numeric Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Numeric Parameter Type</em>'.
	 * @see org.jboss.drools.NumericParameterType
	 * @generated
	 */
	EClass getNumericParameterType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.NumericParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.drools.NumericParameterType#getValue()
	 * @see #getNumericParameterType()
	 * @generated
	 */
	EAttribute getNumericParameterType_Value();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.OnEntryScriptType <em>On Entry Script Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>On Entry Script Type</em>'.
	 * @see org.jboss.drools.OnEntryScriptType
	 * @generated
	 */
	EClass getOnEntryScriptType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.OnEntryScriptType#getScript <em>Script</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Script</em>'.
	 * @see org.jboss.drools.OnEntryScriptType#getScript()
	 * @see #getOnEntryScriptType()
	 * @generated
	 */
	EAttribute getOnEntryScriptType_Script();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.OnEntryScriptType#getScriptFormat <em>Script Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Script Format</em>'.
	 * @see org.jboss.drools.OnEntryScriptType#getScriptFormat()
	 * @see #getOnEntryScriptType()
	 * @generated
	 */
	EAttribute getOnEntryScriptType_ScriptFormat();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.OnExitScriptType <em>On Exit Script Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>On Exit Script Type</em>'.
	 * @see org.jboss.drools.OnExitScriptType
	 * @generated
	 */
	EClass getOnExitScriptType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.OnExitScriptType#getScript <em>Script</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Script</em>'.
	 * @see org.jboss.drools.OnExitScriptType#getScript()
	 * @see #getOnExitScriptType()
	 * @generated
	 */
	EAttribute getOnExitScriptType_Script();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.OnExitScriptType#getScriptFormat <em>Script Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Script Format</em>'.
	 * @see org.jboss.drools.OnExitScriptType#getScriptFormat()
	 * @see #getOnExitScriptType()
	 * @generated
	 */
	EAttribute getOnExitScriptType_ScriptFormat();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see org.jboss.drools.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.Parameter#getResultRequest <em>Result Request</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Result Request</em>'.
	 * @see org.jboss.drools.Parameter#getResultRequest()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_ResultRequest();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.Parameter#getParameterValueGroup <em>Parameter Value Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Parameter Value Group</em>'.
	 * @see org.jboss.drools.Parameter#getParameterValueGroup()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_ParameterValueGroup();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.Parameter#getParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Value</em>'.
	 * @see org.jboss.drools.Parameter#getParameterValue()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_ParameterValue();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Parameter#isKpi <em>Kpi</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kpi</em>'.
	 * @see org.jboss.drools.Parameter#isKpi()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Kpi();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Parameter#isSla <em>Sla</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sla</em>'.
	 * @see org.jboss.drools.Parameter#isSla()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Sla();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Value</em>'.
	 * @see org.jboss.drools.ParameterValue
	 * @generated
	 */
	EClass getParameterValue();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ParameterValue#getInstance <em>Instance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Instance</em>'.
	 * @see org.jboss.drools.ParameterValue#getInstance()
	 * @see #getParameterValue()
	 * @generated
	 */
	EAttribute getParameterValue_Instance();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ParameterValue#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result</em>'.
	 * @see org.jboss.drools.ParameterValue#getResult()
	 * @see #getParameterValue()
	 * @generated
	 */
	EAttribute getParameterValue_Result();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ParameterValue#getValidFor <em>Valid For</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid For</em>'.
	 * @see org.jboss.drools.ParameterValue#getValidFor()
	 * @see #getParameterValue()
	 * @generated
	 */
	EAttribute getParameterValue_ValidFor();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.PoissonDistributionType <em>Poisson Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Poisson Distribution Type</em>'.
	 * @see org.jboss.drools.PoissonDistributionType
	 * @generated
	 */
	EClass getPoissonDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.PoissonDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see org.jboss.drools.PoissonDistributionType#getMean()
	 * @see #getPoissonDistributionType()
	 * @generated
	 */
	EAttribute getPoissonDistributionType_Mean();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.PriorityParameters <em>Priority Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Priority Parameters</em>'.
	 * @see org.jboss.drools.PriorityParameters
	 * @generated
	 */
	EClass getPriorityParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.PriorityParameters#getInterruptible <em>Interruptible</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Interruptible</em>'.
	 * @see org.jboss.drools.PriorityParameters#getInterruptible()
	 * @see #getPriorityParameters()
	 * @generated
	 */
	EReference getPriorityParameters_Interruptible();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.PriorityParameters#getPriority <em>Priority</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Priority</em>'.
	 * @see org.jboss.drools.PriorityParameters#getPriority()
	 * @see #getPriorityParameters()
	 * @generated
	 */
	EReference getPriorityParameters_Priority();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ProcessAnalysisDataType <em>Process Analysis Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Process Analysis Data Type</em>'.
	 * @see org.jboss.drools.ProcessAnalysisDataType
	 * @generated
	 */
	EClass getProcessAnalysisDataType();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.ProcessAnalysisDataType#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see org.jboss.drools.ProcessAnalysisDataType#getGroup()
	 * @see #getProcessAnalysisDataType()
	 * @generated
	 */
	EAttribute getProcessAnalysisDataType_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.ProcessAnalysisDataType#getScenario <em>Scenario</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Scenario</em>'.
	 * @see org.jboss.drools.ProcessAnalysisDataType#getScenario()
	 * @see #getProcessAnalysisDataType()
	 * @generated
	 */
	EReference getProcessAnalysisDataType_Scenario();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.PropertyType <em>Property Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property Type</em>'.
	 * @see org.jboss.drools.PropertyType
	 * @generated
	 */
	EClass getPropertyType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.PropertyType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.jboss.drools.PropertyType#getName()
	 * @see #getPropertyType()
	 * @generated
	 */
	EAttribute getPropertyType_Name();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.RandomDistributionType <em>Random Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Random Distribution Type</em>'.
	 * @see org.jboss.drools.RandomDistributionType
	 * @generated
	 */
	EClass getRandomDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.RandomDistributionType#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see org.jboss.drools.RandomDistributionType#getMax()
	 * @see #getRandomDistributionType()
	 * @generated
	 */
	EAttribute getRandomDistributionType_Max();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.RandomDistributionType#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see org.jboss.drools.RandomDistributionType#getMin()
	 * @see #getRandomDistributionType()
	 * @generated
	 */
	EAttribute getRandomDistributionType_Min();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ResourceParameters <em>Resource Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Resource Parameters</em>'.
	 * @see org.jboss.drools.ResourceParameters
	 * @generated
	 */
	EClass getResourceParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ResourceParameters#getSelection <em>Selection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Selection</em>'.
	 * @see org.jboss.drools.ResourceParameters#getSelection()
	 * @see #getResourceParameters()
	 * @generated
	 */
	EReference getResourceParameters_Selection();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ResourceParameters#getAvailability <em>Availability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Availability</em>'.
	 * @see org.jboss.drools.ResourceParameters#getAvailability()
	 * @see #getResourceParameters()
	 * @generated
	 */
	EReference getResourceParameters_Availability();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ResourceParameters#getQuantity <em>Quantity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Quantity</em>'.
	 * @see org.jboss.drools.ResourceParameters#getQuantity()
	 * @see #getResourceParameters()
	 * @generated
	 */
	EReference getResourceParameters_Quantity();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ResourceParameters#getWorkinghours <em>Workinghours</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Workinghours</em>'.
	 * @see org.jboss.drools.ResourceParameters#getWorkinghours()
	 * @see #getResourceParameters()
	 * @generated
	 */
	EReference getResourceParameters_Workinghours();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.ResourceParameters#getRole <em>Role</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Role</em>'.
	 * @see org.jboss.drools.ResourceParameters#getRole()
	 * @see #getResourceParameters()
	 * @generated
	 */
	EReference getResourceParameters_Role();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.Scenario <em>Scenario</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Scenario</em>'.
	 * @see org.jboss.drools.Scenario
	 * @generated
	 */
	EClass getScenario();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.Scenario#getScenarioParameters <em>Scenario Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Scenario Parameters</em>'.
	 * @see org.jboss.drools.Scenario#getScenarioParameters()
	 * @see #getScenario()
	 * @generated
	 */
	EReference getScenario_ScenarioParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.Scenario#getElementParameters <em>Element Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Element Parameters</em>'.
	 * @see org.jboss.drools.Scenario#getElementParameters()
	 * @see #getScenario()
	 * @generated
	 */
	EReference getScenario_ElementParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.Scenario#getCalendar <em>Calendar</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Calendar</em>'.
	 * @see org.jboss.drools.Scenario#getCalendar()
	 * @see #getScenario()
	 * @generated
	 */
	EReference getScenario_Calendar();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.Scenario#getVendorExtension <em>Vendor Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Vendor Extension</em>'.
	 * @see org.jboss.drools.Scenario#getVendorExtension()
	 * @see #getScenario()
	 * @generated
	 */
	EReference getScenario_VendorExtension();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Scenario#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see org.jboss.drools.Scenario#getAuthor()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Author();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Scenario#getCreated <em>Created</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Created</em>'.
	 * @see org.jboss.drools.Scenario#getCreated()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Created();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Scenario#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.jboss.drools.Scenario#getDescription()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Scenario#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.jboss.drools.Scenario#getId()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Scenario#getInherits <em>Inherits</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Inherits</em>'.
	 * @see org.jboss.drools.Scenario#getInherits()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Inherits();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Scenario#getModified <em>Modified</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Modified</em>'.
	 * @see org.jboss.drools.Scenario#getModified()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Modified();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Scenario#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.jboss.drools.Scenario#getName()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Scenario#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result</em>'.
	 * @see org.jboss.drools.Scenario#getResult()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Result();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.Scenario#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.jboss.drools.Scenario#getVersion()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Version();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ScenarioParameters <em>Scenario Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Scenario Parameters</em>'.
	 * @see org.jboss.drools.ScenarioParameters
	 * @generated
	 */
	EClass getScenarioParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ScenarioParameters#getStart <em>Start</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Start</em>'.
	 * @see org.jboss.drools.ScenarioParameters#getStart()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EReference getScenarioParameters_Start();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.ScenarioParameters#getDuration <em>Duration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Duration</em>'.
	 * @see org.jboss.drools.ScenarioParameters#getDuration()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EReference getScenarioParameters_Duration();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.ScenarioParameters#getGlobalParameter <em>Global Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Global Parameter</em>'.
	 * @see org.jboss.drools.ScenarioParameters#getGlobalParameter()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EReference getScenarioParameters_GlobalParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ScenarioParameters#getBaseCurrencyUnit <em>Base Currency Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Base Currency Unit</em>'.
	 * @see org.jboss.drools.ScenarioParameters#getBaseCurrencyUnit()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EAttribute getScenarioParameters_BaseCurrencyUnit();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ScenarioParameters#getBaseTimeUnit <em>Base Time Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Base Time Unit</em>'.
	 * @see org.jboss.drools.ScenarioParameters#getBaseTimeUnit()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EAttribute getScenarioParameters_BaseTimeUnit();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ScenarioParameters#getReplication <em>Replication</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Replication</em>'.
	 * @see org.jboss.drools.ScenarioParameters#getReplication()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EAttribute getScenarioParameters_Replication();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.ScenarioParameters#getSeed <em>Seed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Seed</em>'.
	 * @see org.jboss.drools.ScenarioParameters#getSeed()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EAttribute getScenarioParameters_Seed();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.ScenarioParametersType <em>Scenario Parameters Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Scenario Parameters Type</em>'.
	 * @see org.jboss.drools.ScenarioParametersType
	 * @generated
	 */
	EClass getScenarioParametersType();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.StringParameterType <em>String Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String Parameter Type</em>'.
	 * @see org.jboss.drools.StringParameterType
	 * @generated
	 */
	EClass getStringParameterType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.StringParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.jboss.drools.StringParameterType#getValue()
	 * @see #getStringParameterType()
	 * @generated
	 */
	EAttribute getStringParameterType_Value();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.TimeParameters <em>Time Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Time Parameters</em>'.
	 * @see org.jboss.drools.TimeParameters
	 * @generated
	 */
	EClass getTimeParameters();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.TimeParameters#getTransferTime <em>Transfer Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Transfer Time</em>'.
	 * @see org.jboss.drools.TimeParameters#getTransferTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_TransferTime();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.TimeParameters#getQueueTime <em>Queue Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Queue Time</em>'.
	 * @see org.jboss.drools.TimeParameters#getQueueTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_QueueTime();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.TimeParameters#getWaitTime <em>Wait Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Wait Time</em>'.
	 * @see org.jboss.drools.TimeParameters#getWaitTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_WaitTime();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.TimeParameters#getSetUpTime <em>Set Up Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Set Up Time</em>'.
	 * @see org.jboss.drools.TimeParameters#getSetUpTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_SetUpTime();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.TimeParameters#getProcessingTime <em>Processing Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Processing Time</em>'.
	 * @see org.jboss.drools.TimeParameters#getProcessingTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_ProcessingTime();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.TimeParameters#getValidationTime <em>Validation Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Validation Time</em>'.
	 * @see org.jboss.drools.TimeParameters#getValidationTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_ValidationTime();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.TimeParameters#getReworkTime <em>Rework Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Rework Time</em>'.
	 * @see org.jboss.drools.TimeParameters#getReworkTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_ReworkTime();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.TriangularDistributionType <em>Triangular Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Triangular Distribution Type</em>'.
	 * @see org.jboss.drools.TriangularDistributionType
	 * @generated
	 */
	EClass getTriangularDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.TriangularDistributionType#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see org.jboss.drools.TriangularDistributionType#getMax()
	 * @see #getTriangularDistributionType()
	 * @generated
	 */
	EAttribute getTriangularDistributionType_Max();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.TriangularDistributionType#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see org.jboss.drools.TriangularDistributionType#getMin()
	 * @see #getTriangularDistributionType()
	 * @generated
	 */
	EAttribute getTriangularDistributionType_Min();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.TriangularDistributionType#getMostLikely <em>Most Likely</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Most Likely</em>'.
	 * @see org.jboss.drools.TriangularDistributionType#getMostLikely()
	 * @see #getTriangularDistributionType()
	 * @generated
	 */
	EAttribute getTriangularDistributionType_MostLikely();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.TruncatedNormalDistributionType <em>Truncated Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Truncated Normal Distribution Type</em>'.
	 * @see org.jboss.drools.TruncatedNormalDistributionType
	 * @generated
	 */
	EClass getTruncatedNormalDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.TruncatedNormalDistributionType#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see org.jboss.drools.TruncatedNormalDistributionType#getMax()
	 * @see #getTruncatedNormalDistributionType()
	 * @generated
	 */
	EAttribute getTruncatedNormalDistributionType_Max();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.TruncatedNormalDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see org.jboss.drools.TruncatedNormalDistributionType#getMean()
	 * @see #getTruncatedNormalDistributionType()
	 * @generated
	 */
	EAttribute getTruncatedNormalDistributionType_Mean();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.TruncatedNormalDistributionType#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see org.jboss.drools.TruncatedNormalDistributionType#getMin()
	 * @see #getTruncatedNormalDistributionType()
	 * @generated
	 */
	EAttribute getTruncatedNormalDistributionType_Min();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.TruncatedNormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Standard Deviation</em>'.
	 * @see org.jboss.drools.TruncatedNormalDistributionType#getStandardDeviation()
	 * @see #getTruncatedNormalDistributionType()
	 * @generated
	 */
	EAttribute getTruncatedNormalDistributionType_StandardDeviation();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.UniformDistributionType <em>Uniform Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Uniform Distribution Type</em>'.
	 * @see org.jboss.drools.UniformDistributionType
	 * @generated
	 */
	EClass getUniformDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.UniformDistributionType#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see org.jboss.drools.UniformDistributionType#getMax()
	 * @see #getUniformDistributionType()
	 * @generated
	 */
	EAttribute getUniformDistributionType_Max();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.UniformDistributionType#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see org.jboss.drools.UniformDistributionType#getMin()
	 * @see #getUniformDistributionType()
	 * @generated
	 */
	EAttribute getUniformDistributionType_Min();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.UserDistributionDataPointType <em>User Distribution Data Point Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>User Distribution Data Point Type</em>'.
	 * @see org.jboss.drools.UserDistributionDataPointType
	 * @generated
	 */
	EClass getUserDistributionDataPointType();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.UserDistributionDataPointType#getParameterValueGroup <em>Parameter Value Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Parameter Value Group</em>'.
	 * @see org.jboss.drools.UserDistributionDataPointType#getParameterValueGroup()
	 * @see #getUserDistributionDataPointType()
	 * @generated
	 */
	EAttribute getUserDistributionDataPointType_ParameterValueGroup();

	/**
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.UserDistributionDataPointType#getParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameter Value</em>'.
	 * @see org.jboss.drools.UserDistributionDataPointType#getParameterValue()
	 * @see #getUserDistributionDataPointType()
	 * @generated
	 */
	EReference getUserDistributionDataPointType_ParameterValue();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.UserDistributionDataPointType#getProbability <em>Probability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Probability</em>'.
	 * @see org.jboss.drools.UserDistributionDataPointType#getProbability()
	 * @see #getUserDistributionDataPointType()
	 * @generated
	 */
	EAttribute getUserDistributionDataPointType_Probability();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.UserDistributionType <em>User Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>User Distribution Type</em>'.
	 * @see org.jboss.drools.UserDistributionType
	 * @generated
	 */
	EClass getUserDistributionType();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.UserDistributionType#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see org.jboss.drools.UserDistributionType#getGroup()
	 * @see #getUserDistributionType()
	 * @generated
	 */
	EAttribute getUserDistributionType_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link org.jboss.drools.UserDistributionType#getUserDistributionDataPoint <em>User Distribution Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>User Distribution Data Point</em>'.
	 * @see org.jboss.drools.UserDistributionType#getUserDistributionDataPoint()
	 * @see #getUserDistributionType()
	 * @generated
	 */
	EReference getUserDistributionType_UserDistributionDataPoint();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.VendorExtension <em>Vendor Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Vendor Extension</em>'.
	 * @see org.jboss.drools.VendorExtension
	 * @generated
	 */
	EClass getVendorExtension();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.VendorExtension#getAny <em>Any</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Any</em>'.
	 * @see org.jboss.drools.VendorExtension#getAny()
	 * @see #getVendorExtension()
	 * @generated
	 */
	EAttribute getVendorExtension_Any();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.VendorExtension#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.jboss.drools.VendorExtension#getName()
	 * @see #getVendorExtension()
	 * @generated
	 */
	EAttribute getVendorExtension_Name();

	/**
	 * Returns the meta object for the attribute list '{@link org.jboss.drools.VendorExtension#getAnyAttribute <em>Any Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Any Attribute</em>'.
	 * @see org.jboss.drools.VendorExtension#getAnyAttribute()
	 * @see #getVendorExtension()
	 * @generated
	 */
	EAttribute getVendorExtension_AnyAttribute();

	/**
	 * Returns the meta object for class '{@link org.jboss.drools.WeibullDistributionType <em>Weibull Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Weibull Distribution Type</em>'.
	 * @see org.jboss.drools.WeibullDistributionType
	 * @generated
	 */
	EClass getWeibullDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.WeibullDistributionType#getScale <em>Scale</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scale</em>'.
	 * @see org.jboss.drools.WeibullDistributionType#getScale()
	 * @see #getWeibullDistributionType()
	 * @generated
	 */
	EAttribute getWeibullDistributionType_Scale();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.WeibullDistributionType#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape</em>'.
	 * @see org.jboss.drools.WeibullDistributionType#getShape()
	 * @see #getWeibullDistributionType()
	 * @generated
	 */
	EAttribute getWeibullDistributionType_Shape();

	/**
	 * Returns the meta object for enum '{@link org.jboss.drools.ResultType <em>Result Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Result Type</em>'.
	 * @see org.jboss.drools.ResultType
	 * @generated
	 */
	EEnum getResultType();

	/**
	 * Returns the meta object for enum '{@link org.jboss.drools.TimeUnit <em>Time Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Time Unit</em>'.
	 * @see org.jboss.drools.TimeUnit
	 * @generated
	 */
	EEnum getTimeUnit();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>Package Name Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Package Name Type</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 *        extendedMetaData="name='packageName_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#string'"
	 * @generated
	 */
	EDataType getPackageNameType();

	/**
	 * Returns the meta object for data type '{@link java.math.BigInteger <em>Priority Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Priority Type</em>'.
	 * @see java.math.BigInteger
	 * @model instanceClass="java.math.BigInteger"
	 *        extendedMetaData="name='priority_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#integer' minInclusive='1'"
	 * @generated
	 */
	EDataType getPriorityType();

	/**
	 * Returns the meta object for data type '{@link org.jboss.drools.ResultType <em>Result Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Result Type Object</em>'.
	 * @see org.jboss.drools.ResultType
	 * @model instanceClass="org.jboss.drools.ResultType"
	 *        extendedMetaData="name='ResultType:Object' baseType='ResultType'"
	 * @generated
	 */
	EDataType getResultTypeObject();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>Rule Flow Group Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Rule Flow Group Type</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 *        extendedMetaData="name='ruleFlowGroup_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#string'"
	 * @generated
	 */
	EDataType getRuleFlowGroupType();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>Task Name Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Task Name Type</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 *        extendedMetaData="name='taskName_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#string'"
	 * @generated
	 */
	EDataType getTaskNameType();

	/**
	 * Returns the meta object for data type '{@link org.jboss.drools.TimeUnit <em>Time Unit Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Time Unit Object</em>'.
	 * @see org.jboss.drools.TimeUnit
	 * @model instanceClass="org.jboss.drools.TimeUnit"
	 *        extendedMetaData="name='TimeUnit:Object' baseType='TimeUnit'"
	 * @generated
	 */
	EDataType getTimeUnitObject();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>Version Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Version Type</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 *        extendedMetaData="name='version_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#string'"
	 * @generated
	 */
	EDataType getVersionType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DroolsFactory getDroolsFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.BetaDistributionTypeImpl <em>Beta Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.BetaDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getBetaDistributionType()
		 * @generated
		 */
		EClass BETA_DISTRIBUTION_TYPE = eINSTANCE.getBetaDistributionType();

		/**
		 * The meta object literal for the '<em><b>Scale</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BETA_DISTRIBUTION_TYPE__SCALE = eINSTANCE.getBetaDistributionType_Scale();

		/**
		 * The meta object literal for the '<em><b>Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BETA_DISTRIBUTION_TYPE__SHAPE = eINSTANCE.getBetaDistributionType_Shape();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.BinomialDistributionTypeImpl <em>Binomial Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.BinomialDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getBinomialDistributionType()
		 * @generated
		 */
		EClass BINOMIAL_DISTRIBUTION_TYPE = eINSTANCE.getBinomialDistributionType();

		/**
		 * The meta object literal for the '<em><b>Probability</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BINOMIAL_DISTRIBUTION_TYPE__PROBABILITY = eINSTANCE.getBinomialDistributionType_Probability();

		/**
		 * The meta object literal for the '<em><b>Trials</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BINOMIAL_DISTRIBUTION_TYPE__TRIALS = eINSTANCE.getBinomialDistributionType_Trials();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.BooleanParameterTypeImpl <em>Boolean Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.BooleanParameterTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getBooleanParameterType()
		 * @generated
		 */
		EClass BOOLEAN_PARAMETER_TYPE = eINSTANCE.getBooleanParameterType();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BOOLEAN_PARAMETER_TYPE__VALUE = eINSTANCE.getBooleanParameterType_Value();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.CalendarImpl <em>Calendar</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.CalendarImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getCalendar()
		 * @generated
		 */
		EClass CALENDAR = eINSTANCE.getCalendar();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CALENDAR__VALUE = eINSTANCE.getCalendar_Value();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CALENDAR__ID = eINSTANCE.getCalendar_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CALENDAR__NAME = eINSTANCE.getCalendar_Name();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ConstantParameterImpl <em>Constant Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ConstantParameterImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getConstantParameter()
		 * @generated
		 */
		EClass CONSTANT_PARAMETER = eINSTANCE.getConstantParameter();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ControlParametersImpl <em>Control Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ControlParametersImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getControlParameters()
		 * @generated
		 */
		EClass CONTROL_PARAMETERS = eINSTANCE.getControlParameters();

		/**
		 * The meta object literal for the '<em><b>Probability</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_PARAMETERS__PROBABILITY = eINSTANCE.getControlParameters_Probability();

		/**
		 * The meta object literal for the '<em><b>Inter Trigger Timer</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_PARAMETERS__INTER_TRIGGER_TIMER = eINSTANCE.getControlParameters_InterTriggerTimer();

		/**
		 * The meta object literal for the '<em><b>Max Trigger Count</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_PARAMETERS__MAX_TRIGGER_COUNT = eINSTANCE.getControlParameters_MaxTriggerCount();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.CostParametersImpl <em>Cost Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.CostParametersImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getCostParameters()
		 * @generated
		 */
		EClass COST_PARAMETERS = eINSTANCE.getCostParameters();

		/**
		 * The meta object literal for the '<em><b>Fixed Cost</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COST_PARAMETERS__FIXED_COST = eINSTANCE.getCostParameters_FixedCost();

		/**
		 * The meta object literal for the '<em><b>Unit Cost</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COST_PARAMETERS__UNIT_COST = eINSTANCE.getCostParameters_UnitCost();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.DateTimeParameterTypeImpl <em>Date Time Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.DateTimeParameterTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getDateTimeParameterType()
		 * @generated
		 */
		EClass DATE_TIME_PARAMETER_TYPE = eINSTANCE.getDateTimeParameterType();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATE_TIME_PARAMETER_TYPE__VALUE = eINSTANCE.getDateTimeParameterType_Value();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.DistributionParameterImpl <em>Distribution Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.DistributionParameterImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getDistributionParameter()
		 * @generated
		 */
		EClass DISTRIBUTION_PARAMETER = eINSTANCE.getDistributionParameter();

		/**
		 * The meta object literal for the '<em><b>Discrete</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DISTRIBUTION_PARAMETER__DISCRETE = eINSTANCE.getDistributionParameter_Discrete();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.DocumentRootImpl <em>Document Root</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.DocumentRootImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getDocumentRoot()
		 * @generated
		 */
		EClass DOCUMENT_ROOT = eINSTANCE.getDocumentRoot();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOCUMENT_ROOT__MIXED = eINSTANCE.getDocumentRoot_Mixed();

		/**
		 * The meta object literal for the '<em><b>XMLNS Prefix Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__XMLNS_PREFIX_MAP = eINSTANCE.getDocumentRoot_XMLNSPrefixMap();

		/**
		 * The meta object literal for the '<em><b>XSI Schema Location</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = eINSTANCE.getDocumentRoot_XSISchemaLocation();

		/**
		 * The meta object literal for the '<em><b>Beta Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BETA_DISTRIBUTION = eINSTANCE.getDocumentRoot_BetaDistribution();

		/**
		 * The meta object literal for the '<em><b>Parameter Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__PARAMETER_VALUE = eINSTANCE.getDocumentRoot_ParameterValue();

		/**
		 * The meta object literal for the '<em><b>Binomial Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION = eINSTANCE.getDocumentRoot_BinomialDistribution();

		/**
		 * The meta object literal for the '<em><b>Boolean Parameter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BOOLEAN_PARAMETER = eINSTANCE.getDocumentRoot_BooleanParameter();

		/**
		 * The meta object literal for the '<em><b>Date Time Parameter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__DATE_TIME_PARAMETER = eINSTANCE.getDocumentRoot_DateTimeParameter();

		/**
		 * The meta object literal for the '<em><b>Duration Parameter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__DURATION_PARAMETER = eINSTANCE.getDocumentRoot_DurationParameter();

		/**
		 * The meta object literal for the '<em><b>Enum Parameter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__ENUM_PARAMETER = eINSTANCE.getDocumentRoot_EnumParameter();

		/**
		 * The meta object literal for the '<em><b>Erlang Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__ERLANG_DISTRIBUTION = eINSTANCE.getDocumentRoot_ErlangDistribution();

		/**
		 * The meta object literal for the '<em><b>Expression Parameter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__EXPRESSION_PARAMETER = eINSTANCE.getDocumentRoot_ExpressionParameter();

		/**
		 * The meta object literal for the '<em><b>Floating Parameter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__FLOATING_PARAMETER = eINSTANCE.getDocumentRoot_FloatingParameter();

		/**
		 * The meta object literal for the '<em><b>Gamma Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__GAMMA_DISTRIBUTION = eINSTANCE.getDocumentRoot_GammaDistribution();

		/**
		 * The meta object literal for the '<em><b>Global</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__GLOBAL = eINSTANCE.getDocumentRoot_Global();

		/**
		 * The meta object literal for the '<em><b>Import</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__IMPORT = eINSTANCE.getDocumentRoot_Import();

		/**
		 * The meta object literal for the '<em><b>Log Normal Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION = eINSTANCE.getDocumentRoot_LogNormalDistribution();

		/**
		 * The meta object literal for the '<em><b>Metadata</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__METADATA = eINSTANCE.getDocumentRoot_Metadata();

		/**
		 * The meta object literal for the '<em><b>Metaentry</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__METAENTRY = eINSTANCE.getDocumentRoot_Metaentry();

		/**
		 * The meta object literal for the '<em><b>Negative Exponential Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION = eINSTANCE.getDocumentRoot_NegativeExponentialDistribution();

		/**
		 * The meta object literal for the '<em><b>Normal Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__NORMAL_DISTRIBUTION = eINSTANCE.getDocumentRoot_NormalDistribution();

		/**
		 * The meta object literal for the '<em><b>Numeric Parameter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__NUMERIC_PARAMETER = eINSTANCE.getDocumentRoot_NumericParameter();

		/**
		 * The meta object literal for the '<em><b>On Entry Script</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__ON_ENTRY_SCRIPT = eINSTANCE.getDocumentRoot_OnEntryScript();

		/**
		 * The meta object literal for the '<em><b>On Exit Script</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__ON_EXIT_SCRIPT = eINSTANCE.getDocumentRoot_OnExitScript();

		/**
		 * The meta object literal for the '<em><b>Poisson Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__POISSON_DISTRIBUTION = eINSTANCE.getDocumentRoot_PoissonDistribution();

		/**
		 * The meta object literal for the '<em><b>Process Analysis Data</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA = eINSTANCE.getDocumentRoot_ProcessAnalysisData();

		/**
		 * The meta object literal for the '<em><b>Random Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__RANDOM_DISTRIBUTION = eINSTANCE.getDocumentRoot_RandomDistribution();

		/**
		 * The meta object literal for the '<em><b>String Parameter</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__STRING_PARAMETER = eINSTANCE.getDocumentRoot_StringParameter();

		/**
		 * The meta object literal for the '<em><b>Triangular Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION = eINSTANCE.getDocumentRoot_TriangularDistribution();

		/**
		 * The meta object literal for the '<em><b>Truncated Normal Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION = eINSTANCE.getDocumentRoot_TruncatedNormalDistribution();

		/**
		 * The meta object literal for the '<em><b>Uniform Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__UNIFORM_DISTRIBUTION = eINSTANCE.getDocumentRoot_UniformDistribution();

		/**
		 * The meta object literal for the '<em><b>User Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__USER_DISTRIBUTION = eINSTANCE.getDocumentRoot_UserDistribution();

		/**
		 * The meta object literal for the '<em><b>User Distribution Data Point</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT = eINSTANCE.getDocumentRoot_UserDistributionDataPoint();

		/**
		 * The meta object literal for the '<em><b>Weibull Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__WEIBULL_DISTRIBUTION = eINSTANCE.getDocumentRoot_WeibullDistribution();

		/**
		 * The meta object literal for the '<em><b>Package Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOCUMENT_ROOT__PACKAGE_NAME = eINSTANCE.getDocumentRoot_PackageName();

		/**
		 * The meta object literal for the '<em><b>Priority</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOCUMENT_ROOT__PRIORITY = eINSTANCE.getDocumentRoot_Priority();

		/**
		 * The meta object literal for the '<em><b>Rule Flow Group</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOCUMENT_ROOT__RULE_FLOW_GROUP = eINSTANCE.getDocumentRoot_RuleFlowGroup();

		/**
		 * The meta object literal for the '<em><b>Task Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOCUMENT_ROOT__TASK_NAME = eINSTANCE.getDocumentRoot_TaskName();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DOCUMENT_ROOT__VERSION = eINSTANCE.getDocumentRoot_Version();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.DurationParameterTypeImpl <em>Duration Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.DurationParameterTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getDurationParameterType()
		 * @generated
		 */
		EClass DURATION_PARAMETER_TYPE = eINSTANCE.getDurationParameterType();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DURATION_PARAMETER_TYPE__VALUE = eINSTANCE.getDurationParameterType_Value();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ElementParametersImpl <em>Element Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ElementParametersImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getElementParameters()
		 * @generated
		 */
		EClass ELEMENT_PARAMETERS = eINSTANCE.getElementParameters();

		/**
		 * The meta object literal for the '<em><b>Time Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_PARAMETERS__TIME_PARAMETERS = eINSTANCE.getElementParameters_TimeParameters();

		/**
		 * The meta object literal for the '<em><b>Control Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_PARAMETERS__CONTROL_PARAMETERS = eINSTANCE.getElementParameters_ControlParameters();

		/**
		 * The meta object literal for the '<em><b>Resource Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_PARAMETERS__RESOURCE_PARAMETERS = eINSTANCE.getElementParameters_ResourceParameters();

		/**
		 * The meta object literal for the '<em><b>Priority Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_PARAMETERS__PRIORITY_PARAMETERS = eINSTANCE.getElementParameters_PriorityParameters();

		/**
		 * The meta object literal for the '<em><b>Cost Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_PARAMETERS__COST_PARAMETERS = eINSTANCE.getElementParameters_CostParameters();

		/**
		 * The meta object literal for the '<em><b>Instance Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_PARAMETERS__INSTANCE_PARAMETERS = eINSTANCE.getElementParameters_InstanceParameters();

		/**
		 * The meta object literal for the '<em><b>Vendor Extension</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_PARAMETERS__VENDOR_EXTENSION = eINSTANCE.getElementParameters_VendorExtension();

		/**
		 * The meta object literal for the '<em><b>Element Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_PARAMETERS__ELEMENT_ID = eINSTANCE.getElementParameters_ElementId();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_PARAMETERS__ID = eINSTANCE.getElementParameters_Id();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ElementParametersTypeImpl <em>Element Parameters Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ElementParametersTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getElementParametersType()
		 * @generated
		 */
		EClass ELEMENT_PARAMETERS_TYPE = eINSTANCE.getElementParametersType();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.EnumParameterTypeImpl <em>Enum Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.EnumParameterTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getEnumParameterType()
		 * @generated
		 */
		EClass ENUM_PARAMETER_TYPE = eINSTANCE.getEnumParameterType();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENUM_PARAMETER_TYPE__GROUP = eINSTANCE.getEnumParameterType_Group();

		/**
		 * The meta object literal for the '<em><b>Parameter Value Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ENUM_PARAMETER_TYPE__PARAMETER_VALUE_GROUP = eINSTANCE.getEnumParameterType_ParameterValueGroup();

		/**
		 * The meta object literal for the '<em><b>Parameter Value</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ENUM_PARAMETER_TYPE__PARAMETER_VALUE = eINSTANCE.getEnumParameterType_ParameterValue();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ErlangDistributionTypeImpl <em>Erlang Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ErlangDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getErlangDistributionType()
		 * @generated
		 */
		EClass ERLANG_DISTRIBUTION_TYPE = eINSTANCE.getErlangDistributionType();

		/**
		 * The meta object literal for the '<em><b>K</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ERLANG_DISTRIBUTION_TYPE__K = eINSTANCE.getErlangDistributionType_K();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ERLANG_DISTRIBUTION_TYPE__MEAN = eINSTANCE.getErlangDistributionType_Mean();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ExpressionParameterTypeImpl <em>Expression Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ExpressionParameterTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getExpressionParameterType()
		 * @generated
		 */
		EClass EXPRESSION_PARAMETER_TYPE = eINSTANCE.getExpressionParameterType();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXPRESSION_PARAMETER_TYPE__VALUE = eINSTANCE.getExpressionParameterType_Value();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.FloatingParameterTypeImpl <em>Floating Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.FloatingParameterTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getFloatingParameterType()
		 * @generated
		 */
		EClass FLOATING_PARAMETER_TYPE = eINSTANCE.getFloatingParameterType();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FLOATING_PARAMETER_TYPE__VALUE = eINSTANCE.getFloatingParameterType_Value();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.GammaDistributionTypeImpl <em>Gamma Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.GammaDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getGammaDistributionType()
		 * @generated
		 */
		EClass GAMMA_DISTRIBUTION_TYPE = eINSTANCE.getGammaDistributionType();

		/**
		 * The meta object literal for the '<em><b>Scale</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GAMMA_DISTRIBUTION_TYPE__SCALE = eINSTANCE.getGammaDistributionType_Scale();

		/**
		 * The meta object literal for the '<em><b>Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GAMMA_DISTRIBUTION_TYPE__SHAPE = eINSTANCE.getGammaDistributionType_Shape();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.GlobalParameterTypeImpl <em>Global Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.GlobalParameterTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getGlobalParameterType()
		 * @generated
		 */
		EClass GLOBAL_PARAMETER_TYPE = eINSTANCE.getGlobalParameterType();

		/**
		 * The meta object literal for the '<em><b>Property</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GLOBAL_PARAMETER_TYPE__PROPERTY = eINSTANCE.getGlobalParameterType_Property();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.GlobalTypeImpl <em>Global Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.GlobalTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getGlobalType()
		 * @generated
		 */
		EClass GLOBAL_TYPE = eINSTANCE.getGlobalType();

		/**
		 * The meta object literal for the '<em><b>Identifier</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GLOBAL_TYPE__IDENTIFIER = eINSTANCE.getGlobalType_Identifier();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GLOBAL_TYPE__TYPE = eINSTANCE.getGlobalType_Type();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ImportTypeImpl <em>Import Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ImportTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getImportType()
		 * @generated
		 */
		EClass IMPORT_TYPE = eINSTANCE.getImportType();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPORT_TYPE__NAME = eINSTANCE.getImportType_Name();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.InstanceParametersImpl <em>Instance Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.InstanceParametersImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getInstanceParameters()
		 * @generated
		 */
		EClass INSTANCE_PARAMETERS = eINSTANCE.getInstanceParameters();

		/**
		 * The meta object literal for the '<em><b>Property</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INSTANCE_PARAMETERS__PROPERTY = eINSTANCE.getInstanceParameters_Property();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.LogNormalDistributionTypeImpl <em>Log Normal Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.LogNormalDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getLogNormalDistributionType()
		 * @generated
		 */
		EClass LOG_NORMAL_DISTRIBUTION_TYPE = eINSTANCE.getLogNormalDistributionType();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG_NORMAL_DISTRIBUTION_TYPE__MEAN = eINSTANCE.getLogNormalDistributionType_Mean();

		/**
		 * The meta object literal for the '<em><b>Standard Deviation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION = eINSTANCE.getLogNormalDistributionType_StandardDeviation();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.MetadataTypeImpl <em>Metadata Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.MetadataTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getMetadataType()
		 * @generated
		 */
		EClass METADATA_TYPE = eINSTANCE.getMetadataType();

		/**
		 * The meta object literal for the '<em><b>Metaentry</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference METADATA_TYPE__METAENTRY = eINSTANCE.getMetadataType_Metaentry();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.MetaentryTypeImpl <em>Metaentry Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.MetaentryTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getMetaentryType()
		 * @generated
		 */
		EClass METAENTRY_TYPE = eINSTANCE.getMetaentryType();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute METAENTRY_TYPE__NAME = eINSTANCE.getMetaentryType_Name();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute METAENTRY_TYPE__VALUE = eINSTANCE.getMetaentryType_Value();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.NegativeExponentialDistributionTypeImpl <em>Negative Exponential Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.NegativeExponentialDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getNegativeExponentialDistributionType()
		 * @generated
		 */
		EClass NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE = eINSTANCE.getNegativeExponentialDistributionType();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE__MEAN = eINSTANCE.getNegativeExponentialDistributionType_Mean();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.NormalDistributionTypeImpl <em>Normal Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.NormalDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getNormalDistributionType()
		 * @generated
		 */
		EClass NORMAL_DISTRIBUTION_TYPE = eINSTANCE.getNormalDistributionType();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NORMAL_DISTRIBUTION_TYPE__MEAN = eINSTANCE.getNormalDistributionType_Mean();

		/**
		 * The meta object literal for the '<em><b>Standard Deviation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION = eINSTANCE.getNormalDistributionType_StandardDeviation();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.NumericParameterTypeImpl <em>Numeric Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.NumericParameterTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getNumericParameterType()
		 * @generated
		 */
		EClass NUMERIC_PARAMETER_TYPE = eINSTANCE.getNumericParameterType();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NUMERIC_PARAMETER_TYPE__VALUE = eINSTANCE.getNumericParameterType_Value();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.OnEntryScriptTypeImpl <em>On Entry Script Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.OnEntryScriptTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getOnEntryScriptType()
		 * @generated
		 */
		EClass ON_ENTRY_SCRIPT_TYPE = eINSTANCE.getOnEntryScriptType();

		/**
		 * The meta object literal for the '<em><b>Script</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ON_ENTRY_SCRIPT_TYPE__SCRIPT = eINSTANCE.getOnEntryScriptType_Script();

		/**
		 * The meta object literal for the '<em><b>Script Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ON_ENTRY_SCRIPT_TYPE__SCRIPT_FORMAT = eINSTANCE.getOnEntryScriptType_ScriptFormat();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.OnExitScriptTypeImpl <em>On Exit Script Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.OnExitScriptTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getOnExitScriptType()
		 * @generated
		 */
		EClass ON_EXIT_SCRIPT_TYPE = eINSTANCE.getOnExitScriptType();

		/**
		 * The meta object literal for the '<em><b>Script</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ON_EXIT_SCRIPT_TYPE__SCRIPT = eINSTANCE.getOnExitScriptType_Script();

		/**
		 * The meta object literal for the '<em><b>Script Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ON_EXIT_SCRIPT_TYPE__SCRIPT_FORMAT = eINSTANCE.getOnExitScriptType_ScriptFormat();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ParameterImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '<em><b>Result Request</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__RESULT_REQUEST = eINSTANCE.getParameter_ResultRequest();

		/**
		 * The meta object literal for the '<em><b>Parameter Value Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__PARAMETER_VALUE_GROUP = eINSTANCE.getParameter_ParameterValueGroup();

		/**
		 * The meta object literal for the '<em><b>Parameter Value</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER__PARAMETER_VALUE = eINSTANCE.getParameter_ParameterValue();

		/**
		 * The meta object literal for the '<em><b>Kpi</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__KPI = eINSTANCE.getParameter_Kpi();

		/**
		 * The meta object literal for the '<em><b>Sla</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__SLA = eINSTANCE.getParameter_Sla();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ParameterValueImpl <em>Parameter Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ParameterValueImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getParameterValue()
		 * @generated
		 */
		EClass PARAMETER_VALUE = eINSTANCE.getParameterValue();

		/**
		 * The meta object literal for the '<em><b>Instance</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_VALUE__INSTANCE = eINSTANCE.getParameterValue_Instance();

		/**
		 * The meta object literal for the '<em><b>Result</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_VALUE__RESULT = eINSTANCE.getParameterValue_Result();

		/**
		 * The meta object literal for the '<em><b>Valid For</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER_VALUE__VALID_FOR = eINSTANCE.getParameterValue_ValidFor();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.PoissonDistributionTypeImpl <em>Poisson Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.PoissonDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getPoissonDistributionType()
		 * @generated
		 */
		EClass POISSON_DISTRIBUTION_TYPE = eINSTANCE.getPoissonDistributionType();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute POISSON_DISTRIBUTION_TYPE__MEAN = eINSTANCE.getPoissonDistributionType_Mean();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.PriorityParametersImpl <em>Priority Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.PriorityParametersImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getPriorityParameters()
		 * @generated
		 */
		EClass PRIORITY_PARAMETERS = eINSTANCE.getPriorityParameters();

		/**
		 * The meta object literal for the '<em><b>Interruptible</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PRIORITY_PARAMETERS__INTERRUPTIBLE = eINSTANCE.getPriorityParameters_Interruptible();

		/**
		 * The meta object literal for the '<em><b>Priority</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PRIORITY_PARAMETERS__PRIORITY = eINSTANCE.getPriorityParameters_Priority();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ProcessAnalysisDataTypeImpl <em>Process Analysis Data Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ProcessAnalysisDataTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getProcessAnalysisDataType()
		 * @generated
		 */
		EClass PROCESS_ANALYSIS_DATA_TYPE = eINSTANCE.getProcessAnalysisDataType();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROCESS_ANALYSIS_DATA_TYPE__GROUP = eINSTANCE.getProcessAnalysisDataType_Group();

		/**
		 * The meta object literal for the '<em><b>Scenario</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROCESS_ANALYSIS_DATA_TYPE__SCENARIO = eINSTANCE.getProcessAnalysisDataType_Scenario();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.PropertyTypeImpl <em>Property Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.PropertyTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getPropertyType()
		 * @generated
		 */
		EClass PROPERTY_TYPE = eINSTANCE.getPropertyType();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY_TYPE__NAME = eINSTANCE.getPropertyType_Name();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.RandomDistributionTypeImpl <em>Random Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.RandomDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getRandomDistributionType()
		 * @generated
		 */
		EClass RANDOM_DISTRIBUTION_TYPE = eINSTANCE.getRandomDistributionType();

		/**
		 * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RANDOM_DISTRIBUTION_TYPE__MAX = eINSTANCE.getRandomDistributionType_Max();

		/**
		 * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RANDOM_DISTRIBUTION_TYPE__MIN = eINSTANCE.getRandomDistributionType_Min();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ResourceParametersImpl <em>Resource Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ResourceParametersImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getResourceParameters()
		 * @generated
		 */
		EClass RESOURCE_PARAMETERS = eINSTANCE.getResourceParameters();

		/**
		 * The meta object literal for the '<em><b>Selection</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_PARAMETERS__SELECTION = eINSTANCE.getResourceParameters_Selection();

		/**
		 * The meta object literal for the '<em><b>Availability</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_PARAMETERS__AVAILABILITY = eINSTANCE.getResourceParameters_Availability();

		/**
		 * The meta object literal for the '<em><b>Quantity</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_PARAMETERS__QUANTITY = eINSTANCE.getResourceParameters_Quantity();

		/**
		 * The meta object literal for the '<em><b>Workinghours</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_PARAMETERS__WORKINGHOURS = eINSTANCE.getResourceParameters_Workinghours();

		/**
		 * The meta object literal for the '<em><b>Role</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_PARAMETERS__ROLE = eINSTANCE.getResourceParameters_Role();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ScenarioImpl <em>Scenario</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ScenarioImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getScenario()
		 * @generated
		 */
		EClass SCENARIO = eINSTANCE.getScenario();

		/**
		 * The meta object literal for the '<em><b>Scenario Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCENARIO__SCENARIO_PARAMETERS = eINSTANCE.getScenario_ScenarioParameters();

		/**
		 * The meta object literal for the '<em><b>Element Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCENARIO__ELEMENT_PARAMETERS = eINSTANCE.getScenario_ElementParameters();

		/**
		 * The meta object literal for the '<em><b>Calendar</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCENARIO__CALENDAR = eINSTANCE.getScenario_Calendar();

		/**
		 * The meta object literal for the '<em><b>Vendor Extension</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCENARIO__VENDOR_EXTENSION = eINSTANCE.getScenario_VendorExtension();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__AUTHOR = eINSTANCE.getScenario_Author();

		/**
		 * The meta object literal for the '<em><b>Created</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__CREATED = eINSTANCE.getScenario_Created();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__DESCRIPTION = eINSTANCE.getScenario_Description();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__ID = eINSTANCE.getScenario_Id();

		/**
		 * The meta object literal for the '<em><b>Inherits</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__INHERITS = eINSTANCE.getScenario_Inherits();

		/**
		 * The meta object literal for the '<em><b>Modified</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__MODIFIED = eINSTANCE.getScenario_Modified();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__NAME = eINSTANCE.getScenario_Name();

		/**
		 * The meta object literal for the '<em><b>Result</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__RESULT = eINSTANCE.getScenario_Result();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__VERSION = eINSTANCE.getScenario_Version();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ScenarioParametersImpl <em>Scenario Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ScenarioParametersImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getScenarioParameters()
		 * @generated
		 */
		EClass SCENARIO_PARAMETERS = eINSTANCE.getScenarioParameters();

		/**
		 * The meta object literal for the '<em><b>Start</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCENARIO_PARAMETERS__START = eINSTANCE.getScenarioParameters_Start();

		/**
		 * The meta object literal for the '<em><b>Duration</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCENARIO_PARAMETERS__DURATION = eINSTANCE.getScenarioParameters_Duration();

		/**
		 * The meta object literal for the '<em><b>Global Parameter</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCENARIO_PARAMETERS__GLOBAL_PARAMETER = eINSTANCE.getScenarioParameters_GlobalParameter();

		/**
		 * The meta object literal for the '<em><b>Base Currency Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO_PARAMETERS__BASE_CURRENCY_UNIT = eINSTANCE.getScenarioParameters_BaseCurrencyUnit();

		/**
		 * The meta object literal for the '<em><b>Base Time Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO_PARAMETERS__BASE_TIME_UNIT = eINSTANCE.getScenarioParameters_BaseTimeUnit();

		/**
		 * The meta object literal for the '<em><b>Replication</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO_PARAMETERS__REPLICATION = eINSTANCE.getScenarioParameters_Replication();

		/**
		 * The meta object literal for the '<em><b>Seed</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO_PARAMETERS__SEED = eINSTANCE.getScenarioParameters_Seed();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.ScenarioParametersTypeImpl <em>Scenario Parameters Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.ScenarioParametersTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getScenarioParametersType()
		 * @generated
		 */
		EClass SCENARIO_PARAMETERS_TYPE = eINSTANCE.getScenarioParametersType();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.StringParameterTypeImpl <em>String Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.StringParameterTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getStringParameterType()
		 * @generated
		 */
		EClass STRING_PARAMETER_TYPE = eINSTANCE.getStringParameterType();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_PARAMETER_TYPE__VALUE = eINSTANCE.getStringParameterType_Value();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.TimeParametersImpl <em>Time Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.TimeParametersImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getTimeParameters()
		 * @generated
		 */
		EClass TIME_PARAMETERS = eINSTANCE.getTimeParameters();

		/**
		 * The meta object literal for the '<em><b>Transfer Time</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIME_PARAMETERS__TRANSFER_TIME = eINSTANCE.getTimeParameters_TransferTime();

		/**
		 * The meta object literal for the '<em><b>Queue Time</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIME_PARAMETERS__QUEUE_TIME = eINSTANCE.getTimeParameters_QueueTime();

		/**
		 * The meta object literal for the '<em><b>Wait Time</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIME_PARAMETERS__WAIT_TIME = eINSTANCE.getTimeParameters_WaitTime();

		/**
		 * The meta object literal for the '<em><b>Set Up Time</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIME_PARAMETERS__SET_UP_TIME = eINSTANCE.getTimeParameters_SetUpTime();

		/**
		 * The meta object literal for the '<em><b>Processing Time</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIME_PARAMETERS__PROCESSING_TIME = eINSTANCE.getTimeParameters_ProcessingTime();

		/**
		 * The meta object literal for the '<em><b>Validation Time</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIME_PARAMETERS__VALIDATION_TIME = eINSTANCE.getTimeParameters_ValidationTime();

		/**
		 * The meta object literal for the '<em><b>Rework Time</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIME_PARAMETERS__REWORK_TIME = eINSTANCE.getTimeParameters_ReworkTime();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.TriangularDistributionTypeImpl <em>Triangular Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.TriangularDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getTriangularDistributionType()
		 * @generated
		 */
		EClass TRIANGULAR_DISTRIBUTION_TYPE = eINSTANCE.getTriangularDistributionType();

		/**
		 * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRIANGULAR_DISTRIBUTION_TYPE__MAX = eINSTANCE.getTriangularDistributionType_Max();

		/**
		 * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRIANGULAR_DISTRIBUTION_TYPE__MIN = eINSTANCE.getTriangularDistributionType_Min();

		/**
		 * The meta object literal for the '<em><b>Most Likely</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRIANGULAR_DISTRIBUTION_TYPE__MOST_LIKELY = eINSTANCE.getTriangularDistributionType_MostLikely();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.TruncatedNormalDistributionTypeImpl <em>Truncated Normal Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.TruncatedNormalDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getTruncatedNormalDistributionType()
		 * @generated
		 */
		EClass TRUNCATED_NORMAL_DISTRIBUTION_TYPE = eINSTANCE.getTruncatedNormalDistributionType();

		/**
		 * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRUNCATED_NORMAL_DISTRIBUTION_TYPE__MAX = eINSTANCE.getTruncatedNormalDistributionType_Max();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRUNCATED_NORMAL_DISTRIBUTION_TYPE__MEAN = eINSTANCE.getTruncatedNormalDistributionType_Mean();

		/**
		 * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRUNCATED_NORMAL_DISTRIBUTION_TYPE__MIN = eINSTANCE.getTruncatedNormalDistributionType_Min();

		/**
		 * The meta object literal for the '<em><b>Standard Deviation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRUNCATED_NORMAL_DISTRIBUTION_TYPE__STANDARD_DEVIATION = eINSTANCE.getTruncatedNormalDistributionType_StandardDeviation();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.UniformDistributionTypeImpl <em>Uniform Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.UniformDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getUniformDistributionType()
		 * @generated
		 */
		EClass UNIFORM_DISTRIBUTION_TYPE = eINSTANCE.getUniformDistributionType();

		/**
		 * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNIFORM_DISTRIBUTION_TYPE__MAX = eINSTANCE.getUniformDistributionType_Max();

		/**
		 * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNIFORM_DISTRIBUTION_TYPE__MIN = eINSTANCE.getUniformDistributionType_Min();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.UserDistributionDataPointTypeImpl <em>User Distribution Data Point Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.UserDistributionDataPointTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getUserDistributionDataPointType()
		 * @generated
		 */
		EClass USER_DISTRIBUTION_DATA_POINT_TYPE = eINSTANCE.getUserDistributionDataPointType();

		/**
		 * The meta object literal for the '<em><b>Parameter Value Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE_GROUP = eINSTANCE.getUserDistributionDataPointType_ParameterValueGroup();

		/**
		 * The meta object literal for the '<em><b>Parameter Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference USER_DISTRIBUTION_DATA_POINT_TYPE__PARAMETER_VALUE = eINSTANCE.getUserDistributionDataPointType_ParameterValue();

		/**
		 * The meta object literal for the '<em><b>Probability</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute USER_DISTRIBUTION_DATA_POINT_TYPE__PROBABILITY = eINSTANCE.getUserDistributionDataPointType_Probability();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.UserDistributionTypeImpl <em>User Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.UserDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getUserDistributionType()
		 * @generated
		 */
		EClass USER_DISTRIBUTION_TYPE = eINSTANCE.getUserDistributionType();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute USER_DISTRIBUTION_TYPE__GROUP = eINSTANCE.getUserDistributionType_Group();

		/**
		 * The meta object literal for the '<em><b>User Distribution Data Point</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference USER_DISTRIBUTION_TYPE__USER_DISTRIBUTION_DATA_POINT = eINSTANCE.getUserDistributionType_UserDistributionDataPoint();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.VendorExtensionImpl <em>Vendor Extension</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.VendorExtensionImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getVendorExtension()
		 * @generated
		 */
		EClass VENDOR_EXTENSION = eINSTANCE.getVendorExtension();

		/**
		 * The meta object literal for the '<em><b>Any</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VENDOR_EXTENSION__ANY = eINSTANCE.getVendorExtension_Any();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VENDOR_EXTENSION__NAME = eINSTANCE.getVendorExtension_Name();

		/**
		 * The meta object literal for the '<em><b>Any Attribute</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VENDOR_EXTENSION__ANY_ATTRIBUTE = eINSTANCE.getVendorExtension_AnyAttribute();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.impl.WeibullDistributionTypeImpl <em>Weibull Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.WeibullDistributionTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getWeibullDistributionType()
		 * @generated
		 */
		EClass WEIBULL_DISTRIBUTION_TYPE = eINSTANCE.getWeibullDistributionType();

		/**
		 * The meta object literal for the '<em><b>Scale</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEIBULL_DISTRIBUTION_TYPE__SCALE = eINSTANCE.getWeibullDistributionType_Scale();

		/**
		 * The meta object literal for the '<em><b>Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WEIBULL_DISTRIBUTION_TYPE__SHAPE = eINSTANCE.getWeibullDistributionType_Shape();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.ResultType <em>Result Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.ResultType
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getResultType()
		 * @generated
		 */
		EEnum RESULT_TYPE = eINSTANCE.getResultType();

		/**
		 * The meta object literal for the '{@link org.jboss.drools.TimeUnit <em>Time Unit</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.TimeUnit
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getTimeUnit()
		 * @generated
		 */
		EEnum TIME_UNIT = eINSTANCE.getTimeUnit();

		/**
		 * The meta object literal for the '<em>Package Name Type</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.String
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getPackageNameType()
		 * @generated
		 */
		EDataType PACKAGE_NAME_TYPE = eINSTANCE.getPackageNameType();

		/**
		 * The meta object literal for the '<em>Priority Type</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.math.BigInteger
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getPriorityType()
		 * @generated
		 */
		EDataType PRIORITY_TYPE = eINSTANCE.getPriorityType();

		/**
		 * The meta object literal for the '<em>Result Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.ResultType
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getResultTypeObject()
		 * @generated
		 */
		EDataType RESULT_TYPE_OBJECT = eINSTANCE.getResultTypeObject();

		/**
		 * The meta object literal for the '<em>Rule Flow Group Type</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.String
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getRuleFlowGroupType()
		 * @generated
		 */
		EDataType RULE_FLOW_GROUP_TYPE = eINSTANCE.getRuleFlowGroupType();

		/**
		 * The meta object literal for the '<em>Task Name Type</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.String
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getTaskNameType()
		 * @generated
		 */
		EDataType TASK_NAME_TYPE = eINSTANCE.getTaskNameType();

		/**
		 * The meta object literal for the '<em>Time Unit Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.TimeUnit
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getTimeUnitObject()
		 * @generated
		 */
		EDataType TIME_UNIT_OBJECT = eINSTANCE.getTimeUnitObject();

		/**
		 * The meta object literal for the '<em>Version Type</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.String
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getVersionType()
		 * @generated
		 */
		EDataType VERSION_TYPE = eINSTANCE.getVersionType();

	}

} //DroolsPackage
