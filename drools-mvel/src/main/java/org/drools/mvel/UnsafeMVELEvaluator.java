/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import org.drools.core.util.MVELExecutor;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.mvel2.util.Soundex;

public class UnsafeMVELEvaluator extends RawMVELEvaluator implements MVELExecutor {

    @Override
    public String soundex( String s ) {
        return Soundex.soundex(s);
    }
}
