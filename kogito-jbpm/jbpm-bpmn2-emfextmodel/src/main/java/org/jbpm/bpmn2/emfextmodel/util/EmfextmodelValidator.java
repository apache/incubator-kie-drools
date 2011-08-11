/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jbpm.bpmn2.emfextmodel.util;

import java.math.BigInteger;

import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.EObjectValidator;

import org.eclipse.emf.ecore.xml.type.util.XMLTypeValidator;

import org.jbpm.bpmn2.emfextmodel.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage
 * @generated
 */
public class EmfextmodelValidator extends EObjectValidator {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final EmfextmodelValidator INSTANCE = new EmfextmodelValidator();

    /**
     * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.common.util.Diagnostic#getSource()
     * @see org.eclipse.emf.common.util.Diagnostic#getCode()
     * @generated
     */
    public static final String DIAGNOSTIC_SOURCE = "org.jbpm.bpmn2.emfextmodel";

    /**
     * A constant with a fixed name that can be used as the base value for additional hand written constants.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

    /**
     * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

    /**
     * The cached base package validator.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XMLTypeValidator xmlTypeValidator;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EmfextmodelValidator() {
        super();
        xmlTypeValidator = XMLTypeValidator.INSTANCE;
    }

    /**
     * Returns the package of this validator switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EPackage getEPackage() {
      return EmfextmodelPackage.eINSTANCE;
    }

    /**
     * Calls <code>validateXXX</code> for the corresponding classifier of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
        switch (classifierID) {
            case EmfextmodelPackage.DOCUMENT_ROOT:
                return validateDocumentRoot((DocumentRoot)value, diagnostics, context);
            case EmfextmodelPackage.GLOBAL_TYPE:
                return validateGlobalType((GlobalType)value, diagnostics, context);
            case EmfextmodelPackage.IMPORT_TYPE:
                return validateImportType((ImportType)value, diagnostics, context);
            case EmfextmodelPackage.ON_ENTRY_SCRIPT_TYPE:
                return validateOnEntryScriptType((OnEntryScriptType)value, diagnostics, context);
            case EmfextmodelPackage.ON_EXIT_SCRIPT_TYPE:
                return validateOnExitScriptType((OnExitScriptType)value, diagnostics, context);
            case EmfextmodelPackage.PACKAGE_NAME_TYPE:
                return validatePackageNameType((String)value, diagnostics, context);
            case EmfextmodelPackage.PRIORITY_TYPE:
                return validatePriorityType((BigInteger)value, diagnostics, context);
            case EmfextmodelPackage.RULE_FLOW_GROUP_TYPE:
                return validateRuleFlowGroupType((String)value, diagnostics, context);
            case EmfextmodelPackage.TASK_NAME_TYPE:
                return validateTaskNameType((String)value, diagnostics, context);
            case EmfextmodelPackage.VERSION_TYPE:
                return validateVersionType((BigInteger)value, diagnostics, context);
            default:
                return true;
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateDocumentRoot(DocumentRoot documentRoot, DiagnosticChain diagnostics, Map<Object, Object> context) {
        return validate_EveryDefaultConstraint(documentRoot, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateGlobalType(GlobalType globalType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        return validate_EveryDefaultConstraint(globalType, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateImportType(ImportType importType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        return validate_EveryDefaultConstraint(importType, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateOnEntryScriptType(OnEntryScriptType onEntryScriptType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        return validate_EveryDefaultConstraint(onEntryScriptType, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateOnExitScriptType(OnExitScriptType onExitScriptType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        return validate_EveryDefaultConstraint(onExitScriptType, diagnostics, context);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validatePackageNameType(String packageNameType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        return true;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validatePriorityType(BigInteger priorityType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        boolean result = validatePriorityType_Min(priorityType, diagnostics, context);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @see #validatePriorityType_Min
     */
    public static final BigInteger PRIORITY_TYPE__MIN__VALUE = new BigInteger("1");

    /**
     * Validates the Min constraint of '<em>Priority Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validatePriorityType_Min(BigInteger priorityType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        boolean result = priorityType.compareTo(PRIORITY_TYPE__MIN__VALUE) >= 0;
        if (!result && diagnostics != null)
            reportMinViolation(EmfextmodelPackage.Literals.PRIORITY_TYPE, priorityType, PRIORITY_TYPE__MIN__VALUE, true, diagnostics, context);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateRuleFlowGroupType(String ruleFlowGroupType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        return true;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateTaskNameType(String taskNameType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        return true;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateVersionType(BigInteger versionType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        boolean result = validateVersionType_Min(versionType, diagnostics, context);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @see #validateVersionType_Min
     */
    public static final BigInteger VERSION_TYPE__MIN__VALUE = new BigInteger("0");

    /**
     * Validates the Min constraint of '<em>Version Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateVersionType_Min(BigInteger versionType, DiagnosticChain diagnostics, Map<Object, Object> context) {
        boolean result = versionType.compareTo(VERSION_TYPE__MIN__VALUE) >= 0;
        if (!result && diagnostics != null)
            reportMinViolation(EmfextmodelPackage.Literals.VERSION_TYPE, versionType, VERSION_TYPE__MIN__VALUE, true, diagnostics, context);
        return result;
    }

    /**
     * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        // TODO
        // Specialize this to return a resource locator for messages specific to this validator.
        // Ensure that you remove @generated or mark it @generated NOT
        return super.getResourceLocator();
    }

} //EmfextmodelValidator
