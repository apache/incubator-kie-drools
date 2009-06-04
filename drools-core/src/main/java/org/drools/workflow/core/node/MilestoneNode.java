package org.drools.workflow.core.node;

import org.drools.workflow.core.Constraint;

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
 * Default implementation of a milestone node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class MilestoneNode extends EventBasedNode implements Constrainable {

	private static final long serialVersionUID = 8552568488755348247L;

	private String            constraint;

    public void addConstraint(String name, Constraint constraint) {
        this.constraint = constraint.getConstraint();
    }
    public void setConstraint(String constraint){
        this.constraint = constraint;
    }

    public String getConstraint(){
        return this.constraint;
    }
    
}
