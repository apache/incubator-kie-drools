/*
 * Copyright 2011 JBoss Inc
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
package org.drools.builder.conf;

import org.drools.conf.MultithreadEvaluationOption;
import org.drools.conf.SequentialOption;
import org.drools.conf.SingleValueKnowledgeBaseOption;

/**
 * <p>
 * An Enum for Left & Right Unlinking option.
 * </p>
 * 
 * <pre>
 * drools.lrUnlinkingEnabled = &lt;true|false&gt;
 * </pre>
 * 
 * <b>DEFAULT = false.</b>
 * 
 * <p>
 * Left & Right unlinking is a RETE optimization that leads to improvements in
 * performance and memory consumption, mainly for large rule bases. It is still
 * an experimental feature in Drools and not active by default. For further
 * details, have look at <a href=
 * "http://blog.athico.com/2010/08/left-and-right-unlinking-community.html">this
 * blog entry</a>.
 * </p>
 * 
 * <p>
 * <b>Note:</b> It will NOT work when:
 * <ul>
 * 
 * <li>
 * {@link SequentialOption} is used, because in sequential mode we disable node
 * memory, which is mandatory for L&R Unlinking to work;</li>
 * 
 * <li>{@link MultithreadEvaluationOption} is used.</li>
 * </ul>
 * </p>
 * 
 */
public enum LRUnlinkingOption implements SingleValueKnowledgeBaseOption {

    ENABLED(true), DISABLED(false);

    /**
     * The property name for the L&R Unlinking option
     */
    public static final String PROPERTY_NAME = "drools.lrUnlinkingEnabled";

    private boolean value;

    LRUnlinkingOption(final boolean value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isLRUnlinkingEnabled() {
        return this.value;
    }

}
