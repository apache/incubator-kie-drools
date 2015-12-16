/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.extractors;

import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.util.MVELSafeHelper;
import org.mvel2.compiler.ExecutableStatement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A class field extractor that uses MVEL engine to extract the actual value for a given
 * expression. We use MVEL to resolve nested accessor expressions.
 */
public class MVELNumberClassFieldReader extends BaseNumberClassFieldReader implements Externalizable, MVELCompileable, MVELClassFieldReader {

    private static final long   serialVersionUID = 510l;

    private ExecutableStatement mvelExpression   = null;

    private String              className;
    private String              expr;
    private boolean             typesafe;
    private Object              evaluationContext;

    public MVELNumberClassFieldReader() {
    }    
    
    public MVELNumberClassFieldReader(String className,
                                    String expr,
                                    boolean typesafe) {
        this.className = className;
        this.expr = expr;
        this.typesafe = typesafe;
        setIndex( -1 );
        
        // field (returns) type and value type are set during compile        
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        this.className = ( String ) in.readObject();
        this.expr = ( String ) in.readObject();
        this.typesafe = in.readBoolean();
        this.evaluationContext = in.readObject();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( this.className );
        out.writeObject(this.expr );
        out.writeBoolean(this.typesafe);
        out.writeObject(this.evaluationContext );
    }
    
    public void setExecutableStatement(ExecutableStatement expression) {
        this.mvelExpression = expression;
    }

    public String getClassName() {
        return this.className;
    }

    public boolean isTypeSafe() {
        return this.typesafe;
    }

    public String getExpression() {
        return this.expr;
    }

    public Object getEvaluationContext() {
        return evaluationContext;
    }

    public void setEvaluationContext(Object evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    public void compile(MVELDialectRuntimeData runtimeData) {
        MVELObjectClassFieldReader.doCompile(this, runtimeData, getEvaluationContext());
    }

    public void compile( MVELDialectRuntimeData runtimeData, RuleImpl rule ) {
        MVELObjectClassFieldReader.doCompile(this, runtimeData, rule.toRuleNameAndPathString());
    }

    /* (non-Javadoc)
     * @see org.kie.base.extractors.BaseObjectClassFieldExtractor#getValue(java.lang.Object)
     */
    public Object getValue(InternalWorkingMemory workingMemory,
                           Object object) {
        return MVELSafeHelper.getEvaluator().executeExpression( mvelExpression,
                                       object  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        result = prime * result + (typesafe ? 1231 : 1237);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( !super.equals( obj ) ) return false;
        if ( getClass() != obj.getClass() ) return false;
        MVELNumberClassFieldReader other = (MVELNumberClassFieldReader) obj;
        if ( className == null ) {
            if ( other.className != null ) return false;
        } else if ( !className.equals( other.className ) ) return false;
        if ( expr == null ) {
            if ( other.expr != null ) return false;
        } else if ( !expr.equals( other.expr ) ) return false;
        return typesafe == other.typesafe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[MVELDateClassFieldReader className=" + className + ", expr=" + expr + ", typesafe=" + typesafe + "]";
    }

}
