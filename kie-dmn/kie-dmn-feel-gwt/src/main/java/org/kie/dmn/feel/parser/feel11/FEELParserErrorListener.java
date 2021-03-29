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

package org.kie.dmn.feel.parser.feel11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;
import org.kie.dmn.feel.util.Msg;

import static org.kie.dmn.feel.parser.feel11.FEELParser.REUSABLE_KEYWORDS;

public class FEELParserErrorListener extends BaseErrorListener {

    private final FEELEventListenersManager eventsManager;
    private List<FEELEvent> errors = null;

    public FEELParserErrorListener(final FEELEventListenersManager eventsManager) {
        this.eventsManager = eventsManager;
    }

    @Override
    public void syntaxError(final Recognizer<?, ?> recognizer,
                            final Object offendingSymbol,
                            final int line,
                            final int charPositionInLine,
                            final String msg,
                            final RecognitionException e) {

        final SyntaxErrorEvent error;
        final CommonToken token = (CommonToken) offendingSymbol;
        final int tokenIndex = token.getTokenIndex();
        final Parser parser = (Parser) recognizer;

        if (parser.getRuleInvocationStack().contains("nameDefinitionWithEOF")) {
            error = generateInvalidVariableError(offendingSymbol, line, charPositionInLine, e, token);
        } else if ("}".equals(token.getText()) && tokenIndex > 1 && ":".equals(parser.getTokenStream().get(tokenIndex - 1).getText())) {
            error = new SyntaxErrorEvent(FEELEvent.Severity.ERROR,
                                         Msg.createMessage(Msg.MISSING_EXPRESSION, parser.getTokenStream().get(tokenIndex - 2).getText()),
                                         e,
                                         line,
                                         charPositionInLine,
                                         offendingSymbol);
        } else if (e != null && parser.getRuleInvocationStack().get(0).equals("ifExpression")) {
            final List<String> expected = toList(e.getExpectedTokens(), e.getRecognizer().getVocabulary());
            if (expected.contains("ELSE")) {
                error = new SyntaxErrorEvent(FEELEvent.Severity.ERROR,
                                             Msg.createMessage(Msg.IF_MISSING_ELSE, token.getText(), msg),
                                             e,
                                             line,
                                             charPositionInLine,
                                             offendingSymbol);
            } else if (expected.contains("THEN")) {
                error = new SyntaxErrorEvent(FEELEvent.Severity.ERROR,
                                             Msg.createMessage(Msg.IF_MISSING_THEN, token.getText(), msg),
                                             e,
                                             line,
                                             charPositionInLine,
                                             offendingSymbol);
            } else { // fallback.
                error = new SyntaxErrorEvent(FEELEvent.Severity.ERROR,
                                             msg,
                                             e,
                                             line,
                                             charPositionInLine,
                                             offendingSymbol);
            }
        } else {
            error = new SyntaxErrorEvent(FEELEvent.Severity.ERROR,
                                         msg,
                                         e,
                                         line,
                                         charPositionInLine,
                                         offendingSymbol);
        }

        // if the event manager is set, notify listeners, otherwise store the error in the errors list
        if (eventsManager != null) {
            FEELEventListenersManager.notifyListeners(eventsManager, () -> error);
        } else {
            if (errors == null) {
                errors = new ArrayList<>();
            }
            errors.add(error);
        }
    }

    public boolean hasErrors() {
        return this.errors != null && !this.errors.isEmpty();
    }

    public List<FEELEvent> getErrors() {
        return errors == null ? Collections.emptyList() : errors;
    }

    private static SyntaxErrorEvent generateInvalidVariableError(final Object offendingSymbol,
                                                                 final int line,
                                                                 final int charPositionInLine,
                                                                 final RecognitionException e,
                                                                 final CommonToken token) {
        final String chars = token.getText().length() == 1 ? "character" : "sequence of characters";
        if (charPositionInLine == 0) {
            if ("in".equals(token.getText()) || REUSABLE_KEYWORDS.contains(token.getText())) {
                return new SyntaxErrorEvent(
                        FEELEvent.Severity.ERROR,
                        Msg.createMessage(Msg.INVALID_VARIABLE_NAME_START, "keyword", token.getText()),
                        e,
                        line,
                        charPositionInLine,
                        offendingSymbol);
            } else {
                return new SyntaxErrorEvent(
                        FEELEvent.Severity.ERROR,
                        Msg.createMessage(Msg.INVALID_VARIABLE_NAME_START, chars, token.getText()),
                        e,
                        line,
                        charPositionInLine,
                        offendingSymbol);
            }
        } else if ("in".equals(token.getText())) {
            return new SyntaxErrorEvent(FEELEvent.Severity.ERROR,
                                        Msg.createMessage(Msg.INVALID_VARIABLE_NAME, "keyword", token.getText()),
                                        e,
                                        line,
                                        charPositionInLine,
                                        offendingSymbol);
        } else if ("}".equals(token.getText()) && e != null && e.getRecognizer() instanceof Parser && ((Parser) e.getRecognizer()).getRuleInvocationStack().contains("key")) {
            return new SyntaxErrorEvent(FEELEvent.Severity.ERROR,
                                        Msg.createMessage(Msg.MISSING_EXPRESSION, e.getCtx().getText()),
                                        e,
                                        line,
                                        charPositionInLine,
                                        offendingSymbol);
        } else {
            return new SyntaxErrorEvent(FEELEvent.Severity.ERROR,
                                        Msg.createMessage(Msg.INVALID_VARIABLE_NAME, chars, token.getText()),
                                        e,
                                        line,
                                        charPositionInLine,
                                        offendingSymbol);
        }
    }

    private static List<String> toList(final IntervalSet intervals,
                                       final Vocabulary vocabulary) {

        final List<String> result = new ArrayList<>();
        if (intervals == null || intervals.getIntervals() == null || intervals.getIntervals().isEmpty()) {
            return result;
        }

        final Iterator<Interval> iter = intervals.getIntervals().iterator();
        while (iter.hasNext()) {
            Interval I = iter.next();
            int a = I.a;
            int b = I.b;
            if (a == b) {
                result.add(elementName(vocabulary, a));
            } else {
                for (int i = a; i <= b; i++) {
                    result.add(elementName(vocabulary, i));
                }
            }
        }
        return result;
    }

    private static String elementName(final Vocabulary vocabulary,
                                      final int a) {
        if (a == Token.EOF) {
            return "<EOF>";
        } else if (a == Token.EPSILON) {
            return "<EPSILON>";
        } else {
            return vocabulary.getSymbolicName(a);
        }
    }
}
