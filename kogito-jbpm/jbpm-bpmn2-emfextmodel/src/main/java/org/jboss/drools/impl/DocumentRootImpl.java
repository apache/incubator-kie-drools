/**
 */
package org.jboss.drools.impl;

import java.math.BigInteger;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.jboss.drools.BetaDistributionType;
import org.jboss.drools.BinomialDistributionType;
import org.jboss.drools.BooleanParameterType;
import org.jboss.drools.DateTimeParameterType;
import org.jboss.drools.DocumentRoot;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.DurationParameterType;
import org.jboss.drools.EnumParameterType;
import org.jboss.drools.ErlangDistributionType;
import org.jboss.drools.ExpressionParameterType;
import org.jboss.drools.FloatingParameterType;
import org.jboss.drools.GammaDistributionType;
import org.jboss.drools.GlobalType;
import org.jboss.drools.ImportType;
import org.jboss.drools.LogNormalDistributionType;
import org.jboss.drools.MetadataType;
import org.jboss.drools.MetaentryType;
import org.jboss.drools.NegativeExponentialDistributionType;
import org.jboss.drools.NormalDistributionType;
import org.jboss.drools.NumericParameterType;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.jboss.drools.ParameterValue;
import org.jboss.drools.PoissonDistributionType;
import org.jboss.drools.ProcessAnalysisDataType;
import org.jboss.drools.StringParameterType;
import org.jboss.drools.TriangularDistributionType;
import org.jboss.drools.TruncatedNormalDistributionType;
import org.jboss.drools.UniformDistributionType;
import org.jboss.drools.UserDistributionDataPointType;
import org.jboss.drools.UserDistributionType;
import org.jboss.drools.WeibullDistributionType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getBetaDistribution <em>Beta Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getParameterValue <em>Parameter Value</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getBinomialDistribution <em>Binomial Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getBooleanParameter <em>Boolean Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getDateTimeParameter <em>Date Time Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getDurationParameter <em>Duration Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getEnumParameter <em>Enum Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getErlangDistribution <em>Erlang Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getExpressionParameter <em>Expression Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getFloatingParameter <em>Floating Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getGammaDistribution <em>Gamma Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getGlobal <em>Global</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getImport <em>Import</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getLogNormalDistribution <em>Log Normal Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getMetadata <em>Metadata</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getMetaentry <em>Metaentry</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getNegativeExponentialDistribution <em>Negative Exponential Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getNormalDistribution <em>Normal Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getNumericParameter <em>Numeric Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getOnEntryScript <em>On Entry Script</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getOnExitScript <em>On Exit Script</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getPoissonDistribution <em>Poisson Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getProcessAnalysisData <em>Process Analysis Data</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getStringParameter <em>String Parameter</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getTriangularDistribution <em>Triangular Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getTruncatedNormalDistribution <em>Truncated Normal Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getUniformDistribution <em>Uniform Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getUserDistribution <em>User Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getUserDistributionDataPoint <em>User Distribution Data Point</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getWeibullDistribution <em>Weibull Distribution</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getPackageName <em>Package Name</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getPriority <em>Priority</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getRuleFlowGroup <em>Rule Flow Group</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getTaskName <em>Task Name</em>}</li>
 *   <li>{@link org.jboss.drools.impl.DocumentRootImpl#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DocumentRootImpl extends EObjectImpl implements DocumentRoot {
	/**
	 * The cached value of the '{@link #getMixed() <em>Mixed</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMixed()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap mixed;

	/**
	 * The cached value of the '{@link #getXMLNSPrefixMap() <em>XMLNS Prefix Map</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXMLNSPrefixMap()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> xMLNSPrefixMap;

	/**
	 * The cached value of the '{@link #getXSISchemaLocation() <em>XSI Schema Location</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getXSISchemaLocation()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, String> xSISchemaLocation;

	/**
	 * The default value of the '{@link #getPackageName() <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageName()
	 * @generated
	 * @ordered
	 */
	protected static final String PACKAGE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPackageName() <em>Package Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPackageName()
	 * @generated
	 * @ordered
	 */
	protected String packageName = PACKAGE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getPriority() <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPriority()
	 * @generated
	 * @ordered
	 */
	protected static final BigInteger PRIORITY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPriority() <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPriority()
	 * @generated
	 * @ordered
	 */
	protected BigInteger priority = PRIORITY_EDEFAULT;

	/**
	 * The default value of the '{@link #getRuleFlowGroup() <em>Rule Flow Group</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuleFlowGroup()
	 * @generated
	 * @ordered
	 */
	protected static final String RULE_FLOW_GROUP_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRuleFlowGroup() <em>Rule Flow Group</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuleFlowGroup()
	 * @generated
	 * @ordered
	 */
	protected String ruleFlowGroup = RULE_FLOW_GROUP_EDEFAULT;

	/**
	 * The default value of the '{@link #getTaskName() <em>Task Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTaskName()
	 * @generated
	 * @ordered
	 */
	protected static final String TASK_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTaskName() <em>Task Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTaskName()
	 * @generated
	 * @ordered
	 */
	protected String taskName = TASK_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DocumentRootImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DroolsPackage.Literals.DOCUMENT_ROOT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getMixed() {
		if (mixed == null) {
			mixed = new BasicFeatureMap(this, DroolsPackage.DOCUMENT_ROOT__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, String> getXMLNSPrefixMap() {
		if (xMLNSPrefixMap == null) {
			xMLNSPrefixMap = new EcoreEMap<String,String>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, DroolsPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP);
		}
		return xMLNSPrefixMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, String> getXSISchemaLocation() {
		if (xSISchemaLocation == null) {
			xSISchemaLocation = new EcoreEMap<String,String>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, EStringToStringMapEntryImpl.class, this, DroolsPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION);
		}
		return xSISchemaLocation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BetaDistributionType getBetaDistribution() {
		return (BetaDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__BETA_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetBetaDistribution(BetaDistributionType newBetaDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__BETA_DISTRIBUTION, newBetaDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBetaDistribution(BetaDistributionType newBetaDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__BETA_DISTRIBUTION, newBetaDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ParameterValue getParameterValue() {
		return (ParameterValue)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__PARAMETER_VALUE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParameterValue(ParameterValue newParameterValue, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__PARAMETER_VALUE, newParameterValue, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParameterValue(ParameterValue newParameterValue) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__PARAMETER_VALUE, newParameterValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BinomialDistributionType getBinomialDistribution() {
		return (BinomialDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetBinomialDistribution(BinomialDistributionType newBinomialDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION, newBinomialDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBinomialDistribution(BinomialDistributionType newBinomialDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION, newBinomialDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BooleanParameterType getBooleanParameter() {
		return (BooleanParameterType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__BOOLEAN_PARAMETER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetBooleanParameter(BooleanParameterType newBooleanParameter, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__BOOLEAN_PARAMETER, newBooleanParameter, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBooleanParameter(BooleanParameterType newBooleanParameter) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__BOOLEAN_PARAMETER, newBooleanParameter);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DateTimeParameterType getDateTimeParameter() {
		return (DateTimeParameterType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__DATE_TIME_PARAMETER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDateTimeParameter(DateTimeParameterType newDateTimeParameter, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__DATE_TIME_PARAMETER, newDateTimeParameter, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDateTimeParameter(DateTimeParameterType newDateTimeParameter) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__DATE_TIME_PARAMETER, newDateTimeParameter);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DurationParameterType getDurationParameter() {
		return (DurationParameterType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__DURATION_PARAMETER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDurationParameter(DurationParameterType newDurationParameter, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__DURATION_PARAMETER, newDurationParameter, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDurationParameter(DurationParameterType newDurationParameter) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__DURATION_PARAMETER, newDurationParameter);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnumParameterType getEnumParameter() {
		return (EnumParameterType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__ENUM_PARAMETER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetEnumParameter(EnumParameterType newEnumParameter, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__ENUM_PARAMETER, newEnumParameter, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEnumParameter(EnumParameterType newEnumParameter) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__ENUM_PARAMETER, newEnumParameter);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ErlangDistributionType getErlangDistribution() {
		return (ErlangDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__ERLANG_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetErlangDistribution(ErlangDistributionType newErlangDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__ERLANG_DISTRIBUTION, newErlangDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setErlangDistribution(ErlangDistributionType newErlangDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__ERLANG_DISTRIBUTION, newErlangDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExpressionParameterType getExpressionParameter() {
		return (ExpressionParameterType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__EXPRESSION_PARAMETER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExpressionParameter(ExpressionParameterType newExpressionParameter, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__EXPRESSION_PARAMETER, newExpressionParameter, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExpressionParameter(ExpressionParameterType newExpressionParameter) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__EXPRESSION_PARAMETER, newExpressionParameter);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FloatingParameterType getFloatingParameter() {
		return (FloatingParameterType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__FLOATING_PARAMETER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetFloatingParameter(FloatingParameterType newFloatingParameter, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__FLOATING_PARAMETER, newFloatingParameter, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFloatingParameter(FloatingParameterType newFloatingParameter) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__FLOATING_PARAMETER, newFloatingParameter);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GammaDistributionType getGammaDistribution() {
		return (GammaDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__GAMMA_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGammaDistribution(GammaDistributionType newGammaDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__GAMMA_DISTRIBUTION, newGammaDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGammaDistribution(GammaDistributionType newGammaDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__GAMMA_DISTRIBUTION, newGammaDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GlobalType getGlobal() {
		return (GlobalType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGlobal(GlobalType newGlobal, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL, newGlobal, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGlobal(GlobalType newGlobal) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL, newGlobal);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImportType getImport() {
		return (ImportType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetImport(ImportType newImport, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT, newImport, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImport(ImportType newImport) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT, newImport);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LogNormalDistributionType getLogNormalDistribution() {
		return (LogNormalDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLogNormalDistribution(LogNormalDistributionType newLogNormalDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION, newLogNormalDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLogNormalDistribution(LogNormalDistributionType newLogNormalDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION, newLogNormalDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetadataType getMetadata() {
		return (MetadataType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__METADATA, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMetadata(MetadataType newMetadata, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__METADATA, newMetadata, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMetadata(MetadataType newMetadata) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__METADATA, newMetadata);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MetaentryType getMetaentry() {
		return (MetaentryType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__METAENTRY, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMetaentry(MetaentryType newMetaentry, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__METAENTRY, newMetaentry, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMetaentry(MetaentryType newMetaentry) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__METAENTRY, newMetaentry);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NegativeExponentialDistributionType getNegativeExponentialDistribution() {
		return (NegativeExponentialDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNegativeExponentialDistribution(NegativeExponentialDistributionType newNegativeExponentialDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION, newNegativeExponentialDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNegativeExponentialDistribution(NegativeExponentialDistributionType newNegativeExponentialDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION, newNegativeExponentialDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NormalDistributionType getNormalDistribution() {
		return (NormalDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__NORMAL_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNormalDistribution(NormalDistributionType newNormalDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__NORMAL_DISTRIBUTION, newNormalDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNormalDistribution(NormalDistributionType newNormalDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__NORMAL_DISTRIBUTION, newNormalDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NumericParameterType getNumericParameter() {
		return (NumericParameterType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__NUMERIC_PARAMETER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetNumericParameter(NumericParameterType newNumericParameter, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__NUMERIC_PARAMETER, newNumericParameter, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNumericParameter(NumericParameterType newNumericParameter) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__NUMERIC_PARAMETER, newNumericParameter);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OnEntryScriptType getOnEntryScript() {
		return (OnEntryScriptType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOnEntryScript(OnEntryScriptType newOnEntryScript, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, newOnEntryScript, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOnEntryScript(OnEntryScriptType newOnEntryScript) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, newOnEntryScript);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OnExitScriptType getOnExitScript() {
		return (OnExitScriptType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOnExitScript(OnExitScriptType newOnExitScript, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, newOnExitScript, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOnExitScript(OnExitScriptType newOnExitScript) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, newOnExitScript);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PoissonDistributionType getPoissonDistribution() {
		return (PoissonDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__POISSON_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetPoissonDistribution(PoissonDistributionType newPoissonDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__POISSON_DISTRIBUTION, newPoissonDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPoissonDistribution(PoissonDistributionType newPoissonDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__POISSON_DISTRIBUTION, newPoissonDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProcessAnalysisDataType getProcessAnalysisData() {
		return (ProcessAnalysisDataType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProcessAnalysisData(ProcessAnalysisDataType newProcessAnalysisData, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA, newProcessAnalysisData, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProcessAnalysisData(ProcessAnalysisDataType newProcessAnalysisData) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA, newProcessAnalysisData);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StringParameterType getStringParameter() {
		return (StringParameterType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__STRING_PARAMETER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetStringParameter(StringParameterType newStringParameter, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__STRING_PARAMETER, newStringParameter, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStringParameter(StringParameterType newStringParameter) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__STRING_PARAMETER, newStringParameter);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TriangularDistributionType getTriangularDistribution() {
		return (TriangularDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTriangularDistribution(TriangularDistributionType newTriangularDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION, newTriangularDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTriangularDistribution(TriangularDistributionType newTriangularDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION, newTriangularDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TruncatedNormalDistributionType getTruncatedNormalDistribution() {
		return (TruncatedNormalDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTruncatedNormalDistribution(TruncatedNormalDistributionType newTruncatedNormalDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION, newTruncatedNormalDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTruncatedNormalDistribution(TruncatedNormalDistributionType newTruncatedNormalDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION, newTruncatedNormalDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UniformDistributionType getUniformDistribution() {
		return (UniformDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__UNIFORM_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUniformDistribution(UniformDistributionType newUniformDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__UNIFORM_DISTRIBUTION, newUniformDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUniformDistribution(UniformDistributionType newUniformDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__UNIFORM_DISTRIBUTION, newUniformDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UserDistributionType getUserDistribution() {
		return (UserDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__USER_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUserDistribution(UserDistributionType newUserDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__USER_DISTRIBUTION, newUserDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUserDistribution(UserDistributionType newUserDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__USER_DISTRIBUTION, newUserDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UserDistributionDataPointType getUserDistributionDataPoint() {
		return (UserDistributionDataPointType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetUserDistributionDataPoint(UserDistributionDataPointType newUserDistributionDataPoint, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT, newUserDistributionDataPoint, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUserDistributionDataPoint(UserDistributionDataPointType newUserDistributionDataPoint) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT, newUserDistributionDataPoint);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WeibullDistributionType getWeibullDistribution() {
		return (WeibullDistributionType)getMixed().get(DroolsPackage.Literals.DOCUMENT_ROOT__WEIBULL_DISTRIBUTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetWeibullDistribution(WeibullDistributionType newWeibullDistribution, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(DroolsPackage.Literals.DOCUMENT_ROOT__WEIBULL_DISTRIBUTION, newWeibullDistribution, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWeibullDistribution(WeibullDistributionType newWeibullDistribution) {
		((FeatureMap.Internal)getMixed()).set(DroolsPackage.Literals.DOCUMENT_ROOT__WEIBULL_DISTRIBUTION, newWeibullDistribution);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPackageName(String newPackageName) {
		String oldPackageName = packageName;
		packageName = newPackageName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.DOCUMENT_ROOT__PACKAGE_NAME, oldPackageName, packageName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BigInteger getPriority() {
		return priority;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPriority(BigInteger newPriority) {
		BigInteger oldPriority = priority;
		priority = newPriority;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.DOCUMENT_ROOT__PRIORITY, oldPriority, priority));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRuleFlowGroup() {
		return ruleFlowGroup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRuleFlowGroup(String newRuleFlowGroup) {
		String oldRuleFlowGroup = ruleFlowGroup;
		ruleFlowGroup = newRuleFlowGroup;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.DOCUMENT_ROOT__RULE_FLOW_GROUP, oldRuleFlowGroup, ruleFlowGroup));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTaskName(String newTaskName) {
		String oldTaskName = taskName;
		taskName = newTaskName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.DOCUMENT_ROOT__TASK_NAME, oldTaskName, taskName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DroolsPackage.DOCUMENT_ROOT__VERSION, oldVersion, version));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case DroolsPackage.DOCUMENT_ROOT__MIXED:
				return ((InternalEList<?>)getMixed()).basicRemove(otherEnd, msgs);
			case DroolsPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
				return ((InternalEList<?>)getXMLNSPrefixMap()).basicRemove(otherEnd, msgs);
			case DroolsPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
				return ((InternalEList<?>)getXSISchemaLocation()).basicRemove(otherEnd, msgs);
			case DroolsPackage.DOCUMENT_ROOT__BETA_DISTRIBUTION:
				return basicSetBetaDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__PARAMETER_VALUE:
				return basicSetParameterValue(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION:
				return basicSetBinomialDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__BOOLEAN_PARAMETER:
				return basicSetBooleanParameter(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__DATE_TIME_PARAMETER:
				return basicSetDateTimeParameter(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__DURATION_PARAMETER:
				return basicSetDurationParameter(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__ENUM_PARAMETER:
				return basicSetEnumParameter(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__ERLANG_DISTRIBUTION:
				return basicSetErlangDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__EXPRESSION_PARAMETER:
				return basicSetExpressionParameter(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__FLOATING_PARAMETER:
				return basicSetFloatingParameter(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__GAMMA_DISTRIBUTION:
				return basicSetGammaDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__GLOBAL:
				return basicSetGlobal(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__IMPORT:
				return basicSetImport(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION:
				return basicSetLogNormalDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__METADATA:
				return basicSetMetadata(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__METAENTRY:
				return basicSetMetaentry(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION:
				return basicSetNegativeExponentialDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__NORMAL_DISTRIBUTION:
				return basicSetNormalDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__NUMERIC_PARAMETER:
				return basicSetNumericParameter(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__ON_ENTRY_SCRIPT:
				return basicSetOnEntryScript(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__ON_EXIT_SCRIPT:
				return basicSetOnExitScript(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__POISSON_DISTRIBUTION:
				return basicSetPoissonDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA:
				return basicSetProcessAnalysisData(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__STRING_PARAMETER:
				return basicSetStringParameter(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION:
				return basicSetTriangularDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION:
				return basicSetTruncatedNormalDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__UNIFORM_DISTRIBUTION:
				return basicSetUniformDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION:
				return basicSetUserDistribution(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT:
				return basicSetUserDistributionDataPoint(null, msgs);
			case DroolsPackage.DOCUMENT_ROOT__WEIBULL_DISTRIBUTION:
				return basicSetWeibullDistribution(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DroolsPackage.DOCUMENT_ROOT__MIXED:
				if (coreType) return getMixed();
				return ((FeatureMap.Internal)getMixed()).getWrapper();
			case DroolsPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
				if (coreType) return getXMLNSPrefixMap();
				else return getXMLNSPrefixMap().map();
			case DroolsPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
				if (coreType) return getXSISchemaLocation();
				else return getXSISchemaLocation().map();
			case DroolsPackage.DOCUMENT_ROOT__BETA_DISTRIBUTION:
				return getBetaDistribution();
			case DroolsPackage.DOCUMENT_ROOT__PARAMETER_VALUE:
				return getParameterValue();
			case DroolsPackage.DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION:
				return getBinomialDistribution();
			case DroolsPackage.DOCUMENT_ROOT__BOOLEAN_PARAMETER:
				return getBooleanParameter();
			case DroolsPackage.DOCUMENT_ROOT__DATE_TIME_PARAMETER:
				return getDateTimeParameter();
			case DroolsPackage.DOCUMENT_ROOT__DURATION_PARAMETER:
				return getDurationParameter();
			case DroolsPackage.DOCUMENT_ROOT__ENUM_PARAMETER:
				return getEnumParameter();
			case DroolsPackage.DOCUMENT_ROOT__ERLANG_DISTRIBUTION:
				return getErlangDistribution();
			case DroolsPackage.DOCUMENT_ROOT__EXPRESSION_PARAMETER:
				return getExpressionParameter();
			case DroolsPackage.DOCUMENT_ROOT__FLOATING_PARAMETER:
				return getFloatingParameter();
			case DroolsPackage.DOCUMENT_ROOT__GAMMA_DISTRIBUTION:
				return getGammaDistribution();
			case DroolsPackage.DOCUMENT_ROOT__GLOBAL:
				return getGlobal();
			case DroolsPackage.DOCUMENT_ROOT__IMPORT:
				return getImport();
			case DroolsPackage.DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION:
				return getLogNormalDistribution();
			case DroolsPackage.DOCUMENT_ROOT__METADATA:
				return getMetadata();
			case DroolsPackage.DOCUMENT_ROOT__METAENTRY:
				return getMetaentry();
			case DroolsPackage.DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION:
				return getNegativeExponentialDistribution();
			case DroolsPackage.DOCUMENT_ROOT__NORMAL_DISTRIBUTION:
				return getNormalDistribution();
			case DroolsPackage.DOCUMENT_ROOT__NUMERIC_PARAMETER:
				return getNumericParameter();
			case DroolsPackage.DOCUMENT_ROOT__ON_ENTRY_SCRIPT:
				return getOnEntryScript();
			case DroolsPackage.DOCUMENT_ROOT__ON_EXIT_SCRIPT:
				return getOnExitScript();
			case DroolsPackage.DOCUMENT_ROOT__POISSON_DISTRIBUTION:
				return getPoissonDistribution();
			case DroolsPackage.DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA:
				return getProcessAnalysisData();
			case DroolsPackage.DOCUMENT_ROOT__STRING_PARAMETER:
				return getStringParameter();
			case DroolsPackage.DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION:
				return getTriangularDistribution();
			case DroolsPackage.DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION:
				return getTruncatedNormalDistribution();
			case DroolsPackage.DOCUMENT_ROOT__UNIFORM_DISTRIBUTION:
				return getUniformDistribution();
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION:
				return getUserDistribution();
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT:
				return getUserDistributionDataPoint();
			case DroolsPackage.DOCUMENT_ROOT__WEIBULL_DISTRIBUTION:
				return getWeibullDistribution();
			case DroolsPackage.DOCUMENT_ROOT__PACKAGE_NAME:
				return getPackageName();
			case DroolsPackage.DOCUMENT_ROOT__PRIORITY:
				return getPriority();
			case DroolsPackage.DOCUMENT_ROOT__RULE_FLOW_GROUP:
				return getRuleFlowGroup();
			case DroolsPackage.DOCUMENT_ROOT__TASK_NAME:
				return getTaskName();
			case DroolsPackage.DOCUMENT_ROOT__VERSION:
				return getVersion();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DroolsPackage.DOCUMENT_ROOT__MIXED:
				((FeatureMap.Internal)getMixed()).set(newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
				((EStructuralFeature.Setting)getXMLNSPrefixMap()).set(newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
				((EStructuralFeature.Setting)getXSISchemaLocation()).set(newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__BETA_DISTRIBUTION:
				setBetaDistribution((BetaDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__PARAMETER_VALUE:
				setParameterValue((ParameterValue)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION:
				setBinomialDistribution((BinomialDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__BOOLEAN_PARAMETER:
				setBooleanParameter((BooleanParameterType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__DATE_TIME_PARAMETER:
				setDateTimeParameter((DateTimeParameterType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__DURATION_PARAMETER:
				setDurationParameter((DurationParameterType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__ENUM_PARAMETER:
				setEnumParameter((EnumParameterType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__ERLANG_DISTRIBUTION:
				setErlangDistribution((ErlangDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__EXPRESSION_PARAMETER:
				setExpressionParameter((ExpressionParameterType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__FLOATING_PARAMETER:
				setFloatingParameter((FloatingParameterType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__GAMMA_DISTRIBUTION:
				setGammaDistribution((GammaDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__GLOBAL:
				setGlobal((GlobalType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__IMPORT:
				setImport((ImportType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION:
				setLogNormalDistribution((LogNormalDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__METADATA:
				setMetadata((MetadataType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__METAENTRY:
				setMetaentry((MetaentryType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION:
				setNegativeExponentialDistribution((NegativeExponentialDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__NORMAL_DISTRIBUTION:
				setNormalDistribution((NormalDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__NUMERIC_PARAMETER:
				setNumericParameter((NumericParameterType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__ON_ENTRY_SCRIPT:
				setOnEntryScript((OnEntryScriptType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__ON_EXIT_SCRIPT:
				setOnExitScript((OnExitScriptType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__POISSON_DISTRIBUTION:
				setPoissonDistribution((PoissonDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA:
				setProcessAnalysisData((ProcessAnalysisDataType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__STRING_PARAMETER:
				setStringParameter((StringParameterType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION:
				setTriangularDistribution((TriangularDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION:
				setTruncatedNormalDistribution((TruncatedNormalDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__UNIFORM_DISTRIBUTION:
				setUniformDistribution((UniformDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION:
				setUserDistribution((UserDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT:
				setUserDistributionDataPoint((UserDistributionDataPointType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__WEIBULL_DISTRIBUTION:
				setWeibullDistribution((WeibullDistributionType)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__PACKAGE_NAME:
				setPackageName((String)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__PRIORITY:
				setPriority((BigInteger)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__RULE_FLOW_GROUP:
				setRuleFlowGroup((String)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__TASK_NAME:
				setTaskName((String)newValue);
				return;
			case DroolsPackage.DOCUMENT_ROOT__VERSION:
				setVersion((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case DroolsPackage.DOCUMENT_ROOT__MIXED:
				getMixed().clear();
				return;
			case DroolsPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
				getXMLNSPrefixMap().clear();
				return;
			case DroolsPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
				getXSISchemaLocation().clear();
				return;
			case DroolsPackage.DOCUMENT_ROOT__BETA_DISTRIBUTION:
				setBetaDistribution((BetaDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__PARAMETER_VALUE:
				setParameterValue((ParameterValue)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION:
				setBinomialDistribution((BinomialDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__BOOLEAN_PARAMETER:
				setBooleanParameter((BooleanParameterType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__DATE_TIME_PARAMETER:
				setDateTimeParameter((DateTimeParameterType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__DURATION_PARAMETER:
				setDurationParameter((DurationParameterType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__ENUM_PARAMETER:
				setEnumParameter((EnumParameterType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__ERLANG_DISTRIBUTION:
				setErlangDistribution((ErlangDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__EXPRESSION_PARAMETER:
				setExpressionParameter((ExpressionParameterType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__FLOATING_PARAMETER:
				setFloatingParameter((FloatingParameterType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__GAMMA_DISTRIBUTION:
				setGammaDistribution((GammaDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__GLOBAL:
				setGlobal((GlobalType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__IMPORT:
				setImport((ImportType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION:
				setLogNormalDistribution((LogNormalDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__METADATA:
				setMetadata((MetadataType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__METAENTRY:
				setMetaentry((MetaentryType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION:
				setNegativeExponentialDistribution((NegativeExponentialDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__NORMAL_DISTRIBUTION:
				setNormalDistribution((NormalDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__NUMERIC_PARAMETER:
				setNumericParameter((NumericParameterType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__ON_ENTRY_SCRIPT:
				setOnEntryScript((OnEntryScriptType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__ON_EXIT_SCRIPT:
				setOnExitScript((OnExitScriptType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__POISSON_DISTRIBUTION:
				setPoissonDistribution((PoissonDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA:
				setProcessAnalysisData((ProcessAnalysisDataType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__STRING_PARAMETER:
				setStringParameter((StringParameterType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION:
				setTriangularDistribution((TriangularDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION:
				setTruncatedNormalDistribution((TruncatedNormalDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__UNIFORM_DISTRIBUTION:
				setUniformDistribution((UniformDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION:
				setUserDistribution((UserDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT:
				setUserDistributionDataPoint((UserDistributionDataPointType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__WEIBULL_DISTRIBUTION:
				setWeibullDistribution((WeibullDistributionType)null);
				return;
			case DroolsPackage.DOCUMENT_ROOT__PACKAGE_NAME:
				setPackageName(PACKAGE_NAME_EDEFAULT);
				return;
			case DroolsPackage.DOCUMENT_ROOT__PRIORITY:
				setPriority(PRIORITY_EDEFAULT);
				return;
			case DroolsPackage.DOCUMENT_ROOT__RULE_FLOW_GROUP:
				setRuleFlowGroup(RULE_FLOW_GROUP_EDEFAULT);
				return;
			case DroolsPackage.DOCUMENT_ROOT__TASK_NAME:
				setTaskName(TASK_NAME_EDEFAULT);
				return;
			case DroolsPackage.DOCUMENT_ROOT__VERSION:
				setVersion(VERSION_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DroolsPackage.DOCUMENT_ROOT__MIXED:
				return mixed != null && !mixed.isEmpty();
			case DroolsPackage.DOCUMENT_ROOT__XMLNS_PREFIX_MAP:
				return xMLNSPrefixMap != null && !xMLNSPrefixMap.isEmpty();
			case DroolsPackage.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION:
				return xSISchemaLocation != null && !xSISchemaLocation.isEmpty();
			case DroolsPackage.DOCUMENT_ROOT__BETA_DISTRIBUTION:
				return getBetaDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__PARAMETER_VALUE:
				return getParameterValue() != null;
			case DroolsPackage.DOCUMENT_ROOT__BINOMIAL_DISTRIBUTION:
				return getBinomialDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__BOOLEAN_PARAMETER:
				return getBooleanParameter() != null;
			case DroolsPackage.DOCUMENT_ROOT__DATE_TIME_PARAMETER:
				return getDateTimeParameter() != null;
			case DroolsPackage.DOCUMENT_ROOT__DURATION_PARAMETER:
				return getDurationParameter() != null;
			case DroolsPackage.DOCUMENT_ROOT__ENUM_PARAMETER:
				return getEnumParameter() != null;
			case DroolsPackage.DOCUMENT_ROOT__ERLANG_DISTRIBUTION:
				return getErlangDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__EXPRESSION_PARAMETER:
				return getExpressionParameter() != null;
			case DroolsPackage.DOCUMENT_ROOT__FLOATING_PARAMETER:
				return getFloatingParameter() != null;
			case DroolsPackage.DOCUMENT_ROOT__GAMMA_DISTRIBUTION:
				return getGammaDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__GLOBAL:
				return getGlobal() != null;
			case DroolsPackage.DOCUMENT_ROOT__IMPORT:
				return getImport() != null;
			case DroolsPackage.DOCUMENT_ROOT__LOG_NORMAL_DISTRIBUTION:
				return getLogNormalDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__METADATA:
				return getMetadata() != null;
			case DroolsPackage.DOCUMENT_ROOT__METAENTRY:
				return getMetaentry() != null;
			case DroolsPackage.DOCUMENT_ROOT__NEGATIVE_EXPONENTIAL_DISTRIBUTION:
				return getNegativeExponentialDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__NORMAL_DISTRIBUTION:
				return getNormalDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__NUMERIC_PARAMETER:
				return getNumericParameter() != null;
			case DroolsPackage.DOCUMENT_ROOT__ON_ENTRY_SCRIPT:
				return getOnEntryScript() != null;
			case DroolsPackage.DOCUMENT_ROOT__ON_EXIT_SCRIPT:
				return getOnExitScript() != null;
			case DroolsPackage.DOCUMENT_ROOT__POISSON_DISTRIBUTION:
				return getPoissonDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__PROCESS_ANALYSIS_DATA:
				return getProcessAnalysisData() != null;
			case DroolsPackage.DOCUMENT_ROOT__STRING_PARAMETER:
				return getStringParameter() != null;
			case DroolsPackage.DOCUMENT_ROOT__TRIANGULAR_DISTRIBUTION:
				return getTriangularDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__TRUNCATED_NORMAL_DISTRIBUTION:
				return getTruncatedNormalDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__UNIFORM_DISTRIBUTION:
				return getUniformDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION:
				return getUserDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__USER_DISTRIBUTION_DATA_POINT:
				return getUserDistributionDataPoint() != null;
			case DroolsPackage.DOCUMENT_ROOT__WEIBULL_DISTRIBUTION:
				return getWeibullDistribution() != null;
			case DroolsPackage.DOCUMENT_ROOT__PACKAGE_NAME:
				return PACKAGE_NAME_EDEFAULT == null ? packageName != null : !PACKAGE_NAME_EDEFAULT.equals(packageName);
			case DroolsPackage.DOCUMENT_ROOT__PRIORITY:
				return PRIORITY_EDEFAULT == null ? priority != null : !PRIORITY_EDEFAULT.equals(priority);
			case DroolsPackage.DOCUMENT_ROOT__RULE_FLOW_GROUP:
				return RULE_FLOW_GROUP_EDEFAULT == null ? ruleFlowGroup != null : !RULE_FLOW_GROUP_EDEFAULT.equals(ruleFlowGroup);
			case DroolsPackage.DOCUMENT_ROOT__TASK_NAME:
				return TASK_NAME_EDEFAULT == null ? taskName != null : !TASK_NAME_EDEFAULT.equals(taskName);
			case DroolsPackage.DOCUMENT_ROOT__VERSION:
				return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (mixed: ");
		result.append(mixed);
		result.append(", packageName: ");
		result.append(packageName);
		result.append(", priority: ");
		result.append(priority);
		result.append(", ruleFlowGroup: ");
		result.append(ruleFlowGroup);
		result.append(", taskName: ");
		result.append(taskName);
		result.append(", version: ");
		result.append(version);
		result.append(')');
		return result.toString();
	}

} //DocumentRootImpl
