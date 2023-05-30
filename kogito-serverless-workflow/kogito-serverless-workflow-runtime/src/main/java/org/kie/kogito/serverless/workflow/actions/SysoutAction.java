/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.actions;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysoutAction extends BaseExpressionAction {

    private static final Logger logger = LoggerFactory.getLogger(SysoutAction.class);

    protected final WorkflowLogLevel logLevel;
    protected final boolean validExpr;

    public SysoutAction(String lang, String expr, String inputVar, String level) {
        super(lang, expr, inputVar);
        logLevel = ConversionUtils.isEmpty(level) ? WorkflowLogLevel.INFO : WorkflowLogLevel.valueOf(level);
        this.validExpr = super.expr.isValid();
    }

    public SysoutAction(String lang, String expr, String inputVar, WorkflowLogLevel logLevel, boolean validExpr) {
        super(lang, expr, inputVar);
        this.logLevel = logLevel;
        this.validExpr = validExpr;
    }

    @Override
    public void execute(KogitoProcessContext context) throws Exception {
        log(validExpr ? evaluate(context, String.class) : expr.asString());
    }

    private void log(String message) {
        switch (logLevel) {
            case TRACE:
                logger.trace(message);
                break;
            case DEBUG:
                logger.debug(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                break;
        }
    }
}
