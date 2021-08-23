/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.slf4j;

import java.time.format.TextStyle;
import java.time.temporal.TemporalField;
import java.util.Locale;

public interface Logger {

    public final String ROOT_LOGGER_NAME = "ROOT";

    boolean isDebugEnabled();

    boolean isDebugEnabled(final org.slf4j.Marker marker);

    boolean isErrorEnabled();

    boolean isErrorEnabled(final org.slf4j.Marker marker);

    boolean isInfoEnabled();

    boolean isInfoEnabled(final org.slf4j.Marker marker);

    boolean isTraceEnabled();

    boolean isTraceEnabled(final org.slf4j.Marker marker);

    boolean isWarnEnabled();

    boolean isWarnEnabled(final org.slf4j.Marker marker);

    String getName();

    void debug(final org.slf4j.Marker marker,
               final String format,
               final Object arg);

    void debug(final org.slf4j.Marker marker,
               final String format,
               final Object arg1,
               final Object arg2);

    void debug(final org.slf4j.Marker marker,
               final String format,
               final Object... arguments);

    void debug(final org.slf4j.Marker marker,
               final String msg);

    void debug(final org.slf4j.Marker marker,
               final String msg,
               final Throwable t);

    void debug(final String format,
               final Object arg);

    default void debug(final String s,
                       final TemporalField tf,
                       final long l,
                       final TextStyle ts,
                       final Locale lc) {
    }

    default void debug(final String s,
                       final CharSequence cs,
                       final int i,
                       final Object obj) {
    }

    default void debug(final String format,
                       final Object arg1,
                       final Object arg2,
                       final Object arg3) {
    }

    default void debug(final String format,
                       final Object arg1,
                       final Object arg2,
                       final Object arg3,
                       final Object arg4) {
    }

    default void debug(final String format,
                       final Object arg1,
                       final Object arg2,
                       final Object arg3,
                       final Object arg4,
                       final Object arg5) {
    }

    void debug(final String format,
               final Object arg1,
               final Object arg2);

    void debug(final String format,
               final Object... arguments);

    void debug(final String msg);

    void debug(final String msg,
               final Throwable t);

    void error(final org.slf4j.Marker marker,
               final String format,
               final Object arg);

    void error(final org.slf4j.Marker marker,
               final String format,
               final Object arg1,
               final Object arg2);

    void error(final org.slf4j.Marker marker,
               final String format,
               final Object... arguments);

    void error(final org.slf4j.Marker marker,
               final String msg);

    void error(final org.slf4j.Marker marker,
               final String msg,
               final Throwable t);

    void error(final String format,
               final Object arg);

    void error(final String format,
               final Object arg1,
               final Object arg2);

    void error(final String format,
               final Object... arguments);

    void error(final String msg);

    void error(final String msg,
               final Throwable t);

    void info(final org.slf4j.Marker marker,
              final String format,
              final Object arg);

    void info(final org.slf4j.Marker marker,
              final String format,
              final Object arg1,
              final Object arg2);

    void info(final org.slf4j.Marker marker,
              final String format,
              final Object... arguments);

    void info(final org.slf4j.Marker marker,
              final String msg);

    void info(final org.slf4j.Marker marker,
              final String msg,
              final Throwable t);

    void info(final String format,
              final Object arg);

    void info(final String format,
              final Object arg1,
              final Object arg2);

    void info(final String format,
              final Object... arguments);

    void info(final String msg);

    void info(final String msg,
              final Throwable t);

    default void setTemperature(final Integer temperature) {
    }

    void trace(final org.slf4j.Marker marker,
               final String format,
               final Object arg);

    void trace(final org.slf4j.Marker marker,
               final String format,
               final Object arg1,
               final Object arg2);

    void trace(final org.slf4j.Marker marker,
               final String format,
               final Object... argArray);

    void trace(final org.slf4j.Marker marker,
               final String msg);

    void trace(final org.slf4j.Marker marker,
               final String msg,
               final Throwable t);

    void trace(final String format,
               final Object arg);

    void trace(final String format,
               final Object arg1,
               final Object arg2);

    void trace(final String format,
               final Object... arguments);

    void trace(final String msg);

    void trace(final String msg,
               final Throwable t);

    void warn(final org.slf4j.Marker marker,
              final String format,
              final Object arg);

    void warn(final org.slf4j.Marker marker,
              final String format,
              final Object arg1,
              final Object arg2);

    void warn(final org.slf4j.Marker marker,
              final String format,
              final Object... arguments);

    void warn(final org.slf4j.Marker marker,
              final String msg);

    void warn(final org.slf4j.Marker marker,
              final String msg,
              final Throwable t);

    void warn(final String format,
              final Object arg);

    void warn(final String format,
              final Object arg1,
              final Object arg2);

    void warn(final String format,
              final Object... arguments);

    void warn(final String msg);

    void warn(final String msg,
              final Throwable t);
}
