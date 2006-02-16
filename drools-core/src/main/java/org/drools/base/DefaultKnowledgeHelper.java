package org.drools.base;

/*
 * $Id: DefaultKnowledgeHelper.java,v 1.1 2005/07/26 01:06:32 mproctor Exp $
 *
 * Copyright 2004 (C) The Werken Company. All Rights Reserved.
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

import java.util.List;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;

public class DefaultKnowledgeHelper
    implements
    KnowledgeHelper {
    private final Rule          rule;
    private final Tuple         tuple;
    private final WorkingMemory workingMemory;

    public DefaultKnowledgeHelper(Rule rule,
                                  Tuple tuple,
                                  WorkingMemory workingMemory) {
        this.rule = rule;
        this.tuple = tuple;
        this.workingMemory = workingMemory;
    }

    public void assertObject(Object object) throws FactException {
        this.workingMemory.assertObject( object );
    }

    public void assertObject(Object object,
                             boolean dynamic) throws FactException {
        this.workingMemory.assertObject( object,
                                         dynamic );
    }

    public void modifyObject(Object object) throws FactException {
        FactHandle handle = this.workingMemory.getFactHandle( object );

        this.workingMemory.modifyObject( handle,
                                         object );
    }

    public void modifyObject(FactHandle handle,
                             Object newObject) throws FactException {
        this.workingMemory.modifyObject( handle,
                                         newObject );
    }

    public void retractObject(Object object) throws FactException {
        retractObject( this.workingMemory.getFactHandle( object ) );
    }

    public void retractObject(FactHandle handle) throws FactException {
        this.workingMemory.retractObject( handle );
    }

    public Rule getRule() {
        return this.rule;
    }

    public List getObjects() {
        return this.workingMemory.getObjects();
    }

    public List getObjects(Class objectClass) {
        return this.workingMemory.getObjects( objectClass );
    }

    public void clearAgenda() {
        this.workingMemory.clearAgenda();
    }

    public Object get(Declaration declaration) {
        return declaration.getValue( this.workingMemory.getObject( this.tuple.get( declaration ) ) );
    }

    public Tuple getTuple() {
        return this.tuple;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }
}
