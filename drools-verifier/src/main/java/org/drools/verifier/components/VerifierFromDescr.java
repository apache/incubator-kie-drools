/**
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierFromDescr extends PatternComponentSource {

    private VerifierComponentType dataSourceType;
    private String                dataSourcePath;

    public VerifierFromDescr(Pattern pattern) {
        super( pattern );
    }

    public String getDataSourcePath() {
        return dataSourcePath;
    }

    public void setDataSourcePath(String path) {
        this.dataSourcePath = path;
    }

    public VerifierComponentType getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(VerifierComponentType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.FROM;
    }
}
