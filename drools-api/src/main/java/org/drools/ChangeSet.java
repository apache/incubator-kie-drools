package org.drools;

import java.util.Collection;

import org.drools.io.Resource;

/**
 * This class is used to provide a ChangeSet info to a ResourceChangeNotifier. It should be used when you implement the ResourceChangeMonitor interface.
 * Each method provides a Collection of removed, added and modified resources, and determined by the monitor. Drools currently only provides the
 * ResourceChangeScanner, which scans the local disk for changes. 
 * 
 * This interface, as well as ResourceChangeMonitor, ResourceChangeNotifier, ResourceChangeScanner and ResourceChangeListener are still considered subject to change. 
 * Use the XML format change-set, as
 * part of the ResourceType api when adding to KnowledgeBuilder, which is considered stable. KnowledgeBuilder currently ignored Added/Modified xml elements,
 * the KnowledgeAgent will use them, when rebuilding the KnowledgeBase.
 * 
 * the xml format has a root level <change-set> element and then it can contain <add>, <modified>, <removed> elements - each one can only be used once.
 * add, modified, removed then contain a list of <resource> elements. Resources may take a configuration, currently only decision table resources use that.
 * <pre>
 * &lt;change-set xmlns='http://drools.org/drools-5.0/change-set'
 *             xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'
 *             xs:schemaLocation='http://drools.org/drools-5.0/change-set drools-change-set-5.0.xsd' &gt;
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
 * 
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

}
