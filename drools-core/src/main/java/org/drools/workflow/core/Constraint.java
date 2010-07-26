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

package org.drools.workflow.core;


/*
 * Copyright 2005 JBoss Inc
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

/**
 * Represents a constraint in a RuleFlow.
 * Can be used to specify conditions in (X)OR-splits. 
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface Constraint {
    /**
     * Typically this method returns the constraint as a String
     * @return the constraint
     */
    String getConstraint();

    /**
     * Method for setting the constraint
     * @param constraint 	the constraint
     */
    void setConstraint(String constraint);

    /**
     * Returns the name of the constraint
     * @return the name of the constraint
     */
    String getName();

    /**
     * Sets the name of the constraint
     * @param name	the name of the constraint
     */
    void setName(String name);

    /**
     * Returns the priority of the constraint
     * 
     * @return the priority of the constraint
     */
    int getPriority();

    /**
     * Method for setting the priority of the constraint
     * 
     * @param priority	the priority of the constraint
     */
    void setPriority(int priority);

    /**
     * Returns the type of the constraint,
     * e.g. "code" or "rule"
     * 
     * @return the type of the constraint
     */
    String getType();

    /**
     * Method for setting the type of the constraint,
     * e.g. "code" or "rule"
     * 
     * @param type  the type of the constraint
     */
    void setType(String type);

    /**
     * Returns the dialect of the constraint,
     * e.g. "mvel" or "java"
     * 
     * @return the dialect of the constraint
     */
    String getDialect();

    /**
     * Method for setting the dialect of the constraint,
     * e.g. "mvel" or "java"
     * 
     * @param type  the dialect of the constraint
     */
    void setDialect(String dialect);
    
    public boolean isDefault();

	public void setDefault(boolean isDefault);
	
    void setMetaData(String name, Object value);
    
    Object getMetaData(String name);

}
