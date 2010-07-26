/**
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

package org.drools;

import org.drools.definition.process.Process;

/**
 * Indicates an error integrating a <code>Process</code> or <code>Package</code>
 * into a <code>RuleBase</code>.
 * 
 * @see RuleBase#addProcess
 * @see RuleBase#addPackage
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 * 
 * @version $Id: RuleIntegrationException.java,v 1.6 2004/09/17 00:14:06
 *          mproctor Exp $
 */
public class ProcessIntegrationException extends IntegrationException {
    /**
     * 
     */
    private static final long serialVersionUID = 400L;
    /** The rule. */
    private final    Process    process;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param process
     *            The offending process.
     */
    public ProcessIntegrationException(final Process process) {
        super( createMessage( process ) );
        this.process = process;
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     * 
     * @param rule
     *            The offending rule.
     */
    public ProcessIntegrationException(final Process process,
                                    final Throwable cause) {
        super( createMessage( process ),
               cause );
        this.process = process;
    }

    /**
     * Retrieve the <code>Rule</code>.
     * 
     * @return The rule.
     */
    public Process getProcess() {
        return this.process;
    }

    private static String createMessage(final Process process) {
        return process.getName() + " cannot be integrated";
    }
}
