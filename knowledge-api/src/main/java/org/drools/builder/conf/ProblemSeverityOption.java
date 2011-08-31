/*
* Copyright 2011 JBoss Inc
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
package org.drools.builder.conf;

import org.drools.builder.ProblemSeverity;


/**
 *
 */
public class ProblemSeverityOption implements MultiValueKnowledgeBuilderOption {

    /**
     * 
     */
    private static final long serialVersionUID = 1492178699571897026L;
    public static String PROPERTY_NAME = "drools.problem.severity.";
    private final String key;
    private final ProblemSeverity severity;
    
    private ProblemSeverityOption(String key, ProblemSeverity severity) {
        this.key = key;
        this.severity = severity;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((severity == null) ? 0 : severity.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProblemSeverityOption other = (ProblemSeverityOption) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (severity != other.severity)
            return false;
        return true;
    }

    public String getPropertyName() {
        return PROPERTY_NAME + key;
    }
    
    public static ProblemSeverityOption get(String key, ProblemSeverity severity) {
        return new ProblemSeverityOption(key, severity);
    }
    
    public static ProblemSeverityOption get(String key, String severityString) {
        ProblemSeverity sev;
        try {
            sev = ProblemSeverity.valueOf(severityString.trim().toUpperCase());
        } catch (IllegalArgumentException iae) {
            sev = ProblemSeverity.INFO;
        }
        return new ProblemSeverityOption(key, sev);
    }
    
    public String getName() {
        return key;
    }
    
    public ProblemSeverity getSeverity() {
        return severity;
    }
    
    @Override
    public String toString() {
        return "ProblemSeverityOption ( name= " + key + " severity=" + severity + ")";
    }

}
