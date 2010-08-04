/*
 *  Copyright 2010 salaboy.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.grid;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author salaboy
 */
public class DirectoryNode {
    private String id;
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<Class<?>, Object>();

    public DirectoryNode() {
        this.id = UUID.randomUUID().toString();
    }

    public DirectoryNode(String id) {
        this.id = id;
    }

    public <T> T get(Class<T> interfaceClass) {
        synchronized (interfaceClass) {
            Object service = services.get(interfaceClass);
            if (service == null) {
                try {
                    Class<?> implementingClass = Class.forName(interfaceClass.getCanonicalName() + "Impl");

                    service = implementingClass.newInstance();
                    services.put(interfaceClass, service);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return interfaceClass.cast(service);
        }
    }

    public <T> void set(Class<T> interfaceClass, T provider) {
        synchronized (interfaceClass) {
            services.put(interfaceClass, provider);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
