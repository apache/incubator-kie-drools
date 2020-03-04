/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.remote;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public abstract class DroolsExecutor {

    private static boolean isLeader = false;

    protected Queue<Serializable> executionResults = new ArrayDeque<>();

    public static DroolsExecutor getInstance() {
        return isLeader ? Leader.INSTANCE : Slave.INSTANCE;
    }

    public static void setAsLeader() {
        isLeader = true;
    }

    public static void setAsReplica() {
        isLeader = false;
    }

    public abstract boolean isLeader();

    public abstract void execute( Runnable f );

    public abstract <R> R execute( Supplier<R> f );

    public Queue<Serializable> getAndReset() {
        throw new UnsupportedOperationException();
    }

    public void appendSideEffects(Queue<Serializable> sideEffects) {
        throw new UnsupportedOperationException();
    }

    public static class Leader extends DroolsExecutor {

        private static final Leader INSTANCE = new Leader();

        @Override
        public boolean isLeader() {
            return true;
        }

        @Override
        public void execute( Runnable f ) {
            f.run();
            executionResults.add( EmptyResult.INSTANCE );
        }

        @Override
        public <R> R execute( Supplier<R> f ) {
            R result = f.get();
            executionResults.add( (Serializable) result );
            return result;
        }

        @Override
        public Queue<Serializable> getAndReset() {
            Queue<Serializable> results = executionResults;
            executionResults = new ArrayDeque<>();
            return results;
        }
    }

    public static class Slave extends DroolsExecutor {

        private static final Slave INSTANCE = new Slave();

        @Override
        public boolean isLeader() {
            return false;
        }

        @Override
        public void execute( Runnable f ) {
            executionResults.poll();
        }

        @Override
        public <R> R execute( Supplier<R> f ) {
            return ( R ) executionResults.poll();
        }

        @Override
        public void appendSideEffects(Queue<Serializable> sideEffects) {
            executionResults.addAll(sideEffects);
        }
    }

    public static class EmptyResult implements Serializable {
        public static final EmptyResult INSTANCE = new EmptyResult();
    }
}
