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
package org.jboss.drools;

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
	 * The meta object id for the '{@link org.jboss.drools.impl.DocumentRootImpl <em>Document Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.DocumentRootImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getDocumentRoot()
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
	 * The feature id for the '<em><b>Meta Data</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__META_DATA = 5;

	/**
	 * The feature id for the '<em><b>On Entry Script</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__ON_ENTRY_SCRIPT = 6;

	/**
	 * The feature id for the '<em><b>On Exit Script</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__ON_EXIT_SCRIPT = 7;

	/**
	 * The feature id for the '<em><b>Package Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__PACKAGE_NAME = 8;

	/**
	 * The feature id for the '<em><b>Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__PRIORITY = 9;

	/**
	 * The feature id for the '<em><b>Rule Flow Group</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__RULE_FLOW_GROUP = 10;

	/**
	 * The feature id for the '<em><b>Task Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__TASK_NAME = 11;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT__VERSION = 12;

	/**
	 * The number of structural features of the '<em>Document Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_ROOT_FEATURE_COUNT = 13;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.GlobalTypeImpl <em>Global Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.GlobalTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getGlobalType()
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
	 * The meta object id for the '{@link org.jboss.drools.impl.ImportTypeImpl <em>Import Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.ImportTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getImportType()
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
	 * The meta object id for the '{@link org.jboss.drools.impl.MetaDataTypeImpl <em>Meta Data Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.MetaDataTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getMetaDataType()
	 * @generated
	 */
	int META_DATA_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Meta Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META_DATA_TYPE__META_VALUE = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META_DATA_TYPE__NAME = 1;

	/**
	 * The number of structural features of the '<em>Meta Data Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int META_DATA_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.jboss.drools.impl.OnEntryScriptTypeImpl <em>On Entry Script Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jboss.drools.impl.OnEntryScriptTypeImpl
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getOnEntryScriptType()
	 * @generated
	 */
	int ON_ENTRY_SCRIPT_TYPE = 4;

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
	int ON_EXIT_SCRIPT_TYPE = 5;

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
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getPackageNameType()
	 * @generated
	 */
	int PACKAGE_NAME_TYPE = 6;

	/**
	 * The meta object id for the '<em>Priority Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.math.BigInteger
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getPriorityType()
	 * @generated
	 */
	int PRIORITY_TYPE = 7;

	/**
	 * The meta object id for the '<em>Rule Flow Group Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getRuleFlowGroupType()
	 * @generated
	 */
	int RULE_FLOW_GROUP_TYPE = 8;

	/**
	 * The meta object id for the '<em>Task Name Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getTaskNameType()
	 * @generated
	 */
	int TASK_NAME_TYPE = 9;

	/**
	 * The meta object id for the '<em>Version Type</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.jboss.drools.impl.DroolsPackageImpl#getVersionType()
	 * @generated
	 */
	int VERSION_TYPE = 10;


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
	 * Returns the meta object for the containment reference '{@link org.jboss.drools.DocumentRoot#getMetaData <em>Meta Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Meta Data</em>'.
	 * @see org.jboss.drools.DocumentRoot#getMetaData()
	 * @see #getDocumentRoot()
	 * @generated
	 */
	EReference getDocumentRoot_MetaData();

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
	 * Returns the meta object for class '{@link org.jboss.drools.MetaDataType <em>Meta Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Meta Data Type</em>'.
	 * @see org.jboss.drools.MetaDataType
	 * @generated
	 */
	EClass getMetaDataType();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.MetaDataType#getMetaValue <em>Meta Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Meta Value</em>'.
	 * @see org.jboss.drools.MetaDataType#getMetaValue()
	 * @see #getMetaDataType()
	 * @generated
	 */
	EAttribute getMetaDataType_MetaValue();

	/**
	 * Returns the meta object for the attribute '{@link org.jboss.drools.MetaDataType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.jboss.drools.MetaDataType#getName()
	 * @see #getMetaDataType()
	 * @generated
	 */
	EAttribute getMetaDataType_Name();

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
		 * The meta object literal for the '<em><b>Meta Data</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT_ROOT__META_DATA = eINSTANCE.getDocumentRoot_MetaData();

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
		 * The meta object literal for the '{@link org.jboss.drools.impl.MetaDataTypeImpl <em>Meta Data Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jboss.drools.impl.MetaDataTypeImpl
		 * @see org.jboss.drools.impl.DroolsPackageImpl#getMetaDataType()
		 * @generated
		 */
		EClass META_DATA_TYPE = eINSTANCE.getMetaDataType();

		/**
		 * The meta object literal for the '<em><b>Meta Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META_DATA_TYPE__META_VALUE = eINSTANCE.getMetaDataType_MetaValue();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute META_DATA_TYPE__NAME = eINSTANCE.getMetaDataType_Name();

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
