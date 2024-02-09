/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.lang.types.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.util.EvalHelper.PropertyValueResult;

/**
 * Internal class for an immutable DTO, implementing FEELPropertyAccessible interface, and wrapping a generic POJO.
 */
public class ImmutableFPAWrappingPOJO implements FEELPropertyAccessible {

    private final Object wrapping;
    private final Map<String, Method> properties = new HashMap<>();
    private volatile Map<String, Object> asMap;

    public ImmutableFPAWrappingPOJO(Object wrapping) {
        this.wrapping = wrapping;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(wrapping.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if ("class".equals(pd.getName())) {
                    continue; // skip for .getClass() accessor.
                }
                Method readMethod = pd.getReadMethod();
                FEELProperty ann = readMethod.getAnnotation(FEELProperty.class);
                if (ann != null) {
                    properties.put(ann.value(), readMethod);
                } else {
                    properties.put(pd.getName(), readMethod);
                }
            }
        } catch (IntrospectionException e) {
            throw new AccessorRuntimeException("Unable to introspect: " + wrapping, e);
        }
    }

    private Map<String, Object> getAsMap() {
        Map<String, Object> localView = asMap;
        if (localView == null) {
            synchronized (this) {
                localView = asMap;
                if (localView == null) {
                    asMap = localView = new HashMap<>();
                    for (Entry<String, Method> kv : properties.entrySet()) {
                        try {
                            asMap.put(kv.getKey(), kv.getValue().invoke(wrapping));
                        } catch (Exception e) {
                            throw new AccessorRuntimeException("Unable to access property :" + kv.getKey(), e);
                        }
                    }
                }
            }
        }
        return localView;
    }

    @Override
    public AbstractPropertyValueResult getFEELProperty(String property) {
        Map<String, Object> localview = getAsMap();
        if (localview.containsKey(property)) {
            return PropertyValueResult.ofValue(localview.get(property));
        } else {
            return PropertyValueResult.notDefined();
        }
    }

    @Override
    public void setFEELProperty(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> allFEELProperties() {
        return getAsMap();
    }

    @Override
    public void fromMap(Map<String, Object> values) {
        throw new UnsupportedOperationException();
    }

    public static class AccessorRuntimeException extends RuntimeException {

        public AccessorRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }

    }

}
