/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.meta.org.test;

import java.util.List;

import org.drools.core.metadata.MetadataHolder;

public class SubKlassImpl
    extends KlassImpl
    implements SubKlass, MetadataHolder {

    protected Integer subProp;

    protected List<AnotherKlass> links;

    public SubKlassImpl() {
        super();
    }

    public Integer getSubProp() {
        return subProp;
    }

    public void setSubProp(Integer value) {
        this.subProp = value;
    }

    private final SubKlass_ k = new SubKlass_( this );

    public SubKlass_ get_() {
        return k;
    }

    public List<AnotherKlass> getLinks() {
        return links;
    }

    public void setLinks( List<AnotherKlass> links ) {
        this.links = links;
    }
}

