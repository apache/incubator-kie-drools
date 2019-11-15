/*
 * Copyright 2005 JBoss Inc
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

package org.drools.ruleunit.impl;

import org.drools.core.base.DefaultKnowledgeHelper;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.spi.Activation;
import org.drools.core.util.bitmask.BitMask;
import org.drools.ruleunit.datasources.DataSourceFactHandle;
import org.drools.ruleunit.executor.RuleUnitSessionImpl;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.ruleunit.RuleUnit;

public class RuleUnitKnowledgeHelper extends DefaultKnowledgeHelper {

    RuleUnitSessionImpl ruleUnitsession;

    public RuleUnitKnowledgeHelper( RuleUnitSessionImpl ruleUnitsession ) {
        super(ruleUnitsession.getSession());
        this.ruleUnitsession = ruleUnitsession;
    }

    public RuleUnitKnowledgeHelper( Activation activation, RuleUnitSessionImpl ruleUnitsession ) {
        super(activation, ruleUnitsession.getSession());
        this.ruleUnitsession = ruleUnitsession;
    }

    @Override
    public void run( Object ruleUnit ) {
        ruleUnitsession.getRuleUnitExecutor().switchToRuleUnit( (RuleUnit) ruleUnit, activation );
    }

    @Override
    public void run(Class<?> ruleUnitClass) {
        ruleUnitsession.getRuleUnitExecutor().switchToRuleUnit( (Class<? extends RuleUnit>) ruleUnitClass, activation );
    }

    @Override
    public void guard(Object ruleUnit) {
        ruleUnitsession.getRuleUnitExecutor().guardRuleUnit( (RuleUnit) ruleUnit, activation );
    }

    @Override
    public void guard(Class<?> ruleUnitClass) {
        ruleUnitsession.getRuleUnitExecutor().guardRuleUnit( (Class<? extends RuleUnit>) ruleUnitClass, activation );
    }

    @Override
    public void update( final FactHandle handle, BitMask mask, Class modifiedClass ) {
        DataSourceFactHandle h = lookupDSFactHandle( (InternalFactHandle) handle );

        if (h != null && h.getDataSource() != null ) {
            // This handle has been insert from a datasource, so update it
            h.getDataSource().update( handle,
                    (( InternalFactHandle ) handle).getObject(),
                    mask,
                    modifiedClass,
                    this.activation );
            return;
        }

        super.update( handle, mask, modifiedClass );
    }

    private DataSourceFactHandle lookupDSFactHandle( InternalFactHandle h ) {
        if (h == null) {
            return null;
        }
        if (h instanceof DataSourceFactHandle) {
            return (DataSourceFactHandle) h;
        }
        return lookupDSFactHandle( h.getParentHandle() );
    }
}
