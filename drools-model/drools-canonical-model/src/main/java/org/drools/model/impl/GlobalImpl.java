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
package org.drools.model.impl;

import org.drools.model.Global;

public class GlobalImpl<T> extends VariableImpl<T> implements Global<T>, ModelComponent {
    private final String pkg;

    public GlobalImpl(Class<T> type, String pkg) {
        super(type);
        this.pkg = pkg;
    }

    public GlobalImpl(Class<T> type, String pkg, String name) {
        super(type, name);
        this.pkg = pkg;
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        GlobalImpl global = ( GlobalImpl ) o;
        if (!getType().equals( global.getType() )) return false;
        if (!getName().equals( global.getName() )) return false;
        return pkg != null ? pkg.equals( global.pkg ) : global.pkg == null;
    }
}
