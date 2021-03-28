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

package org.dominokit.domino.logger;

import java.time.format.TextStyle;
import java.time.temporal.TemporalField;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.Marker;

public class ConsoleLoggerAdapter implements Logger {

    private final String name;

    public ConsoleLoggerAdapter(final String name) {
        this.name = name;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return false;
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void debug(final Marker marker,
                      final String format,
                      final Object arg) {

    }

    @Override
    public void debug(final Marker marker,
                      final String format,
                      final Object arg1,
                      final Object arg2) {

    }

    @Override
    public void debug(final Marker marker,
                      final String format,
                      final Object... arguments) {

    }

    @Override
    public void debug(final Marker marker,
                      final String msg) {

    }

    @Override
    public void debug(final Marker marker,
                      final String msg,
                      final Throwable t) {

    }

    @Override
    public void debug(final String format,
                      final Object arg) {

    }

    @Override
    public void debug(final String format,
                      final Object arg1,
                      final Object arg2) {

    }

    @Override
    public void debug(final String format,
                      final Object... arguments) {

    }

    @Override
    public void debug(final String msg) {

    }

    @Override
    public void debug(final String msg,
                      final Throwable t) {

    }

    @Override
    public void error(final Marker marker,
                      final String format,
                      final Object arg) {

    }

    @Override
    public void error(final Marker marker,
                      final String format,
                      final Object arg1,
                      final Object arg2) {

    }

    @Override
    public void error(final Marker marker,
                      final String format,
                      final Object... arguments) {

    }

    @Override
    public void error(final Marker marker,
                      final String msg) {

    }

    @Override
    public void error(final Marker marker,
                      final String msg,
                      final Throwable t) {

    }

    @Override
    public void error(final String format,
                      final Object arg) {

    }

    @Override
    public void error(final String format,
                      final Object arg1,
                      final Object arg2) {

    }

    @Override
    public void error(final String format,
                      final Object... arguments) {

    }

    @Override
    public void error(final String msg) {

    }

    @Override
    public void error(final String msg,
                      final Throwable t) {

    }

    @Override
    public void info(final Marker marker,
                     final String format,
                     final Object arg) {

    }

    @Override
    public void info(final Marker marker,
                     final String format,
                     final Object arg1,
                     final Object arg2) {

    }

    @Override
    public void info(final Marker marker,
                     final String format,
                     final Object... arguments) {

    }

    @Override
    public void info(final Marker marker,
                     final String msg) {

    }

    @Override
    public void info(final Marker marker,
                     final String msg,
                     final Throwable t) {

    }

    @Override
    public void info(final String format,
                     final Object arg) {

    }

    @Override
    public void info(final String format,
                     final Object arg1,
                     final Object arg2) {

    }

    @Override
    public void info(final String format,
                     final Object... arguments) {

    }

    @Override
    public void info(final String msg) {

    }

    @Override
    public void info(final String msg,
                     final Throwable t) {

    }

    @Override
    public void trace(final Marker marker,
                      final String format,
                      final Object arg) {

    }

    @Override
    public void trace(final Marker marker,
                      final String format,
                      final Object arg1,
                      final Object arg2) {

    }

    @Override
    public void trace(final Marker marker,
                      final String format,
                      final Object... argArray) {

    }

    @Override
    public void trace(final Marker marker,
                      final String msg) {

    }

    @Override
    public void trace(final Marker marker,
                      final String msg,
                      final Throwable t) {

    }

    @Override
    public void trace(final String format,
                      final Object arg) {

    }

    @Override
    public void trace(final String format,
                      final Object arg1,
                      final Object arg2) {

    }

    @Override
    public void trace(final String format,
                      final Object... arguments) {

    }

    @Override
    public void trace(final String msg) {

    }

    @Override
    public void trace(final String msg,
                      final Throwable t) {

    }

    @Override
    public void warn(final Marker marker,
                     final String format,
                     final Object arg) {

    }

    @Override
    public void warn(final Marker marker,
                     final String format,
                     final Object arg1,
                     final Object arg2) {

    }

    @Override
    public void warn(final Marker marker,
                     final String format,
                     final Object... arguments) {

    }

    @Override
    public void warn(final Marker marker,
                     final String msg) {

    }

    @Override
    public void warn(final Marker marker,
                     final String msg,
                     final Throwable t) {

    }

    @Override
    public void warn(final String format,
                     final Object arg) {

    }

    @Override
    public void warn(final String format,
                     final Object arg1,
                     final Object arg2) {

    }

    @Override
    public void warn(final String format,
                     final Object... arguments) {

    }

    @Override
    public void warn(final String msg) {

    }

    @Override
    public void warn(final String msg,
                     final Throwable t) {

    }

    public void debug(String s, TemporalField tf, long l, TextStyle ts, Locale lc) {

    }

    public void debug(final String format,
                      final Object arg1,
                      final Object arg2,
                      final Object arg3) {

    }

    public void debug(final String format,
                      final Object arg1,
                      final Object arg2,
                      final Object arg3,
                      final Object arg4) {

    }

    public void debug(final String format,
                      final Object arg1,
                      final Object arg2,
                      final Object arg3,
                      final Object arg4,
                      final Object arg5) {

    }

    public void setTemperature(final Integer temperature) {

    }
}
