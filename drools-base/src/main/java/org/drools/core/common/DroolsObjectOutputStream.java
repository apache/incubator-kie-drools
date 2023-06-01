/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class DroolsObjectOutputStream extends ObjectOutputStream {
    
    private final Map<String, Object> customExtensions = new HashMap<>();

    private final Map<String, Object> clonedByIdentity;

    private final boolean cloning;

    public DroolsObjectOutputStream(OutputStream out) throws IOException {
        this(out, false);
    }

    public DroolsObjectOutputStream(OutputStream out, boolean cloning) throws IOException {
        super(out);
        this.cloning = cloning;
        this.clonedByIdentity = cloning ? new HashMap<>() : null;
    }

    public Map<String, Object> getCustomExtensions() {
        return customExtensions;
    }

    public void addCustomExtensions(String key, Object extension) {
        this.customExtensions.put(key, extension);
    }

    public void addCloneByIdentity(String key, Object identity) {
        clonedByIdentity.put( key, identity );
    }

    public Map<String, Object> getClonedByIdentity() {
        return clonedByIdentity;
    }

    public boolean isCloning() {
        return cloning;
    }

    @Override
    protected void writeObjectOverride( Object obj ) throws IOException {
        super.writeObjectOverride( obj );
    }
}
