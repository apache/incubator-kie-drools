/*
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
package org.kie.kogito.event.process;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public enum KogitoMarshallEventFlag {
    RETRIGGER(1),
    CLOUDEVENT_ID(2),
    FUNCTION_ARGS(4);

    private int value;

    KogitoMarshallEventFlag(int value) {
        this.value = value;
    }

    public static int buildFlags(Set<KogitoMarshallEventFlag> flags) {
        int result = 0;
        for (KogitoMarshallEventFlag flag : flags) {
            result |= flag.value;
        }
        return result;
    }

    public static Set<KogitoMarshallEventFlag> buildFlagsSet(Integer flags) {
        if (flags == null) {
            return EnumSet.noneOf(KogitoMarshallEventFlag.class);
        }
        Set<KogitoMarshallEventFlag> result = new HashSet<>();
        for (KogitoMarshallEventFlag flag : KogitoMarshallEventFlag.values()) {
            if ((flags & flag.value) > 0) {
                result.add(flag);
            }
        }
        return result;
    }

}
