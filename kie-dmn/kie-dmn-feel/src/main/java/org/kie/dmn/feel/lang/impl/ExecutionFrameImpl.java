package org.kie.dmn.feel.lang.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.EvalHelper.PropertyValueResult;

public class ExecutionFrameImpl
        implements ExecutionFrame {

    private ExecutionFrame parentFrame;

    private Map<String, Object> variables;
    private Object rootObject;

    public ExecutionFrameImpl(ExecutionFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.variables = new HashMap<>();
    }

    public ExecutionFrameImpl(ExecutionFrame parentFrame, int size) {
        this.parentFrame = parentFrame;
        this.variables = new HashMap<>(size);
    }

    public ExecutionFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(ExecutionFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    @Override
    public Object getValue(String symbol) {
        symbol = EvalHelper.normalizeVariableName( symbol );
        if (rootObject != null) {
            PropertyValueResult dv = EvalHelper.getDefinedValue(rootObject, symbol);
            if (dv.isDefined()) {
                return dv.getValueResult().getOrElse(null);
            }
        }
        if ( variables.containsKey( symbol ) ) {
            return variables.get( symbol );
        }
        if ( parentFrame != null ) {
            return parentFrame.getValue( symbol );
        }
        return null;
    }

    @Override
    public boolean isDefined(String symbol) {
        symbol = EvalHelper.normalizeVariableName( symbol );
        if (rootObject != null) {
            if (EvalHelper.getDefinedValue(rootObject, symbol).isDefined()) {
                return true;
            } else {
                // do nothing! it might be shaded at this level for "item" or being in the parent frame.
            }
        }
        if ( variables.containsKey( symbol ) ) {
            return true;
        }
        if ( parentFrame != null ) {
            return parentFrame.isDefined( symbol );
        }
        return false;
    }

    @Override
    public void setValue(String symbol, Object value) {
        this.variables.put( EvalHelper.normalizeVariableName( symbol ), value );
    }

    @Override
    public Map<String, Object> getAllValues() {
        return this.variables;
    }

    @Override
    public void setRootObject(Object v) {
        this.rootObject = v;
    }

    @Override
    public Object getRootObject() {
        return rootObject;
    }
}
