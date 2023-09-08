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
package org.kie.dmn.xls2dmn.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DTHeaderInfo {

    private final String sheetName;
    private final List<String> original;
    private final int hIndex;
    private final Collection<String> requiredInput;
    private final Collection<String> requiredDecision;

    public DTHeaderInfo(String sheetName, List<String> original, int hIndex, List<String> requiredInput, List<String> requiredDecision) {
        this.sheetName = sheetName;
        this.original = new ArrayList<>(original);
        this.hIndex = hIndex;
        this.requiredInput = new ArrayList<>(requiredInput);
        this.requiredDecision = new ArrayList<>(requiredDecision);
    }

    @Override
    public String toString() {
        return "DTHeaderInfo [hIndex=" + hIndex + ", original=" + original + ", requiredDecision=" + requiredDecision + ", requiredInput=" + requiredInput + ", sheetName=" + sheetName + "]";
    }

    public String getSheetName() {
        return sheetName;
    }

    public List<String> getOriginal() {
        return original;
    }

    public int gethIndex() {
        return hIndex;
    }

    public Collection<String> getRequiredInput() {
        return requiredInput;
    }

    public Collection<String> getRequiredDecision() {
        return requiredDecision;
    }

}
