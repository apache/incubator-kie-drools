package org.drools.smf;

import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;

public interface ConsequenceInvoker extends Invoker
{
    public void invoke(Tuple tuple,
                       Declaration[] declarations,
                       KnowledgeHelper drools,
                       Map applicationData) throws Exception;
}
