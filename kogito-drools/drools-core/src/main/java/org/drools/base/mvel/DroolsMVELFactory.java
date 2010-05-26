package org.drools.base.mvel;
 
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.mvel2.CompileException;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.BaseVariableResolverFactory;
import org.mvel2.integration.impl.LocalVariableResolverFactory;
import org.mvel2.integration.impl.StaticMethodImportResolverFactory;
 
public class DroolsMVELFactory extends BaseVariableResolverFactory
        implements
        DroolsGlobalVariableMVELFactory,
        DroolsLocalVariableMVELFactory,
        LocalVariableResolverFactory,
        Externalizable,
        Cloneable {
 
    private static final long serialVersionUID = 400L;
 
    /**
     * Holds the instance of the variables.
     */
    private InternalFactHandle[] tupleObjects;
 
    private KnowledgeHelper knowledgeHelper;
 
    private Object object;
 
    private Map localDeclarations;
 
    private Map<String, Declaration> previousDeclarations;
    
    // this is a cache for previously declared objects in case they 
    // get retracted during the execution of an MVEL consequence
    private Map<String, Object> previousDeclarationsObjectCache; 
 
    private Map globals;
 
    private WorkingMemory workingMemory;
 
    private Map localVariables;
    
    private String[] inputIdentifiers;
 
    public DroolsMVELFactory() {
        previousDeclarationsObjectCache = new HashMap<String, Object>();
    }
 
    public DroolsMVELFactory(final Map previousDeclarations,
                             final Map localDeclarations,
                             final Map globals) {
        this(previousDeclarations,
                localDeclarations,
                globals,
                null);
    }
 
    public DroolsMVELFactory(final Map previousDeclarations,
                             final Map localDeclarations,
                             final Map globals,
                             final String[] inputIdentifiers) {
        this.previousDeclarations = (Map<String, Declaration>) previousDeclarations;
        this.localDeclarations = localDeclarations;
        this.globals = globals;
        this.inputIdentifiers = inputIdentifiers;
 
        if (inputIdentifiers != null && MVELDebugHandler.isDebugMode()) {
            for (int i = 0; i < inputIdentifiers.length; i++) {
                isResolveable(inputIdentifiers[i]);
            }
        }
        previousDeclarationsObjectCache = new HashMap<String, Object>();
    }
 
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        tupleObjects = (InternalFactHandle[]) in.readObject();
        knowledgeHelper = (KnowledgeHelper) in.readObject();
        object = in.readObject();
        localDeclarations = (Map) in.readObject();
        previousDeclarations = (Map<String, Declaration>) in.readObject();
        globals = (Map) in.readObject();
        workingMemory = (WorkingMemory) in.readObject();
        localVariables = (Map) in.readObject();
    }
 
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(tupleObjects);
        out.writeObject(knowledgeHelper);
        out.writeObject(object);
        out.writeObject(localDeclarations);
        out.writeObject(previousDeclarations);
        out.writeObject(globals);
        out.writeObject(workingMemory);
        out.writeObject(localVariables);
    }
 
    public static void addStaticImport(StaticMethodImportResolverFactory factory,
                                       String staticImportEntry,
                                       ClassLoader classLoader) {
        int index = staticImportEntry.lastIndexOf('.');
        String className = staticImportEntry.substring(0,
                index);
        String methodName = staticImportEntry.substring(index + 1);
 
        try {
            Class cls = classLoader.loadClass(className);
            Method[] methods = cls.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(methodName)) {
                    factory.createVariable(methodName,
                            methods[i]);
                    break;
                }
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to dynamically load method '" + staticImportEntry + "'");
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
                           final Map<String, Object> variables ) {
        if (tuple != null) {
            this.tupleObjects = ((LeftTuple) tuple).toFactHandles();
        }
        this.knowledgeHelper = knowledgeHelper;
        this.object = object;
        this.workingMemory = workingMemory;
        if (variables == null) {
            if (this.localVariables == null) {
                this.localVariables = new HashMap();
            }
            else {
                this.localVariables.clear();
            }
        }
        else {
            this.localVariables = variables;
        }
        
        if( this.previousDeclarations != null ) {
            // take a snapshot of required objects for variable resolution
            for( Map.Entry<String, Declaration> entry : this.previousDeclarations.entrySet() ) {
                Declaration decl = knowledgeHelper != null ? knowledgeHelper.getDeclaration( entry.getValue().getIdentifier() ) : entry.getValue();
                this.previousDeclarationsObjectCache.put( entry.getKey(), getTupleObjectFor( decl ) );
            }
        }
    }
 
    private Object getTupleObjectFor(Declaration declaration) {
        int i = declaration.getPattern().getOffset();
        return ( i < this.tupleObjects.length ) ? this.tupleObjects[i].getObject() : null;
    }

    public KnowledgeHelper getKnowledgeHelper() {
        return this.knowledgeHelper;
    }
 
    public Object getValue(final Declaration declaration) {
        if( this.previousDeclarationsObjectCache.containsKey( declaration.getIdentifier() ) ) {
            return this.previousDeclarationsObjectCache.get( declaration.getIdentifier() );
        } else {
            return getTupleObjectFor( declaration );
        }
    }
    public InternalFactHandle getFactHandle(final Declaration declaration){
        int i = declaration.getPattern().getOffset();
        return this.tupleObjects[i];
    }
 
    public Object getValue(final String identifier) {
        return this.workingMemory.getGlobal(identifier);
    }
 
    public Object getLocalValue(final String identifier) {
        return this.localVariables.get(identifier);
    }
 
    public void setLocalValue(final String identifier,
                              final Object value) {
        if (this.localVariables == null) {
            this.localVariables = new HashMap();
        }
        this.localVariables.put(identifier,
                value);
    }
 
    public VariableResolver createVariable(String name, Object value) {
        VariableResolver vr;
 
        try {
            vr = getVariableResolver(name);
        }
        catch (UnresolveablePropertyException e) {
            vr = null;
        }
 
        if (vr != null && vr.getType() != null) {
            throw new CompileException("variable already defined within scope: " + vr.getType() + " " + name);
        }
        else {
            return addResolver(name, new LocalVariableResolver(this, name), value);
        }
    }
 
    public VariableResolver createVariable(String name,
                                           Object value,
                                           Class type) {
 
 
        VariableResolver vr;
 
        try {
            vr = getVariableResolver(name);
        }
        catch (UnresolveablePropertyException e) {
            vr = null;
        }
 
        if (vr != null && vr.getType() != null) {
            throw new CompileException("variable already defined within scope: " + vr.getType() + " " + name);
        }
        else {
            return addResolver(name, new LocalVariableResolver(this, name, type), value);
        }
 
    }
 
    @Override
    public VariableResolver createIndexedVariable(int index,
                                                  String name,
                                                  Object value) {
        return super.createIndexedVariable(index,
                name,
                value);
    }
 
    @Override
    public VariableResolver createIndexedVariable(int index,
                                                  String name,
                                                  Object value,
                                                  Class<?> type) {
        return super.createIndexedVariable(index,
                name,
                value,
                type);
    }
 
    public boolean isResolveable(String name) {
        if (DroolsMVELKnowledgeHelper.DROOLS.equals(name)) {
            addResolver(DroolsMVELKnowledgeHelper.DROOLS,
                    new DroolsMVELKnowledgeHelper(this));
            return true;
        }
        else if (this.variableResolvers != null && this.variableResolvers.containsKey(name)) {
            return true;
        }
        else if (DroolsMVELKnowledgeHelper.CONTEXT.equals(name)) {
            addResolver(DroolsMVELKnowledgeHelper.CONTEXT,
                    new DroolsMVELKnowledgeHelper(this));
            return true;
        }
        else if (this.previousDeclarations != null && this.previousDeclarations.containsKey(name)) {
            addResolver(name,
                    new DroolsMVELPreviousDeclarationVariable((Declaration) this.previousDeclarations.get(name),
                            this));
            return true;
        }
        else if (this.localDeclarations != null && this.localDeclarations.containsKey(name)) {
            addResolver(name,
                    new DroolsMVELLocalDeclarationVariable((Declaration) this.localDeclarations.get(name),
                            this));
            return true;
        }
        else if (this.globals.containsKey(name)) {
            addResolver(name,
                    new DroolsMVELGlobalVariable(name,
                            (Class) this.globals.get(name),
                            this));
            return true;
        }
        else if (nextFactory != null) {
            return nextFactory.isResolveable(name);
        }
 
        return false;
    }
 
    public VariableResolver addResolver(String name,
                                        VariableResolver vr,
                                        Object value) {
        if (this.variableResolvers == null) {
            this.variableResolvers = new HashMap();
        }
        this.variableResolvers.put(name, vr);
        vr.setValue(value);
        return vr;
    }
 
    public VariableResolver addResolver(String name,
                                        VariableResolver vr) {
        if (this.variableResolvers == null) {
            this.variableResolvers = new HashMap();
        }
        this.variableResolvers.put(name, vr);
        return vr;
    }
 
    public boolean isTarget(String name) {
        if (this.variableResolvers != null) {
            return this.variableResolvers.containsKey(name);
        }
        else {
            return false;
        }
    }
 
    public Object clone() {
        return new DroolsMVELFactory(this.previousDeclarations,
                this.localDeclarations,
                this.globals,
                this.inputIdentifiers );
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
