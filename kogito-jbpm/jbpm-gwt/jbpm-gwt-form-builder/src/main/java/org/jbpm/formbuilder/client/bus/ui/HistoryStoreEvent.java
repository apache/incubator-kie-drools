package org.jbpm.formbuilder.client.bus.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;

public class HistoryStoreEvent extends GwtEvent<HistoryStoreHandler> {

    public static final Type<HistoryStoreHandler> TYPE = new Type<HistoryStoreHandler>();
    
    private final List<String> tokens = new ArrayList<String>();
    private final ValueChangeHandler<String> valueChangeHandler;
    
    public HistoryStoreEvent(String token) {
        super();
        this.tokens.add(token);
        this.valueChangeHandler = null;
    }

    public HistoryStoreEvent(String... tokens) {
        super();
        if (tokens != null) {
            for (String token : tokens) {
                this.tokens.add(token);
            }
        }
        this.valueChangeHandler = null;
    }

    public HistoryStoreEvent(ValueChangeHandler<String> valueChangeHandler) {
        super();
        this.valueChangeHandler = valueChangeHandler;
    }
    
    public List<String> getTokens() {
        return tokens;
    }
    
    public ValueChangeHandler<String> getValueChangeHandler() {
        return valueChangeHandler;
    }

    @Override
    public Type<HistoryStoreHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HistoryStoreHandler handler) {
        handler.onEvent(this);
    }

}
