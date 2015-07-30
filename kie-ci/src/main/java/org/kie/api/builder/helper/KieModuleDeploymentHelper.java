/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.api.builder.helper;

/**
 * This class provides users with the ability to programmatically create
 * kjars and deploy them to the available maven repositories. 
 * </p>
 * Both a fluent and "single-method" interface are provided.
 */
public class KieModuleDeploymentHelper {

    public static final FluentKieModuleDeploymentHelper newFluentInstance() { 
        return new KieModuleDeploymentHelperImpl();
    }
    
    public static final SingleKieModuleDeploymentHelper newSingleInstance() { 
        return new KieModuleDeploymentHelperImpl();
    }
}
