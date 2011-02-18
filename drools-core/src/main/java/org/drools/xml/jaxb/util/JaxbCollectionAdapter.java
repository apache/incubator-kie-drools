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

package org.drools.xml.jaxb.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JaxbCollectionAdapter extends XmlAdapter<JaxbListWrapper, Collection> {

    @Override
    public JaxbListWrapper marshal(Collection v) throws Exception {
        if ( v == null ) {
            return new JaxbListWrapper( new Object[0] ); 
        }
        return new JaxbListWrapper( v.toArray( new Object[v.size()]) );
    }

    @Override
    public Collection unmarshal(JaxbListWrapper v) throws Exception {
        return Arrays.asList( v.getElements() );
    }

}
