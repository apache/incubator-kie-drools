/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.workbench.models.testscenarios.shared;

/**
 * Returned by the builder.
 */
public class BuilderResultLine {

    private String assetFormat;
    private String assetName;
    private String uuid;
    private String message;

    public String getAssetFormat() {
        return assetFormat;
    }

    public BuilderResultLine setAssetFormat( final String assetFormat ) {
        this.assetFormat = assetFormat;
        return this;
    }

    public String getAssetName() {
        return assetName;
    }

    public BuilderResultLine setAssetName( final String assetName ) {
        this.assetName = assetName;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public BuilderResultLine setUuid( final String uuid ) {
        this.uuid = uuid;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BuilderResultLine setMessage( final String message ) {
        this.message = message;
        return this;
    }

    public String toString() {
        return "Asset: "
                + assetName
                + "."
                + assetFormat
                + "\n"
                + "Message: "
                + message
                + "\n"
                + "UUID: "
                + uuid;
    }

}
