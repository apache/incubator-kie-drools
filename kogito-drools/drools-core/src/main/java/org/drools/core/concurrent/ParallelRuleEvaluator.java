/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.drools.core.common.DefaultAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.spi.Activation;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.internal.concurrent.ExecutorProviderFactory;

public class ParallelRuleEvaluator extends AbstractRuleEvaluator implements RuleEvaluator {

    private static final RuleAgendaItem POISON_PILL = new RuleAgendaItem();

    private final int evaluatorsNr = RuleBasePartitionId.PARALLEL_PARTITIONS_NUMBER;

    private RuleEvaluatorCallable[] evaluators = new RuleEvaluatorCallable[evaluatorsNr];
    private Future<Integer>[] results = new Future[evaluatorsNr];

    private AgendaFilter filter;
    private int fireCount;
    private int fireLimit;

    public ParallelRuleEvaluator( DefaultAgenda agenda ) {
        super(agenda);
        for (int i = 0; i < evaluatorsNr; i++) {
            evaluators[i] = new RuleEvaluatorCallable();
        }
    }

    private static class Completion {
        private static final CompletionService<Integer> service = ExecutorProviderFactory.getExecutorProvider().getCompletionService();
    }

    @Override
    public int evaluateAndFire( AgendaFilter filter,
                                int fireCount,
                                int fireLimit,
                                InternalAgendaGroup group ) {
        this.filter = filter;
        this.fireCount = fireCount;
        this.fireLimit = fireLimit;

        Activation[] activations = group.getActivations();
        for ( Activation activation : activations ) {
            RuleAgendaItem item = (RuleAgendaItem) activation;
            int index = item.getPartition().getParallelEvaluationSlot();
            RuleEvaluatorCallable evaluator = evaluators[index];
            evaluator.enqueue( item );
            if ( !evaluator.running ) {
                evaluator.running = true;
                results[index] = Completion.service.submit( evaluator );
            }
        }

        int localFireCount = 0;
        for (int i = 0; i < evaluatorsNr; i++) {
            if (results[i] != null) {
                try {
                    evaluators[i].enqueue( POISON_PILL );
                    localFireCount += results[i].get();
                } catch (Exception e) {
                    throw new RuntimeException( e );
                } finally {
                    results[i] = null;
                }
            }
        }

        return localFireCount;
    }

    @Override
    public KnowledgeHelper getKnowledgeHelper() {
        throw new UnsupportedOperationException();
    }

    public class RuleEvaluatorCallable implements Callable<Integer> {
        private final BlockingQueue<RuleAgendaItem> queue = new LinkedBlockingQueue<>();

        private final KnowledgeHelper knowledgeHelper = newKnowledgeHelper();

        private boolean running = false;

        @Override
        public Integer call() {
            int count = 0;
            while (true) {
                try {
                    RuleAgendaItem item = queue.take();
                    if (item == POISON_PILL) {
                        break;
                    }
                    count += internalEvaluateAndFire( filter, fireCount, fireLimit, item );
                } catch (InterruptedException e) {
                    throw new RuntimeException( e );
                }
            }
            running = false;
            return count;
        }

        private void enqueue(RuleAgendaItem item) {
            queue.offer( item );
        }
    }
}
