/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.datamodel.rule;

import java.util.HashMap;
import java.util.Map;

/**
 * Definition of a CEP Window
 */
public class CEPWindow
        implements HasParameterizedOperator {

    protected String operator;
    protected Map<String, String> parameters;

    public CEPWindow() {

    }

    public void setOperator( String operator ) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public void clearParameters() {
        this.parameters = null;
    }

    public String getParameter( String key ) {
        if ( parameters == null ) {
            return null;
        }
        String parameter = parameters.get( key );
        return parameter;
    }

    public void setParameter( String key,
                              String parameter ) {
        if ( parameters == null ) {
            parameters = new HashMap<String, String>();
        }
        parameters.put( key,
                        parameter );
    }

    public void deleteParameter( String key ) {
        if ( this.parameters == null ) {
            return;
        }
        parameters.remove( key );
    }

    public Map<String, String> getParameters() {
        if ( this.parameters == null ) {
            this.parameters = new HashMap<String, String>();
        }
        return this.parameters;
    }

    public void setParameters( Map<String, String> parameters ) {
        this.parameters = parameters;
    }

    public boolean isDefined() {
        if ( parameters == null ) {
            return false;
        }
        return this.parameters.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CEPWindow cepWindow = (CEPWindow) o;

        if (operator != null ? !operator.equals(cepWindow.operator) : cepWindow.operator != null) return false;
        if (parameters != null ? !parameters.equals(cepWindow.parameters) : cepWindow.parameters != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = operator != null ? operator.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
