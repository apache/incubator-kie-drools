/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.guided.dtable.shared.conversion;

/**
 * A container for a new Asset created during the conversion process
 */
public class ConversionAsset {

    private static final long serialVersionUID = 540L;

    private String uuid;
    private String format;

    public ConversionAsset() {
    }

    public ConversionAsset( final String uuid,
                            final String format ) {
        this.uuid = uuid;
        this.format = format;
    }

    public String getUUID() {
        return uuid;
    }

    public String getFormat() {
        return format;
    }

}