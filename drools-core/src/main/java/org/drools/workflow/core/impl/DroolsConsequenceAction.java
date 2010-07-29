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

package org.drools.workflow.core.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.drools.workflow.core.DroolsAction;

public class DroolsConsequenceAction extends DroolsAction implements Serializable {
	
    private static final long serialVersionUID = 510l;
    
    private String dialect = "mvel";
    private String consequence;
    
    public DroolsConsequenceAction() {
    }
	
	public DroolsConsequenceAction(String dialect, String consequence) {
	    this.dialect = dialect;
		this.consequence = consequence;
	}
	
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( dialect );
        out.writeObject( consequence );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
    ClassNotFoundException {
        super.readExternal( in );
        dialect = (String) in.readObject();
        consequence = (String) in.readObject();
    }    
  
	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}
	
	public String getConsequence() {
		return consequence;
	}
	
	public void setDialect(String dialect) {
	    this.dialect = dialect;
	}
	
	public String getDialect() {
	    return dialect;
	}

	public String toString() {
		return consequence;
	}
}
