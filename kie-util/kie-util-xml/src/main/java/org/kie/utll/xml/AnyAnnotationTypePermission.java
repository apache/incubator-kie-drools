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
package org.kie.utll.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.annotations.XStreamInclude;
import com.thoughtworks.xstream.security.TypePermission;

/**
 * Permission for any type which is annotated with an XStream annotation.
 * This presumes that because the class has an XStream annotation, it was designed with XStream in mind,
 * and therefore it is not vulnerable. Jackson and JAXB follow this philosophy too.
 */
// TODO Replace with upstream one when upgrading to XStream 1.5.0
// See https://github.com/x-stream/xstream/pull/99)
public class AnyAnnotationTypePermission implements TypePermission {

    @Override
    public boolean allows(final Class type) {
        if (type == null) {
            return false;
        }
        return type.isAnnotationPresent(XStreamAlias.class)
                || type.isAnnotationPresent(XStreamAliasType.class)
                || type.isAnnotationPresent(XStreamInclude.class);
    }

}
