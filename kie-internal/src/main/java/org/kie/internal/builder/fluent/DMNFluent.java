package org.kie.internal.builder.fluent;

/**
 * See {@link DMNRuntimeFluent}
 */
public interface DMNFluent<T extends DMNFluent, U> {

    T setInput(String name, Object value);

    T setActiveModel(String namespace, String modelName);

    T setActiveModel(String resourcePath);

    T getModel(String namespace, String modelName);

    T getModel(String resourcePath);

    T evaluateModel();

    T getAllContext();

    T getDecisionResults();

    T getMessages();

    U end();
}
