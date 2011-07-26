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

package org.drools.base.extractors;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.ValueType;
import org.drools.base.mvel.MVELCompileable;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.MVELDialectRuntimeData;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;

/**
 * A class field extractor that uses MVEL engine to extract the actual value for a given
 * expression. We use MVEL to resolve nested accessor expressions.
 */
public class MVELClassFieldReader extends BaseObjectClassFieldReader implements Externalizable, MVELCompileable {

    private static final long  serialVersionUID = 510l;

    private ExecutableStatement mvelExpression   = null;
    
    private String className;
    private String expr;
    private boolean typesafe;
    

    public MVELClassFieldReader() {
    }    
    
    public MVELClassFieldReader(String className,
                                String expr,
                                boolean typesafe) {
        this.className = className;
        this.expr = expr;
        this.typesafe = typesafe;
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.className = ( String ) in.readObject();
        this.expr = ( String ) in.readObject();
        this.typesafe = in.readBoolean();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.className );
        out.writeObject( this.expr );
        out.writeBoolean( this.typesafe );
    }


    public void compile(MVELDialectRuntimeData runtimeData) {
        Class cls = null;
        try {            
            cls = runtimeData.getRootClassLoader().loadClass( this.className );
        } catch ( ClassNotFoundException e ) {
            throw new IllegalStateException( "Unable to compile as Class could not b e found '" + className + "'");
        }
        ParserContext context = new ParserContext(runtimeData.getParserConfiguration());
        context.addInput( "this", cls );
        context.setStrongTyping( typesafe );  
        
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        this.mvelExpression = (ExecutableStatement)MVEL.compileExpression( expr, context);
        
        Class returnType = this.mvelExpression.getKnownEgressType();
        setFieldType( returnType );
        setValueType( ValueType.determineValueType( returnType ) );        
    }    

//    public MVELClassFieldReader(Class cls,
//                                String fieldName,
//                                CacheEntry cache) {
//        super( -1, // index
//               null, //Object.class, // fieldType
//               null ); //ValueType.determineValueType( Object.class ) ); // value type
//        ParserContext context = new ParserContext();
//        context.addInput( "this", cls );
//        context.setStrongTyping( true );
//        
////        if  ( !fieldName.startsWith( "this." ) ) {
////            fieldName = "this." + fieldName;
////        }
//        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
//        this.mvelExpression = (ExecutableStatement)MVEL.compileExpression( fieldName, context);
//        
//        Class returnType = this.mvelExpression.getKnownEgressType();
//        setFieldType( returnType );
//        setValueType( ValueType.determineValueType( returnType ) );
//        
//
////        Set inputs = compiler.getParserContextState().getInputs().keySet();
////        for ( Iterator it = inputs.iterator(); it.hasNext(); ) {
////            String basefield = (String) it.next();
////            if ( "this".equals( basefield ) ) {
////                continue;
////            }
////            InternalReadAccessor extr = cache.getReadAccessor( new AccessorKey( cls.getName(),
////                                                                                basefield,
////                                                                                AccessorKey.AccessorType.FieldAccessor ),
////                                                               cls );
////            this.extractors.put( basefield,
////                                 extr );
////        }
//    }

    //    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    //        super.readExternal(in);
    //        mvelExpression  = (CompiledExpression)in.readObject();
    //        extractors  = (Map)in.readObject();
    //    }
    //
    //    public void writeExternal(ObjectOutput out) throws IOException {
    //        super.writeExternal(out);
    //        out.writeObject(mvelExpression);
    //        out.writeObject(extractors);
    //    }

    /* (non-Javadoc)
     * @see org.drools.base.extractors.BaseObjectClassFieldExtractor#getValue(java.lang.Object)
     */
    public Object getValue(InternalWorkingMemory workingMemory,
                           Object object) {
        return MVEL.executeExpression( mvelExpression,
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
        MVELClassFieldReader other = (MVELClassFieldReader) obj;
        if ( className == null ) {
            if ( other.className != null ) return false;
        } else if ( !className.equals( other.className ) ) return false;
        if ( expr == null ) {
            if ( other.expr != null ) return false;
        } else if ( !expr.equals( other.expr ) ) return false;
        if ( typesafe != other.typesafe ) return false;
        return true;
    }

}
