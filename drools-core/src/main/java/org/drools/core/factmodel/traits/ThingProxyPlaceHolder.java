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

package org.drools.core.factmodel.traits;

import java.io.Serializable;
import java.util.BitSet;

public class ThingProxyPlaceHolder<K> extends TraitProxy implements Thing<K>, TraitType, Serializable {

    private transient static ThingProxyPlaceHolder singleton;

    public static ThingProxyPlaceHolder getThingPlaceHolder() {
        if ( singleton == null ) {
            singleton = new ThingProxyPlaceHolder();
        }
        return singleton;
    }

    public ThingProxyPlaceHolder() {
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