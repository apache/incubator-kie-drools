package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.mvel.DataConversion;
import org.mvel.integration.VariableResolver;
import org.mvel.integration.impl.BaseVariableResolverFactory;
import org.mvel.integration.impl.LocalVariableResolverFactory;
import org.mvel.integration.impl.StaticMethodImportResolverFactory;

public class DroolsMVELFactory extends BaseVariableResolverFactory
    implements
    LocalVariableResolverFactory,
    Externalizable,
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

    static {
        //for handling dates as string literals
        DataConversion.addConversionHandler( Date.class,
                                             new MVELDateCoercion() );
        DataConversion.addConversionHandler( Calendar.class,
                                             new MVELCalendarCoercion() );
    }

    public DroolsMVELFactory() {

    }

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
            for ( int i = 0; i < externals.length; i++ ) {
                for ( Iterator it = externals[i].iterator(); it.hasNext(); ) {
                    String identifier = (String) it.next();
                    isResolveable( identifier );
                }
            }
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        tupleObjects = (Object[]) in.readObject();
        knowledgeHelper = (KnowledgeHelper) in.readObject();
        object = in.readObject();
        localDeclarations = (Map) in.readObject();
        previousDeclarations = (Map) in.readObject();
        globals = (Map) in.readObject();
        workingMemory = (WorkingMemory) in.readObject();
        localVariables = (Map) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( tupleObjects );
        out.writeObject( knowledgeHelper );
        out.writeObject( object );
        out.writeObject( localDeclarations );
        out.writeObject( previousDeclarations );
        out.writeObject( globals );
        out.writeObject( workingMemory );
        out.writeObject( localVariables );
    }

    public static void addStaticImport(StaticMethodImportResolverFactory factory,
                                       String staticImportEntry,
                                       ClassLoader classLoader) {
        int index = staticImportEntry.lastIndexOf( '.' );
        String className = staticImportEntry.substring( 0,
                                                        index );
        String methodName = staticImportEntry.substring( index + 1 );

        try {
            Class cls = classLoader.loadClass( className );
            Method[] methods = cls.getDeclaredMethods();
            for ( int i = 0; i < methods.length; i++ ) {
                if ( methods[i].getName().equals( methodName ) ) {
                    factory.createVariable( methodName,
                                            methods[i] );
                    break;
                }
            }
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( "Unable to dynamically load method '" + staticImportEntry + "'" );
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
            this.tupleObjects = ((LeftTuple) tuple).toObjectArray();
        }
        this.knowledgeHelper = knowledgeHelper;
        this.object = object;
        this.workingMemory = workingMemory;
        if ( variables == null ) {
            if ( this.localVariables == null ) {
                this.localVariables = new HashMap();
            } else {
                this.localVariables.clear();
            }
        } else {
            this.localVariables = variables;
        }
    }

    public KnowledgeHelper getKnowledgeHelper() {
        return this.knowledgeHelper;
    }

    public Object getValue(final Declaration declaration) {
        int i = declaration.getPattern().getOffset();
        return this.tupleObjects[i];
    }

    public Object getValue(final String identifier) {
        return this.workingMemory.getGlobal( identifier );
    }

    public Object getLocalValue(final String identifier) {
        return this.localVariables.get( identifier );
    }

    public void setLocalValue(final String identifier,
                              final Object value) {
        if ( this.localVariables == null ) {
            this.localVariables = new HashMap();
        }
        this.localVariables.put( identifier,
                                 value );
    }

    public VariableResolver createVariable(String name,
                                           Object value) {
        VariableResolver vr = getVariableResolver( name );
        if ( vr == null ) {
            addResolver( name,
                         vr = new LocalVariableResolver( this,
                                                         name ) );
        }

        vr.setValue( value );
        return vr;
    }

    public VariableResolver createVariable(String name,
                                           Object value,
                                           Class type) {
        VariableResolver vr = getVariableResolver( name );
        if ( vr == null ) {
            addResolver( name,
                         vr = new LocalVariableResolver( this,
                                                         name,
                                                         type ) );
        }

        vr.setValue( value );
        return vr;
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
        } else if ( this.workingMemory.getGlobal( name ) != null ) {
            addResolver( name,
                         new DroolsMVELGlobalVariable( name,
                                                       (Class) this.globals.get( name ),
                                                       this ) );
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

    /**
     * @return the localDeclarations
     */
    public Map getLocalDeclarations() {
        return localDeclarations;
    }

    /**
     * @return the previousDeclarations
     */
    public Map getPreviousDeclarations() {
        return previousDeclarations;
    }

    /**
     * @return the globals
     */
    protected Map getGlobals() {
        return globals;
    }

    /**
     * @return the localVariables
     */
    protected Map getLocalVariables() {
        return localVariables;
    }
}
