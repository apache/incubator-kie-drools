/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.management;

import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.StandardMBean;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.kie.api.management.KieBaseConfigurationMonitorMBean;
import org.kie.api.management.ObjectTypeNodeMonitorMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation for the KnowledgeBaseMBean
 */
public class KnowledgeBaseMonitoring
    implements
    DynamicMBean {

    protected static final transient Logger logger = LoggerFactory.getLogger(KnowledgeBaseMonitoring.class);

    private static final String ATTR_PACKAGES      = "Packages";
    private static final String ATTR_GLOBALS       = "Globals";
    private static final String ATTR_SESSION_COUNT = "SessionCount";
    private static final String ATTR_ID            = "Id";

    private static final String OP_STOP_INTERNAL_MBEANS  = "stopInternalMBeans";
    private static final String OP_START_INTERNAL_MBEANS = "startInternalMBeans";

    private static final String KBASE_PREFIX = "org.drools.kbases";

    // ************************************************************************************************
    // MBean attributes
    //
    private InternalKnowledgeBase kbase;
    private ObjectName     name;

    private OpenMBeanInfoSupport info;

    // ************************************************************************************************
    // Define and instantiate all info related to the globals table
    //
    private static String[]   globalsColNames = {"name", "class"};
    private static String[]   globalsColDescr = {"Global identifier", "Fully qualified class name"};
    private static OpenType[] globalsColTypes = {SimpleType.STRING, SimpleType.STRING};
    private static CompositeType globalsType;
    private static String[] index = {"name"};
    private static TabularType globalsTableType;

    static {
        try {
            globalsType = new CompositeType("globalsType",
                                            "Globals row type",
                                            globalsColNames,
                                            globalsColDescr,
                                            globalsColTypes);
            globalsTableType = new TabularType("globalsTableType",
                                               "List of globals",
                                               globalsType,
                                               index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ************************************************************************************************

    // Constructor
    public KnowledgeBaseMonitoring(InternalKnowledgeBase kbase) {
        this.kbase = kbase;
        this.name = DroolsManagementAgent.createObjectName(KBASE_PREFIX + ":type=" + kbase.getId());

        initOpenMBeanInfo();
    }

    /**
     *  Initialize the open mbean metadata
     */
    private void initOpenMBeanInfo() {
        OpenMBeanAttributeInfoSupport[] attributes = new OpenMBeanAttributeInfoSupport[4];
        OpenMBeanConstructorInfoSupport[] constructors = new OpenMBeanConstructorInfoSupport[1];
        OpenMBeanOperationInfoSupport[] operations = new OpenMBeanOperationInfoSupport[2];
        MBeanNotificationInfo[] notifications = new MBeanNotificationInfo[0];

        try {
            // Define the attributes 
            attributes[0] = new OpenMBeanAttributeInfoSupport(ATTR_ID,
                                                              "Knowledge Base Id",
                                                              SimpleType.STRING,
                                                              true,
                                                              false,
                                                              false);
            attributes[1] = new OpenMBeanAttributeInfoSupport(ATTR_SESSION_COUNT,
                                                              "Number of created sessions for this Knowledge Base",
                                                              SimpleType.LONG,
                                                              true,
                                                              false,
                                                              false);
            attributes[2] = new OpenMBeanAttributeInfoSupport(ATTR_GLOBALS,
                                                              "List of globals",
                                                               globalsTableType,
                                                               true,
                                                               false,
                                                               false );
            attributes[3] = new OpenMBeanAttributeInfoSupport( ATTR_PACKAGES,
                                                               "List of Packages",
                                                               new ArrayType( 1,
                                                                              SimpleType.STRING ),
                                                               true,
                                                               false,
                                                               false );
            //No arg constructor                
            constructors[0] = new OpenMBeanConstructorInfoSupport( "KnowledgeBaseMonitoringMXBean",
                                                                   "Constructs a KnowledgeBaseMonitoringMXBean instance.",
                                                                   new OpenMBeanParameterInfoSupport[0] );
            //Operations 
            OpenMBeanParameterInfo[] params = new OpenMBeanParameterInfoSupport[0];
            operations[0] = new OpenMBeanOperationInfoSupport( OP_START_INTERNAL_MBEANS,
                                                               "Creates, registers and starts all the dependent MBeans that allow monitor all the details in this KnowledgeBase.",
                                                               params,
                                                               SimpleType.VOID,
                                                               MBeanOperationInfo.INFO );
            operations[1] = new OpenMBeanOperationInfoSupport( OP_STOP_INTERNAL_MBEANS,
                                                               "Stops and disposes all the dependent MBeans that allow monitor all the details in this KnowledgeBase.",
                                                               params,
                                                               SimpleType.VOID,
                                                               MBeanOperationInfo.INFO );

            //Build the info 
            info = new OpenMBeanInfoSupport( this.getClass().getName(),
                                             "Knowledge Base Monitor MXBean",
                                             attributes,
                                             constructors,
                                             operations,
                                             notifications );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public ObjectName getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeBaseMBean#getGlobals()
     */
    @SuppressWarnings("unchecked")
    public TabularData getGlobals() throws OpenDataException {
        TabularDataSupport globalsTable = new TabularDataSupport( globalsTableType );
        for ( Map.Entry<String, Class< ? >> global : ((Map<String, Class< ? >>) kbase.getGlobals()).entrySet() ) {
            Object[] itemValues = {global.getKey(), global.getValue().getName()};
            CompositeData result = new CompositeDataSupport( globalsType,
                                                             globalsColNames,
                                                             itemValues );
            globalsTable.put( result );
        }
        return globalsTable;
    }

    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeBaseMBean#getId()
     */
    public String getId() {
        return kbase.getId();
    }

    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeBaseMBean#getPackages()
     */
    public String[] getPackages() {
        return kbase.getPackagesMap().keySet().toArray( new String[0] );
    }

    /* (non-Javadoc)
     * @see org.drools.core.management.KnowledgeBaseMBean#getSessionCount()
     */
    public long getSessionCount() {
        return kbase.getWorkingMemoryCounter();
    }

    public void startInternalMBeans() {
        for ( EntryPointNode epn : kbase.getRete().getEntryPointNodes().values() ) {
            for ( ObjectTypeNode otn : epn.getObjectTypeNodes().values() ) {
                ObjectTypeNodeMonitor otnm = new ObjectTypeNodeMonitor( otn );
                try {
                    final StandardMBean adapter = new StandardMBean(otnm, ObjectTypeNodeMonitorMBean.class);
                    ObjectName name = DroolsManagementAgent.createObjectName( this.name.getCanonicalName() + ",group=EntryPoints,EntryPoint=" + otnm.getNameSufix() + ",ObjectType=" + ((ClassObjectType) otn.getObjectType()).getClassName() );
                    DroolsManagementAgent.getInstance().registerMBean( kbase,
                                                                       adapter,
                                                                       name );
                } catch ( NotCompliantMBeanException e ) {
                    logger.error( "Unable to register ObjectTypeNodeMonitor mbean for OTN "+otn.getObjectType()+" into the platform MBean Server", e);
                }
            }
        }
        final KieBaseConfigurationMonitor kbcm = new KieBaseConfigurationMonitor( kbase.getConfiguration() );
        try {
            final StandardMBean adapter = new StandardMBean(kbcm, KieBaseConfigurationMonitorMBean.class);
            ObjectName name = DroolsManagementAgent.createObjectName( this.name.getCanonicalName() + ",group=Configuration" );
            DroolsManagementAgent.getInstance().registerMBean( kbase,
                                                               adapter,
                                                               name );
        } catch ( NotCompliantMBeanException e ) {
            logger.error( "Unable to register KBaseConfigurationMonitor mbean into the platform MBean Server", e);
        }
    }

    public void stopInternalMBeans() {
        DroolsManagementAgent.getInstance().unregisterDependentsMBeansFromOwner( kbase );
    }

    public Object getAttribute(String attributeName) throws AttributeNotFoundException,
                                                    MBeanException,
                                                    ReflectionException {
        if ( attributeName == null ) {
            throw new RuntimeOperationsException( new IllegalArgumentException( "attributeName cannot be null" ),
                                                  "Cannot invoke a getter of " + getClass().getName() );
        } else if ( attributeName.equals( ATTR_ID ) ) {
            return getId();
        } else if ( attributeName.equals( ATTR_SESSION_COUNT ) ) {
            return Long.valueOf( getSessionCount() );
        } else if ( attributeName.equals( ATTR_GLOBALS ) ) {
            try {
                return getGlobals();
            } catch ( OpenDataException e ) {
                throw new RuntimeOperationsException( new RuntimeException( "Error retrieving globals list",
                                                                            e ),
                                                      "Error retrieving globals list " + e.getMessage() );
            }
        } else if ( attributeName.equals( ATTR_PACKAGES ) ) {
            return getPackages();
        }
        throw new AttributeNotFoundException( "Cannot find " + attributeName + " attribute " );
    }

    public AttributeList getAttributes(String[] attributeNames) {
        AttributeList resultList = new AttributeList();
        if ( attributeNames.length == 0 ) return resultList;
        for ( int i = 0; i < attributeNames.length; i++ ) {
            try {
                Object value = getAttribute( attributeNames[i] );
                resultList.add( new Attribute( attributeNames[i],
                                               value ) );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return (resultList);
    }

    public MBeanInfo getMBeanInfo() {
        return info;
    }

    public Object invoke(String operationName,
                         Object[] params,
                         String[] signature) throws MBeanException,
                                            ReflectionException {
        if ( operationName.equals( OP_START_INTERNAL_MBEANS ) ) {
             startInternalMBeans();
        } else if ( operationName.equals( OP_STOP_INTERNAL_MBEANS ) ) {
            stopInternalMBeans();
        } else {
            throw new ReflectionException( new NoSuchMethodException( operationName ),
                                           "Cannot find the operation " + operationName );
        }
        return null;
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
                                                 InvalidAttributeValueException,
                                                 MBeanException,
                                                 ReflectionException {
        throw new AttributeNotFoundException( "No attribute can be set in this MBean" );
    }

    public AttributeList setAttributes(AttributeList attributes) {
        return new AttributeList();
    }

}
