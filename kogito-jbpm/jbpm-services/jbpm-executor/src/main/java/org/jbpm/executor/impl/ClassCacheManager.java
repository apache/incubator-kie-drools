/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandCallback;
import org.kie.api.executor.CommandContext;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.runtime.Closeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple cache to keep classes of commands and callback to not attempt to load them every time.
 *
 */

public class ClassCacheManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassCacheManager.class);
    
    private final Map<String, Command> commandCache = new HashMap<String, Command>();
    private final Map<String, CommandCallback> callbackCache = new HashMap<String, CommandCallback>();  

    /**
     * Finds command by FQCN and if not found loads the class and store the instance in
     * the cache.
     * @param name - fully qualified class name of the command
     * @return initialized class instance
     */
    public Command findCommand(String name, ClassLoader cl) {
        synchronized (commandCache) {
            
            if (!commandCache.containsKey(name)) {
                
                try {
                    Command commandInstance = (Command) Class.forName(name, true, cl).newInstance();
                    commandCache.put(name, commandInstance);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Unknown Command implementation with name '" + name + "'");
                }
    
            } else {
                Command cmd = commandCache.get(name);
                if (!cmd.getClass().getClassLoader().equals(cl)) {
                    commandCache.remove(name);
                    try {
                        Command commandInstance = (Command) Class.forName(name, true, cl).newInstance();
                        commandCache.put(name, commandInstance);
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Unknown Command implementation with name '" + name + "'");
                    } 
                }
            }

       
        }
        return commandCache.get(name);
    }

    /**
     * Finds command callback by FQCN and if not found loads the class and store the instance in
     * the cache.
     * @param name - fully qualified class name of the command callback
     * @return initialized class instance
     */
    public CommandCallback findCommandCallback(String name, ClassLoader cl) {
        synchronized (callbackCache) {
            
            if (!callbackCache.containsKey(name)) {
                try {
                    CommandCallback commandCallbackInstance = (CommandCallback) Class.forName(name, true, cl).newInstance();
                    return commandCallbackInstance;
                    //                            callbackCache.put(name, commandCallbackInstance);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Unknown Command implementation with name '" + name + "'");
                }

            } else {
                CommandCallback cmdCallback = callbackCache.get(name);
                if (!cmdCallback.getClass().getClassLoader().equals(cl)) {
                    callbackCache.remove(name);
                    try {
                        CommandCallback commandCallbackInstance = (CommandCallback) Class.forName(name, true, cl).newInstance();
                        callbackCache.put(name, commandCallbackInstance);
                    } catch (Exception ex) {
                        throw new IllegalArgumentException("Unknown Command implementation with name '" + name + "'");
                    }
                }
            }

        }
        return callbackCache.get(name);
    }

    /**
     * Builds completely initialized list of callbacks for given context.
     * @param ctx contextual data given by execution service
     * @return
     */
    public List<CommandCallback> buildCommandCallback(CommandContext ctx, ClassLoader cl) {
        List<CommandCallback> callbackList = new ArrayList<CommandCallback>();
        if (ctx != null && ctx.getData("callbacks") != null) {
            logger.debug("Callback: {}", ctx.getData("callbacks"));
            String[] callbacksArray = ((String) ctx.getData("callbacks")).split(",");
            List<String> callbacks = (List<String>) Arrays.asList(callbacksArray);
            for (String callbackName : callbacks) {
                CommandCallback handler = findCommandCallback(callbackName.trim(), cl);
                callbackList.add(handler);
            }
        }
        return callbackList;
    }
    
    protected void closeInstance(Object instance) {
    	if (instance == null) {
    		return;
    	}
    	
    	if (instance instanceof Closeable) {
    		((Closeable) instance).close();
    	} else if (instance instanceof Cacheable) {
    		((Cacheable) instance).close();
    	}
    }
        
    public void dispose() {
    	if (commandCache != null) {
    		for (Object command : commandCache.values()) {
    			closeInstance(command);
    		}
    	}
    	
    	if (callbackCache != null) {
    		for (Object callback : callbackCache.values()) {
    			closeInstance(callback);
    		}
    	}
    }

}
