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
 * A representation of the model object '<em><b>Global Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.GlobalType#getIdentifier <em>Identifier</em>}</li>
 *   <li>{@link org.jbpm.bpmn2.emfextmodel.GlobalType#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getGlobalType()
 * @model extendedMetaData="name='global_._type' kind='empty'"
 * @generated
 */
public interface GlobalType extends EObject {
    /**
     * Returns the value of the '<em><b>Identifier</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Identifier</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Identifier</em>' attribute.
     * @see #setIdentifier(String)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getGlobalType_Identifier()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='identifier'"
     * @generated
     */
    String getIdentifier();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.GlobalType#getIdentifier <em>Identifier</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Identifier</em>' attribute.
     * @see #getIdentifier()
     * @generated
     */
    void setIdentifier(String value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(String)
     * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage#getGlobalType_Type()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='type'"
     * @generated
     */
    String getType();

    /**
     * Sets the value of the '{@link org.jbpm.bpmn2.emfextmodel.GlobalType#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(String value);

} // GlobalType
