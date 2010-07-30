/**
 * Copyright 2010 JBoss Inc
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

package org.drools;

import java.util.Collection;
import java.util.Map;

import org.drools.io.Resource;

/**
 * <p>
 * This class is used to provide a ChangeSet info to a ResourceChangeNotifier. It should be used when you implement the ResourceChangeMonitor interface.
 * Each method provides a Collection of removed, added and modified resources, and determined by the monitor. Drools currently only provides the
 * ResourceChangeScanner, which scans the local disk for changes. The scanning only works with the ChangeSet is applied to a KnowledgeAgent, and not a KnowledgeBase. 
 * </p>
 * 
 * <p>
 * The xml format has a root level <change-set> element and then it can contain &lt;add&gt;, &lt;modified&gt;, &lt;removed&gt; elements - each one can only be used once.
 * add, modified, removed then contain a list of &lt;resource&gt; elements. Resources may take a configuration, currently only decision table resources use that.
 * </p>
 *
 * <p>KnowledgeBuilder currently ignores Added/Modified xml elements, the KnowledgeAgent will use them, when rebuilding the KnowledgeBase.</p>
 * 
 * <pre>
 * &lt;change-set xmlns='http://drools.org/drools-5.0/change-set'
 *             xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'
 *             xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' &gt;
 *  &lt;add&gt;
 *       &lt;resource source='http:org/domain/myrules.drl' type='DRL' /&gt;
 *       &lt;resource source='classpath:data/IntegrationExampleTest.xls' type="DTABLE"&gt;
 *           &lt;decisiontable-conf input-type="XLS" worksheet-name="Tables_2" /&gt;
 *       &lt;/resource&gt;
 *       &lt;resource source='file:org/drools/decisiontable/myflow.drf' type='DRF' /&gt;
 *   &lt;/add&gt;
 * &lt;/change-set&gt;
 * </pre>
 * 
 * <p>Notice that each resource defines a protocol for it's source, this is because when using a ChangeSet all resource elements are turned into a URL instance, so it
 * obeys the format as provided by the JDK URL class. There is one exception which is the classpath protocol, which is handled separately. Here the resource is loaded
 * from the classpath, where it uses the default ClassLoader of the KnowledgeBase.<p>
 * 
 * <p>A path, when using file based protocols, can point to a folder. In such a situation all the resources in that folder will be added. When used with a KnowledgeAgent,
 * which is also set to scan directories, it will continue to scan the directory for new or removed resources.</p>
 * 
 * <p>
 * The ChangeSet can be used as a ResourceType with the KnowledgeBuilder or applied directly to a KnowledgeAgent.
 * </p>
 * 
 * <p>Example showing a changeset being used with a KnowledgeBuilder:</p>
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newUrlResource( url ),
 *               ResourceType.ChangeSet );
 * </pre>
 * 
 * <p>Example showing a changeset being applied to a KnowledgeAgent:</p>
 * <pre>
 * KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "MyAgent" );
 * kagent.applyChangeSet( ResourceFactory.newUrlResource( url ) );
 * KnowledgeBase kbase = kagent.getKnowledgeBase();
 * </pre>
 * 
 * <p>
 * This interface, as well as ResourceChangeMonitor, ResourceChangeNotifier, ResourceChangeScanner and ResourceChangeListener are still considered subject to change. 
 * Use the XML format change-set, as part of the ResourceType api when adding to KnowledgeBuilder, which is considered stable.
 * </p>
 */
public interface ChangeSet {
    /**
     * Returns an immutable Collection of removed Resources for this ChangeSet
     * @return
     */
    public Collection<Resource> getResourcesRemoved();

    /**
     * Returns an immutable Collection of added Resources for this ChangeSet
     * @return
     */
    public Collection<Resource> getResourcesAdded();

    /**
     * Returns an immutable Collection of modified Resources for this ChangeSet
     * @return
     */
    public Collection<Resource> getResourcesModified();

    /**
     * Returns a collection containing the full names (package+name) of the kdefinitions to be removed.
     * @return
     */
    Collection<String> getKnowledgeDefinitionsRemoved();


}
