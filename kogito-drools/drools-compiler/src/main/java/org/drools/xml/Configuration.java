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



/**
 * Configuration passed to a configurable <code>SemanticComponent</code>.
 * 
 * <p>
 * A <code>Configuration</code> may actually form a tree-shaped structure in
 * order to hold complex configuration data. Each node in the tree is
 * represented by a <code>Configuration</code> object that has a name and may
 * contain attributes, children and text.
 * </p>
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: Configuration.java,v 1.5 2005/01/23 18:16:20 mproctor Exp $
 */
public interface Configuration
{
    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

    /** Empty <code>Configuration</code> array. */
    Configuration[] EMPTY_ARRAY = new Configuration[0];
    
    // ----------------------------------------------------------------------
    //     Interface
    // ----------------------------------------------------------------------

    /**
     * Retrieve the node name.
     * 
     * @return The node name.
     */
    String getName();

    /**
     * Retrieve the node text.
     * 
     * @return The node text.
     */
    String getText();

    /**
     * Retrieve an attribute value.
     * 
     * @param name The attribute name.
     * 
     * @return The attribute value or <code>null</code> if no attribute
     *         matches the specified name.
     */
    String getAttribute(String name);

    /**
     * Retrieve all attribute names.
     * 
     * @return The attribute names.
     */
    String[] getAttributeNames();

    /**
     * Retrieve a child node.
     * 
     * @param name The child name.
     * 
     * @return The first child matching the specified name, otherwise
     *         <code>null</code> if none match.
     */
    Configuration getChild(String name);

    /**
     * Retrieve children nodes.
     * 
     * @param name The child name.
     * 
     * @return All children matching the specified name, otherwise an empty
     *         array if none match.
     */
    Configuration[] getChildren(String name);

    /**
     * Retrieve all children nodes.
     * 
     * @return All children nodes, otherwise an empty array if this node
     *         contains no children.
     */
    Configuration[] getChildren();    
}