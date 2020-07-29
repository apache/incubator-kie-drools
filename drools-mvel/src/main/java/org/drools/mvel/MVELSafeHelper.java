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

import org.kie.internal.security.KiePolicyHelper;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;

public class MVELSafeHelper {

    private static class MVELEvaluatorHolder {

        private static final MVELEvaluator evaluator = KiePolicyHelper.isPolicyEnabled() ?
                new SafeMVELEvaluator() :
                new UnsafeMVELEvaluator();
    }

    private MVELSafeHelper() {
    }

    public static MVELEvaluator getEvaluator() {
        return MVELEvaluatorHolder.evaluator;
    }
}
