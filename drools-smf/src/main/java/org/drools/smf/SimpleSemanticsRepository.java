package org.drools.smf;

/*
 * $Id: SimpleSemanticsRepository.java,v 1.4 2005/04/07 17:42:14 mproctor Exp $
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

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory repository of semantic modules.
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
public class SimpleSemanticsRepository implements SemanticsRepository
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Semantic modules, indexed by URI. */
    private Map modules;

	private ClassLoader classLoader;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /**
     * Construct an empty repository.
     */
    public SimpleSemanticsRepository()
    {
        this.modules = new HashMap( );
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //     org.drools.smf.SemanticsRepository
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	void setClassLoader(ClassLoader classLoader)
	{
		this.classLoader = classLoader;
	}

    /**
     * Register a <code>SemanticModule</code> with a URI.
     *
     * @param module The <code>SemanticModule</code>.
     */
    public void registerSemanticModule(SemanticModule module)
    {
        this.modules.put( module.getUri( ), module );
    }

    /**
     * @see SemanticsRepository
     */
    public SemanticModule lookupSemanticModule(String uri) throws NoSuchSemanticModuleException
    {
        if ( !this.modules.containsKey( uri ) )
        {
            throw new NoSuchSemanticModuleException( uri );
        }

        return ( SemanticModule ) this.modules.get( uri );
    }

    /**
     * @see SemanticsRepository
     */
    public SemanticModule[] getSemanticModules()
    {
        return ( SemanticModule[] ) this.modules.values( ).toArray(SemanticModule.EMPTY_ARRAY );
    }

	public ClassLoader getSemanticModuleClassLoader()
	{
		return this.classLoader;
	}
}