package org.drools.base.mvel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.mvel.CompileException;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.impl.BaseVariableResolverFactory;

public class DroolsMVELFactory extends BaseVariableResolverFactory
    implements
    Serializable,
    Cloneable {

    private static final long serialVersionUID = 400L;

    /**
     * Holds the instance of the variables.
     */
    private Object[]          tupleObjects;
    private KnowledgeHelper   knowledgeHelper;
    private Object            object;
    private Map               localDeclarations;
    private Map               previousDeclarations;
    private Map               globals;

    private WorkingMemory     workingMemory;

    private Map               localVariables;

    public DroolsMVELFactory(final Map previousDeclarations,
                             final Map localDeclarations,
                             final Map globals) {
        this( previousDeclarations,
              localDeclarations,
              globals,
              null );        
    }
    
    public DroolsMVELFactory(final Map previousDeclarations,
                             final Map localDeclarations,
                             final Map globals,
                             final List[] externals) {
        this.previousDeclarations = previousDeclarations;
        this.localDeclarations = localDeclarations;
        this.globals = globals;
        
        if ( externals != null && MVELDebugHandler.isDebugMode() ) {
            for( int i = 0; i < externals.length; i++ ) {
                for ( Iterator it = externals[i].iterator(); it.hasNext(); ) {
                    String identifier = ( String ) it.next();
                    isResolveable( identifier );
                }
            }
        }
    }    
    
    public Map getVariableResolvers() {
        return this.variableResolvers;
    }

    public Object getObject() {
        return this.object;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public void setContext(final Tuple tuple,
                           final KnowledgeHelper knowledgeHelper,
                           final Object object,
                           final WorkingMemory workingMemory,
                           final Map variables) {
        if ( tuple != null ) {
           this.tupleObjects = ((ReteTuple) tuple).toObjectArray();
        }
        this.knowledgeHelper = knowledgeHelper;
        this.object = object;
        this.workingMemory = workingMemory;
        this.localVariables = variables;
    }

    public KnowledgeHelper getKnowledgeHelper() {
        return this.knowledgeHelper;
    }

    public Object getValue(final Declaration declaration) {
        int i = declaration.getPattern().getOffset();
        return this.tupleObjects[ i ];
    }

    public Object getValue(final String identifier) {
        return this.workingMemory.getGlobal( identifier );
    }

    public Object getLocalValue(final String identifier) {
        return this.localVariables.get( identifier );
    }

    public void setLocalValue(final String identifier,
                              final Object value) {
        this.localVariables.put( identifier,
                                 value );
    }

    public VariableResolver createVariable(String name,
                                           Object value) {
        VariableResolver vr = getVariableResolver( name );
        if ( vr != null ) {
            if ( this.localVariables == null ) {
                this.localVariables = new HashMap();
            }            
            vr.setValue( value );
            return vr;
        } else {
            if ( this.localVariables == null ) {
                this.localVariables = new HashMap();
            }
            addResolver( name,
                         vr = new LocalVariableResolver( this,
                                                         name ) );
            vr.setValue( value );
            return vr;
        }
    }

    public VariableResolver createVariable(String name,
                                           Object value,
                                           Class type) {
        VariableResolver vr = getVariableResolver( name );
        if ( vr != null && vr.getType() != null ) {
            throw new CompileException( "variable already defined within scope: " + vr.getType() + " " + name );
        } else {
            if ( this.localVariables == null ) {
                this.localVariables = new HashMap();
            }
            addResolver( name,
                         vr = new LocalVariableResolver( this,
                                                         name,
                                                         type ) );
            vr.setValue( value );
            return vr;
        }
    }

    public boolean isResolveable(String name) {
        if ( DroolsMVELKnowledgeHelper.DROOLS.equals( name ) ) {
            addResolver( DroolsMVELKnowledgeHelper.DROOLS,
                         new DroolsMVELKnowledgeHelper( this ) );
            return true;

        } else if ( this.variableResolvers != null && this.variableResolvers.containsKey( name ) ) {
            return true;
        } else if ( this.previousDeclarations != null && this.previousDeclarations.containsKey( name ) ) {
            addResolver( name,
                         new DroolsMVELPreviousDeclarationVariable( (Declaration) this.previousDeclarations.get( name ),
                                                                    this ) );
            return true;
        } else if ( this.localDeclarations != null && this.localDeclarations.containsKey( name ) ) {
            addResolver( name,
                         new DroolsMVELLocalDeclarationVariable( (Declaration) this.localDeclarations.get( name ),
                                                                 this ) );
            return true;
        } else if ( this.globals.containsKey( name ) ) {
            addResolver( name,
                         new DroolsMVELGlobalVariable( name,
                                                       (Class) this.globals.get( name ),
                                                       this ) );
            return true;
        } else if ( this.variableResolvers != null && this.variableResolvers.containsKey( name ) ) {
            addResolver( name,
                         new LocalVariableResolver( this,
                                                    name ) );
            return true;
        } else if ( nextFactory != null ) {
            return nextFactory.isResolveable( name );
        }

        return false;
    }

    private void addResolver(String name,
                             VariableResolver vr) {
        if ( this.variableResolvers == null ) {
            this.variableResolvers = new HashMap();
        }
        this.variableResolvers.put( name,
                                    vr );
    }

    public boolean isTarget(String name) {
        if ( this.variableResolvers != null ) {
            return this.variableResolvers.containsKey( name );
        } else {
            return false;
        }
    }

    public Object clone() {
        return new DroolsMVELFactory( this.previousDeclarations,
                                      this.localDeclarations,
                                      this.globals );
    }
}
