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

package org.drools.beliefs.bayes.assembler;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.lang.descr.BaseDescr;
import org.kie.api.io.Resource;

public class BayesNetworkAssemblerError extends DroolsError {
    private String    message;

    public BayesNetworkAssemblerError(Resource resource,
                                      final String message) {
        super( resource );
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int[] getLines() {
        return new int[0];
    }
}
