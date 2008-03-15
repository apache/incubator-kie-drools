package org.drools.spi;

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

import java.io.Externalizable;

import org.drools.reteoo.ReteTuple;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;

public interface PropagationContext
    extends
    Externalizable {

    public static final int ASSERTION     = 0;
    public static final int RETRACTION    = 1;
    public static final int MODIFICATION  = 2;
    public static final int RULE_ADDITION = 3;
    public static final int RULE_REMOVAL  = 4;

    public long getPropagationNumber();

    public Rule getRuleOrigin();

    public Activation getActivationOrigin();

    public int getType();

    public int getActiveActivations();

    public int getDormantActivations();

    public void addRetractedTuple(Rule rule,
                                  Activation activation);

    public Activation removeRetractedTuple(Rule rule,
                                           ReteTuple tuple);

    public void clearRetractedTuples();

    public void releaseResources();

    public EntryPoint getEntryPoint();

}