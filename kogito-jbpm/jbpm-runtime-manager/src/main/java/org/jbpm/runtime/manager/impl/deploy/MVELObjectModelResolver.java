/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.runtime.manager.impl.deploy;

import java.util.Map;

import org.drools.core.util.MVELSafeHelper;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.ObjectModelResolver;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MVELObjectModelResolver implements ObjectModelResolver {

	private static final Logger logger = LoggerFactory.getLogger(MVELObjectModelResolver.class);
	
	public static final String ID = "mvel";
	
	@Override
	public Object getInstance(ObjectModel model, ClassLoader cl, Map<String, Object> contextParams) {
		ParserConfiguration config = new ParserConfiguration();
        config.setClassLoader(cl);
        ParserContext ctx = new ParserContext(config);
        if (contextParams != null) {
            for (Map.Entry<String, Object> entry : contextParams.entrySet()) {
                ctx.addVariable(entry.getKey(), entry.getValue().getClass());
            }
        }

        Object compiledExpression = MVEL.compileExpression(model.getIdentifier(), ctx);
        return MVELSafeHelper.getEvaluator().executeExpression( compiledExpression, contextParams );		
	}

	@Override
	public boolean accept(String resolverId) {
		if (ID.equals(resolverId)) {
			return true;
		}
		logger.debug("Resolver id {} is not accepted by {}", resolverId, this.getClass());
		return false;
	}

}
