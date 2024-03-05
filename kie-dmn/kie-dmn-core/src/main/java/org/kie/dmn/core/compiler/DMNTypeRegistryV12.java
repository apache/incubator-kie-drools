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
package org.kie.dmn.core.compiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;

public class DMNTypeRegistryV12 extends DMNTypeRegistryAbstract {

    private static final DMNType UNKNOWN = SimpleTypeImpl.UNKNOWN_DMNTYPE(KieDMNModelInstrumentedBase.URI_FEEL);

    public DMNTypeRegistryV12() {
        super(Collections.emptyMap());
    }

    public DMNTypeRegistryV12(Map<String, QName> aliases) {
        super(aliases);
    }

    @Override
    public DMNType unknown() {
        return UNKNOWN;
    }

    /** 
     * DMN v1.2 spec, chapter 7.3.2 ItemDefinition metamodel
     * FEEL built-in data types: number, string, boolean, days and time duration, years and months duration, time, and date and time. Was missing from spec document: date, Any, list, function, context.
     */
    public static final List<BuiltInType> ITEMDEF_TYPEREF_FEEL_BUILTIN = Collections.unmodifiableList(Arrays.asList(BuiltInType.NUMBER,
                                                                                                                    BuiltInType.STRING,
                                                                                                                    BuiltInType.BOOLEAN,
                                                                                                                    BuiltInType.DURATION,
                                                                                                                    BuiltInType.DATE,
                                                                                                                    BuiltInType.TIME,
                                                                                                                    BuiltInType.DATE_TIME,
                                                                                                                    BuiltInType.UNKNOWN,
                                                                                                                    BuiltInType.LIST,
                                                                                                                    BuiltInType.FUNCTION,
                                                                                                                    BuiltInType.CONTEXT));

    @Override
    public String feelNS() {
        return KieDMNModelInstrumentedBase.URI_FEEL;
    }

}
