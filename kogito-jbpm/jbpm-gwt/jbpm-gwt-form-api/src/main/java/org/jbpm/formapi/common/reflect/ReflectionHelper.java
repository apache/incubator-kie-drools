/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formapi.common.reflect;

import com.gwtent.reflection.client.ClassType;
import com.gwtent.reflection.client.Constructor;
import com.gwtent.reflection.client.TypeOracle;

public class ReflectionHelper {

    public static Object newInstance(String klass) throws Exception {
    	if (TypeOracle.Instance == null) { //TypeOracle.Instance is null on server side
    		Class<?> clazz = com.google.gwt.user.client.rpc.impl.ReflectionHelper.loadClass(klass);
    		return com.google.gwt.user.client.rpc.impl.ReflectionHelper.newInstance(clazz);
    	} else {
	   		ClassType<?> classType = TypeOracle.Instance.getClassType(klass);
	        Constructor<?> constructor = classType.findConstructor();
	        return constructor.newInstance();
    	}
    }
}
