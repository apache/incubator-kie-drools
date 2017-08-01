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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element Parameters</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link bpsim.ElementParameters#getTimeParameters <em>Time Parameters</em>}</li>
 *   <li>{@link bpsim.ElementParameters#getControlParameters <em>Control Parameters</em>}</li>
 *   <li>{@link bpsim.ElementParameters#getResourceParameters <em>Resource Parameters</em>}</li>
 *   <li>{@link bpsim.ElementParameters#getPriorityParameters <em>Priority Parameters</em>}</li>
 *   <li>{@link bpsim.ElementParameters#getCostParameters <em>Cost Parameters</em>}</li>
 *   <li>{@link bpsim.ElementParameters#getPropertyParameters <em>Property Parameters</em>}</li>
 *   <li>{@link bpsim.ElementParameters#getVendorExtension <em>Vendor Extension</em>}</li>
 *   <li>{@link bpsim.ElementParameters#getElementRef <em>Element Ref</em>}</li>
 *   <li>{@link bpsim.ElementParameters#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see bpsim.BpsimPackage#getElementParameters()
 * @model extendedMetaData="name='ElementParameters' kind='elementOnly'"
 * @generated
 */
public interface ElementParameters extends EObject {
	/**
	 * Returns the value of the '<em><b>Time Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time Parameters</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time Parameters</em>' containment reference.
	 * @see #setTimeParameters(TimeParameters)
	 * @see bpsim.BpsimPackage#getElementParameters_TimeParameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='TimeParameters' namespace='##targetNamespace'"
	 * @generated
	 */
	TimeParameters getTimeParameters();

	/**
	 * Sets the value of the '{@link bpsim.ElementParameters#getTimeParameters <em>Time Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Time Parameters</em>' containment reference.
	 * @see #getTimeParameters()
	 * @generated
	 */
	void setTimeParameters(TimeParameters value);

	/**
	 * Returns the value of the '<em><b>Control Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Control Parameters</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Control Parameters</em>' containment reference.
	 * @see #setControlParameters(ControlParameters)
	 * @see bpsim.BpsimPackage#getElementParameters_ControlParameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ControlParameters' namespace='##targetNamespace'"
	 * @generated
	 */
	ControlParameters getControlParameters();

	/**
	 * Sets the value of the '{@link bpsim.ElementParameters#getControlParameters <em>Control Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Control Parameters</em>' containment reference.
	 * @see #getControlParameters()
	 * @generated
	 */
	void setControlParameters(ControlParameters value);

	/**
	 * Returns the value of the '<em><b>Resource Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resource Parameters</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resource Parameters</em>' containment reference.
	 * @see #setResourceParameters(ResourceParameters)
	 * @see bpsim.BpsimPackage#getElementParameters_ResourceParameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='ResourceParameters' namespace='##targetNamespace'"
	 * @generated
	 */
	ResourceParameters getResourceParameters();

	/**
	 * Sets the value of the '{@link bpsim.ElementParameters#getResourceParameters <em>Resource Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource Parameters</em>' containment reference.
	 * @see #getResourceParameters()
	 * @generated
	 */
	void setResourceParameters(ResourceParameters value);

	/**
	 * Returns the value of the '<em><b>Priority Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Priority Parameters</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Priority Parameters</em>' containment reference.
	 * @see #setPriorityParameters(PriorityParameters)
	 * @see bpsim.BpsimPackage#getElementParameters_PriorityParameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='PriorityParameters' namespace='##targetNamespace'"
	 * @generated
	 */
	PriorityParameters getPriorityParameters();

	/**
	 * Sets the value of the '{@link bpsim.ElementParameters#getPriorityParameters <em>Priority Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Priority Parameters</em>' containment reference.
	 * @see #getPriorityParameters()
	 * @generated
	 */
	void setPriorityParameters(PriorityParameters value);

	/**
	 * Returns the value of the '<em><b>Cost Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Cost Parameters</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cost Parameters</em>' containment reference.
	 * @see #setCostParameters(CostParameters)
	 * @see bpsim.BpsimPackage#getElementParameters_CostParameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='CostParameters' namespace='##targetNamespace'"
	 * @generated
	 */
	CostParameters getCostParameters();

	/**
	 * Sets the value of the '{@link bpsim.ElementParameters#getCostParameters <em>Cost Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cost Parameters</em>' containment reference.
	 * @see #getCostParameters()
	 * @generated
	 */
	void setCostParameters(CostParameters value);

	/**
	 * Returns the value of the '<em><b>Property Parameters</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Property Parameters</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Property Parameters</em>' containment reference.
	 * @see #setPropertyParameters(PropertyParameters)
	 * @see bpsim.BpsimPackage#getElementParameters_PropertyParameters()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='PropertyParameters' namespace='##targetNamespace'"
	 * @generated
	 */
	PropertyParameters getPropertyParameters();

	/**
	 * Sets the value of the '{@link bpsim.ElementParameters#getPropertyParameters <em>Property Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Property Parameters</em>' containment reference.
	 * @see #getPropertyParameters()
	 * @generated
	 */
	void setPropertyParameters(PropertyParameters value);

	/**
	 * Returns the value of the '<em><b>Vendor Extension</b></em>' containment reference list.
	 * The list contents are of type {@link bpsim.VendorExtension}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Vendor Extension</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vendor Extension</em>' containment reference list.
	 * @see bpsim.BpsimPackage#getElementParameters_VendorExtension()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='VendorExtension' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<VendorExtension> getVendorExtension();

	/**
	 * Returns the value of the '<em><b>Element Ref</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Element Ref</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Element Ref</em>' attribute.
	 * @see #setElementRef(String)
	 * @see bpsim.BpsimPackage#getElementParameters_ElementRef()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.ID"
	 *        extendedMetaData="kind='attribute' name='elementRef'"
	 * @generated
	 */
	String getElementRef();

	/**
	 * Sets the value of the '{@link bpsim.ElementParameters#getElementRef <em>Element Ref</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Element Ref</em>' attribute.
	 * @see #getElementRef()
	 * @generated
	 */
	void setElementRef(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see bpsim.BpsimPackage#getElementParameters_Id()
	 * @model id="true" dataType="org.eclipse.emf.ecore.xml.type.ID"
	 *        extendedMetaData="kind='attribute' name='id'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link bpsim.ElementParameters#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // ElementParameters
