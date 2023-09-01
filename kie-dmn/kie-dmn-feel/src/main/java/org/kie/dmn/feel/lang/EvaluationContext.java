package org.kie.dmn.feel.lang;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;

public interface EvaluationContext {

    void enterFrame();

    void exitFrame();

    EvaluationContext current();

    void setValue(String name, Object value );

    Object getValue(String name );

    Object getValue(String[] name );

    boolean isDefined( String name );

    boolean isDefined( String[] name );

    Map<String, Object> getAllValues();

    DMNRuntime getDMNRuntime();

    ClassLoader getRootClassLoader();

    void notifyEvt(Supplier<FEELEvent> event);


    Collection<FEELEventListener> getListeners();

    void setRootObject(Object v);

    Object getRootObject();

}
