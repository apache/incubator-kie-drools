/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.builder;

import java.io.File;

import org.kie.util.ServiceRegistryImpl;

/**
 * A factory for Kie artifacts
 *
 */
public interface KieFactory {
    GAV newGav(String groupId, String artifactId, String version);
    
    KieFileSystem newKieFileSystem( );
    
    KieProject newKieProject();

    public static class Factory {
        private static KieFactory INSTANCE;
        static {
            try {
                INSTANCE = ServiceRegistryImpl.getInstance().get( KieFactory.class );
            } catch (Exception e) {
                throw new RuntimeException("Unable to instance KieFactory", e);
            }
        }
        public static KieFactory get() {
            return INSTANCE;
        }
    }

}
