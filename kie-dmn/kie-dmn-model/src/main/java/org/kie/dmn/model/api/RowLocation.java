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
package org.kie.dmn.model.api;

import javax.xml.stream.Location;

public class RowLocation implements Location {

    private int lineNumber;
    private String publicId;
    private String systemId;

    public RowLocation(Location from) {
        this.lineNumber = from.getLineNumber();
        this.publicId = from.getPublicId();
        this.systemId = from.getSystemId();
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

    @Override
    public int getColumnNumber() {
        return -1;
    }

    @Override
    public int getCharacterOffset() {
        return -1;
    }

    @Override
    public String getPublicId() {
        return this.publicId;
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RowLocation [getLineNumber()=").append(getLineNumber()).append(", getColumnNumber()=").append(getColumnNumber()).append(", getCharacterOffset()=").append(getCharacterOffset()).append(
                                                                                                                                                                                                               ", getPublicId()=")
               .append(getPublicId()).append(", getSystemId()=").append(getSystemId()).append("]");
        return builder.toString();
    }
}