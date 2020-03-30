/*
 *
 *   Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.jbpm.serverless.workflow.api.serializers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.serverless.workflow.api.interfaces.Extension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ExtensionSerializer extends StdSerializer<Extension> {

    private Map<String, Class<? extends Extension>> extensionsMap = new HashMap<>();

    public ExtensionSerializer() {
        this(Extension.class);
    }

    protected ExtensionSerializer(Class<Extension> t) {
        super(t);
    }

    public void addExtension(String extensionId, Class<? extends Extension> extensionClass) {
        this.extensionsMap.put(extensionId, extensionClass);
    }

    @Override
    public void serialize(Extension extension,
                          JsonGenerator gen,
                          SerializerProvider provider) throws IOException {

        String extensionId = extension.getExtensionId();

        if (extensionsMap.containsKey(extensionId)) {
            // serialize after setting default bean values...
            BeanSerializerFactory.instance.createSerializer(provider,
                                                            TypeFactory.defaultInstance().constructType(extensionsMap.get(extensionId))).serialize(extension,
                                                                                                                                                   gen,
                                                                                                                                                   provider);
        } else {
            throw new IllegalArgumentException("Extension handler not registered for: " + extensionId);
        }
    }
}