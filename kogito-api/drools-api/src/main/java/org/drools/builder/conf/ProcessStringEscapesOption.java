/*
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

package org.drools.builder.conf;


/**
 * An Enum for ProcessStringEscapes option.
 * 
 * drools.parser.processStringEscapes = &lt;true|false&gt; 
 * 
 * DEFAULT = true
 * 
 * When parsing a DRL file, drools will by default process the String escapes and
 * convert them into the appropriate character. For instance, if drools find a
 * "\n" inside a String, drools will convert that into a single new line character.
 * If you want that to show up as the two characters BACK_SLASH+N, you need to escape
 * the SLASH: "\\n", the same way you do in Java files.
 * 
 * This is different, though, from what happened in Drools 4. Drools 4 never processed
 * String escapes, making it impossible to encode special characters into Strings. But,
 * if for any reason, you need the Drools 4 behaviour when parsing files, just set this
 * option to NO (false).
 * 
 * @author etirelli
 */
public enum ProcessStringEscapesOption implements SingleValueKnowledgeBuilderOption {
    
    YES(true),
    NO(false);

    /**
     * The property name for the process string escapes option
     */
    public static final String PROPERTY_NAME = "drools.parser.processStringEscapes";
    
    private boolean value;
    
    ProcessStringEscapesOption( final boolean value ) {
        this.value = value;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }
    
    public boolean isProcessStringEscapes() {
        return this.value;
    }

}
