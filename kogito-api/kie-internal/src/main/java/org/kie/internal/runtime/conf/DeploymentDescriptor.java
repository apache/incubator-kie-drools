/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.runtime.conf;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kie.api.remote.Remotable;

/**
 * Deployment descriptor defines configurable components of deployable unit - kjar
 * that will be used upon deployment to execution environment providing flexible and
 * independent setup per deployment unit
 *
 */
public interface DeploymentDescriptor {

    public static final String META_INF_LOCATION = "META-INF/kie-deployment-descriptor.xml";
    public static final String TYPE_ALL = "all";
    public static final String TYPE_VIEW = "view";
    public static final String TYPE_EXECUTE = "execute";

    /**
     * Returns name of the JPA persistence unit to be used for runtime engine
     * <ul>
     *  <li>non empty should correspond to defined persistence unit in persistence.xml</li>
     *  <li>null to use default persistence unit </li>
     * </ul>
     * @return non empty string should correspond to defined persistence unit in persistence.xml,
     *  in case null is returned persistence will use default one - org.jbpm.domain
     */
    String getPersistenceUnit();

    /**
     * Returns name of the JPA persistence unit to be used by audit component
     * <ul>
     *  <li>non empty should correspond to defined persistence unit in persistence.xml</li>
     *  <li>null to use default persistence unit </li>
     * </ul>
     * @return non empty string should correspond to defined persistence unit in persistence.xml,
     *  in case null is returned persistence will use default one - org.jbpm.domain
     */
    String getAuditPersistenceUnit();

    /**
     * Returns the audit type configuration
     * @return
     */
    AuditMode getAuditMode();

    /**
     * Returns the runtime engine persistence type configuration
     * @return
     */
    PersistenceMode getPersistenceMode();

    /**
     * Returns runtime strategy to be used, default Singleton.
     * @return
     */
    RuntimeStrategy getRuntimeStrategy();

    /**
     * Returns list of object marshaling strategies to be applied on <code>KieSession</code>
     * @return
     */
    List<ObjectModel> getMarshallingStrategies();

    /**
     * Returns list of event listeners (process, agenda, rule runtime) to be applied on <code>KieSession</code>
     * @return
     */
    List<ObjectModel> getEventListeners();

    /**
     * Return list of globals to be applied on <code>KieSession</code>
     * @return
     */
    List<NamedObjectModel> getGlobals();

    /**
     * Return list of work item handlers to be applied on <code>KieSession</code>
     * @return
     */
    List<NamedObjectModel> getWorkItemHandlers();

    /**
     * Returns list of task event listeners to be applied on <code>KieSession</code>
     * @return
     */
    List<ObjectModel> getTaskEventListeners();

    /**
     * List of (kie) environment entries to be registered
     * @return
     */
    List<NamedObjectModel> getEnvironmentEntries();

    /**
     * List of kiesession configuration entries to be registered
     * @return
     */
    List<NamedObjectModel> getConfiguration();

    /**
     * Returns implementation specific builder to construct instances of the descriptor;
     * @return
     */
    DeploymentDescriptorBuilder getBuilder();

    /**
     * Returns all required roles required to be granted access to the deployment.
     * Empty list or null means no security will be applied.
     * @return
     */
    List<String> getRequiredRoles();

    /**
     * Returns required for given type of applicability. Supported types are:
     * <ul>
     * <li>all</li>
     * <li>view</li>
     * <li>execute</li>
     * </ul>
     * @param type indicates the type which required roles shoud apply
     * @return
     */
    List<String> getRequiredRoles(String type);

    /**
     * Returns additional classes that shall be added to remote services context.
     * All classes that directly belong to kjar (or are declared as dependency of type kjar)
     * do not need to be listed as part of this collection.
     * @return
     */
    List<String> getClasses();

    /**
     * Returns whether or not the classes used in the serialization mechanism
     * are limited to classes annotated with @{@link Remotable}, @{@link XmlRootElement},
     * or @{@link XmlType}.
     * In the 6.x branches, the default is false (classes are <i>not</i> limited.)
     * In the 7.x branches, the default is true (classes <i>are</i> limited.)
     * @return a {@link Boolean}
     */
    Boolean getLimitSerializationClasses();

    /**
     * Returns XML representation of this descriptor instance
     * @return
     */
    String toXml();
}
