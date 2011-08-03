/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.jbpm.bpmn2.emfextmodel.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.jbpm.bpmn2.emfextmodel.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage
 * @generated
 */
public class EmfextmodelAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static EmfextmodelPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EmfextmodelAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = EmfextmodelPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EmfextmodelSwitch<Adapter> modelSwitch =
        new EmfextmodelSwitch<Adapter>() {
            @Override
            public Adapter caseDocumentRoot(DocumentRoot object) {
                return createDocumentRootAdapter();
            }
            @Override
            public Adapter caseGlobalType(GlobalType object) {
                return createGlobalTypeAdapter();
            }
            @Override
            public Adapter caseImportType(ImportType object) {
                return createImportTypeAdapter();
            }
            @Override
            public Adapter caseOnEntryScriptType(OnEntryScriptType object) {
                return createOnEntryScriptTypeAdapter();
            }
            @Override
            public Adapter caseOnExitScriptType(OnExitScriptType object) {
                return createOnExitScriptTypeAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link org.jbpm.bpmn2.emfextmodel.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.jbpm.bpmn2.emfextmodel.DocumentRoot
     * @generated
     */
    public Adapter createDocumentRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.jbpm.bpmn2.emfextmodel.GlobalType <em>Global Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.jbpm.bpmn2.emfextmodel.GlobalType
     * @generated
     */
    public Adapter createGlobalTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.jbpm.bpmn2.emfextmodel.ImportType <em>Import Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.jbpm.bpmn2.emfextmodel.ImportType
     * @generated
     */
    public Adapter createImportTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.jbpm.bpmn2.emfextmodel.OnEntryScriptType <em>On Entry Script Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.jbpm.bpmn2.emfextmodel.OnEntryScriptType
     * @generated
     */
    public Adapter createOnEntryScriptTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.jbpm.bpmn2.emfextmodel.OnExitScriptType <em>On Exit Script Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.jbpm.bpmn2.emfextmodel.OnExitScriptType
     * @generated
     */
    public Adapter createOnExitScriptTypeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //EmfextmodelAdapterFactory
