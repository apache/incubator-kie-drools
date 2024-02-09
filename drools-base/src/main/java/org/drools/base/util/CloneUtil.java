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
package org.drools.base.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.drools.base.common.DroolsObjectInputStream;
import org.drools.base.common.DroolsObjectOutputStream;

public class CloneUtil {

    public static <T extends Externalizable> T deepClone(T origin) {
        return origin == null ? null : deepClone(origin, origin.getClass().getClassLoader());
    }

    public static <T extends Externalizable> T deepClone(T origin, ClassLoader classLoader) {
        return deepClone(origin, classLoader, Collections.emptyMap());
    }

    public static <T extends Externalizable> T deepClone(T origin, ClassLoader classLoader, Map<String, Object> cloningResources) {
        if (origin == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DroolsObjectOutputStream oos = new DroolsObjectOutputStream(baos, true);
            if ( cloningResources != null ) { cloningResources.forEach( (k, v) -> oos.addCustomExtensions(k, v) ); }
            oos.writeObject(origin);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            DroolsObjectInputStream ois = new DroolsObjectInputStream(bais, classLoader, oos.getClonedByIdentity());
            if ( cloningResources != null ) { cloningResources.forEach( (k, v) -> ois.addCustomExtensions(k, v) ); }
            Object deepCopy = ois.readObject();
            return (T)deepCopy;
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }
    }
}
