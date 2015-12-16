/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.spi;

import org.drools.core.definitions.rule.impl.RuleImpl;

/**
 * Indicates an error during a <code>Consequence</code> invocation.
 * 
 * @see Consequence
 */
public class ConsequenceException extends RuntimeException {
    private static final long serialVersionUID = 510l;
    private RuleImpl          rule;
    private String            info;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    public ConsequenceException() {
        // intentionally left blank
    }

    public ConsequenceException(final String message) {
        super( message );
    }

    /**
     * Construct with a root cause.
     * 
     * @param rootCause
     *            The root cause of this exception.
     */
    public ConsequenceException(final Throwable rootCause) {
        super( rootCause );
    }

    public ConsequenceException(RuleImpl rule) {
        this.rule = rule;
    }

    /**
     * Construct with a message. Keep this from old ConsequenceException for
     * backward compatability
     * 
     */
    public ConsequenceException(final String message,
                                final RuleImpl rule) {
        super( message );
        this.rule = rule;
    }

    /**
     * Construct with a root cause. Keep this from old ConsequenceException for
     * backward compatability
     * 
     * @param rootCause
     *            The root cause of this exception.
     */
    public ConsequenceException(final Throwable rootCause,
                                final RuleImpl rule) {
        super( rootCause );
        this.rule = rule;
    }

    public ConsequenceException(final String message,
                                final RuleImpl rule,
                                final String info) {
        super( message );
        this.rule = rule;
        this.info = info;
    }

    /**
     * Construct with a root cause.
     * 
     * @param rootCause
     *            The root cause of this exception.
     */
    public ConsequenceException(final Throwable rootCause,
                                final RuleImpl rule,
                                final String info) {
        super( rootCause );
        this.rule = rule;
        this.info = info;
    }

    public RuleImpl getRule() {
        return this.rule;
    }

    /**
     * Set arbitrary extra information about the condition.
     * 
     * <p>
     * The info property may be used to communicate the actual block text or
     * other information in the case that Consequence does not have block text.
     * </p>
     */
    public void setInfo(final String info) {
        this.info = info;
    }

    public String getInfo() {
        return this.info;
    }
    
    @Override
    public String getMessage() {
        String msg;
        if( this.rule != null ) {
            msg = "Exception executing consequence for rule '"+this.rule.getName()+"' : "+super.getMessage();
        } else {
            msg = "Exception executing consequence. Rule name unknown. Message: " + super.getMessage();
        }
        return msg;
    }
    
    @Override
    public String toString() {
        return getMessage();
    }
}
