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
package org.drools.compiler.kie.builder.impl.event;

import org.kie.api.builder.KieScanner.Status;
import org.kie.api.event.kiescanner.KieScannerStatusChangeEvent;

public class KieScannerStatusChangeEventImpl  implements KieScannerStatusChangeEvent {
    private final Status status;
    public KieScannerStatusChangeEventImpl(Status status) {
        this.status = status;
    }
    public Status getStatus() {
        return status;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof KieScannerStatusChangeEventImpl)) {
            return false;
        }
        KieScannerStatusChangeEventImpl other = (KieScannerStatusChangeEventImpl) obj;
        if (status != other.status) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KieScannerStatusChangeEvent [status=").append(status).append("]");
        return builder.toString();
    }
}