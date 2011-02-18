/*
 * Copyright 2006 JBoss Inc
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
 *
 * Created on May 22, 2007
 */
package org.drools.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to hold contextual information during DRL parsing
 * 
 * @author etirelli, krisv
 */
public class Location {
    
    public static final int LOCATION_UNKNOWN = 0;
    
    public static final int LOCATION_LHS_BEGIN_OF_CONDITION = 1;
    public static final int LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS = 2;
    public static final int LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR = 3;
    public static final int LOCATION_LHS_BEGIN_OF_CONDITION_NOT = 4;
    
    public static final int LOCATION_LHS_INSIDE_CONDITION_START = 100;
    public static final int LOCATION_LHS_INSIDE_CONDITION_OPERATOR = 101;
    public static final int LOCATION_LHS_INSIDE_CONDITION_ARGUMENT = 102;
    public static final int LOCATION_LHS_INSIDE_CONDITION_END = 103;

    public static final int LOCATION_LHS_INSIDE_EVAL = 200;
    
    public static final int LOCATION_LHS_FROM = 300;
    public static final int LOCATION_LHS_FROM_COLLECT = 301;
    public static final int LOCATION_LHS_FROM_ACCUMULATE = 302;
    public static final int LOCATION_LHS_FROM_ACCUMULATE_INIT = 303;
    public static final int LOCATION_LHS_FROM_ACCUMULATE_INIT_INSIDE = 304;
    public static final int LOCATION_LHS_FROM_ACCUMULATE_ACTION = 305;
    public static final int LOCATION_LHS_FROM_ACCUMULATE_ACTION_INSIDE = 306;
    public static final int LOCATION_LHS_FROM_ACCUMULATE_REVERSE = 307;
    public static final int LOCATION_LHS_FROM_ACCUMULATE_REVERSE_INSIDE = 308;
    public static final int LOCATION_LHS_FROM_ACCUMULATE_RESULT = 309;
    public static final int LOCATION_LHS_FROM_ACCUMULATE_RESULT_INSIDE = 310;
    public static final int LOCATION_LHS_FROM_ENTRY_POINT = 311;
    
    public static final int LOCATION_RHS = 1000;
    public static final int LOCATION_RULE_HEADER = 2000;
    public static final int LOCATION_RULE_HEADER_KEYWORD = 2001;
    
    public static final String LOCATION_PROPERTY_CLASS_NAME = "ClassName";
    public static final String LOCATION_PROPERTY_PROPERTY_NAME = "PropertyName";
    public static final String LOCATION_PROPERTY_OPERATOR = "Operator";
    public static final String LOCATION_EVAL_CONTENT = "EvalContent";
    public static final String LOCATION_FROM_CONTENT = "FromContent";
    public static final String LOCATION_PROPERTY_FROM_ACCUMULATE_INIT_CONTENT = "FromAccumulateInitContent";
    public static final String LOCATION_PROPERTY_FROM_ACCUMULATE_ACTION_CONTENT = "FromAccumulateActionContent";
    public static final String LOCATION_PROPERTY_FROM_ACCUMULATE_REVERSE_CONTENT = "FromAccumulateReverseContent";
    public static final String LOCATION_PROPERTY_FROM_ACCUMULATE_RESULT_CONTENT = "FromAccumulateResultContent";
    public static final String LOCATION_PROPERTY_FROM_ACCUMULATE_EXPRESSION_CONTENT = "FromAccumulateExpressionContent";
    public static final String LOCATION_LHS_CONTENT = "LHSContent";
    public static final String LOCATION_RHS_CONTENT = "RHSContent";
    public static final String LOCATION_HEADER_CONTENT = "HeaderContent";
    
    private int type;
    private Map properties = new HashMap();

    public Location(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public void setType(int type) {
        this.type = type;
    }
}
