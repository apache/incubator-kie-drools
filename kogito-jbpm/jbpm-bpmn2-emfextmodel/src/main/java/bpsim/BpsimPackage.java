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
 * @see bpsim.BpsimFactory
 * @model kind="package"
 * @generated
 */
public interface BpsimPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "bpsim";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.bpsim.org/schemas/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "bpsim";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	BpsimPackage eINSTANCE = bpsim.impl.BpsimPackageImpl.init();

	/**
	 * The meta object id for the '{@link bpsim.impl.ParameterValueImpl <em>Parameter Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ParameterValueImpl
	 * @see bpsim.impl.BpsimPackageImpl#getParameterValue()
	 * @generated
	 */
	int PARAMETER_VALUE = 24;

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
	 * The meta object id for the '{@link bpsim.impl.DistributionParameterImpl <em>Distribution Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.DistributionParameterImpl
	 * @see bpsim.impl.BpsimPackageImpl#getDistributionParameter()
	 * @generated
	 */
	int DISTRIBUTION_PARAMETER = 9;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_PARAMETER__CURRENCY_UNIT = PARAMETER_VALUE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_PARAMETER__TIME_UNIT = PARAMETER_VALUE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Distribution Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_PARAMETER_FEATURE_COUNT = PARAMETER_VALUE_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link bpsim.impl.BetaDistributionTypeImpl <em>Beta Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.BetaDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getBetaDistributionType()
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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.BinomialDistributionTypeImpl <em>Binomial Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.BinomialDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getBinomialDistributionType()
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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.ConstantParameterImpl <em>Constant Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ConstantParameterImpl
	 * @see bpsim.impl.BpsimPackageImpl#getConstantParameter()
	 * @generated
	 */
	int CONSTANT_PARAMETER = 5;

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
	 * The meta object id for the '{@link bpsim.impl.BooleanParameterTypeImpl <em>Boolean Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.BooleanParameterTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getBooleanParameterType()
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
	 * The meta object id for the '{@link bpsim.impl.BPSimDataTypeImpl <em>BP Sim Data Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.BPSimDataTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getBPSimDataType()
	 * @generated
	 */
	int BP_SIM_DATA_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BP_SIM_DATA_TYPE__GROUP = 0;

	/**
	 * The feature id for the '<em><b>Scenario</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BP_SIM_DATA_TYPE__SCENARIO = 1;

	/**
	 * The number of structural features of the '<em>BP Sim Data Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BP_SIM_DATA_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link bpsim.impl.CalendarImpl <em>Calendar</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.CalendarImpl
	 * @see bpsim.impl.BpsimPackageImpl#getCalendar()
	 * @generated
	 */
	int CALENDAR = 4;

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
	 * The meta object id for the '{@link bpsim.impl.ControlParametersImpl <em>Control Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ControlParametersImpl
	 * @see bpsim.impl.BpsimPackageImpl#getControlParameters()
	 * @generated
	 */
	int CONTROL_PARAMETERS = 6;

	/**
	 * The feature id for the '<em><b>Probability</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_PARAMETERS__PROBABILITY = 0;

	/**
	 * The feature id for the '<em><b>Condition</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_PARAMETERS__CONDITION = 1;

	/**
	 * The feature id for the '<em><b>Inter Trigger Timer</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_PARAMETERS__INTER_TRIGGER_TIMER = 2;

	/**
	 * The feature id for the '<em><b>Trigger Count</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_PARAMETERS__TRIGGER_COUNT = 3;

	/**
	 * The number of structural features of the '<em>Control Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTROL_PARAMETERS_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link bpsim.impl.CostParametersImpl <em>Cost Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.CostParametersImpl
	 * @see bpsim.impl.BpsimPackageImpl#getCostParameters()
	 * @generated
	 */
	int COST_PARAMETERS = 7;

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
	 * The meta object id for the '{@link bpsim.impl.DateTimeParameterTypeImpl <em>Date Time Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.DateTimeParameterTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getDateTimeParameterType()
	 * @generated
	 */
	int DATE_TIME_PARAMETER_TYPE = 8;

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
	 * The meta object id for the '{@link bpsim.impl.DocumentRootImpl <em>Document Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.DocumentRootImpl
	 * @see bpsim.impl.BpsimPackageImpl#getDocumentRoot()
	 * @generated
	 */
	int DOCUMENT_ROOT = 10;

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
	 * The feature id for the '<em><b>BP Sim Data</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__BP_SIM_DATA = 7;

	/**
	 * The feature id for the '<em><b>Date Time Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__DATE_TIME_PARAMETER = 8;

	/**
	 * The feature id for the '<em><b>Duration Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__DURATION_PARAMETER = 9;

	/**
	 * The feature id for the '<em><b>Enum Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__ENUM_PARAMETER = 10;

	/**
	 * The feature id for the '<em><b>Erlang Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__ERLANG_DISTRIBUTION = 11;

	/**
	 * The feature id for the '<em><b>Expression Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__EXPRESSION_PARAMETER = 12;

	/**
	 * The feature id for the '<em><b>Floating Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__FLOATING_PARAMETER = 13;

	/**
	 * The feature id for the '<em><b>Gamma Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__GAMMA_DISTRIBUTION = 14;

	/**
	 * The feature id for the '<em><b>Log Normal Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION = 15;

	/**
	 * The feature id for the '<em><b>Negative Exponential Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION = 16;

	/**
	 * The feature id for the '<em><b>Normal Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__NORMAL_DISTRIBUTION = 17;

	/**
	 * The feature id for the '<em><b>Numeric Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__NUMERIC_PARAMETER = 18;

	/**
	 * The feature id for the '<em><b>Poisson Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__POISSON_DISTRIBUTION = 19;

	/**
	 * The feature id for the '<em><b>String Parameter</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__STRING_PARAMETER = 20;

	/**
	 * The feature id for the '<em><b>Triangular Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION = 21;

	/**
	 * The feature id for the '<em><b>Truncated Normal Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION = 22;

	/**
	 * The feature id for the '<em><b>Uniform Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__UNIFORM_DISTRIBUTION = 23;

	/**
	 * The feature id for the '<em><b>User Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__USER_DISTRIBUTION = 24;

	/**
	 * The feature id for the '<em><b>User Distribution Data Point</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT = 25;

	/**
	 * The feature id for the '<em><b>Weibull Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__WEIBULL_DISTRIBUTION = 26;

	/**
	 * The number of structural features of the '<em>Document Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT_FEATURE_COUNT = 27;

	/**
	 * The meta object id for the '{@link bpsim.impl.DurationParameterTypeImpl <em>Duration Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.DurationParameterTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getDurationParameterType()
	 * @generated
	 */
	int DURATION_PARAMETER_TYPE = 11;

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
	 * The meta object id for the '{@link bpsim.impl.ElementParametersImpl <em>Element Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ElementParametersImpl
	 * @see bpsim.impl.BpsimPackageImpl#getElementParameters()
	 * @generated
	 */
	int ELEMENT_PARAMETERS = 12;

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
	 * The feature id for the '<em><b>Property Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__PROPERTY_PARAMETERS = 5;

	/**
	 * The feature id for the '<em><b>Vendor Extension</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__VENDOR_EXTENSION = 6;

	/**
	 * The feature id for the '<em><b>Element Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS__ELEMENT_REF = 7;

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
	 * The meta object id for the '{@link bpsim.impl.ElementParametersTypeImpl <em>Element Parameters Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ElementParametersTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getElementParametersType()
	 * @generated
	 */
	int ELEMENT_PARAMETERS_TYPE = 13;

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
	 * The feature id for the '<em><b>Property Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__PROPERTY_PARAMETERS = ELEMENT_PARAMETERS__PROPERTY_PARAMETERS;

	/**
	 * The feature id for the '<em><b>Vendor Extension</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__VENDOR_EXTENSION = ELEMENT_PARAMETERS__VENDOR_EXTENSION;

	/**
	 * The feature id for the '<em><b>Element Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ELEMENT_PARAMETERS_TYPE__ELEMENT_REF = ELEMENT_PARAMETERS__ELEMENT_REF;

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
	 * The meta object id for the '{@link bpsim.impl.EnumParameterTypeImpl <em>Enum Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.EnumParameterTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getEnumParameterType()
	 * @generated
	 */
	int ENUM_PARAMETER_TYPE = 14;

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
	 * The meta object id for the '{@link bpsim.impl.ErlangDistributionTypeImpl <em>Erlang Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ErlangDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getErlangDistributionType()
	 * @generated
	 */
	int ERLANG_DISTRIBUTION_TYPE = 15;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ERLANG_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ERLANG_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.ExpressionParameterTypeImpl <em>Expression Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ExpressionParameterTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getExpressionParameterType()
	 * @generated
	 */
	int EXPRESSION_PARAMETER_TYPE = 16;

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
	 * The meta object id for the '{@link bpsim.impl.FloatingParameterTypeImpl <em>Floating Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.FloatingParameterTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getFloatingParameterType()
	 * @generated
	 */
	int FLOATING_PARAMETER_TYPE = 17;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOATING_PARAMETER_TYPE__CURRENCY_UNIT = CONSTANT_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOATING_PARAMETER_TYPE__TIME_UNIT = CONSTANT_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOATING_PARAMETER_TYPE__VALUE = CONSTANT_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Floating Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FLOATING_PARAMETER_TYPE_FEATURE_COUNT = CONSTANT_PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link bpsim.impl.GammaDistributionTypeImpl <em>Gamma Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.GammaDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getGammaDistributionType()
	 * @generated
	 */
	int GAMMA_DISTRIBUTION_TYPE = 18;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.LogNormalDistributionTypeImpl <em>Log Normal Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.LogNormalDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getLogNormalDistributionType()
	 * @generated
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE = 19;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.NegativeExponentialDistributionTypeImpl <em>Negative Exponential Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.NegativeExponentialDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getNegativeExponentialDistributionType()
	 * @generated
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE = 20;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NEGATIVE_EXPONENTIAL_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.NormalDistributionTypeImpl <em>Normal Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.NormalDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getNormalDistributionType()
	 * @generated
	 */
	int NORMAL_DISTRIBUTION_TYPE = 21;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.NumericParameterTypeImpl <em>Numeric Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.NumericParameterTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getNumericParameterType()
	 * @generated
	 */
	int NUMERIC_PARAMETER_TYPE = 22;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PARAMETER_TYPE__CURRENCY_UNIT = CONSTANT_PARAMETER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PARAMETER_TYPE__TIME_UNIT = CONSTANT_PARAMETER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PARAMETER_TYPE__VALUE = CONSTANT_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Numeric Parameter Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_PARAMETER_TYPE_FEATURE_COUNT = CONSTANT_PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link bpsim.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ParameterImpl
	 * @see bpsim.impl.BpsimPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 23;

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
	 * The meta object id for the '{@link bpsim.impl.PoissonDistributionTypeImpl <em>Poisson Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.PoissonDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getPoissonDistributionType()
	 * @generated
	 */
	int POISSON_DISTRIBUTION_TYPE = 25;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.PriorityParametersImpl <em>Priority Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.PriorityParametersImpl
	 * @see bpsim.impl.BpsimPackageImpl#getPriorityParameters()
	 * @generated
	 */
	int PRIORITY_PARAMETERS = 26;

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
	 * The meta object id for the '{@link bpsim.impl.PropertyParametersImpl <em>Property Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.PropertyParametersImpl
	 * @see bpsim.impl.BpsimPackageImpl#getPropertyParameters()
	 * @generated
	 */
	int PROPERTY_PARAMETERS = 27;

	/**
	 * The feature id for the '<em><b>Property</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_PARAMETERS__PROPERTY = 0;

	/**
	 * The number of structural features of the '<em>Property Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_PARAMETERS_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link bpsim.impl.PropertyTypeImpl <em>Property Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.PropertyTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getPropertyType()
	 * @generated
	 */
	int PROPERTY_TYPE = 28;

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
	 * The meta object id for the '{@link bpsim.impl.ResourceParametersImpl <em>Resource Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ResourceParametersImpl
	 * @see bpsim.impl.BpsimPackageImpl#getResourceParameters()
	 * @generated
	 */
	int RESOURCE_PARAMETERS = 29;

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
	 * The feature id for the '<em><b>Role</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_PARAMETERS__ROLE = 3;

	/**
	 * The number of structural features of the '<em>Resource Parameters</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_PARAMETERS_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link bpsim.impl.ScenarioImpl <em>Scenario</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ScenarioImpl
	 * @see bpsim.impl.BpsimPackageImpl#getScenario()
	 * @generated
	 */
	int SCENARIO = 30;

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
	 * The feature id for the '<em><b>Vendor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__VENDOR = 12;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO__VERSION = 13;

	/**
	 * The number of structural features of the '<em>Scenario</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_FEATURE_COUNT = 14;

	/**
	 * The meta object id for the '{@link bpsim.impl.ScenarioParametersImpl <em>Scenario Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ScenarioParametersImpl
	 * @see bpsim.impl.BpsimPackageImpl#getScenarioParameters()
	 * @generated
	 */
	int SCENARIO_PARAMETERS = 31;

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
	 * The feature id for the '<em><b>Property Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS__PROPERTY_PARAMETERS = 2;

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
	 * The meta object id for the '{@link bpsim.impl.ScenarioParametersTypeImpl <em>Scenario Parameters Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.ScenarioParametersTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getScenarioParametersType()
	 * @generated
	 */
	int SCENARIO_PARAMETERS_TYPE = 32;

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
	 * The feature id for the '<em><b>Property Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SCENARIO_PARAMETERS_TYPE__PROPERTY_PARAMETERS = SCENARIO_PARAMETERS__PROPERTY_PARAMETERS;

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
	 * The meta object id for the '{@link bpsim.impl.StringParameterTypeImpl <em>String Parameter Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.StringParameterTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getStringParameterType()
	 * @generated
	 */
	int STRING_PARAMETER_TYPE = 33;

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
	 * The meta object id for the '{@link bpsim.impl.TimeParametersImpl <em>Time Parameters</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.TimeParametersImpl
	 * @see bpsim.impl.BpsimPackageImpl#getTimeParameters()
	 * @generated
	 */
	int TIME_PARAMETERS = 34;

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
	 * The meta object id for the '{@link bpsim.impl.TriangularDistributionTypeImpl <em>Triangular Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.TriangularDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getTriangularDistributionType()
	 * @generated
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE = 35;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE__MODE = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Triangular Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link bpsim.impl.TruncatedNormalDistributionTypeImpl <em>Truncated Normal Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.TruncatedNormalDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getTruncatedNormalDistributionType()
	 * @generated
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE = 36;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRUNCATED_NORMAL_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.UniformDistributionTypeImpl <em>Uniform Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.UniformDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getUniformDistributionType()
	 * @generated
	 */
	int UNIFORM_DISTRIBUTION_TYPE = 37;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.impl.UserDistributionDataPointTypeImpl <em>User Distribution Data Point Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.UserDistributionDataPointTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getUserDistributionDataPointType()
	 * @generated
	 */
	int USER_DISTRIBUTION_DATA_POINT_TYPE = 38;

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
	 * The meta object id for the '{@link bpsim.impl.UserDistributionTypeImpl <em>User Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.UserDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getUserDistributionType()
	 * @generated
	 */
	int USER_DISTRIBUTION_TYPE = 39;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The feature id for the '<em><b>Discrete</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE__DISCRETE = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>User Distribution Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_DISTRIBUTION_TYPE_FEATURE_COUNT = DISTRIBUTION_PARAMETER_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link bpsim.impl.VendorExtensionImpl <em>Vendor Extension</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.VendorExtensionImpl
	 * @see bpsim.impl.BpsimPackageImpl#getVendorExtension()
	 * @generated
	 */
	int VENDOR_EXTENSION = 40;

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
	 * The meta object id for the '{@link bpsim.impl.WeibullDistributionTypeImpl <em>Weibull Distribution Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.impl.WeibullDistributionTypeImpl
	 * @see bpsim.impl.BpsimPackageImpl#getWeibullDistributionType()
	 * @generated
	 */
	int WEIBULL_DISTRIBUTION_TYPE = 41;

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
	 * The feature id for the '<em><b>Currency Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEIBULL_DISTRIBUTION_TYPE__CURRENCY_UNIT = DISTRIBUTION_PARAMETER__CURRENCY_UNIT;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WEIBULL_DISTRIBUTION_TYPE__TIME_UNIT = DISTRIBUTION_PARAMETER__TIME_UNIT;

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
	 * The meta object id for the '{@link bpsim.ResultType <em>Result Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.ResultType
	 * @see bpsim.impl.BpsimPackageImpl#getResultType()
	 * @generated
	 */
	int RESULT_TYPE = 42;

	/**
	 * The meta object id for the '{@link bpsim.TimeUnit <em>Time Unit</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.TimeUnit
	 * @see bpsim.impl.BpsimPackageImpl#getTimeUnit()
	 * @generated
	 */
	int TIME_UNIT = 43;

	/**
	 * The meta object id for the '<em>Result Type Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.ResultType
	 * @see bpsim.impl.BpsimPackageImpl#getResultTypeObject()
	 * @generated
	 */
	int RESULT_TYPE_OBJECT = 44;

	/**
	 * The meta object id for the '<em>Time Unit Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see bpsim.TimeUnit
	 * @see bpsim.impl.BpsimPackageImpl#getTimeUnitObject()
	 * @generated
	 */
	int TIME_UNIT_OBJECT = 45;


	/**
	 * Returns the meta object for class '{@link bpsim.BetaDistributionType <em>Beta Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Beta Distribution Type</em>'.
	 * @see bpsim.BetaDistributionType
	 * @generated
	 */
	EClass getBetaDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.BetaDistributionType#getScale <em>Scale</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scale</em>'.
	 * @see bpsim.BetaDistributionType#getScale()
	 * @see #getBetaDistributionType()
	 * @generated
	 */
	EAttribute getBetaDistributionType_Scale();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.BetaDistributionType#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape</em>'.
	 * @see bpsim.BetaDistributionType#getShape()
	 * @see #getBetaDistributionType()
	 * @generated
	 */
	EAttribute getBetaDistributionType_Shape();

	/**
	 * Returns the meta object for class '{@link bpsim.BinomialDistributionType <em>Binomial Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Binomial Distribution Type</em>'.
	 * @see bpsim.BinomialDistributionType
	 * @generated
	 */
	EClass getBinomialDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.BinomialDistributionType#getProbability <em>Probability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Probability</em>'.
	 * @see bpsim.BinomialDistributionType#getProbability()
	 * @see #getBinomialDistributionType()
	 * @generated
	 */
	EAttribute getBinomialDistributionType_Probability();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.BinomialDistributionType#getTrials <em>Trials</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Trials</em>'.
	 * @see bpsim.BinomialDistributionType#getTrials()
	 * @see #getBinomialDistributionType()
	 * @generated
	 */
	EAttribute getBinomialDistributionType_Trials();

	/**
	 * Returns the meta object for class '{@link bpsim.BooleanParameterType <em>Boolean Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Boolean Parameter Type</em>'.
	 * @see bpsim.BooleanParameterType
	 * @generated
	 */
	EClass getBooleanParameterType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.BooleanParameterType#isValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see bpsim.BooleanParameterType#isValue()
	 * @see #getBooleanParameterType()
	 * @generated
	 */
	EAttribute getBooleanParameterType_Value();

	/**
	 * Returns the meta object for class '{@link bpsim.BPSimDataType <em>BP Sim Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>BP Sim Data Type</em>'.
	 * @see bpsim.BPSimDataType
	 * @generated
	 */
	EClass getBPSimDataType();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.BPSimDataType#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see bpsim.BPSimDataType#getGroup()
	 * @see #getBPSimDataType()
	 * @generated
	 */
	EAttribute getBPSimDataType_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.BPSimDataType#getScenario <em>Scenario</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Scenario</em>'.
	 * @see bpsim.BPSimDataType#getScenario()
	 * @see #getBPSimDataType()
	 * @generated
	 */
	EReference getBPSimDataType_Scenario();

	/**
	 * Returns the meta object for class '{@link bpsim.Calendar <em>Calendar</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Calendar</em>'.
	 * @see bpsim.Calendar
	 * @generated
	 */
	EClass getCalendar();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Calendar#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see bpsim.Calendar#getValue()
	 * @see #getCalendar()
	 * @generated
	 */
	EAttribute getCalendar_Value();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Calendar#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see bpsim.Calendar#getId()
	 * @see #getCalendar()
	 * @generated
	 */
	EAttribute getCalendar_Id();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Calendar#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpsim.Calendar#getName()
	 * @see #getCalendar()
	 * @generated
	 */
	EAttribute getCalendar_Name();

	/**
	 * Returns the meta object for class '{@link bpsim.ConstantParameter <em>Constant Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Constant Parameter</em>'.
	 * @see bpsim.ConstantParameter
	 * @generated
	 */
	EClass getConstantParameter();

	/**
	 * Returns the meta object for class '{@link bpsim.ControlParameters <em>Control Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Control Parameters</em>'.
	 * @see bpsim.ControlParameters
	 * @generated
	 */
	EClass getControlParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ControlParameters#getProbability <em>Probability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Probability</em>'.
	 * @see bpsim.ControlParameters#getProbability()
	 * @see #getControlParameters()
	 * @generated
	 */
	EReference getControlParameters_Probability();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ControlParameters#getCondition <em>Condition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Condition</em>'.
	 * @see bpsim.ControlParameters#getCondition()
	 * @see #getControlParameters()
	 * @generated
	 */
	EReference getControlParameters_Condition();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ControlParameters#getInterTriggerTimer <em>Inter Trigger Timer</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Inter Trigger Timer</em>'.
	 * @see bpsim.ControlParameters#getInterTriggerTimer()
	 * @see #getControlParameters()
	 * @generated
	 */
	EReference getControlParameters_InterTriggerTimer();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ControlParameters#getTriggerCount <em>Trigger Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Trigger Count</em>'.
	 * @see bpsim.ControlParameters#getTriggerCount()
	 * @see #getControlParameters()
	 * @generated
	 */
	EReference getControlParameters_TriggerCount();

	/**
	 * Returns the meta object for class '{@link bpsim.CostParameters <em>Cost Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cost Parameters</em>'.
	 * @see bpsim.CostParameters
	 * @generated
	 */
	EClass getCostParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.CostParameters#getFixedCost <em>Fixed Cost</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Fixed Cost</em>'.
	 * @see bpsim.CostParameters#getFixedCost()
	 * @see #getCostParameters()
	 * @generated
	 */
	EReference getCostParameters_FixedCost();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.CostParameters#getUnitCost <em>Unit Cost</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Unit Cost</em>'.
	 * @see bpsim.CostParameters#getUnitCost()
	 * @see #getCostParameters()
	 * @generated
	 */
	EReference getCostParameters_UnitCost();

	/**
	 * Returns the meta object for class '{@link bpsim.DateTimeParameterType <em>Date Time Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Date Time Parameter Type</em>'.
	 * @see bpsim.DateTimeParameterType
	 * @generated
	 */
	EClass getDateTimeParameterType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.DateTimeParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see bpsim.DateTimeParameterType#getValue()
	 * @see #getDateTimeParameterType()
	 * @generated
	 */
	EAttribute getDateTimeParameterType_Value();

	/**
	 * Returns the meta object for class '{@link bpsim.DistributionParameter <em>Distribution Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Distribution Parameter</em>'.
	 * @see bpsim.DistributionParameter
	 * @generated
	 */
	EClass getDistributionParameter();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.DistributionParameter#getCurrencyUnit <em>Currency Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Currency Unit</em>'.
	 * @see bpsim.DistributionParameter#getCurrencyUnit()
	 * @see #getDistributionParameter()
	 * @generated
	 */
	EAttribute getDistributionParameter_CurrencyUnit();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.DistributionParameter#getTimeUnit <em>Time Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time Unit</em>'.
	 * @see bpsim.DistributionParameter#getTimeUnit()
	 * @see #getDistributionParameter()
	 * @generated
	 */
	EAttribute getDistributionParameter_TimeUnit();

	/**
	 * Returns the meta object for class '{@link bpsim.DocumentRoot <em>Document Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Document Root</em>'.
	 * @see bpsim.DocumentRoot
	 * @generated
	 */
	EClass getDocumentRoot();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.DocumentRoot#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see bpsim.DocumentRoot#getMixed()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EAttribute getDocumentRoot_Mixed();

	/**
	 * Returns the meta object for the map '{@link bpsim.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
	 * @see bpsim.DocumentRoot#getXMLNSPrefixMap()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XMLNSPrefixMap();

	/**
	 * Returns the meta object for the map '{@link bpsim.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>XSI Schema Location</em>'.
	 * @see bpsim.DocumentRoot#getXSISchemaLocation()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_XSISchemaLocation();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getBetaDistribution <em>Beta Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Beta Distribution</em>'.
	 * @see bpsim.DocumentRoot#getBetaDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BetaDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameter Value</em>'.
	 * @see bpsim.DocumentRoot#getParameterValue()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_ParameterValue();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getBinomialDistribution <em>Binomial Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Binomial Distribution</em>'.
	 * @see bpsim.DocumentRoot#getBinomialDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BinomialDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getBooleanParameter <em>Boolean Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Boolean Parameter</em>'.
	 * @see bpsim.DocumentRoot#getBooleanParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BooleanParameter();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getBPSimData <em>BP Sim Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>BP Sim Data</em>'.
	 * @see bpsim.DocumentRoot#getBPSimData()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_BPSimData();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getDateTimeParameter <em>Date Time Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Date Time Parameter</em>'.
	 * @see bpsim.DocumentRoot#getDateTimeParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_DateTimeParameter();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getDurationParameter <em>Duration Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Duration Parameter</em>'.
	 * @see bpsim.DocumentRoot#getDurationParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_DurationParameter();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getEnumParameter <em>Enum Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Enum Parameter</em>'.
	 * @see bpsim.DocumentRoot#getEnumParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_EnumParameter();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getErlangDistribution <em>Erlang Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Erlang Distribution</em>'.
	 * @see bpsim.DocumentRoot#getErlangDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_ErlangDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getExpressionParameter <em>Expression Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Expression Parameter</em>'.
	 * @see bpsim.DocumentRoot#getExpressionParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_ExpressionParameter();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getFloatingParameter <em>Floating Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Floating Parameter</em>'.
	 * @see bpsim.DocumentRoot#getFloatingParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_FloatingParameter();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getGammaDistribution <em>Gamma Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Gamma Distribution</em>'.
	 * @see bpsim.DocumentRoot#getGammaDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_GammaDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getLogNormalDistribution <em>Log Normal Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Log Normal Distribution</em>'.
	 * @see bpsim.DocumentRoot#getLogNormalDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_LogNormalDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getNegativeExponentialDistribution <em>Negative Exponential Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Negative Exponential Distribution</em>'.
	 * @see bpsim.DocumentRoot#getNegativeExponentialDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_NegativeExponentialDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getNormalDistribution <em>Normal Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Normal Distribution</em>'.
	 * @see bpsim.DocumentRoot#getNormalDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_NormalDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getNumericParameter <em>Numeric Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Numeric Parameter</em>'.
	 * @see bpsim.DocumentRoot#getNumericParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_NumericParameter();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getPoissonDistribution <em>Poisson Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Poisson Distribution</em>'.
	 * @see bpsim.DocumentRoot#getPoissonDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_PoissonDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getStringParameter <em>String Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>String Parameter</em>'.
	 * @see bpsim.DocumentRoot#getStringParameter()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_StringParameter();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getTriangularDistribution <em>Triangular Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Triangular Distribution</em>'.
	 * @see bpsim.DocumentRoot#getTriangularDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_TriangularDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getTruncatedNormalDistribution <em>Truncated Normal Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Truncated Normal Distribution</em>'.
	 * @see bpsim.DocumentRoot#getTruncatedNormalDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_TruncatedNormalDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getUniformDistribution <em>Uniform Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Uniform Distribution</em>'.
	 * @see bpsim.DocumentRoot#getUniformDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_UniformDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getUserDistribution <em>User Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>User Distribution</em>'.
	 * @see bpsim.DocumentRoot#getUserDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_UserDistribution();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getUserDistributionDataPoint <em>User Distribution Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>User Distribution Data Point</em>'.
	 * @see bpsim.DocumentRoot#getUserDistributionDataPoint()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_UserDistributionDataPoint();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.DocumentRoot#getWeibullDistribution <em>Weibull Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Weibull Distribution</em>'.
	 * @see bpsim.DocumentRoot#getWeibullDistribution()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_WeibullDistribution();

	/**
	 * Returns the meta object for class '{@link bpsim.DurationParameterType <em>Duration Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Duration Parameter Type</em>'.
	 * @see bpsim.DurationParameterType
	 * @generated
	 */
	EClass getDurationParameterType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.DurationParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see bpsim.DurationParameterType#getValue()
	 * @see #getDurationParameterType()
	 * @generated
	 */
	EAttribute getDurationParameterType_Value();

	/**
	 * Returns the meta object for class '{@link bpsim.ElementParameters <em>Element Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element Parameters</em>'.
	 * @see bpsim.ElementParameters
	 * @generated
	 */
	EClass getElementParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ElementParameters#getTimeParameters <em>Time Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Time Parameters</em>'.
	 * @see bpsim.ElementParameters#getTimeParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_TimeParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ElementParameters#getControlParameters <em>Control Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Control Parameters</em>'.
	 * @see bpsim.ElementParameters#getControlParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_ControlParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ElementParameters#getResourceParameters <em>Resource Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Resource Parameters</em>'.
	 * @see bpsim.ElementParameters#getResourceParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_ResourceParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ElementParameters#getPriorityParameters <em>Priority Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Priority Parameters</em>'.
	 * @see bpsim.ElementParameters#getPriorityParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_PriorityParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ElementParameters#getCostParameters <em>Cost Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Cost Parameters</em>'.
	 * @see bpsim.ElementParameters#getCostParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_CostParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ElementParameters#getPropertyParameters <em>Property Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Property Parameters</em>'.
	 * @see bpsim.ElementParameters#getPropertyParameters()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_PropertyParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.ElementParameters#getVendorExtension <em>Vendor Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Vendor Extension</em>'.
	 * @see bpsim.ElementParameters#getVendorExtension()
	 * @see #getElementParameters()
	 * @generated
	 */
	EReference getElementParameters_VendorExtension();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ElementParameters#getElementRef <em>Element Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Element Ref</em>'.
	 * @see bpsim.ElementParameters#getElementRef()
	 * @see #getElementParameters()
	 * @generated
	 */
	EAttribute getElementParameters_ElementRef();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ElementParameters#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see bpsim.ElementParameters#getId()
	 * @see #getElementParameters()
	 * @generated
	 */
	EAttribute getElementParameters_Id();

	/**
	 * Returns the meta object for class '{@link bpsim.ElementParametersType <em>Element Parameters Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Element Parameters Type</em>'.
	 * @see bpsim.ElementParametersType
	 * @generated
	 */
	EClass getElementParametersType();

	/**
	 * Returns the meta object for class '{@link bpsim.EnumParameterType <em>Enum Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Enum Parameter Type</em>'.
	 * @see bpsim.EnumParameterType
	 * @generated
	 */
	EClass getEnumParameterType();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.EnumParameterType#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see bpsim.EnumParameterType#getGroup()
	 * @see #getEnumParameterType()
	 * @generated
	 */
	EAttribute getEnumParameterType_Group();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.EnumParameterType#getParameterValueGroup <em>Parameter Value Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Parameter Value Group</em>'.
	 * @see bpsim.EnumParameterType#getParameterValueGroup()
	 * @see #getEnumParameterType()
	 * @generated
	 */
	EAttribute getEnumParameterType_ParameterValueGroup();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.EnumParameterType#getParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Value</em>'.
	 * @see bpsim.EnumParameterType#getParameterValue()
	 * @see #getEnumParameterType()
	 * @generated
	 */
	EReference getEnumParameterType_ParameterValue();

	/**
	 * Returns the meta object for class '{@link bpsim.ErlangDistributionType <em>Erlang Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Erlang Distribution Type</em>'.
	 * @see bpsim.ErlangDistributionType
	 * @generated
	 */
	EClass getErlangDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ErlangDistributionType#getK <em>K</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>K</em>'.
	 * @see bpsim.ErlangDistributionType#getK()
	 * @see #getErlangDistributionType()
	 * @generated
	 */
	EAttribute getErlangDistributionType_K();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ErlangDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see bpsim.ErlangDistributionType#getMean()
	 * @see #getErlangDistributionType()
	 * @generated
	 */
	EAttribute getErlangDistributionType_Mean();

	/**
	 * Returns the meta object for class '{@link bpsim.ExpressionParameterType <em>Expression Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Expression Parameter Type</em>'.
	 * @see bpsim.ExpressionParameterType
	 * @generated
	 */
	EClass getExpressionParameterType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ExpressionParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see bpsim.ExpressionParameterType#getValue()
	 * @see #getExpressionParameterType()
	 * @generated
	 */
	EAttribute getExpressionParameterType_Value();

	/**
	 * Returns the meta object for class '{@link bpsim.FloatingParameterType <em>Floating Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Floating Parameter Type</em>'.
	 * @see bpsim.FloatingParameterType
	 * @generated
	 */
	EClass getFloatingParameterType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.FloatingParameterType#getCurrencyUnit <em>Currency Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Currency Unit</em>'.
	 * @see bpsim.FloatingParameterType#getCurrencyUnit()
	 * @see #getFloatingParameterType()
	 * @generated
	 */
	EAttribute getFloatingParameterType_CurrencyUnit();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.FloatingParameterType#getTimeUnit <em>Time Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time Unit</em>'.
	 * @see bpsim.FloatingParameterType#getTimeUnit()
	 * @see #getFloatingParameterType()
	 * @generated
	 */
	EAttribute getFloatingParameterType_TimeUnit();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.FloatingParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see bpsim.FloatingParameterType#getValue()
	 * @see #getFloatingParameterType()
	 * @generated
	 */
	EAttribute getFloatingParameterType_Value();

	/**
	 * Returns the meta object for class '{@link bpsim.GammaDistributionType <em>Gamma Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Gamma Distribution Type</em>'.
	 * @see bpsim.GammaDistributionType
	 * @generated
	 */
	EClass getGammaDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.GammaDistributionType#getScale <em>Scale</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scale</em>'.
	 * @see bpsim.GammaDistributionType#getScale()
	 * @see #getGammaDistributionType()
	 * @generated
	 */
	EAttribute getGammaDistributionType_Scale();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.GammaDistributionType#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape</em>'.
	 * @see bpsim.GammaDistributionType#getShape()
	 * @see #getGammaDistributionType()
	 * @generated
	 */
	EAttribute getGammaDistributionType_Shape();

	/**
	 * Returns the meta object for class '{@link bpsim.LogNormalDistributionType <em>Log Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Log Normal Distribution Type</em>'.
	 * @see bpsim.LogNormalDistributionType
	 * @generated
	 */
	EClass getLogNormalDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.LogNormalDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see bpsim.LogNormalDistributionType#getMean()
	 * @see #getLogNormalDistributionType()
	 * @generated
	 */
	EAttribute getLogNormalDistributionType_Mean();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.LogNormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Standard Deviation</em>'.
	 * @see bpsim.LogNormalDistributionType#getStandardDeviation()
	 * @see #getLogNormalDistributionType()
	 * @generated
	 */
	EAttribute getLogNormalDistributionType_StandardDeviation();

	/**
	 * Returns the meta object for class '{@link bpsim.NegativeExponentialDistributionType <em>Negative Exponential Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Negative Exponential Distribution Type</em>'.
	 * @see bpsim.NegativeExponentialDistributionType
	 * @generated
	 */
	EClass getNegativeExponentialDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.NegativeExponentialDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see bpsim.NegativeExponentialDistributionType#getMean()
	 * @see #getNegativeExponentialDistributionType()
	 * @generated
	 */
	EAttribute getNegativeExponentialDistributionType_Mean();

	/**
	 * Returns the meta object for class '{@link bpsim.NormalDistributionType <em>Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Normal Distribution Type</em>'.
	 * @see bpsim.NormalDistributionType
	 * @generated
	 */
	EClass getNormalDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.NormalDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see bpsim.NormalDistributionType#getMean()
	 * @see #getNormalDistributionType()
	 * @generated
	 */
	EAttribute getNormalDistributionType_Mean();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.NormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Standard Deviation</em>'.
	 * @see bpsim.NormalDistributionType#getStandardDeviation()
	 * @see #getNormalDistributionType()
	 * @generated
	 */
	EAttribute getNormalDistributionType_StandardDeviation();

	/**
	 * Returns the meta object for class '{@link bpsim.NumericParameterType <em>Numeric Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Numeric Parameter Type</em>'.
	 * @see bpsim.NumericParameterType
	 * @generated
	 */
	EClass getNumericParameterType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.NumericParameterType#getCurrencyUnit <em>Currency Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Currency Unit</em>'.
	 * @see bpsim.NumericParameterType#getCurrencyUnit()
	 * @see #getNumericParameterType()
	 * @generated
	 */
	EAttribute getNumericParameterType_CurrencyUnit();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.NumericParameterType#getTimeUnit <em>Time Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time Unit</em>'.
	 * @see bpsim.NumericParameterType#getTimeUnit()
	 * @see #getNumericParameterType()
	 * @generated
	 */
	EAttribute getNumericParameterType_TimeUnit();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.NumericParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see bpsim.NumericParameterType#getValue()
	 * @see #getNumericParameterType()
	 * @generated
	 */
	EAttribute getNumericParameterType_Value();

	/**
	 * Returns the meta object for class '{@link bpsim.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see bpsim.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.Parameter#getResultRequest <em>Result Request</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Result Request</em>'.
	 * @see bpsim.Parameter#getResultRequest()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_ResultRequest();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.Parameter#getParameterValueGroup <em>Parameter Value Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Parameter Value Group</em>'.
	 * @see bpsim.Parameter#getParameterValueGroup()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_ParameterValueGroup();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.Parameter#getParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameter Value</em>'.
	 * @see bpsim.Parameter#getParameterValue()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_ParameterValue();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Parameter#isKpi <em>Kpi</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kpi</em>'.
	 * @see bpsim.Parameter#isKpi()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Kpi();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Parameter#isSla <em>Sla</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sla</em>'.
	 * @see bpsim.Parameter#isSla()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Sla();

	/**
	 * Returns the meta object for class '{@link bpsim.ParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter Value</em>'.
	 * @see bpsim.ParameterValue
	 * @generated
	 */
	EClass getParameterValue();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ParameterValue#getInstance <em>Instance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Instance</em>'.
	 * @see bpsim.ParameterValue#getInstance()
	 * @see #getParameterValue()
	 * @generated
	 */
	EAttribute getParameterValue_Instance();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ParameterValue#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result</em>'.
	 * @see bpsim.ParameterValue#getResult()
	 * @see #getParameterValue()
	 * @generated
	 */
	EAttribute getParameterValue_Result();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ParameterValue#getValidFor <em>Valid For</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Valid For</em>'.
	 * @see bpsim.ParameterValue#getValidFor()
	 * @see #getParameterValue()
	 * @generated
	 */
	EAttribute getParameterValue_ValidFor();

	/**
	 * Returns the meta object for class '{@link bpsim.PoissonDistributionType <em>Poisson Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Poisson Distribution Type</em>'.
	 * @see bpsim.PoissonDistributionType
	 * @generated
	 */
	EClass getPoissonDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.PoissonDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see bpsim.PoissonDistributionType#getMean()
	 * @see #getPoissonDistributionType()
	 * @generated
	 */
	EAttribute getPoissonDistributionType_Mean();

	/**
	 * Returns the meta object for class '{@link bpsim.PriorityParameters <em>Priority Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Priority Parameters</em>'.
	 * @see bpsim.PriorityParameters
	 * @generated
	 */
	EClass getPriorityParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.PriorityParameters#getInterruptible <em>Interruptible</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Interruptible</em>'.
	 * @see bpsim.PriorityParameters#getInterruptible()
	 * @see #getPriorityParameters()
	 * @generated
	 */
	EReference getPriorityParameters_Interruptible();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.PriorityParameters#getPriority <em>Priority</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Priority</em>'.
	 * @see bpsim.PriorityParameters#getPriority()
	 * @see #getPriorityParameters()
	 * @generated
	 */
	EReference getPriorityParameters_Priority();

	/**
	 * Returns the meta object for class '{@link bpsim.PropertyParameters <em>Property Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property Parameters</em>'.
	 * @see bpsim.PropertyParameters
	 * @generated
	 */
	EClass getPropertyParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.PropertyParameters#getProperty <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Property</em>'.
	 * @see bpsim.PropertyParameters#getProperty()
	 * @see #getPropertyParameters()
	 * @generated
	 */
	EReference getPropertyParameters_Property();

	/**
	 * Returns the meta object for class '{@link bpsim.PropertyType <em>Property Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property Type</em>'.
	 * @see bpsim.PropertyType
	 * @generated
	 */
	EClass getPropertyType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.PropertyType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpsim.PropertyType#getName()
	 * @see #getPropertyType()
	 * @generated
	 */
	EAttribute getPropertyType_Name();

	/**
	 * Returns the meta object for class '{@link bpsim.ResourceParameters <em>Resource Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Resource Parameters</em>'.
	 * @see bpsim.ResourceParameters
	 * @generated
	 */
	EClass getResourceParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ResourceParameters#getSelection <em>Selection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Selection</em>'.
	 * @see bpsim.ResourceParameters#getSelection()
	 * @see #getResourceParameters()
	 * @generated
	 */
	EReference getResourceParameters_Selection();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ResourceParameters#getAvailability <em>Availability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Availability</em>'.
	 * @see bpsim.ResourceParameters#getAvailability()
	 * @see #getResourceParameters()
	 * @generated
	 */
	EReference getResourceParameters_Availability();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ResourceParameters#getQuantity <em>Quantity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Quantity</em>'.
	 * @see bpsim.ResourceParameters#getQuantity()
	 * @see #getResourceParameters()
	 * @generated
	 */
	EReference getResourceParameters_Quantity();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.ResourceParameters#getRole <em>Role</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Role</em>'.
	 * @see bpsim.ResourceParameters#getRole()
	 * @see #getResourceParameters()
	 * @generated
	 */
	EReference getResourceParameters_Role();

	/**
	 * Returns the meta object for class '{@link bpsim.Scenario <em>Scenario</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Scenario</em>'.
	 * @see bpsim.Scenario
	 * @generated
	 */
	EClass getScenario();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.Scenario#getScenarioParameters <em>Scenario Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Scenario Parameters</em>'.
	 * @see bpsim.Scenario#getScenarioParameters()
	 * @see #getScenario()
	 * @generated
	 */
	EReference getScenario_ScenarioParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.Scenario#getElementParameters <em>Element Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Element Parameters</em>'.
	 * @see bpsim.Scenario#getElementParameters()
	 * @see #getScenario()
	 * @generated
	 */
	EReference getScenario_ElementParameters();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.Scenario#getCalendar <em>Calendar</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Calendar</em>'.
	 * @see bpsim.Scenario#getCalendar()
	 * @see #getScenario()
	 * @generated
	 */
	EReference getScenario_Calendar();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.Scenario#getVendorExtension <em>Vendor Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Vendor Extension</em>'.
	 * @see bpsim.Scenario#getVendorExtension()
	 * @see #getScenario()
	 * @generated
	 */
	EReference getScenario_VendorExtension();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see bpsim.Scenario#getAuthor()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Author();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getCreated <em>Created</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Created</em>'.
	 * @see bpsim.Scenario#getCreated()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Created();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see bpsim.Scenario#getDescription()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Description();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see bpsim.Scenario#getId()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Id();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getInherits <em>Inherits</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Inherits</em>'.
	 * @see bpsim.Scenario#getInherits()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Inherits();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getModified <em>Modified</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Modified</em>'.
	 * @see bpsim.Scenario#getModified()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Modified();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpsim.Scenario#getName()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Name();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getResult <em>Result</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Result</em>'.
	 * @see bpsim.Scenario#getResult()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Result();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getVendor <em>Vendor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Vendor</em>'.
	 * @see bpsim.Scenario#getVendor()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Vendor();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.Scenario#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see bpsim.Scenario#getVersion()
	 * @see #getScenario()
	 * @generated
	 */
	EAttribute getScenario_Version();

	/**
	 * Returns the meta object for class '{@link bpsim.ScenarioParameters <em>Scenario Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Scenario Parameters</em>'.
	 * @see bpsim.ScenarioParameters
	 * @generated
	 */
	EClass getScenarioParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ScenarioParameters#getStart <em>Start</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Start</em>'.
	 * @see bpsim.ScenarioParameters#getStart()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EReference getScenarioParameters_Start();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ScenarioParameters#getDuration <em>Duration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Duration</em>'.
	 * @see bpsim.ScenarioParameters#getDuration()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EReference getScenarioParameters_Duration();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.ScenarioParameters#getPropertyParameters <em>Property Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Property Parameters</em>'.
	 * @see bpsim.ScenarioParameters#getPropertyParameters()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EReference getScenarioParameters_PropertyParameters();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ScenarioParameters#getBaseCurrencyUnit <em>Base Currency Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Base Currency Unit</em>'.
	 * @see bpsim.ScenarioParameters#getBaseCurrencyUnit()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EAttribute getScenarioParameters_BaseCurrencyUnit();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ScenarioParameters#getBaseTimeUnit <em>Base Time Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Base Time Unit</em>'.
	 * @see bpsim.ScenarioParameters#getBaseTimeUnit()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EAttribute getScenarioParameters_BaseTimeUnit();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ScenarioParameters#getReplication <em>Replication</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Replication</em>'.
	 * @see bpsim.ScenarioParameters#getReplication()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EAttribute getScenarioParameters_Replication();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.ScenarioParameters#getSeed <em>Seed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Seed</em>'.
	 * @see bpsim.ScenarioParameters#getSeed()
	 * @see #getScenarioParameters()
	 * @generated
	 */
	EAttribute getScenarioParameters_Seed();

	/**
	 * Returns the meta object for class '{@link bpsim.ScenarioParametersType <em>Scenario Parameters Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Scenario Parameters Type</em>'.
	 * @see bpsim.ScenarioParametersType
	 * @generated
	 */
	EClass getScenarioParametersType();

	/**
	 * Returns the meta object for class '{@link bpsim.StringParameterType <em>String Parameter Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String Parameter Type</em>'.
	 * @see bpsim.StringParameterType
	 * @generated
	 */
	EClass getStringParameterType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.StringParameterType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see bpsim.StringParameterType#getValue()
	 * @see #getStringParameterType()
	 * @generated
	 */
	EAttribute getStringParameterType_Value();

	/**
	 * Returns the meta object for class '{@link bpsim.TimeParameters <em>Time Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Time Parameters</em>'.
	 * @see bpsim.TimeParameters
	 * @generated
	 */
	EClass getTimeParameters();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.TimeParameters#getTransferTime <em>Transfer Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Transfer Time</em>'.
	 * @see bpsim.TimeParameters#getTransferTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_TransferTime();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.TimeParameters#getQueueTime <em>Queue Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Queue Time</em>'.
	 * @see bpsim.TimeParameters#getQueueTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_QueueTime();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.TimeParameters#getWaitTime <em>Wait Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Wait Time</em>'.
	 * @see bpsim.TimeParameters#getWaitTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_WaitTime();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.TimeParameters#getSetUpTime <em>Set Up Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Set Up Time</em>'.
	 * @see bpsim.TimeParameters#getSetUpTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_SetUpTime();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.TimeParameters#getProcessingTime <em>Processing Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Processing Time</em>'.
	 * @see bpsim.TimeParameters#getProcessingTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_ProcessingTime();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.TimeParameters#getValidationTime <em>Validation Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Validation Time</em>'.
	 * @see bpsim.TimeParameters#getValidationTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_ValidationTime();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.TimeParameters#getReworkTime <em>Rework Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Rework Time</em>'.
	 * @see bpsim.TimeParameters#getReworkTime()
	 * @see #getTimeParameters()
	 * @generated
	 */
	EReference getTimeParameters_ReworkTime();

	/**
	 * Returns the meta object for class '{@link bpsim.TriangularDistributionType <em>Triangular Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Triangular Distribution Type</em>'.
	 * @see bpsim.TriangularDistributionType
	 * @generated
	 */
	EClass getTriangularDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.TriangularDistributionType#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see bpsim.TriangularDistributionType#getMax()
	 * @see #getTriangularDistributionType()
	 * @generated
	 */
	EAttribute getTriangularDistributionType_Max();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.TriangularDistributionType#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see bpsim.TriangularDistributionType#getMin()
	 * @see #getTriangularDistributionType()
	 * @generated
	 */
	EAttribute getTriangularDistributionType_Min();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.TriangularDistributionType#getMode <em>Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mode</em>'.
	 * @see bpsim.TriangularDistributionType#getMode()
	 * @see #getTriangularDistributionType()
	 * @generated
	 */
	EAttribute getTriangularDistributionType_Mode();

	/**
	 * Returns the meta object for class '{@link bpsim.TruncatedNormalDistributionType <em>Truncated Normal Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Truncated Normal Distribution Type</em>'.
	 * @see bpsim.TruncatedNormalDistributionType
	 * @generated
	 */
	EClass getTruncatedNormalDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.TruncatedNormalDistributionType#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see bpsim.TruncatedNormalDistributionType#getMax()
	 * @see #getTruncatedNormalDistributionType()
	 * @generated
	 */
	EAttribute getTruncatedNormalDistributionType_Max();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.TruncatedNormalDistributionType#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see bpsim.TruncatedNormalDistributionType#getMean()
	 * @see #getTruncatedNormalDistributionType()
	 * @generated
	 */
	EAttribute getTruncatedNormalDistributionType_Mean();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.TruncatedNormalDistributionType#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see bpsim.TruncatedNormalDistributionType#getMin()
	 * @see #getTruncatedNormalDistributionType()
	 * @generated
	 */
	EAttribute getTruncatedNormalDistributionType_Min();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.TruncatedNormalDistributionType#getStandardDeviation <em>Standard Deviation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Standard Deviation</em>'.
	 * @see bpsim.TruncatedNormalDistributionType#getStandardDeviation()
	 * @see #getTruncatedNormalDistributionType()
	 * @generated
	 */
	EAttribute getTruncatedNormalDistributionType_StandardDeviation();

	/**
	 * Returns the meta object for class '{@link bpsim.UniformDistributionType <em>Uniform Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Uniform Distribution Type</em>'.
	 * @see bpsim.UniformDistributionType
	 * @generated
	 */
	EClass getUniformDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.UniformDistributionType#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see bpsim.UniformDistributionType#getMax()
	 * @see #getUniformDistributionType()
	 * @generated
	 */
	EAttribute getUniformDistributionType_Max();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.UniformDistributionType#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see bpsim.UniformDistributionType#getMin()
	 * @see #getUniformDistributionType()
	 * @generated
	 */
	EAttribute getUniformDistributionType_Min();

	/**
	 * Returns the meta object for class '{@link bpsim.UserDistributionDataPointType <em>User Distribution Data Point Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>User Distribution Data Point Type</em>'.
	 * @see bpsim.UserDistributionDataPointType
	 * @generated
	 */
	EClass getUserDistributionDataPointType();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.UserDistributionDataPointType#getParameterValueGroup <em>Parameter Value Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Parameter Value Group</em>'.
	 * @see bpsim.UserDistributionDataPointType#getParameterValueGroup()
	 * @see #getUserDistributionDataPointType()
	 * @generated
	 */
	EAttribute getUserDistributionDataPointType_ParameterValueGroup();

	/**
	 * Returns the meta object for the containment reference '{@link bpsim.UserDistributionDataPointType#getParameterValue <em>Parameter Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Parameter Value</em>'.
	 * @see bpsim.UserDistributionDataPointType#getParameterValue()
	 * @see #getUserDistributionDataPointType()
	 * @generated
	 */
	EReference getUserDistributionDataPointType_ParameterValue();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.UserDistributionDataPointType#getProbability <em>Probability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Probability</em>'.
	 * @see bpsim.UserDistributionDataPointType#getProbability()
	 * @see #getUserDistributionDataPointType()
	 * @generated
	 */
	EAttribute getUserDistributionDataPointType_Probability();

	/**
	 * Returns the meta object for class '{@link bpsim.UserDistributionType <em>User Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>User Distribution Type</em>'.
	 * @see bpsim.UserDistributionType
	 * @generated
	 */
	EClass getUserDistributionType();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.UserDistributionType#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see bpsim.UserDistributionType#getGroup()
	 * @see #getUserDistributionType()
	 * @generated
	 */
	EAttribute getUserDistributionType_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link bpsim.UserDistributionType#getUserDistributionDataPoint <em>User Distribution Data Point</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>User Distribution Data Point</em>'.
	 * @see bpsim.UserDistributionType#getUserDistributionDataPoint()
	 * @see #getUserDistributionType()
	 * @generated
	 */
	EReference getUserDistributionType_UserDistributionDataPoint();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.UserDistributionType#isDiscrete <em>Discrete</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Discrete</em>'.
	 * @see bpsim.UserDistributionType#isDiscrete()
	 * @see #getUserDistributionType()
	 * @generated
	 */
	EAttribute getUserDistributionType_Discrete();

	/**
	 * Returns the meta object for class '{@link bpsim.VendorExtension <em>Vendor Extension</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Vendor Extension</em>'.
	 * @see bpsim.VendorExtension
	 * @generated
	 */
	EClass getVendorExtension();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.VendorExtension#getAny <em>Any</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Any</em>'.
	 * @see bpsim.VendorExtension#getAny()
	 * @see #getVendorExtension()
	 * @generated
	 */
	EAttribute getVendorExtension_Any();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.VendorExtension#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see bpsim.VendorExtension#getName()
	 * @see #getVendorExtension()
	 * @generated
	 */
	EAttribute getVendorExtension_Name();

	/**
	 * Returns the meta object for the attribute list '{@link bpsim.VendorExtension#getAnyAttribute <em>Any Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Any Attribute</em>'.
	 * @see bpsim.VendorExtension#getAnyAttribute()
	 * @see #getVendorExtension()
	 * @generated
	 */
	EAttribute getVendorExtension_AnyAttribute();

	/**
	 * Returns the meta object for class '{@link bpsim.WeibullDistributionType <em>Weibull Distribution Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Weibull Distribution Type</em>'.
	 * @see bpsim.WeibullDistributionType
	 * @generated
	 */
	EClass getWeibullDistributionType();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.WeibullDistributionType#getScale <em>Scale</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scale</em>'.
	 * @see bpsim.WeibullDistributionType#getScale()
	 * @see #getWeibullDistributionType()
	 * @generated
	 */
	EAttribute getWeibullDistributionType_Scale();

	/**
	 * Returns the meta object for the attribute '{@link bpsim.WeibullDistributionType#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape</em>'.
	 * @see bpsim.WeibullDistributionType#getShape()
	 * @see #getWeibullDistributionType()
	 * @generated
	 */
	EAttribute getWeibullDistributionType_Shape();

	/**
	 * Returns the meta object for enum '{@link bpsim.ResultType <em>Result Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Result Type</em>'.
	 * @see bpsim.ResultType
	 * @generated
	 */
	EEnum getResultType();

	/**
	 * Returns the meta object for enum '{@link bpsim.TimeUnit <em>Time Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Time Unit</em>'.
	 * @see bpsim.TimeUnit
	 * @generated
	 */
	EEnum getTimeUnit();

	/**
	 * Returns the meta object for data type '{@link bpsim.ResultType <em>Result Type Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Result Type Object</em>'.
	 * @see bpsim.ResultType
	 * @model instanceClass="bpsim.ResultType"
	 *        extendedMetaData="name='ResultType:Object' baseType='ResultType'"
	 * @generated
	 */
	EDataType getResultTypeObject();

	/**
	 * Returns the meta object for data type '{@link bpsim.TimeUnit <em>Time Unit Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Time Unit Object</em>'.
	 * @see bpsim.TimeUnit
	 * @model instanceClass="bpsim.TimeUnit"
	 *        extendedMetaData="name='TimeUnit:Object' baseType='TimeUnit'"
	 * @generated
	 */
	EDataType getTimeUnitObject();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	BpsimFactory getBpsimFactory();

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
		 * The meta object literal for the '{@link bpsim.impl.BetaDistributionTypeImpl <em>Beta Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.BetaDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getBetaDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.BinomialDistributionTypeImpl <em>Binomial Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.BinomialDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getBinomialDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.BooleanParameterTypeImpl <em>Boolean Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.BooleanParameterTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getBooleanParameterType()
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
		 * The meta object literal for the '{@link bpsim.impl.BPSimDataTypeImpl <em>BP Sim Data Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.BPSimDataTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getBPSimDataType()
		 * @generated
		 */
		EClass BP_SIM_DATA_TYPE = eINSTANCE.getBPSimDataType();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BP_SIM_DATA_TYPE__GROUP = eINSTANCE.getBPSimDataType_Group();

		/**
		 * The meta object literal for the '<em><b>Scenario</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BP_SIM_DATA_TYPE__SCENARIO = eINSTANCE.getBPSimDataType_Scenario();

		/**
		 * The meta object literal for the '{@link bpsim.impl.CalendarImpl <em>Calendar</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.CalendarImpl
		 * @see bpsim.impl.BpsimPackageImpl#getCalendar()
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
		 * The meta object literal for the '{@link bpsim.impl.ConstantParameterImpl <em>Constant Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ConstantParameterImpl
		 * @see bpsim.impl.BpsimPackageImpl#getConstantParameter()
		 * @generated
		 */
		EClass CONSTANT_PARAMETER = eINSTANCE.getConstantParameter();

		/**
		 * The meta object literal for the '{@link bpsim.impl.ControlParametersImpl <em>Control Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ControlParametersImpl
		 * @see bpsim.impl.BpsimPackageImpl#getControlParameters()
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
		 * The meta object literal for the '<em><b>Condition</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_PARAMETERS__CONDITION = eINSTANCE.getControlParameters_Condition();

		/**
		 * The meta object literal for the '<em><b>Inter Trigger Timer</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_PARAMETERS__INTER_TRIGGER_TIMER = eINSTANCE.getControlParameters_InterTriggerTimer();

		/**
		 * The meta object literal for the '<em><b>Trigger Count</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONTROL_PARAMETERS__TRIGGER_COUNT = eINSTANCE.getControlParameters_TriggerCount();

		/**
		 * The meta object literal for the '{@link bpsim.impl.CostParametersImpl <em>Cost Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.CostParametersImpl
		 * @see bpsim.impl.BpsimPackageImpl#getCostParameters()
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
		 * The meta object literal for the '{@link bpsim.impl.DateTimeParameterTypeImpl <em>Date Time Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.DateTimeParameterTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getDateTimeParameterType()
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
		 * The meta object literal for the '{@link bpsim.impl.DistributionParameterImpl <em>Distribution Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.DistributionParameterImpl
		 * @see bpsim.impl.BpsimPackageImpl#getDistributionParameter()
		 * @generated
		 */
		EClass DISTRIBUTION_PARAMETER = eINSTANCE.getDistributionParameter();

		/**
		 * The meta object literal for the '<em><b>Currency Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DISTRIBUTION_PARAMETER__CURRENCY_UNIT = eINSTANCE.getDistributionParameter_CurrencyUnit();

		/**
		 * The meta object literal for the '<em><b>Time Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DISTRIBUTION_PARAMETER__TIME_UNIT = eINSTANCE.getDistributionParameter_TimeUnit();

		/**
		 * The meta object literal for the '{@link bpsim.impl.DocumentRootImpl <em>Document Root</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.DocumentRootImpl
		 * @see bpsim.impl.BpsimPackageImpl#getDocumentRoot()
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
		 * The meta object literal for the '<em><b>BP Sim Data</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__BP_SIM_DATA = eINSTANCE.getDocumentRoot_BPSimData();

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
		 * The meta object literal for the '<em><b>Log Normal Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION = eINSTANCE.getDocumentRoot_LogNormalDistribution();

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
		 * The meta object literal for the '<em><b>Poisson Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__POISSON_DISTRIBUTION = eINSTANCE.getDocumentRoot_PoissonDistribution();

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
		 * The meta object literal for the '{@link bpsim.impl.DurationParameterTypeImpl <em>Duration Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.DurationParameterTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getDurationParameterType()
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
		 * The meta object literal for the '{@link bpsim.impl.ElementParametersImpl <em>Element Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ElementParametersImpl
		 * @see bpsim.impl.BpsimPackageImpl#getElementParameters()
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
		 * The meta object literal for the '<em><b>Property Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_PARAMETERS__PROPERTY_PARAMETERS = eINSTANCE.getElementParameters_PropertyParameters();

		/**
		 * The meta object literal for the '<em><b>Vendor Extension</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ELEMENT_PARAMETERS__VENDOR_EXTENSION = eINSTANCE.getElementParameters_VendorExtension();

		/**
		 * The meta object literal for the '<em><b>Element Ref</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_PARAMETERS__ELEMENT_REF = eINSTANCE.getElementParameters_ElementRef();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ELEMENT_PARAMETERS__ID = eINSTANCE.getElementParameters_Id();

		/**
		 * The meta object literal for the '{@link bpsim.impl.ElementParametersTypeImpl <em>Element Parameters Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ElementParametersTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getElementParametersType()
		 * @generated
		 */
		EClass ELEMENT_PARAMETERS_TYPE = eINSTANCE.getElementParametersType();

		/**
		 * The meta object literal for the '{@link bpsim.impl.EnumParameterTypeImpl <em>Enum Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.EnumParameterTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getEnumParameterType()
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
		 * The meta object literal for the '{@link bpsim.impl.ErlangDistributionTypeImpl <em>Erlang Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ErlangDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getErlangDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.ExpressionParameterTypeImpl <em>Expression Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ExpressionParameterTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getExpressionParameterType()
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
		 * The meta object literal for the '{@link bpsim.impl.FloatingParameterTypeImpl <em>Floating Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.FloatingParameterTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getFloatingParameterType()
		 * @generated
		 */
		EClass FLOATING_PARAMETER_TYPE = eINSTANCE.getFloatingParameterType();

		/**
		 * The meta object literal for the '<em><b>Currency Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FLOATING_PARAMETER_TYPE__CURRENCY_UNIT = eINSTANCE.getFloatingParameterType_CurrencyUnit();

		/**
		 * The meta object literal for the '<em><b>Time Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FLOATING_PARAMETER_TYPE__TIME_UNIT = eINSTANCE.getFloatingParameterType_TimeUnit();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FLOATING_PARAMETER_TYPE__VALUE = eINSTANCE.getFloatingParameterType_Value();

		/**
		 * The meta object literal for the '{@link bpsim.impl.GammaDistributionTypeImpl <em>Gamma Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.GammaDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getGammaDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.LogNormalDistributionTypeImpl <em>Log Normal Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.LogNormalDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getLogNormalDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.NegativeExponentialDistributionTypeImpl <em>Negative Exponential Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.NegativeExponentialDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getNegativeExponentialDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.NormalDistributionTypeImpl <em>Normal Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.NormalDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getNormalDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.NumericParameterTypeImpl <em>Numeric Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.NumericParameterTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getNumericParameterType()
		 * @generated
		 */
		EClass NUMERIC_PARAMETER_TYPE = eINSTANCE.getNumericParameterType();

		/**
		 * The meta object literal for the '<em><b>Currency Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NUMERIC_PARAMETER_TYPE__CURRENCY_UNIT = eINSTANCE.getNumericParameterType_CurrencyUnit();

		/**
		 * The meta object literal for the '<em><b>Time Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NUMERIC_PARAMETER_TYPE__TIME_UNIT = eINSTANCE.getNumericParameterType_TimeUnit();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NUMERIC_PARAMETER_TYPE__VALUE = eINSTANCE.getNumericParameterType_Value();

		/**
		 * The meta object literal for the '{@link bpsim.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ParameterImpl
		 * @see bpsim.impl.BpsimPackageImpl#getParameter()
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
		 * The meta object literal for the '{@link bpsim.impl.ParameterValueImpl <em>Parameter Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ParameterValueImpl
		 * @see bpsim.impl.BpsimPackageImpl#getParameterValue()
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
		 * The meta object literal for the '{@link bpsim.impl.PoissonDistributionTypeImpl <em>Poisson Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.PoissonDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getPoissonDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.PriorityParametersImpl <em>Priority Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.PriorityParametersImpl
		 * @see bpsim.impl.BpsimPackageImpl#getPriorityParameters()
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
		 * The meta object literal for the '{@link bpsim.impl.PropertyParametersImpl <em>Property Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.PropertyParametersImpl
		 * @see bpsim.impl.BpsimPackageImpl#getPropertyParameters()
		 * @generated
		 */
		EClass PROPERTY_PARAMETERS = eINSTANCE.getPropertyParameters();

		/**
		 * The meta object literal for the '<em><b>Property</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROPERTY_PARAMETERS__PROPERTY = eINSTANCE.getPropertyParameters_Property();

		/**
		 * The meta object literal for the '{@link bpsim.impl.PropertyTypeImpl <em>Property Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.PropertyTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getPropertyType()
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
		 * The meta object literal for the '{@link bpsim.impl.ResourceParametersImpl <em>Resource Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ResourceParametersImpl
		 * @see bpsim.impl.BpsimPackageImpl#getResourceParameters()
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
		 * The meta object literal for the '<em><b>Role</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_PARAMETERS__ROLE = eINSTANCE.getResourceParameters_Role();

		/**
		 * The meta object literal for the '{@link bpsim.impl.ScenarioImpl <em>Scenario</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ScenarioImpl
		 * @see bpsim.impl.BpsimPackageImpl#getScenario()
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
		 * The meta object literal for the '<em><b>Vendor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__VENDOR = eINSTANCE.getScenario_Vendor();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SCENARIO__VERSION = eINSTANCE.getScenario_Version();

		/**
		 * The meta object literal for the '{@link bpsim.impl.ScenarioParametersImpl <em>Scenario Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ScenarioParametersImpl
		 * @see bpsim.impl.BpsimPackageImpl#getScenarioParameters()
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
		 * The meta object literal for the '<em><b>Property Parameters</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SCENARIO_PARAMETERS__PROPERTY_PARAMETERS = eINSTANCE.getScenarioParameters_PropertyParameters();

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
		 * The meta object literal for the '{@link bpsim.impl.ScenarioParametersTypeImpl <em>Scenario Parameters Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.ScenarioParametersTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getScenarioParametersType()
		 * @generated
		 */
		EClass SCENARIO_PARAMETERS_TYPE = eINSTANCE.getScenarioParametersType();

		/**
		 * The meta object literal for the '{@link bpsim.impl.StringParameterTypeImpl <em>String Parameter Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.StringParameterTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getStringParameterType()
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
		 * The meta object literal for the '{@link bpsim.impl.TimeParametersImpl <em>Time Parameters</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.TimeParametersImpl
		 * @see bpsim.impl.BpsimPackageImpl#getTimeParameters()
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
		 * The meta object literal for the '{@link bpsim.impl.TriangularDistributionTypeImpl <em>Triangular Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.TriangularDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getTriangularDistributionType()
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
		 * The meta object literal for the '<em><b>Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRIANGULAR_DISTRIBUTION_TYPE__MODE = eINSTANCE.getTriangularDistributionType_Mode();

		/**
		 * The meta object literal for the '{@link bpsim.impl.TruncatedNormalDistributionTypeImpl <em>Truncated Normal Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.TruncatedNormalDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getTruncatedNormalDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.UniformDistributionTypeImpl <em>Uniform Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.UniformDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getUniformDistributionType()
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
		 * The meta object literal for the '{@link bpsim.impl.UserDistributionDataPointTypeImpl <em>User Distribution Data Point Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.UserDistributionDataPointTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getUserDistributionDataPointType()
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
		 * The meta object literal for the '{@link bpsim.impl.UserDistributionTypeImpl <em>User Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.UserDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getUserDistributionType()
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
		 * The meta object literal for the '<em><b>Discrete</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute USER_DISTRIBUTION_TYPE__DISCRETE = eINSTANCE.getUserDistributionType_Discrete();

		/**
		 * The meta object literal for the '{@link bpsim.impl.VendorExtensionImpl <em>Vendor Extension</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.VendorExtensionImpl
		 * @see bpsim.impl.BpsimPackageImpl#getVendorExtension()
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
		 * The meta object literal for the '{@link bpsim.impl.WeibullDistributionTypeImpl <em>Weibull Distribution Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.impl.WeibullDistributionTypeImpl
		 * @see bpsim.impl.BpsimPackageImpl#getWeibullDistributionType()
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
		 * The meta object literal for the '{@link bpsim.ResultType <em>Result Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.ResultType
		 * @see bpsim.impl.BpsimPackageImpl#getResultType()
		 * @generated
		 */
		EEnum RESULT_TYPE = eINSTANCE.getResultType();

		/**
		 * The meta object literal for the '{@link bpsim.TimeUnit <em>Time Unit</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.TimeUnit
		 * @see bpsim.impl.BpsimPackageImpl#getTimeUnit()
		 * @generated
		 */
		EEnum TIME_UNIT = eINSTANCE.getTimeUnit();

		/**
		 * The meta object literal for the '<em>Result Type Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.ResultType
		 * @see bpsim.impl.BpsimPackageImpl#getResultTypeObject()
		 * @generated
		 */
		EDataType RESULT_TYPE_OBJECT = eINSTANCE.getResultTypeObject();

		/**
		 * The meta object literal for the '<em>Time Unit Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see bpsim.TimeUnit
		 * @see bpsim.impl.BpsimPackageImpl#getTimeUnitObject()
		 * @generated
		 */
		EDataType TIME_UNIT_OBJECT = eINSTANCE.getTimeUnitObject();

	}

} //BpsimPackage
