package org.kie.api.runtime;

import java.util.Map;

import org.kie.api.KieBase;

public interface RequestContext extends Context {

    Object getResult();

    void setResult(Object result);

    RequestContext with(KieBase kieBase);

    RequestContext with(KieSession kieSession);

    Context getConversationContext();

    Context getApplicationContext();

    Map<String, Object> getOutputs();

    void setOutput(String identifier, Object value);

    void removeOutput(String identifier);

    @SuppressWarnings("unchecked")
    default <T> T getOutput(String identifier) {
        return (T) getOutputs().get(identifier);
    }

    default boolean hasOutput(String identifier) {
        return getOutputs().containsKey(identifier);
    }

    static RequestContext create() {
        return create(RequestContext.class.getClassLoader());
    }

    static RequestContext create(ClassLoader classLoader) {
        try {
            return (RequestContext) Class.forName("org.drools.commands.RequestContextImpl", true, classLoader).getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance RequestContext, please add org.drools:drools-commands to your classpath", e);
        }
    }
}
