/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jbpm.bpmn2.emfextmodel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelFactory
 * @model kind="package"
 * @generated
 */
public interface EmfextmodelPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "emfextmodel";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.jbpm.org/bpmn2/emfextmodel";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "emfextmodel";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EmfextmodelPackage eINSTANCE = org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl.init();

    /**
     * The meta object id for the '{@link org.jbpm.bpmn2.emfextmodel.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.jbpm.bpmn2.emfextmodel.impl.DocumentRootImpl
     * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getDocumentRoot()
     * @generated
     */
    int DOCUMENT_ROOT = 0;

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
     * The feature id for the '<em><b>Global</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__GLOBAL = 3;

    /**
     * The feature id for the '<em><b>Import</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__IMPORT = 4;

    /**
     * The feature id for the '<em><b>On Entry Script</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ON_ENTRY_SCRIPT = 5;

    /**
     * The feature id for the '<em><b>On Exit Script</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ON_EXIT_SCRIPT = 6;

    /**
     * The feature id for the '<em><b>Package Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__PACKAGE_NAME = 7;

    /**
     * The feature id for the '<em><b>Rule Flow Group</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__RULE_FLOW_GROUP = 8;

    /**
     * The feature id for the '<em><b>Task Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__TASK_NAME = 9;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__VERSION = 10;

    /**
     * The number of structural features of the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_FEATURE_COUNT = 11;

    /**
     * The meta object id for the '{@link org.jbpm.bpmn2.emfextmodel.impl.GlobalTypeImpl <em>Global Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.jbpm.bpmn2.emfextmodel.impl.GlobalTypeImpl
     * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getGlobalType()
     * @generated
     */
    int GLOBAL_TYPE = 1;

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
     * The meta object id for the '{@link org.jbpm.bpmn2.emfextmodel.impl.ImportTypeImpl <em>Import Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.jbpm.bpmn2.emfextmodel.impl.ImportTypeImpl
     * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getImportType()
     * @generated
     */
    int IMPORT_TYPE = 2;

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
     * The meta object id for the '{@link org.jbpm.bpmn2.emfextmodel.impl.OnEntryScriptTypeImpl <em>On Entry Script Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.jbpm.bpmn2.emfextmodel.impl.OnEntryScriptTypeImpl
     * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getOnEntryScriptType()
     * @generated
     */
    int ON_ENTRY_SCRIPT_TYPE = 3;

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
     * The meta object id for the '{@link org.jbpm.bpmn2.emfextmodel.impl.OnExitScriptTypeImpl <em>On Exit Script Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.jbpm.bpmn2.emfextmodel.impl.OnExitScriptTypeImpl
     * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getOnExitScriptType()
     * @generated
     */
    int ON_EXIT_SCRIPT_TYPE = 4;

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
     * The meta object id for the '<em>Package Name Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getPackageNameType()
     * @generated
     */
    int PACKAGE_NAME_TYPE = 5;

    /**
     * The meta object id for the '<em>Rule Flow Group Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getRuleFlowGroupType()
     * @generated
     */
    int RULE_FLOW_GROUP_TYPE = 6;

    /**
     * The meta object id for the '<em>Task Name Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getTaskNameType()
     * @generated
     */
    int TASK_NAME_TYPE = 7;

    /**
     * The meta object id for the '<em>Version Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.math.BigInteger
     * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getVersionType()
     * @generated
     */
    int VERSION_TYPE = 8;


    /**
     * Returns the meta object for class '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Document Root</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot
     * @generated
     */
    EClass getDocumentRoot();

    /**
     * Returns the meta object for the attribute list '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_Mixed();

    /**
     * Returns the meta object for the map '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XMLNSPrefixMap();

    /**
     * Returns the meta object for the map '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XSISchemaLocation();

    /**
     * Returns the meta object for the containment reference '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getGlobal <em>Global</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Global</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getGlobal()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_Global();

    /**
     * Returns the meta object for the containment reference '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getImport <em>Import</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Import</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getImport()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_Import();

    /**
     * Returns the meta object for the containment reference '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getOnEntryScript <em>On Entry Script</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>On Entry Script</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getOnEntryScript()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_OnEntryScript();

    /**
     * Returns the meta object for the containment reference '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getOnExitScript <em>On Exit Script</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>On Exit Script</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getOnExitScript()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_OnExitScript();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getPackageName <em>Package Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Package Name</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getPackageName()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_PackageName();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getRuleFlowGroup <em>Rule Flow Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Rule Flow Group</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getRuleFlowGroup()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_RuleFlowGroup();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getTaskName <em>Task Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Task Name</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getTaskName()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_TaskName();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getVersion <em>Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Version</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot#getVersion()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_Version();

    /**
     * Returns the meta object for class '{@link org.jbpm.bpmn2.emfextmodel.GlobalType <em>Global Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Global Type</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.GlobalType
     * @generated
     */
    EClass getGlobalType();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.GlobalType#getIdentifier <em>Identifier</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Identifier</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.GlobalType#getIdentifier()
     * @see #getGlobalType()
     * @generated
     */
    EAttribute getGlobalType_Identifier();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.GlobalType#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.GlobalType#getType()
     * @see #getGlobalType()
     * @generated
     */
    EAttribute getGlobalType_Type();

    /**
     * Returns the meta object for class '{@link org.jbpm.bpmn2.emfextmodel.ImportType <em>Import Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Import Type</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.ImportType
     * @generated
     */
    EClass getImportType();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.ImportType#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.ImportType#getName()
     * @see #getImportType()
     * @generated
     */
    EAttribute getImportType_Name();

    /**
     * Returns the meta object for class '{@link org.jbpm.bpmn2.emfextmodel.OnEntryScriptType <em>On Entry Script Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>On Entry Script Type</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.OnEntryScriptType
     * @generated
     */
    EClass getOnEntryScriptType();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.OnEntryScriptType#getScript <em>Script</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Script</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.OnEntryScriptType#getScript()
     * @see #getOnEntryScriptType()
     * @generated
     */
    EAttribute getOnEntryScriptType_Script();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.OnEntryScriptType#getScriptFormat <em>Script Format</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Script Format</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.OnEntryScriptType#getScriptFormat()
     * @see #getOnEntryScriptType()
     * @generated
     */
    EAttribute getOnEntryScriptType_ScriptFormat();

    /**
     * Returns the meta object for class '{@link org.jbpm.bpmn2.emfextmodel.OnExitScriptType <em>On Exit Script Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>On Exit Script Type</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.OnExitScriptType
     * @generated
     */
    EClass getOnExitScriptType();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.OnExitScriptType#getScript <em>Script</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Script</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.OnExitScriptType#getScript()
     * @see #getOnExitScriptType()
     * @generated
     */
    EAttribute getOnExitScriptType_Script();

    /**
     * Returns the meta object for the attribute '{@link org.jbpm.bpmn2.emfextmodel.OnExitScriptType#getScriptFormat <em>Script Format</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Script Format</em>'.
     * @see org.jbpm.bpmn2.emfextmodel.OnExitScriptType#getScriptFormat()
     * @see #getOnExitScriptType()
     * @generated
     */
    EAttribute getOnExitScriptType_ScriptFormat();

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
     * Returns the meta object for data type '{@link java.math.BigInteger <em>Version Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Version Type</em>'.
     * @see java.math.BigInteger
     * @model instanceClass="java.math.BigInteger"
     *        extendedMetaData="name='version_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#integer' minInclusive='0'"
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
    EmfextmodelFactory getEmfextmodelFactory();

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
         * The meta object literal for the '{@link org.jbpm.bpmn2.emfextmodel.impl.DocumentRootImpl <em>Document Root</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.jbpm.bpmn2.emfextmodel.impl.DocumentRootImpl
         * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getDocumentRoot()
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
         * The meta object literal for the '<em><b>Package Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DOCUMENT_ROOT__PACKAGE_NAME = eINSTANCE.getDocumentRoot_PackageName();

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
         * The meta object literal for the '{@link org.jbpm.bpmn2.emfextmodel.impl.GlobalTypeImpl <em>Global Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.jbpm.bpmn2.emfextmodel.impl.GlobalTypeImpl
         * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getGlobalType()
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
         * The meta object literal for the '{@link org.jbpm.bpmn2.emfextmodel.impl.ImportTypeImpl <em>Import Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.jbpm.bpmn2.emfextmodel.impl.ImportTypeImpl
         * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getImportType()
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
         * The meta object literal for the '{@link org.jbpm.bpmn2.emfextmodel.impl.OnEntryScriptTypeImpl <em>On Entry Script Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.jbpm.bpmn2.emfextmodel.impl.OnEntryScriptTypeImpl
         * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getOnEntryScriptType()
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
         * The meta object literal for the '{@link org.jbpm.bpmn2.emfextmodel.impl.OnExitScriptTypeImpl <em>On Exit Script Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.jbpm.bpmn2.emfextmodel.impl.OnExitScriptTypeImpl
         * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getOnExitScriptType()
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
         * The meta object literal for the '<em>Package Name Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getPackageNameType()
         * @generated
         */
        EDataType PACKAGE_NAME_TYPE = eINSTANCE.getPackageNameType();

        /**
         * The meta object literal for the '<em>Rule Flow Group Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getRuleFlowGroupType()
         * @generated
         */
        EDataType RULE_FLOW_GROUP_TYPE = eINSTANCE.getRuleFlowGroupType();

        /**
         * The meta object literal for the '<em>Task Name Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getTaskNameType()
         * @generated
         */
        EDataType TASK_NAME_TYPE = eINSTANCE.getTaskNameType();

        /**
         * The meta object literal for the '<em>Version Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.math.BigInteger
         * @see org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl#getVersionType()
         * @generated
         */
        EDataType VERSION_TYPE = eINSTANCE.getVersionType();

    }

} //EmfextmodelPackage
