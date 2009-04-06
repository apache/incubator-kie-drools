package org.drools.runtime.pipeline;

/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
import java.util.Map;

import org.drools.runtime.CommandExecutor;

public interface PipelineContext {

    ClassLoader getClassLoader();

    Map<String, Object> getProperties();

    void setResult(Object result);

    Object getResult();

    ResultHandler getResultHandler();
    
    CommandExecutor getCommandExecutor();

}
