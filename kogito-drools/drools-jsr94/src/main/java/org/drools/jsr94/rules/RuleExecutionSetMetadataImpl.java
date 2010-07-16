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

package org.drools.jsr94.rules;

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

import javax.rules.RuleExecutionSetMetadata;

/**
 * The Drools implementation of the <code>RuleExecutionSetMetadata</code>
 * interface which exposes some simple properties of the
 * <code>RuleExecutionSet</code> to the runtime user.
 *
 * @see RuleExecutionSetMetadata
 */
public class RuleExecutionSetMetadataImpl
    implements
    RuleExecutionSetMetadata {
    /**
     * 
     */
    private static final long serialVersionUID = 400L;

    /** The URI for this <code>RuleExecutionSet</code>. */
    private final String      uri;

    /** The name of this RuleExecutionSet. */
    private final String      name;

    /** The description of this <code>RuleExecutionSet</code>. */
    private final String      description;

    /**
     * Constructs an instance of <code>RuleExecutionSetMetadata</code>.
     *
     * @param uri The URI for this <code>RuleExecutionSet</code>.
     * @param name The name of this <code>RuleExecutionSet</code>.
     * @param description The description of this <code>RuleExecutionSet</code>.
     */
    public RuleExecutionSetMetadataImpl(final String uri,
                                        final String name,
                                        final String description) {
        this.uri = uri;
        this.name = name;
        this.description = description;
    }

    /**
     * Get the URI for this <code>RuleExecutionSet</code>.
     *
     * @return The URI for this <code>RuleExecutionSet</code>.
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * Get the name of this <code>RuleExecutionSet</code>.
     *
     * @return The name of this <code>RuleExecutionSet</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get a short description about this <code>RuleExecutionSet</code>.
     *
     * @return The description of this <code>RuleExecutionSet</code>
     *         or <code>null</code>.
     */
    public String getDescription() {
        return this.description;
    }
}
