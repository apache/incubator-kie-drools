package org.kie.dmn.api.core;

import java.util.List;

import org.kie.dmn.api.marshalling.DMNExtensionRegister;

public interface DMNCompilerConfiguration {

    List<DMNExtensionRegister> getRegisteredExtensions();
    void addExtensions(List<DMNExtensionRegister> extensions);
    void addExtension(DMNExtensionRegister extension);

    void addListener(AfterGeneratingSourcesListener listener);
    List<AfterGeneratingSourcesListener> getAfterGeneratingSourcesListeners();

}
