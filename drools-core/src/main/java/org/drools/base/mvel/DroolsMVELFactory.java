package org.drools.base.mvel;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.mvel.CompileException;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.VariableResolverFactory;
import org.mvel.integration.impl.BaseVariableResolverFactory;
import org.mvel.integration.impl.ClassImportResolverFactory;
import org.mvel.integration.impl.MapVariableResolver;
import org.mvel.integration.impl.StaticMethodImportResolverFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DroolsMVELFactory extends BaseVariableResolverFactory implements Serializable {
    /**
     * Holds the instance of the variables.
     */
    //private Map           variables;
    //    public DroolsMVELFactory(Map variables) {
    //        this.variables = variables;
    //    }
    private Tuple           tuple;
    private KnowledgeHelper knowledgeHelper;
    private Object          object;
    private Map             localDeclarations;
    private Map             previousDeclarations;
    private Map             globals;

    private WorkingMemory   workingMemory;

    private Map             variables;

    public DroolsMVELFactory(final Map previousDeclarations,
                             final Map localDeclarations,
                             final Map globals) {
        this.previousDeclarations = previousDeclarations;
        this.localDeclarations = localDeclarations;
        this.globals = globals;
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
        this.tuple = tuple;
        this.knowledgeHelper = knowledgeHelper;
        this.object = object;
        this.workingMemory = workingMemory;
        this.variables = variables;
    }

    public KnowledgeHelper getKnowledgeHelper() {
        return this.knowledgeHelper;
    }

    public Object getValue(final Declaration declaration) {
        return this.tuple.get( declaration ).getObject();
    }

    public Object getValue(final String identifier) {
        return this.workingMemory.getGlobal( identifier );
    }

    public VariableResolver createVariable(String name,
                                           Object value) {
        VariableResolver vr = getVariableResolver( name );
        if ( vr != null ) {
            vr.setValue( value );
            return vr;
        } else {
            if ( this.variables == null ) {
                this.variables = new HashMap();
            }
            addResolver( name,
                         vr = new MapVariableResolver( this.variables,
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
            if ( this.variables == null ) {
                this.variables = new HashMap();
            }
            addResolver( name,
                         vr = new MapVariableResolver( this.variables,
                                                       name,
                                                       type ) );
            vr.setValue( value );
            return vr;
        }
    }

    public boolean isResolveable(String name) {
        if ( DroolsMVELKnowledgeHelper.DROOLS.equals( name  ) ) {
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
                         new MapVariableResolver( this.variableResolvers,
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
}
