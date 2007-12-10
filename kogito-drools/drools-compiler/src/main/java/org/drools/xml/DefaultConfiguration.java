package org.drools.xml;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of <code>Configuration</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: DefaultConfiguration.java,v 1.2 2004/09/17 00:25:09 mproctor
 *          Exp $
 */
public class DefaultConfiguration
    implements
    Configuration {
    // ----------------------------------------------------------------------
    //     Class members
    // ----------------------------------------------------------------------

    /** Empty <code>String</code> array. */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    // ----------------------------------------------------------------------
    //     Instance members
    // ----------------------------------------------------------------------

    /** Node name. */
    private final String          name;

    /** Node text. */
    private String                text               = "";

    /** Node attributes. */
    private final Map             attrs;

    /** Children nodes. */
    private final List            children;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param name
     *            The name of the node.
     */
    public DefaultConfiguration(final String name) {
        this.name = name;
        this.attrs = new HashMap();
        this.children = new ArrayList();
    }

    /**
     * @see Configuration
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the node text.
     * 
     * @param text
     *            The text.
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * @see Configuration
     */
    public String getText() {
        return this.text;
    }

    /**
     * Set an attribute value.
     * 
     * @param name
     *            The attribute name.
     * @param value
     *            The attribute value.
     */
    public void setAttribute(final String name,
                             final String value) {
        this.attrs.put( name,
                        value );
    }

    /**
     * @see Configuration
     */
    public String getAttribute(final String name) {
        return (String) this.attrs.get( name );
    }

    /**
     * @see Configuration
     */
    public String[] getAttributeNames() {
        return (String[]) this.attrs.keySet().toArray( DefaultConfiguration.EMPTY_STRING_ARRAY );
    }

    /**
     * Add a child <code>Configuration</code>.
     * 
     * @param config
     *            The child.
     */
    public void addChild(final Configuration config) {
        this.children.add( config );
    }

    /**
     * @see Configuration
     */
    public Configuration getChild(final String name) {
        for ( final Iterator childIter = this.children.iterator(); childIter.hasNext(); ) {
            final Configuration eachConfig = (Configuration) childIter.next();

            if ( eachConfig.getName().equals( name ) ) {
                return eachConfig;
            }
        }

        return null;
    }

    /**
     * @see Configuration
     */
    public Configuration[] getChildren(final String name) {
        final List result = new ArrayList();

        for ( final Iterator childIter = this.children.iterator(); childIter.hasNext(); ) {
            final Configuration eachConfig = (Configuration) childIter.next();

            if ( eachConfig.getName().equals( name ) ) {
                result.add( eachConfig );
            }
        }

        return (Configuration[]) result.toArray( Configuration.EMPTY_ARRAY );
    }

    /**
     * @see Configuration
     */
    public Configuration[] getChildren() {
        return (Configuration[]) this.children.toArray( Configuration.EMPTY_ARRAY );
    }
}