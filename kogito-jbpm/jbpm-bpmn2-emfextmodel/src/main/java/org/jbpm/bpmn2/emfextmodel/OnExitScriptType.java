/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jbpm.bpmn2.emfextmodel;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>On Exit Script Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.OnExitScriptType#getScript <em>Script</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.OnExitScriptType#getScriptFormat <em>Script Format</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getOnExitScriptType()
 * @model extendedMetaData="name='onExit-script_._type' kind='elementOnly'"
 * @generated
 */
public interface OnExitScriptType extends EObject {
    /**
     * Returns the value of the '<em><b>Script</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Script</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Script</em>' attribute.
     * @see #setScript(String)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getOnExitScriptType_Script()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='element' name='script' namespace='##targetNamespace'"
     * @generated
     */
    String getScript();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.OnExitScriptType#getScript <em>Script</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Script</em>' attribute.
     * @see #getScript()
     * @generated
     */
    void setScript(String value);

    /**
     * Returns the value of the '<em><b>Script Format</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Script Format</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Script Format</em>' attribute.
     * @see #setScriptFormat(String)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getOnExitScriptType_ScriptFormat()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='scriptFormat'"
     * @generated
     */
    String getScriptFormat();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.OnExitScriptType#getScriptFormat <em>Script Format</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Script Format</em>' attribute.
     * @see #getScriptFormat()
     * @generated
     */
    void setScriptFormat(String value);

} // OnExitScriptType
