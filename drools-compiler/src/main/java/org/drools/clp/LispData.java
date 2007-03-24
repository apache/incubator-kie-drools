package org.drools.clp;

public class LispData
    implements
    LispList {
    
    private ListValueHandler list;
    private FunctionRegistry registry;
    private ExecutionBuildContext context;
    
    public LispData() {
        this.list = new ListValueHandler() ;
    }

    public void add(ValueHandler valueHandler) {
        this.list.add( valueHandler );
    }
    
    public LispList createList() {
        LispList list = new LispForm( );
        
        list.setContext( this.context );
        list.setRegistry( this.registry );
        
        return list;
    }
    
    public ValueHandler getValueHandler() {
        return this.list;
    }
    
    public void setContext(ExecutionBuildContext context) {
        this.context = context;
    }


    public void setRegistry(FunctionRegistry registry) {
        this.registry = registry;
    }    

    public ValueHandler[] toArray() {
        return this.list.getList();
    }
}
