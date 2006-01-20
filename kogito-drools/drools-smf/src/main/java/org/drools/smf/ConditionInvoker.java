package org.drools.smf;

import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

/**
 * @deprecated Not used in Drools 3.0
 *
 */
public interface ConditionInvoker extends Invoker
{
    public boolean invoke(Tuple tuple,
                          Declaration[] decls,
                          Map applicationData) throws Exception;
}
