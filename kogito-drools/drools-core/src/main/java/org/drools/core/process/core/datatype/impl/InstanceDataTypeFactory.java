/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.process.core.datatype.impl;

import org.drools.core.process.core.datatype.DataType;
import org.drools.core.process.core.datatype.DataTypeFactory;

/**
 * A data type factory that always returns the same instance of a given class.
 */
public class InstanceDataTypeFactory implements DataTypeFactory {

    private static final long serialVersionUID = 510l;
    
    private Class<?> dataTypeClass;
    private DataType instance;

    public InstanceDataTypeFactory(final Class<?> dataTypeClass) {
        this.dataTypeClass = dataTypeClass;
    }

    public DataType createDataType() {
        if (this.instance == null) {
            try {
                this.instance = (DataType) this.dataTypeClass.newInstance();
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(
                        "Could not create data type for class "
                                + this.dataTypeClass, e);
            } catch (final InstantiationException e) {
                throw new RuntimeException(
                        "Could not create data type for class "
                                + this.dataTypeClass, e);
            }
        }
        return this.instance;
    }

}
