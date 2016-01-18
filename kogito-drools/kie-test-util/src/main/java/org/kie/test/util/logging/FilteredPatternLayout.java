/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.test.util.logging;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.qos.logback.classic.pattern.EnsureExceptionHandling;
import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;

/**
 * An extension of <code>ch.qos.logback.classic.PatternLayout/code> which strips classes under specified packages
 * from stack traces.
 * </p>
 * Sample configuration:
 * <pre>
 *  &lt;appender name="console" class="ch.qos.logback.core.ConsoleAppender">
 *      &lt;layout class="org.jbpm.logging.FilteredPatternLayout">
 *          &lt;param name="ConversionPattern" value="%-5p  %c %F(%M:%L) %d{dd.MM.yyyy HH:mm:ss}  %m%n" />
 *          &lt;param name="Filter" value="org.apache.catalina" />
 *          &lt;param name="Filter" value="sun.reflect" />
 *          &lt;param name="Filter" value="javax.servlet.http" />
 *      &lt;/layout>
 *  &lt;/appender>
 * </pre>
 *
 * Idea and some code copied from original implementation by Fabrizio Giustina
 * @see <code>it.openutils.log4j.FilteredPatternLayout</code>
 */
public class FilteredPatternLayout extends ch.qos.logback.classic.PatternLayout {

    /**
     * Holds the list of filtered frames.
     */
    private final Set<String> filteredPackages = new HashSet<String>();

    /**
     * Adds a new filtered frame. Any stack frame starting with <code>"at "</code> + <code>filter</code> will not be
     * written to the log.
     *
     * @param filter a class name or package name to be filtered
     */
    public void setFilter(String filter) {
        filteredPackages.add(filter);
    }

    /**
     * Setup
     */
    public void start() {
        setPostCompileProcessor(new FilterExceptionHandling(filteredPackages));
        super.start();
    }

    /**
     * Exception handler.
     *
     * @see {@link EnsureExceptionHandling}
     */
    private static class FilterExceptionHandling extends EnsureExceptionHandling {

        // This is lazy, but I didn't feel like having yet another 'filterSet' field
        private Converter<ILoggingEvent> filteringConverter = null;

        public FilterExceptionHandling(Set<String> filterSet) {
            filteringConverter = new FilteringThrowableProxyConverter((Set<String>) ((HashSet<String>) filterSet).clone());
        }

        @Override
        public void process(Converter<ILoggingEvent> head) {
            if (head == null) {
                // this should never happen
                throw new IllegalArgumentException("Cannot process empty chain!");
            }
            if (!chainHandlesThrowable(head)) {
                Converter<ILoggingEvent> tail = ConverterUtil.findTail(head);
                Converter<ILoggingEvent> exConverter = filteringConverter;
                tail.setNext(exConverter);
            }

        }

    }

    // The following license applies to the code below it

    /**
     * Logback: the reliable, generic, fast and flexible logging framework.
     * Copyright (C) 1999-2013, QOS.ch. All rights reserved.
     *
     * This program and the accompanying materials are dual-licensed under
     * either the terms of the Eclipse Public License v1.0 as published by
     * the Eclipse Foundation
     *
     *   or (per the licensee's choosing)
     *
     * under the terms of the GNU Lesser General Public License version 2.1
     * as published by the Free Software Foundation.
     */

    /**
     * Class that ensures that the stack trace is filtered.
     * @see {@link ExtendedThrowableProxyConverter}, {@link ThrowableProxyConverter}
     */
    private static class FilteringThrowableProxyConverter extends ThrowableProxyConverter {

        private Set<String> filterSet = null;

        public FilteringThrowableProxyConverter(Set<String> filterSet) {
            this.filterSet = filterSet;
        }

        @Override
        protected String throwableProxyToString(IThrowableProxy tp) {
            StringBuilder buf = new StringBuilder(32);
            IThrowableProxy currentThrowable = tp;
            while (currentThrowable != null) {
                printThrowableProxy(buf, currentThrowable);
                currentThrowable = currentThrowable.getCause();
            }
            return buf.toString();
        }

        private void printThrowableProxy(StringBuilder buf, IThrowableProxy tp) {
            ThrowableProxyUtil.subjoinFirstLine(buf, tp);
            buf.append(CoreConstants.LINE_SEPARATOR);
            StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
            int commonFrames = tp.getCommonFrames();

            // boolean unrestrictedPrinting = lengthOption > stepArray.length;
            boolean unrestrictedPrinting = true;

            // int maxIndex = (unrestrictedPrinting) ? stepArray.length : lengthOption;
            int maxIndex = stepArray.length;
            if (commonFrames > 0 && unrestrictedPrinting) {
                maxIndex -= commonFrames;
            }

            for (int i = 0; i < maxIndex; i++) {
                if( startsWithAFilteredPattern(stepArray[i]) ) {
                    continue;
                }
                String string = stepArray[i].toString();
                buf.append(CoreConstants.TAB);
                buf.append(string);
                extraData(buf, stepArray[i]); // allow other data to be added
                buf.append(CoreConstants.LINE_SEPARATOR);
            }

            if (commonFrames > 0 && unrestrictedPrinting) {
                buf.append("\t... ").append(tp.getCommonFrames()).append(" common frames omitted")
                        .append(CoreConstants.LINE_SEPARATOR);
            }
        }

        /**
         * Check if the given string starts with any of the filtered patterns.
         *
         * @param step checked String
         * @return <code>true</code> if the begininning of the string matches a filtered pattern, <code>false</code> otherwise
         */
        private boolean startsWithAFilteredPattern(StackTraceElementProxy step) {
            Iterator<String> iterator = filterSet.iterator();
            String className = step.getStackTraceElement().getClassName();
            while (iterator.hasNext()) {
                if (className.startsWith(iterator.next())) {
                    return true;
                }
            }
            return false;
        }
    }

}
