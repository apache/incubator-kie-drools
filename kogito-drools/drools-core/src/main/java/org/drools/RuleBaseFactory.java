package org.drools;

import java.util.UUID;

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

/**
 * This is a utility to create rule bases based on the type of engine you wish to use.
 * 
 */
public class RuleBaseFactory {

    private static final RuleBaseFactory INSTANCE = new RuleBaseFactory();

    private RuleBaseFactory() {
    }

    public static RuleBaseFactory getInstance() {
        return RuleBaseFactory.INSTANCE;
    }

    /** Create a new default rule base (RETEOO type engine) */
    public static RuleBase newRuleBase() {
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    public static RuleBase newRuleBase( final String rulebaseId ) {
        return RuleBaseFactory.newRuleBase( rulebaseId,
                                            RuleBase.RETEOO,
                                            null );
    }

    public static RuleBase newRuleBase(final RuleBaseConfiguration config) {
        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    public static RuleBase newRuleBase(final String rulebaseId,
                                       final RuleBaseConfiguration config) {
        return RuleBaseFactory.newRuleBase( rulebaseId,
                                            RuleBase.RETEOO,
                                            config );
    }

    public static RuleBase newRuleBase(final String rulebaseId,
                                       final int type) {
        return RuleBaseFactory.newRuleBase( rulebaseId,
                                            type,
                                            null );
    }

    public static RuleBase newRuleBase(final int type) {
        return RuleBaseFactory.newRuleBase( type,
                                            null );
    }

    /** Create a new RuleBase of the appropriate type */
    public static RuleBase newRuleBase(final int type,
                                       final RuleBaseConfiguration config) {
        return RuleBaseFactory.newRuleBase( UUID.randomUUID().toString(),
                                            type,
                                            config );
    }

    /** Create a new RuleBase of the appropriate type */
    public static RuleBase newRuleBase(final String rulebaseId,
                                       final int type,
                                       final RuleBaseConfiguration config) {
        switch ( type ) {
            case RuleBase.RETEOO :

                return new org.drools.reteoo.ReteooRuleBase( rulebaseId,
                                                             config );
            default :
                throw new IllegalArgumentException( "Unknown engine type: " + type );

        }
    }

}
