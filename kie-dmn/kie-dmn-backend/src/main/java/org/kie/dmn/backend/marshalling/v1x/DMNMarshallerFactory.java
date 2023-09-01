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
package org.kie.dmn.backend.marshalling.v1x;

import java.util.List;

import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.DMNMarshaller;

public final class DMNMarshallerFactory {

    public static DMNMarshaller newDefaultMarshaller() {
        return new XStreamMarshaller();
    }

    public static DMNMarshaller newMarshallerWithExtensions(List<DMNExtensionRegister> extensionElementRegisters) {
        return new XStreamMarshaller(extensionElementRegisters);
    }

    private DMNMarshallerFactory() {
        // Constructing instances is not allowed for this class
    }
}
