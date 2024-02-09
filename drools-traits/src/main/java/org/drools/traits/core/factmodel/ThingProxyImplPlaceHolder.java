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
package org.drools.traits.core.factmodel;

import java.io.Serializable;
import java.util.BitSet;

import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.TraitType;
import org.drools.base.factmodel.traits.TraitableBean;

public class ThingProxyImplPlaceHolder<K> extends TraitProxyImpl implements Thing<K>,
                                                                            TraitType, Serializable {

    private static final long serialVersionUID = 6017272084020598391L;

    private transient static ThingProxyImplPlaceHolder singleton;

    public static ThingProxyImplPlaceHolder getThingPlaceHolder() {
        if ( singleton == null ) {
            singleton = new ThingProxyImplPlaceHolder();
        }
        return singleton;
    }

    public ThingProxyImplPlaceHolder() {
        setTypeCode( new BitSet() );
    }

    @Override
    public boolean _isVirtual() {
        return true;
    }

    public K getCore() {
        return null;
    }

    public boolean isTop() {
        return true;
    }

    @Override
    public String _getTraitName() {
        return Thing.class.getName();
    }

    @Override
    public TraitableBean getObject() {
        return null;
    }

    @Override
    public boolean equals( Object o ) {
        return o == singleton;
    }

    @Override
    public int hashCode() {
        return Thing.class.hashCode() ^ 31;
    }
}