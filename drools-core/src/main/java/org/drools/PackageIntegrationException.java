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

package org.drools;

import org.drools.rule.Package;

/**
 * Indicates an error integrating a <code>Package</code> into a
 * <code>RuleBase</code>.
 * 
 * @see RuleBase#addRule
 * @see RuleBase#addPackage
 * 
 */
public class PackageIntegrationException extends IntegrationException {
    /**
     * 
     */
    private static final long serialVersionUID = 510l;
    /** The rule. */
    private final Package     pkg;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param pkg
     *            The offending rule.
     */
    public PackageIntegrationException(final String message,
                                       final Package pkg) {
        super( message );
        this.pkg = pkg;
    }

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param pkg
     *            The offending rule.
     */
    public PackageIntegrationException(final Package pkg) {
        super( createMessage( pkg ) );
        this.pkg = pkg;
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     * 
     * @param pkg
     *            The offending rule.
     */
    public PackageIntegrationException(final Package pkg,
                                       final Throwable cause) {
        super( createMessage( pkg ),
               cause );
        this.pkg = pkg;
    }

    /**
     * Retrieve the <code>Package</code>.
     * 
     * @return The pkg
     */
    public Package getPackage() {
        return this.pkg;
    }

    private static String createMessage(final Package pkg) {
        return pkg.getName() + " cannot be integrated";
    }
}
