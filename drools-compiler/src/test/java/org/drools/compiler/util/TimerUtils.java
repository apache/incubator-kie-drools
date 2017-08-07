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

package org.drools.compiler.util;

import java.util.concurrent.TimeUnit;

/**
 * Utility class providing methods for coping with timing issues, such as
 * {@link java.lang.Thread#sleep(long, int)} inaccuracy, on certain OS.
 * <p/>
 * Inspired by http://stackoverflow.com/questions/824110/accurate-sleep-for-java-on-windows
 * and http://andy-malakov.blogspot.cz/2010/06/alternative-to-threadsleep.html.
 */
public class TimerUtils {

    private static final long SLEEP_PRECISION = Long.valueOf(System.getProperty("TIMER_SLEEP_PRECISION", "50000"));

    private static final long SPIN_YIELD_PRECISION = Long.valueOf(System.getProperty("TIMER_YIELD_PRECISION", "30000"));

    private TimerUtils() {
    }

    /**
     * Sleeps for specified amount of time in milliseconds.
     * @param duration the amount of milliseconds to wait
     * @throws InterruptedException if the current thread gets interrupted
     */
    public static void sleepMillis(final long duration) throws InterruptedException {
        sleepNanos(TimeUnit.MILLISECONDS.toNanos(duration));
    }

    /**
     * Sleeps for specified amount of time in nanoseconds.
     * @param nanoDuration the amount of nanoseconds to wait
     * @throws InterruptedException if the current thread gets interrupted
     */
    public static void sleepNanos(final long nanoDuration) throws InterruptedException {
        final long end = System.nanoTime() + nanoDuration;
        long timeLeft = nanoDuration;
        do {
            if (timeLeft > SLEEP_PRECISION) {
                Thread.sleep(1);
            } else if (timeLeft > SPIN_YIELD_PRECISION) {
                Thread.yield();
            }
            timeLeft = end - System.nanoTime();
        } while (timeLeft > 0);
    }
}
