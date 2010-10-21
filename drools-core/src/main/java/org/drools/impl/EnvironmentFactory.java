/**
 * Copyright 2010 JBoss Inc
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

package org.drools.impl;

import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.marshalling.impl.SerializablePlaceholderResolverStrategy;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;

public class EnvironmentFactory {
	
	private static ThreadLocal<Environment> environment = new ThreadLocal<Environment>();
	
	public static Environment newEnvironment() {
            Environment env = new EnvironmentImpl();
            env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{new SerializablePlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)});
	    return env;
//		Environment environment = EnvironmentFactory.environment.get();
//		if (environment == null) {
//			environment = new EnvironmentImpl();
//			EnvironmentFactory.environment.set(environment);
//		}
//		return environment;
	}

}
