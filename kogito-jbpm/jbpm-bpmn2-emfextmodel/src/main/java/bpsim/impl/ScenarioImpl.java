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
package bpsim.impl;

import bpsim.BpsimPackage;
import bpsim.Calendar;
import bpsim.ElementParameters;
import bpsim.ElementParametersType;
import bpsim.Scenario;
import bpsim.ScenarioParameters;
import bpsim.ScenarioParametersType;
import bpsim.VendorExtension;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Scenario</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link bpsim.impl.ScenarioImpl#getScenarioParameters <em>Scenario Parameters</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getElementParameters <em>Element Parameters</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getCalendar <em>Calendar</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getVendorExtension <em>Vendor Extension</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getCreated <em>Created</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getId <em>Id</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getInherits <em>Inherits</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getModified <em>Modified</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getName <em>Name</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getResult <em>Result</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getVendor <em>Vendor</em>}</li>
 *   <li>{@link bpsim.impl.ScenarioImpl#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ScenarioImpl extends EObjectImpl implements Scenario {
	/**
	 * The cached value of the '{@link #getScenarioParameters() <em>Scenario Parameters</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getScenarioParameters()
	 * @generated
	 * @ordered
	 */
	protected ScenarioParameters scenarioParameters;

	/**
	 * The cached value of the '{@link #getElementParameters() <em>Element Parameters</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getElementParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<ElementParameters> elementParameters;

	/**
	 * The cached value of the '{@link #getCalendar() <em>Calendar</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCalendar()
	 * @generated
	 * @ordered
	 */
	protected EList<Calendar> calendar;

	/**
	 * The cached value of the '{@link #getVendorExtension() <em>Vendor Extension</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVendorExtension()
	 * @generated
	 * @ordered
	 */
	protected EList<VendorExtension> vendorExtension;

	/**
	 * The default value of the '{@link #getAuthor() <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected static final String AUTHOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAuthor() <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected String author = AUTHOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getCreated() <em>Created</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreated()
	 * @generated
	 * @ordered
	 */
	protected static final Object CREATED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCreated() <em>Created</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCreated()
	 * @generated
	 * @ordered
	 */
	protected Object created = CREATED_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getInherits() <em>Inherits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInherits()
	 * @generated
	 * @ordered
	 */
	protected static final String INHERITS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInherits() <em>Inherits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInherits()
	 * @generated
	 * @ordered
	 */
	protected String inherits = INHERITS_EDEFAULT;

	/**
	 * The default value of the '{@link #getModified() <em>Modified</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModified()
	 * @generated
	 * @ordered
	 */
	protected static final Object MODIFIED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getModified() <em>Modified</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModified()
	 * @generated
	 * @ordered
	 */
	protected Object modified = MODIFIED_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getResult() <em>Result</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResult()
	 * @generated
	 * @ordered
	 */
	protected static final String RESULT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getResult() <em>Result</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResult()
	 * @generated
	 * @ordered
	 */
	protected String result = RESULT_EDEFAULT;

	/**
	 * The default value of the '{@link #getVendor() <em>Vendor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVendor()
	 * @generated
	 * @ordered
	 */
	protected static final String VENDOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getVendor() <em>Vendor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVendor()
	 * @generated
	 * @ordered
	 */
	protected String vendor = VENDOR_EDEFAULT;

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
	protected ScenarioImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BpsimPackage.Literals.SCENARIO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScenarioParameters getScenarioParameters() {
		return scenarioParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetScenarioParameters(ScenarioParameters newScenarioParameters, NotificationChain msgs) {
		ScenarioParameters oldScenarioParameters = scenarioParameters;
		scenarioParameters = newScenarioParameters;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__SCENARIO_PARAMETERS, oldScenarioParameters, newScenarioParameters);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setScenarioParameters(ScenarioParameters newScenarioParameters) {
		if (newScenarioParameters != scenarioParameters) {
			NotificationChain msgs = null;
			if (scenarioParameters != null)
				msgs = ((InternalEObject)scenarioParameters).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.SCENARIO__SCENARIO_PARAMETERS, null, msgs);
			if (newScenarioParameters != null)
				msgs = ((InternalEObject)newScenarioParameters).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BpsimPackage.SCENARIO__SCENARIO_PARAMETERS, null, msgs);
			msgs = basicSetScenarioParameters(newScenarioParameters, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__SCENARIO_PARAMETERS, newScenarioParameters, newScenarioParameters));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ElementParameters> getElementParameters() {
		if (elementParameters == null) {
			elementParameters = new EObjectContainmentEList<ElementParameters>(ElementParameters.class, this, BpsimPackage.SCENARIO__ELEMENT_PARAMETERS);
		}
		return elementParameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Calendar> getCalendar() {
		if (calendar == null) {
			calendar = new EObjectContainmentEList<Calendar>(Calendar.class, this, BpsimPackage.SCENARIO__CALENDAR);
		}
		return calendar;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<VendorExtension> getVendorExtension() {
		if (vendorExtension == null) {
			vendorExtension = new EObjectContainmentEList<VendorExtension>(VendorExtension.class, this, BpsimPackage.SCENARIO__VENDOR_EXTENSION);
		}
		return vendorExtension;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAuthor(String newAuthor) {
		String oldAuthor = author;
		author = newAuthor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__AUTHOR, oldAuthor, author));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getCreated() {
		return created;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCreated(Object newCreated) {
		Object oldCreated = created;
		created = newCreated;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__CREATED, oldCreated, created));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getInherits() {
		return inherits;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInherits(String newInherits) {
		String oldInherits = inherits;
		inherits = newInherits;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__INHERITS, oldInherits, inherits));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getModified() {
		return modified;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModified(Object newModified) {
		Object oldModified = modified;
		modified = newModified;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__MODIFIED, oldModified, modified));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getResult() {
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setResult(String newResult) {
		String oldResult = result;
		result = newResult;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__RESULT, oldResult, result));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVendor(String newVendor) {
		String oldVendor = vendor;
		vendor = newVendor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__VENDOR, oldVendor, vendor));
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
			eNotify(new ENotificationImpl(this, Notification.SET, BpsimPackage.SCENARIO__VERSION, oldVersion, version));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BpsimPackage.SCENARIO__SCENARIO_PARAMETERS:
				return basicSetScenarioParameters(null, msgs);
			case BpsimPackage.SCENARIO__ELEMENT_PARAMETERS:
				return ((InternalEList<?>)getElementParameters()).basicRemove(otherEnd, msgs);
			case BpsimPackage.SCENARIO__CALENDAR:
				return ((InternalEList<?>)getCalendar()).basicRemove(otherEnd, msgs);
			case BpsimPackage.SCENARIO__VENDOR_EXTENSION:
				return ((InternalEList<?>)getVendorExtension()).basicRemove(otherEnd, msgs);
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
			case BpsimPackage.SCENARIO__SCENARIO_PARAMETERS:
				return getScenarioParameters();
			case BpsimPackage.SCENARIO__ELEMENT_PARAMETERS:
				return getElementParameters();
			case BpsimPackage.SCENARIO__CALENDAR:
				return getCalendar();
			case BpsimPackage.SCENARIO__VENDOR_EXTENSION:
				return getVendorExtension();
			case BpsimPackage.SCENARIO__AUTHOR:
				return getAuthor();
			case BpsimPackage.SCENARIO__CREATED:
				return getCreated();
			case BpsimPackage.SCENARIO__DESCRIPTION:
				return getDescription();
			case BpsimPackage.SCENARIO__ID:
				return getId();
			case BpsimPackage.SCENARIO__INHERITS:
				return getInherits();
			case BpsimPackage.SCENARIO__MODIFIED:
				return getModified();
			case BpsimPackage.SCENARIO__NAME:
				return getName();
			case BpsimPackage.SCENARIO__RESULT:
				return getResult();
			case BpsimPackage.SCENARIO__VENDOR:
				return getVendor();
			case BpsimPackage.SCENARIO__VERSION:
				return getVersion();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case BpsimPackage.SCENARIO__SCENARIO_PARAMETERS:
				setScenarioParameters((ScenarioParameters)newValue);
				return;
			case BpsimPackage.SCENARIO__ELEMENT_PARAMETERS:
				getElementParameters().clear();
				getElementParameters().addAll((Collection<? extends ElementParameters>)newValue);
				return;
			case BpsimPackage.SCENARIO__CALENDAR:
				getCalendar().clear();
				getCalendar().addAll((Collection<? extends Calendar>)newValue);
				return;
			case BpsimPackage.SCENARIO__VENDOR_EXTENSION:
				getVendorExtension().clear();
				getVendorExtension().addAll((Collection<? extends VendorExtension>)newValue);
				return;
			case BpsimPackage.SCENARIO__AUTHOR:
				setAuthor((String)newValue);
				return;
			case BpsimPackage.SCENARIO__CREATED:
				setCreated(newValue);
				return;
			case BpsimPackage.SCENARIO__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case BpsimPackage.SCENARIO__ID:
				setId((String)newValue);
				return;
			case BpsimPackage.SCENARIO__INHERITS:
				setInherits((String)newValue);
				return;
			case BpsimPackage.SCENARIO__MODIFIED:
				setModified(newValue);
				return;
			case BpsimPackage.SCENARIO__NAME:
				setName((String)newValue);
				return;
			case BpsimPackage.SCENARIO__RESULT:
				setResult((String)newValue);
				return;
			case BpsimPackage.SCENARIO__VENDOR:
				setVendor((String)newValue);
				return;
			case BpsimPackage.SCENARIO__VERSION:
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
			case BpsimPackage.SCENARIO__SCENARIO_PARAMETERS:
				setScenarioParameters((ScenarioParameters)null);
				return;
			case BpsimPackage.SCENARIO__ELEMENT_PARAMETERS:
				getElementParameters().clear();
				return;
			case BpsimPackage.SCENARIO__CALENDAR:
				getCalendar().clear();
				return;
			case BpsimPackage.SCENARIO__VENDOR_EXTENSION:
				getVendorExtension().clear();
				return;
			case BpsimPackage.SCENARIO__AUTHOR:
				setAuthor(AUTHOR_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO__CREATED:
				setCreated(CREATED_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO__ID:
				setId(ID_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO__INHERITS:
				setInherits(INHERITS_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO__MODIFIED:
				setModified(MODIFIED_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO__NAME:
				setName(NAME_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO__RESULT:
				setResult(RESULT_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO__VENDOR:
				setVendor(VENDOR_EDEFAULT);
				return;
			case BpsimPackage.SCENARIO__VERSION:
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
			case BpsimPackage.SCENARIO__SCENARIO_PARAMETERS:
				return scenarioParameters != null;
			case BpsimPackage.SCENARIO__ELEMENT_PARAMETERS:
				return elementParameters != null && !elementParameters.isEmpty();
			case BpsimPackage.SCENARIO__CALENDAR:
				return calendar != null && !calendar.isEmpty();
			case BpsimPackage.SCENARIO__VENDOR_EXTENSION:
				return vendorExtension != null && !vendorExtension.isEmpty();
			case BpsimPackage.SCENARIO__AUTHOR:
				return AUTHOR_EDEFAULT == null ? author != null : !AUTHOR_EDEFAULT.equals(author);
			case BpsimPackage.SCENARIO__CREATED:
				return CREATED_EDEFAULT == null ? created != null : !CREATED_EDEFAULT.equals(created);
			case BpsimPackage.SCENARIO__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case BpsimPackage.SCENARIO__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
			case BpsimPackage.SCENARIO__INHERITS:
				return INHERITS_EDEFAULT == null ? inherits != null : !INHERITS_EDEFAULT.equals(inherits);
			case BpsimPackage.SCENARIO__MODIFIED:
				return MODIFIED_EDEFAULT == null ? modified != null : !MODIFIED_EDEFAULT.equals(modified);
			case BpsimPackage.SCENARIO__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case BpsimPackage.SCENARIO__RESULT:
				return RESULT_EDEFAULT == null ? result != null : !RESULT_EDEFAULT.equals(result);
			case BpsimPackage.SCENARIO__VENDOR:
				return VENDOR_EDEFAULT == null ? vendor != null : !VENDOR_EDEFAULT.equals(vendor);
			case BpsimPackage.SCENARIO__VERSION:
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
		result.append(" (author: ");
		result.append(author);
		result.append(", created: ");
		result.append(created);
		result.append(", description: ");
		result.append(description);
		result.append(", id: ");
		result.append(id);
		result.append(", inherits: ");
		result.append(inherits);
		result.append(", modified: ");
		result.append(modified);
		result.append(", name: ");
		result.append(name);
		result.append(", result: ");
		result.append(result);
		result.append(", vendor: ");
		result.append(vendor);
		result.append(", version: ");
		result.append(version);
		result.append(')');
		return result.toString();
	}

} //ScenarioImpl
