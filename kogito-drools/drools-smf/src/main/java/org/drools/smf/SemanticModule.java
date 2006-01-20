package org.drools.smf;

/*
 * $Id: SemanticModule.java,v 1.9 2005/02/04 02:13:38 mproctor Exp $
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

import java.util.Set;

/**
 * Collection of entities forming a semantic module.
 *
 * @see org.drools.spi
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
public interface SemanticModule
{
    // ----------------------------------------------------------------------
    //     Constants
    // ----------------------------------------------------------------------

    /**
     * Empty <code>SemanticModule</code> array.
     */
    SemanticModule[] EMPTY_ARRAY = new SemanticModule[0];

    // ----------------------------------------------------------------------
    //     Interface
    // ----------------------------------------------------------------------

    /**
     * Retrieve the URI that identifies this semantic module.
     *
     * @return The URI.
     */
    String getUri();
    
    String getType(String name);
    
    RuleFactory getRuleFactory(String name);

    Set getRuleFactoryNames();

    /**
     * Retrieve a semantic object type by name.
     *
     * @param name the name.
     *
     * @return The object type implementation or <code>null</code> if none is
     *         bound to the name.
     */
    ObjectTypeFactory getObjectTypeFactory(String name);

    /**
     * Retrieve the set of all object type names.
     *
     * @return The set of names.
     */
    Set getObjectTypeFactoryNames();

    /**
     * Retrieve a semantic condition by name.
     *
     * @param name the name.
     *
     * @return The condition implementation or <code>null</code> if none is
     *         bound to the name.
     */
    ConditionFactory getConditionFactory(String name);

    /**
     * Retrieve the set of all condition names.
     *
     * @return The set of names.
     */
    Set getConditionFactoryNames();

    /**
     * Retrieve a semantic consequence by name.
     *
     * @param name the name.
     *
     * @return The consequence implementation or <code>null</code> if none is
     *         bound to the name.
     */
    ConsequenceFactory getConsequenceFactory(String name);

    /**
     * Retrieve the set of all consequence names.
     *
     * @return The set of names.
     */
    Set getConsequenceFactoryNames();

    DurationFactory getDurationFactory(String name);

    Set getDurationFactoryNames();


    void addImportEntryFactory(String name, ImportEntryFactory factory);

    ImportEntryFactory getImportEntryFactory(String name);

    Set getImportEntryFactoryNames();     

    void addApplicationDataFactory(String name, ApplicationDataFactory factory);
    
    ApplicationDataFactory getApplicationDataFactory(String name);
    
    Set getApplicationDataFactoryNames();

    FunctionsFactory getFunctionsFactory(String name);

    Set getFunctionsFactoryNames();     
    
    
    PredicateEvaluatorFactory getPredicateEvaluatorFactory(String name);
    
    Set getPredicateEvaluatorFactoryNames();
    
    
    ReturnValueEvaluatorFactory getReturnValueEvaluatorFactory(String name);
    
    Set getReturnValueEvaluatorFactoryNames();
    

}