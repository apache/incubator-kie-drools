package org.drools.reteoo;

/*
 * $Id: Builder.java,v 1.72 2005/02/02 00:23:21 mproctor Exp $
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.InitialFact;
import org.drools.RuleBase;
import org.drools.RuleIntegrationException;
import org.drools.RuleSetIntegrationException;
import org.drools.conflict.DefaultConflictResolver;
import org.drools.rule.And;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.Not;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;
import org.drools.spi.ClassObjectType;
import org.drools.spi.Condition;
import org.drools.spi.ConflictResolver;
import org.drools.spi.Constraint;
import org.drools.spi.RuleBaseContext;

/**
 * Builds the Rete-OO network for a <code>RuleSet</code>.
 * 
 * @see org.drools.rule.RuleSet
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * TODO Make joinForCondition actually be intelligent enough to build optimal
 * joins. Currently using forgy's original description of 2-input nodes, which I
 * feel (but don't know for sure, is sub-optimal.
 */
class Builder
{
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Rete network to build against. */
    private Rete              rete;

    /** Rule-sets added. */
    private List              ruleSets;

    /** Nodes that have been attached. */
    private Map               attachedNodes;

    private Map               applicationData;
   
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a <code>Builder</code> against an existing <code>Rete</code>
     * network.
     */
    Builder()
    {
        this.rete = new Rete( );
        this.ruleSets = new ArrayList( );
        this.attachedNodes = new HashMap( );
        this.applicationData = new HashMap( );   

        
        this.rete.getOrCreateObjectTypeNode( new ClassObjectType( InitialFact.class ) );
    } 

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Add a <code>Rule</code> to the network.
     * 
     * @param rule
     *            The rule to add.
     * 
     * @throws RuleIntegrationException
     *             if an error prevents complete construction of the network for
     *             the <code>Rule</code>.
     */
    void addRule(Rule rule) throws RuleIntegrationException
    {        
        And[] and = rule.getProcessPatterns();
        for ( int i = 0; i < and.length; i++ )
        {
            addRule( and[i], rule );
        }
    }
    
    private void addRule( And and, Rule rule )
    {
        for(Iterator it = and.getChildren().iterator(); it.hasNext(); )
        {
            Object object = it.next();
            
            if ( object instanceof Not )
            {
               Not not = (Not) object;
               Object child = not.getChild();
               
               if ( child instanceof Column)
               {
                   
                   Column column = (Column) child;
                   if ( !hasDependencies(column.getConstraints() ) )
                   {
                       
                   }
                   
               }
               new NotNode();
            }
        }
    }
    
    
    private boolean hasDependencies(List constraints)
    {
        boolean hasDependencies = false;
        for (Iterator it = constraints.iterator(); it.hasNext(); )
        {
            Constraint constraint = (Constraint)it.next();
            if (constraint.getRequiredDeclarations().length > 0)
            {
                return true;
            }
        }    
        return false;
    }


    

}
