/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.session;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.kie.internal.utils.KieHelper;

@RunWith(Parameterized.class)
public abstract class AbstractConcurrentSessionTest {

    protected final boolean enforcedJitting;
    protected final boolean serializeKieBase;

    @Parameterized.Parameters(name = "Enforced jitting={0}, Serialize KieBase={1}")
    public static List<Boolean[]> getTestParameters() {
        return Arrays.asList(
                new Boolean[]{false, false},
                new Boolean[]{false, true},
                new Boolean[]{true, false},
                new Boolean[]{true, true});
    }

    public AbstractConcurrentSessionTest(final boolean enforcedJitting, final boolean serializeKieBase) {
        this.enforcedJitting = enforcedJitting;
        this.serializeKieBase = serializeKieBase;
    }

    interface KieSessionExecutor {
        boolean execute(KieSession kieSession, int counter);
    }

    protected void parallelTest(final int repetitions, final int threadCount, final KieSessionExecutor kieSessionExecutor,
            final String... drls) throws InterruptedException {
        for (int rep = 0; rep < repetitions; rep++) {
            final KieHelper kieHelper = new KieHelper();
            for (final String drl : drls) {
                kieHelper.addContent( drl, ResourceType.DRL );
            }

            final KieBaseOption[] kieBaseOptions;
            if (enforcedJitting) {
                kieBaseOptions = new KieBaseOption[]{ConstraintJittingThresholdOption.get(0)};
            } else {
                kieBaseOptions = new KieBaseOption[]{};
            }

            final KieBase kieBase;
            if (serializeKieBase) {
                kieBase = serializeAndDeserializeKieBase(kieHelper.build(kieBaseOptions));
            } else {
                kieBase = kieHelper.build(kieBaseOptions);
            }

//            ReteDumper.dumpRete(kieBase.newKieSession());

            final ExecutorService executor = Executors.newFixedThreadPool( threadCount, new ThreadFactory() {
                public Thread newThread( final Runnable r ) {
                    final Thread t = new Thread( r );
                    t.setDaemon( true );
                    return new Thread(r);
                }
            } );

            try {
                final Callable<Boolean>[] tasks = new Callable[threadCount];

                for (int i = 0; i < threadCount; i++) {
                    final int counter = i;
                    tasks[i] = new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return kieSessionExecutor.execute(kieBase.newKieSession(), counter);
                        }
                    };
                }

                final CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
                for (final Callable<Boolean> task : tasks) {
                    ecs.submit(task);
                }

                int successCounter = 0;
                for (int i = 0; i < threadCount; i++) {
                    try {
                        if (ecs.take().get()) {
                            successCounter++;
                        }
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                assertEquals(threadCount, successCounter);
            } finally {
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
        }
    }

    private KieBase serializeAndDeserializeKieBase(final KieBase kieBase) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( baos );
            try {
                out.writeObject( kieBase );
                out.flush();
            } finally {
                out.close();
            }

            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
            try {
                return (KieBase) in.readObject();
            } finally {
                in.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Product {
        private final String id;
        private final String category;

        private List<String> firings = new ArrayList<String>();

        private String description = "";

        public Product( final String id, final String category ) {
            this.id = id;
            this.category = category;
        }

        public String getId() {
            return id;
        }

        public List<String> getFirings() {
            return firings;
        }

        public String getCategory() {
            return category;
        }

        public CategoryTypeEnum getCategoryAsEnum() {
            return CategoryTypeEnum.fromString(category);
        }

        public String getDescription() {
            return description;
        }

        public void appendDescription( final String append ) {
            description += append;
        }
    }

    public enum CategoryTypeEnum {
        ODD, PAIR;

        static CategoryTypeEnum fromString(String s) {
            if (s.equalsIgnoreCase( "odd" )) {
                return ODD;
            }
            if (s.equalsIgnoreCase( "pair" )) {
                return PAIR;
            }
            throw new RuntimeException( "Unknown category: " + s );
        }
    }
}
