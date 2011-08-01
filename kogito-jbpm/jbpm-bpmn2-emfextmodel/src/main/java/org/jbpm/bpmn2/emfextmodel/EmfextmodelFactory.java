/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jbpm.bpmn2.emfextmodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage
 * @generated
 */
public interface EmfextmodelFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EmfextmodelFactory eINSTANCE = org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Document Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Document Root</em>'.
     * @generated
     */
    DocumentRoot createDocumentRoot();

    /**
     * Returns a new object of class '<em>On Entry Script Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>On Entry Script Type</em>'.
     * @generated
     */
    OnEntryScriptType createOnEntryScriptType();

    /**
     * Returns a new object of class '<em>On Exit Script Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>On Exit Script Type</em>'.
     * @generated
     */
    OnExitScriptType createOnExitScriptType();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    EmfextmodelPackage getEmfextmodelPackage();

} //EmfextmodelFactory
