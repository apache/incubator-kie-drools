package org.drools.smf;

/*
 * $Id: DefaultConfiguration.java,v 1.2 2005/05/04 16:58:40 memelet Exp $
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
public class DefaultConfiguration implements Configuration
{
    // ----------------------------------------------------------------------
    //     Class members
    // ----------------------------------------------------------------------

    /** Empty <code>String</code> array. */
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    // ----------------------------------------------------------------------
    //     Instance members
    // ----------------------------------------------------------------------

    /** Node name. */
    private String name;

    /** Node text. */
    private String text = "";

    /** Node attributes. */
    private Map attrs;

    /** Children nodes. */
    private List children;

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param name
     *            The name of the node.
     */
    public DefaultConfiguration( String name )
    {
        this.name = name;
        this.attrs = new HashMap( );
        this.children = new ArrayList( );
    }

    /**
     * @see Configuration
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Set the node text.
     * 
     * @param text
     *            The text.
     */
    public void setText( String text )
    {
        this.text = text;
    }

    /**
     * @see Configuration
     */
    public String getText()    
    {
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
    public void setAttribute( String name, String value )
    {
        this.attrs.put( name, value );
    }

    /**
     * @see Configuration
     */
    public String getAttribute( String name )
    {
        return (String) this.attrs.get( name );
    }

    /**
     * @see Configuration
     */
    public String[] getAttributeNames()
    {
        return (String[]) this.attrs.keySet( ).toArray( EMPTY_STRING_ARRAY );
    }

    /**
     * Add a child <code>Configuration</code>.
     * 
     * @param config
     *            The child.
     */
    public void addChild( Configuration config )
    {
        this.children.add( config );
    }

    /**
     * @see Configuration
     */
    public Configuration getChild( String name )
    {
        for ( Iterator childIter = this.children.iterator( ); childIter
                .hasNext( ); )
        {
            Configuration eachConfig = (Configuration) childIter.next( );

            if ( eachConfig.getName( ).equals( name ) )
            {
                return eachConfig;
            }
        }

        return null;
    }

    /**
     * @see Configuration
     */
    public Configuration[] getChildren( String name )
    {
        List result = new ArrayList( );

        for ( Iterator childIter = this.children.iterator( ); childIter
                .hasNext( ); )
        {
            Configuration eachConfig = (Configuration) childIter.next( );

            if ( eachConfig.getName( ).equals( name ) )
            {
                result.add( eachConfig );
            }
        }

        return (Configuration[]) result.toArray( Configuration.EMPTY_ARRAY );
    }

    /**
     * @see Configuration
     */
    public Configuration[] getChildren()
    {
        return (Configuration[]) this.children
                .toArray( Configuration.EMPTY_ARRAY );
    }
}