/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.xml.jaxb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.drools.core.xml.jaxb.util.JaxbListWrapper.JaxbWrapperType;

public class JaxbListAdapter extends XmlAdapter<JaxbListWrapper, Collection> {

    @Override
    public JaxbListWrapper marshal( Collection v ) throws Exception {
        if( v == null ) {
            return new JaxbListWrapper(new Object[0]);
        } else if( v instanceof List ) {
            return new JaxbListWrapper(v.toArray(new Object[v.size()]), JaxbWrapperType.LIST);
        } else if( v instanceof Set ) {
            return new JaxbListWrapper(v.toArray(new Object[v.size()]), JaxbWrapperType.SET);
        } else {
            throw new UnsupportedOperationException("Unsupported collection type: " + v.getClass());
        }
    }

    @Override
    public Collection unmarshal( JaxbListWrapper v ) throws Exception {
        if( v.getType() == null ) {
            // the Arrays.asList() impl is non-modifiable
            return new ArrayList(Arrays.asList(v.getElements()));
        } else {
            switch ( v.getType() ) {
            case LIST:
                return new ArrayList(Arrays.asList(v.getElements()));
            case SET:
                return new HashSet(Arrays.asList(v.getElements()));
            default:
                throw new UnsupportedOperationException("Unsupported collection type: " + v.getType());
            }
        }
    }

}
