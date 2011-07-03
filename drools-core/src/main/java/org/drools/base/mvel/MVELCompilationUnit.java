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

package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.drools.FactHandle;
import org.drools.RuntimeDroolsException;
import org.drools.base.EvaluatorWrapper;
import org.drools.base.ModifyInterceptor;
import org.drools.common.AgendaItem;
import org.drools.common.EqualityKey;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.definition.rule.Rule;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RightTuple;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.runtime.Globals;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.GlobalResolver;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.DataConversion;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.BaseVariableResolverFactory;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;
import org.mvel2.integration.impl.IndexVariableResolver;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.util.SimpleVariableSpaceModel;

public class MVELCompilationUnit
    implements
    Externalizable,
    Cloneable {

    private static final long                    serialVersionUID = 510l;

    private String                               name;

    private String                               expression;

    private String[]                             globalIdentifiers;
    private EvaluatorWrapper[]                   operators;

    private Declaration[]                        previousDeclarations;
    private Declaration[]                        localDeclarations;
    private String[]                             otherIdentifiers;

    private String[]                             inputIdentifiers;
    private String[]                             inputTypes;

    private int                                  languageLevel;
    private boolean                              strictMode;
    
    private SimpleVariableSpaceModel             varModel;

    private int                                  allVarsLength;

    private static Map                           interceptors     = new HashMap( 2 );
    static {
        interceptors.put( "Modify",
                          new ModifyInterceptor() );
    }

    static {
        //for handling dates as string literals
        DataConversion.addConversionHandler( Date.class,
                                             new MVELDateCoercion() );
        DataConversion.addConversionHandler( Calendar.class,
                                             new MVELCalendarCoercion() );
    }

    private static final Map<String, Class< ? >> primitivesMap    = new HashMap<String, Class< ? >>();
    static {
        primitivesMap.put( "int",
                           int.class );
        primitivesMap.put( "boolean",
                           boolean.class );
        primitivesMap.put( "float",
                           float.class );
        primitivesMap.put( "long",
                           long.class );
        primitivesMap.put( "short",
                           short.class );
        primitivesMap.put( "byte",
                           byte.class );
        primitivesMap.put( "double",
                           double.class );
        primitivesMap.put( "char",
                           char.class );
    }

    public static final Object                   COMPILER_LOCK    = new Object();

    public MVELCompilationUnit() {
    }

    public MVELCompilationUnit(String name,
                               String expression,
                               String[] globalIdentifiers,
                               EvaluatorWrapper[] operators,
                               Declaration[] previousDeclarations,
                               Declaration[] localDeclarations,
                               String[] otherIdentifiers,
                               String[] inputIdentifiers,
                               String[] inputTypes,
                               int languageLevel,
                               boolean strictMode) {
        this.name = name;
        this.expression = expression;

        this.globalIdentifiers = globalIdentifiers;
        this.operators = operators;

        this.previousDeclarations = previousDeclarations;
        this.localDeclarations = localDeclarations;
        this.otherIdentifiers = otherIdentifiers;

        this.inputIdentifiers = inputIdentifiers;
        this.inputTypes = inputTypes;

        this.languageLevel = languageLevel;
        this.strictMode = strictMode;
    }

    public String getExpression() {
        return expression;
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeUTF( name );

        out.writeUTF( expression );

        out.writeObject( globalIdentifiers );
        out.writeObject( operators );

        out.writeObject( previousDeclarations );
        out.writeObject( localDeclarations );
        out.writeObject( otherIdentifiers );

        out.writeObject( inputIdentifiers );
        out.writeObject( inputTypes );

        out.writeInt( languageLevel );
        out.writeBoolean( strictMode );
    }

    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        name = in.readUTF();
        expression = in.readUTF();

        globalIdentifiers = (String[]) in.readObject();
        operators = (EvaluatorWrapper[]) in.readObject();

        previousDeclarations = (Declaration[]) in.readObject();
        localDeclarations = (Declaration[]) in.readObject();
        otherIdentifiers = (String[]) in.readObject();

        inputIdentifiers = (String[]) in.readObject();
        inputTypes = (String[]) in.readObject();

        languageLevel = in.readInt();
        strictMode = in.readBoolean();
    }    

    public Serializable getCompiledExpression(MVELDialectRuntimeData runtimeData ) {        
        ParserConfiguration conf = runtimeData.getParserConfiguration();
        final ParserContext parserContext = new ParserContext( conf );
        if ( MVELDebugHandler.isDebugMode() ) {
            parserContext.setDebugSymbols( true );
        }

        parserContext.setStrictTypeEnforcement( strictMode );
        parserContext.setStrongTyping( strictMode );
        parserContext.setIndexAllocation( true );

        if ( interceptors != null ) {
            parserContext.setInterceptors( interceptors );
        }

        parserContext.addIndexedInput( inputIdentifiers );
                
        String identifier = null;
        String type = null;
        try {
            for ( int i = 0, length = inputIdentifiers.length; i < length; i++ ) {
                identifier = inputIdentifiers[i];
                type = inputTypes[i];
                Class< ? > cls = loadClass( runtimeData.getRootClassLoader(),
                                            inputTypes[i] );
                parserContext.addInput( inputIdentifiers[i],
                                        cls );
            }
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeDroolsException( "Unable to resolve class '" + type + "' for identifier '" + identifier );
        }

        parserContext.setSourceFile( name );

        String[] varNames = parserContext.getIndexedVarNames();
        
        ExecutableStatement stmt = (ExecutableStatement) compile( expression,
                                                                  runtimeData.getRootClassLoader(),
                                                                  parserContext,
                                                                  languageLevel );
        
        Set<String> localNames = parserContext.getVariables().keySet();

        parserContext.addIndexedLocals(localNames);

        String[] locals = localNames.toArray(new String[localNames.size()]);
        String[] allVars = new String[varNames.length + locals.length];

        System.arraycopy(varNames, 0, allVars, 0, varNames.length);
        System.arraycopy(locals, 0, allVars, varNames.length, locals.length);        
        
        this.varModel = new SimpleVariableSpaceModel(allVars);
        this.allVarsLength = allVars.length;
        
        return stmt;
    }
    
    public VariableResolverFactory createFactory() {
        Object[] vals = new Object[inputIdentifiers.length];

        VariableResolverFactory factory = varModel.createFactory( vals );
        DroolsVarFactory df = new  DroolsVarFactory();
        factory.setNextFactory( df );       
        return factory;
    }
    
    public VariableResolverFactory getFactory(final Object knowledgeHelper,
                                              final Rule rule,
                                              final Object rightObject,
                                              final LeftTuple tuples,
                                              final Object[] otherVars,
                                              final InternalWorkingMemory workingMemory,
                                              final GlobalResolver globals) {
        VariableResolverFactory factory = createFactory();
        updateFactory(knowledgeHelper, rule, rightObject, tuples, otherVars, workingMemory, globals, factory);
        return factory;
    }
    
    public void updateFactory(final Object knowledgeHelper,
                                              final Rule rule,
                                              final Object rightObject,
                                              final LeftTuple tuples,
                                              final Object[] otherVars,
                                              final InternalWorkingMemory workingMemory,
                                              final GlobalResolver globals,
                                              VariableResolverFactory factory) {
        int varLength = inputIdentifiers.length;

        int i = 0;
        if ( rightObject != null ) {
            factory.getIndexedVariableResolver( i++ ).setValue( rightObject );
        }
        factory.getIndexedVariableResolver( i++ ).setValue( knowledgeHelper );
        factory.getIndexedVariableResolver( i++ ).setValue( knowledgeHelper );
        factory.getIndexedVariableResolver( i++ ).setValue( rule );

        if ( globalIdentifiers != null ) {
            for ( int j = 0, length = globalIdentifiers.length; j < length; j++ ) {
                factory.getIndexedVariableResolver( i++ ).setValue( globals.resolveGlobal( this.globalIdentifiers[j] ) );
            }
        }

        InternalFactHandle[] handles;
        if( tuples != null ) {
            handles = ((LeftTuple) tuples).toFactHandles();
        } else {
            handles = new InternalFactHandle[0];
        }
        if ( operators != null ) {
            for ( int j = 0, length = operators.length; j < length; j++ ) {
                // TODO: need to have one operator per working memory
                factory.getIndexedVariableResolver( i++ ).setValue( operators[j].setWorkingMemory( workingMemory ) );
                if ( operators[j].getLeftBinding() != null ) {
                    if( operators[j].getLeftBinding().getIdentifier().equals( "this" )) {
                        operators[j].setLeftHandle( (InternalFactHandle) workingMemory.getFactHandle( rightObject ) );
                    } else {
                        operators[j].setLeftHandle( getFactHandle( operators[j].getLeftBinding(),
                                                                   handles ) );
                    }
                }
                if ( operators[j].getRightBinding() != null ) {
                    if( operators[j].getRightBinding().getIdentifier().equals( "this" )) {
                        operators[j].setRightHandle( (InternalFactHandle) workingMemory.getFactHandle( rightObject ) );
                    } else {
                        operators[j].setRightHandle( getFactHandle( operators[j].getRightBinding(),
                                                                    handles ) );
                    }
                }
            }
        }

        IdentityHashMap<Object, FactHandle> identityMap = null;
        if ( knowledgeHelper != null ) {
            identityMap = new IdentityHashMap<Object, FactHandle>();
        }

        if ( tuples != null ) {
            if ( this.previousDeclarations != null && this.previousDeclarations.length > 0 ) {
                // Consequences with 'or's will have different declaration offsets, so use the one's from the RTN's subrule.
                Declaration[] prevDecl = this.previousDeclarations;
                if ( knowledgeHelper != null ) {
                    // we know this is a rule
                    prevDecl = ((AgendaItem)((KnowledgeHelper)knowledgeHelper).getActivation()).getRuleTerminalNode().getDeclarations();
                }

                for ( int j = 0, length = prevDecl.length; j < length; j++ ) {
                    Declaration decl = prevDecl[j];
                    InternalFactHandle handle = getFactHandle( decl,
                                                               handles );

                    Object o = decl.getValue( (InternalWorkingMemory) workingMemory,
                                              handle.getObject() );
                    if ( knowledgeHelper != null && decl.isPatternDeclaration() ) {
                        identityMap.put( decl.getIdentifier(),
                                         handle );
                    }
                    factory.getIndexedVariableResolver( i++ ).setValue(  o );
                }
            }
        }

        if ( this.localDeclarations != null && this.localDeclarations.length > 0 ) {
            for ( int j = 0, length = this.localDeclarations.length; j < length; j++ ) {
                Declaration decl = this.localDeclarations[j];
                Object o = decl.getValue( (InternalWorkingMemory) workingMemory,
                                          rightObject );
                factory.getIndexedVariableResolver( i++ ).setValue( o );
            }
        }

        int otherVarsPos = 0;
        if ( otherVars != null ) {
            otherVarsPos = i;
            for ( Object o : otherVars ) {
                factory.getIndexedVariableResolver( i++ ).setValue( o );
            }
        }
        int otherVarsLength = i - otherVarsPos;
        
        for ( i = varLength; i < this.allVarsLength; i++ ) {
            // null all local vars
            factory.getIndexedVariableResolver( i ).setValue( null );
        }
        
        DroolsVarFactory df = ( DroolsVarFactory )  factory.getNextFactory();

        df.setOtherVarsPos( otherVarsPos );
        df.setOtherVarsLength( otherVarsLength );
        
        if ( knowledgeHelper != null && knowledgeHelper instanceof KnowledgeHelper ) {
            KnowledgeHelper kh = ( KnowledgeHelper ) knowledgeHelper;
            kh.setIdentityMap( identityMap );
            df.setKnowledgeHelper( kh );
        }        
    }

    private static InternalFactHandle getFactHandle( Declaration declaration,
                                                     InternalFactHandle[] handles ) {
        return handles.length >= declaration.getPattern().getOffset() ? handles[declaration.getPattern().getOffset()] : null;
    }

    public static Serializable compile( final String text,
                                        final ClassLoader classLoader,
                                        final ParserContext parserContext,
                                        final int languageLevel ) {
        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;

        if ( MVELDebugHandler.isDebugMode() ) {
            parserContext.setDebugSymbols( true );
        }

        Serializable expr = null;
        expr = MVEL.compileExpression( text.trim(),
                                       parserContext );

        return expr;
    }

    public static Class loadClass( ClassLoader classLoader,
                                   String className ) throws ClassNotFoundException {
        Class cls = primitivesMap.get( className );
        if ( cls == null ) {
            cls = classLoader.loadClass( className );
        }
        return cls;

    }

    public void replaceDeclaration( Declaration declaration,
                                    Declaration resolved ) {
        if ( previousDeclarations != null ) {
            for ( int i = 0; i < previousDeclarations.length; i++ ) {
                if ( previousDeclarations[i].equals( declaration ) ) {
                    previousDeclarations[i] = resolved;
                }
            }
        }
        if ( localDeclarations != null ) {
            for ( int i = 0; i < localDeclarations.length; i++ ) {
                if ( localDeclarations[i].equals( declaration ) ) {
                    localDeclarations[i] = resolved;
                }
            }
        }
    }

    @Override
    public MVELCompilationUnit clone() {
        MVELCompilationUnit unit = new MVELCompilationUnit( name,
                                                            expression,
                                                            globalIdentifiers,
                                                            operators,
                                                            previousDeclarations,
                                                            localDeclarations,
                                                            otherIdentifiers,
                                                            inputIdentifiers,
                                                            inputTypes,
                                                            languageLevel,
                                                            strictMode );
        unit.varModel = this.varModel;
        return unit;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }
    
    public String[] getGlobalIdentifiers() {
        return globalIdentifiers;
    }

    public Declaration[] getPreviousDeclarations() {
        return previousDeclarations;
    }

    public void setPreviousDeclarations( Declaration[] previousDeclarations ) {
        this.previousDeclarations = previousDeclarations;
    }

    public Declaration[] getLocalDeclarations() {
        return localDeclarations;
    }

    public String[] getOtherIdentifiers() {
        return otherIdentifiers;
    }

    public String[] getInputIdentifiers() {
        return inputIdentifiers;
    }

    public String[] getInputTypes() {
        return inputTypes;
    }
    
    public int getLanguageLevel() {
        return languageLevel;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public static Map getInterceptors() {
        return interceptors;
    }

    public static Map<String, Class< ? >> getPrimitivesmap() {
        return primitivesMap;
    }

    public static Object getCompilerLock() {
        return COMPILER_LOCK;
    }

    public static class DroolsVarFactory implements VariableResolverFactory {
    
        private KnowledgeHelper knowledgeHelper;
    
        private int             otherVarsPos;
        private int             otherVarsLength;
        
//        private Object[]        values;
    
//        public DroolsMVELIndexedFactory(String[] varNames,
//                                         Object[] values) {
//            this.indexedVariableNames = varNames;
//            this.values = values;
//            this.indexedVariableResolvers = createResolvers( values );
//        }
//    
//        public DroolsMVELIndexedFactory(String[] varNames,
//                                        Object[] values,
//                                        VariableResolverFactory factory) {
//            this.indexedVariableNames = varNames;
//            this.values = values;
//            this.nextFactory = new MapVariableResolverFactory();
//            this.nextFactory.setNextFactory( factory );
//            this.indexedVariableResolvers = createResolvers( values );
//        }
//    
//        private static VariableResolver[] createResolvers( Object[] values ) {
//            VariableResolver[] vr = new VariableResolver[values.length];
//            for ( int i = 0; i < values.length; i++ ) {
//                vr[i] = new IndexVariableResolver( i,
//                                                   values );
//            }
//            return vr;
//        }
    
        public KnowledgeHelper getKnowledgeHelper() {
            return this.knowledgeHelper ;
        }
    
        public void setKnowledgeHelper(KnowledgeHelper kh) {
            this.knowledgeHelper = kh;
        }
    
        public int getOtherVarsPos() {
            return otherVarsPos;
        }
    
        public void setOtherVarsPos( int otherVarsPos ) {
            this.otherVarsPos = otherVarsPos;
        }
    
        public int getOtherVarsLength() {
            return otherVarsLength;
        }
    
        public void setOtherVarsLength( int otherVarsLength ) {
            this.otherVarsLength = otherVarsLength;
        }

        public VariableResolver createIndexedVariable( int index,
                                                       String name,
                                                       Object value ) {
            throw new UnsupportedOperationException(); 
//            indexedVariableResolvers[index].setValue( value );
//            return indexedVariableResolvers[index];
        }
    
        public VariableResolver getIndexedVariableResolver( int index ) {
            throw new UnsupportedOperationException(); 
            //return indexedVariableResolvers[index];
        }
    
        public VariableResolver createVariable( String name,
                                                Object value ) {
            throw new UnsupportedOperationException();            
//            VariableResolver vr = getResolver( name );
//            if ( vr != null ) {
//                vr.setValue( value );
//                return vr;
//            } else {
//                if ( nextFactory == null ) nextFactory = new MapVariableResolverFactory( new HashMap() );
//                return nextFactory.createVariable( name,
//                                                   value );
//            }
        }
    
        public VariableResolver createVariable( String name,
                                                Object value,
                                                Class< ? > type ) {
            throw new UnsupportedOperationException();            
//            VariableResolver vr = getResolver( name );
//            if ( vr != null ) {
//                if ( vr.getType() != null ) {
//                    throw new RuntimeException( "variable already defined within scope: " + vr.getType() + " " + name );
//                } else {
//                    vr.setValue( value );
//                    return vr;
//                }
//            } else {
//                if ( nextFactory == null ) nextFactory = new MapVariableResolverFactory( new HashMap() );
//                return nextFactory.createVariable( name,
//                                                   value,
//                                                   type );
//            }
        }
    
        public VariableResolver getVariableResolver( String name ) {
            throw new UnsupportedOperationException();
        }
    
        public boolean isResolveable( String name ) {
            //return isTarget( name ) || (nextFactory != null && nextFactory.isResolveable( name ));
            return false;
        }
    
        protected VariableResolver addResolver( String name,
                                                VariableResolver vr ) {
            throw new UnsupportedOperationException();
//            variableResolvers.put( name,
//                                   vr );
//            return vr;
        }
    
        private VariableResolver getResolver( String name ) {
//            for ( int i = 0; i < indexedVariableNames.length; i++ ) {
//                if ( indexedVariableNames[i].equals( name ) ) {
//                    return indexedVariableResolvers[i];
//                }
//            }
            return null;
        }
    
        public boolean isTarget( String name ) {
//            for ( String indexedVariableName : indexedVariableNames ) {
//                if ( indexedVariableName.equals( name ) ) {
//                    return true;
//                }
//            }
            return false;
        }
    
        public Set<String> getKnownVariables() {
//            Set<String> vars = new HashSet<String>();
//            for ( int i = 0; i < indexedVariableNames.length; i++ ) {
//                vars.add( indexedVariableNames[i] );
//            }
            return Collections.emptySet();
        }
    
        public void clear() {
            // variableResolvers.clear();
    
        }
    
        public boolean isIndexedFactory() {
            return false;
        }

        public VariableResolver createIndexedVariable(int index,
                                                      String name,
                                                      Object value,
                                                      Class< ? > typee) {
            // TODO Auto-generated method stub
            return null;
        }

        public VariableResolver setIndexedVariableResolver(int index,
                                                           VariableResolver variableResolver) {
            // TODO Auto-generated method stub
            return null;
        }

        public VariableResolverFactory getNextFactory() {
            // TODO Auto-generated method stub
            return null;
        }

        public VariableResolverFactory setNextFactory(VariableResolverFactory resolverFactory) {
            // TODO Auto-generated method stub
            return null;
        }

        public int variableIndexOf(String name) {
            // TODO Auto-generated method stub
            return 0;
        }

        public boolean tiltFlag() {
            // TODO Auto-generated method stub
            return false;
        }

        public void setTiltFlag(boolean tilt) {
            // TODO Auto-generated method stub
            
        }
    }

}
