/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jbpm.bpmn2.emfextmodel;

import java.math.BigInteger;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getMixed <em>Mixed</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getOnEntryScript <em>On Entry Script</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getOnExitScript <em>On Exit Script</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getPackageName <em>Package Name</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getRuleFlowGroup <em>Rule Flow Group</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getTaskName <em>Task Name</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot()
 * @model extendedMetaData="name='' kind='mixed'"
 * @generated
 */
public interface DocumentRoot extends EObject {
    /**
     * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mixed</em>' attribute list.
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot_Mixed()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='elementWildcard' name=':mixed'"
     * @generated
     */
    FeatureMap getMixed();

    /**
     * Returns the value of the '<em><b>XMLNS Prefix Map</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>XMLNS Prefix Map</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>XMLNS Prefix Map</em>' map.
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot_XMLNSPrefixMap()
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>" transient="true"
     *        extendedMetaData="kind='attribute' name='xmlns:prefix'"
     * @generated
     */
    EMap<String, String> getXMLNSPrefixMap();

    /**
     * Returns the value of the '<em><b>XSI Schema Location</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>XSI Schema Location</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>XSI Schema Location</em>' map.
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot_XSISchemaLocation()
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>" transient="true"
     *        extendedMetaData="kind='attribute' name='xsi:schemaLocation'"
     * @generated
     */
    EMap<String, String> getXSISchemaLocation();

    /**
     * Returns the value of the '<em><b>On Entry Script</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>On Entry Script</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>On Entry Script</em>' containment reference.
     * @see #setOnEntryScript(OnEntryScriptType)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot_OnEntryScript()
     * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='onEntry-script' namespace='##targetNamespace'"
     * @generated
     */
    OnEntryScriptType getOnEntryScript();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getOnEntryScript <em>On Entry Script</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>On Entry Script</em>' containment reference.
     * @see #getOnEntryScript()
     * @generated
     */
    void setOnEntryScript(OnEntryScriptType value);

    /**
     * Returns the value of the '<em><b>On Exit Script</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>On Exit Script</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>On Exit Script</em>' containment reference.
     * @see #setOnExitScript(OnExitScriptType)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot_OnExitScript()
     * @model containment="true" upper="-2" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='onExit-script' namespace='##targetNamespace'"
     * @generated
     */
    OnExitScriptType getOnExitScript();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getOnExitScript <em>On Exit Script</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>On Exit Script</em>' containment reference.
     * @see #getOnExitScript()
     * @generated
     */
    void setOnExitScript(OnExitScriptType value);

    /**
     * Returns the value of the '<em><b>Package Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Package Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Package Name</em>' attribute.
     * @see #setPackageName(String)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot_PackageName()
     * @model dataType="org.jbpm.bpmn2.emfextmodel.PackageNameType"
     *        extendedMetaData="kind='attribute' name='packageName' namespace='##targetNamespace'"
     * @generated
     */
    String getPackageName();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getPackageName <em>Package Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Package Name</em>' attribute.
     * @see #getPackageName()
     * @generated
     */
    void setPackageName(String value);

    /**
     * Returns the value of the '<em><b>Rule Flow Group</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Rule Flow Group</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Rule Flow Group</em>' attribute.
     * @see #setRuleFlowGroup(String)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot_RuleFlowGroup()
     * @model dataType="org.jbpm.bpmn2.emfextmodel.RuleFlowGroupType"
     *        extendedMetaData="kind='attribute' name='ruleFlowGroup' namespace='##targetNamespace'"
     * @generated
     */
    String getRuleFlowGroup();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getRuleFlowGroup <em>Rule Flow Group</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Rule Flow Group</em>' attribute.
     * @see #getRuleFlowGroup()
     * @generated
     */
    void setRuleFlowGroup(String value);

    /**
     * Returns the value of the '<em><b>Task Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Task Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Task Name</em>' attribute.
     * @see #setTaskName(String)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot_TaskName()
     * @model dataType="org.jbpm.bpmn2.emfextmodel.TaskNameType"
     *        extendedMetaData="kind='attribute' name='taskName' namespace='##targetNamespace'"
     * @generated
     */
    String getTaskName();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getTaskName <em>Task Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Task Name</em>' attribute.
     * @see #getTaskName()
     * @generated
     */
    void setTaskName(String value);

    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Version</em>' attribute.
     * @see #setVersion(BigInteger)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getDocumentRoot_Version()
     * @model dataType="org.jbpm.bpmn2.emfextmodel.VersionType"
     *        extendedMetaData="kind='attribute' name='version' namespace='##targetNamespace'"
     * @generated
     */
    BigInteger getVersion();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     * @generated
     */
    void setVersion(BigInteger value);

} // DocumentRoot
