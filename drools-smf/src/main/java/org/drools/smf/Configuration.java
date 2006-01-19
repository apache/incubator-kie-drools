package org.drools.smf;

/*
 * $Id: Configuration.java,v 1.5 2005/01/23 18:16:20 mproctor Exp $
 * 
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 * 
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 * 
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 * 
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 * 
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *  
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