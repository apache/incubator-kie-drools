package org.drools.clp;

import org.drools.clp.valuehandlers.ListValueHandler;

public class LispData
    implements
    LispList {

    private ListValueHandler      list;
    private ExecutionBuildContext context;

    public LispData() {
        this.list = new ListValueHandler();
    }

    public void add(ValueHandler valueHandler) {
        this.list.add( valueHandler );
    }

    public LispList createList() {
        LispList list = new LispForm();

        list.setContext( this.context );

        return list;
    }

    public ValueHandler getValueHandler() {
        return this.list;
    }

    public void setContext(ExecutionBuildContext context) {
        this.context = context;
    }

    public ValueHandler[] toArray() {
        return this.list.getList();
    }
}
