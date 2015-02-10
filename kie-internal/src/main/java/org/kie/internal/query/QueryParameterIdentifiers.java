package org.kie.internal.query;

import java.util.concurrent.atomic.AtomicInteger;

public interface QueryParameterIdentifiers {

    // meta identifiers
    
    public final static String FIRST_RESULT = "firstResult";
    public final static String MAX_RESULTS = "maxResults";
    public final static String FLUSH_MODE = "flushMode";
    
    public static final String PAGE_NUMBER = "page number";
    public static final String PAGE_SIZE = "page size";
    
    public static final String FILTER = "filter";
    public static final String ORDER_BY = "orderby";
    public static final String ORDER_TYPE = "orderType";
    public static final String ASCENDING_VALUE = "ASC";
    public static final String DESCENDING_VALUE = "DESC";

    // general identifiers
   
    static AtomicInteger idGen = new AtomicInteger(1);
    
    public static final String PROCESS_INSTANCE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String WORK_ITEM_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String DEPLOYMENT_ID_LIST = String.valueOf(idGen.getAndIncrement());
    
    // audit identifiers

    // - (process instance log) 
    public static final String START_DATE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String DURATION_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String END_DATE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String IDENTITY_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_NAME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_VERSION_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_INSTANCE_STATUS_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String OUTCOME_LIST = String.valueOf(idGen.getAndIncrement());
    
    // - (node instance log) 
    public static final String NODE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String NODE_INSTANCE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String NODE_NAME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String NODE_TYPE_LIST = String.valueOf(idGen.getAndIncrement());
    
    // - (variable instance log) 
    public static final String VARIABLE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String VARIABLE_INSTANCE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String DATE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String VAR_VALUE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String VALUE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String OLD_VALUE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String EXTERNAL_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String LAST_VARIABLE_LIST = String.valueOf(idGen.getAndIncrement());
   
    public static final String VAR_VAL_SEPARATOR = ":";
    
    // task identifiers
    
    public static final String TASK_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String BUSINESS_ADMIN_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String POTENTIAL_OWNER_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String STAKEHOLDER_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String ACTUAL_OWNER_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String CREATED_BY_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_STATUS_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String LANGUAGE = String.valueOf(idGen.getAndIncrement());
    public static final String DEPLOYMENT_ID = String.valueOf(idGen.getAndIncrement());
    public static final String CREATED_ON_ID = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_EVENT_DATE_ID = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_NAME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_DESCRIPTION_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_AUDIT_STATUS_LIST = String.valueOf(idGen.getAndIncrement());
    
    // executor identifiers
    public static final String EXECUTOR_STATUS_ID = String.valueOf(idGen.getAndIncrement());
    public static final String EXECUTOR_TIME_ID = String.valueOf(idGen.getAndIncrement());
    
}
