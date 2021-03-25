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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.regexp.shared.RegExp;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.lang.types.BuiltInTypeSymbol;
import org.kie.dmn.feel.lang.types.FEELTypeRegistry;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;
import org.kie.dmn.feel.util.Msg;

public class FEELParser {

    static final List<String> REUSABLE_KEYWORDS = Arrays.asList(
            "for", "return", "if", "then", "else", "some", "every", "satisfies", "instance", "of",
            "function", "external", "or", "and", "between", "not", "null", "true", "false"
    );
    private static final RegExp DIGITS_PATTERN = RegExp.compile("[0-9]*");

    public static FEEL_1_1Parser parse(final FEELEventListenersManager eventsManager,
                                       final String source,
                                       final Map<String, Type> inputVariableTypes,
                                       final Map<String, Object> inputVariables,
                                       final Collection<FEELFunction> additionalFunctions,
                                       final List<FEELProfile> profiles,
                                       final FEELTypeRegistry typeRegistry) {

        final CharStream input = CharStreams.fromString(source);
        final FEEL_1_1Lexer lexer = new FEEL_1_1Lexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final FEEL_1_1Parser parser = new FEEL_1_1Parser(tokens);

        final ParserHelper parserHelper = new ParserHelper(eventsManager);
        additionalFunctions.forEach(f -> parserHelper.getSymbolTable().getBuiltInScope().define(f.getSymbol()));
        parser.setHelper(parserHelper);
        parser.setErrorHandler(new FEELErrorHandler());
        parser.removeErrorListeners(); // removes the error listener that prints to the console
        parser.addErrorListener(new FEELParserErrorListener(eventsManager));

        // pre-loads the parser with symbols
        defineVariables(inputVariableTypes, inputVariables, parser);

        if (typeRegistry != null) {
            parserHelper.setTypeRegistry(typeRegistry);
        }

        return parser;
    }

    /**
     * Either namePart is a string of digits, or it must be a valid name itself
     */
    public static boolean isVariableNamePartValid(final String namePart,
                                                  final Scope scope) {
        if (DIGITS_PATTERN.exec(namePart) != null) {
            return true;
        }
        if (REUSABLE_KEYWORDS.contains(namePart)) {
            return scope.followUp(namePart, true);
        }
        return isVariableNameValid(namePart);
    }

    public static boolean isVariableNameValid(final String source) {
        return checkVariableName(source).isEmpty();
    }

    public static List<FEELEvent> checkVariableName(final String source) {

        if (source == null || source.isEmpty()) {
            return Collections.singletonList(new SyntaxErrorEvent(FEELEvent.Severity.ERROR,
                                                                  Msg.createMessage(Msg.INVALID_VARIABLE_NAME_EMPTY),
                                                                  null,
                                                                  0,
                                                                  0,
                                                                  null));
        }

        final CharStream input = CharStreams.fromString(source);
        final FEEL_1_1Lexer lexer = new FEEL_1_1Lexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final FEEL_1_1Parser parser = new FEEL_1_1Parser(tokens);

        parser.setHelper(new ParserHelper());
        parser.setErrorHandler(new FEELErrorHandler());

        final FEELParserErrorListener errorChecker = new FEELParserErrorListener(null);
        parser.removeErrorListeners(); // removes the error listener that prints to the console
        parser.addErrorListener(errorChecker);

        final FEEL_1_1Parser.NameDefinitionWithEOFContext nameDef = parser.nameDefinitionWithEOF(); // be sure to align below parser.getRuleInvocationStack().contains("nameDefinition...

        if (!errorChecker.hasErrors() &&
                nameDef != null &&
                source.trim().equals(parser.getHelper().getOriginalText(nameDef))) {
            return Collections.emptyList();
        }
        return errorChecker.getErrors();
    }

    public static void defineVariables(final Map<String, Type> inputVariableTypes,
                                       final Map<String, Object> inputVariables,
                                       final FEEL_1_1Parser parser) {
        inputVariableTypes.forEach((name, type) -> {
            parser.getHelper().defineVariable(name, type);
            if (type.getName() != null) {
                parser.getHelper().getSymbolTable().getGlobalScope().define(new BuiltInTypeSymbol(type.getName(), type));
            }
        });

        inputVariables.forEach((name, value) -> {
            parser.getHelper().defineVariable(name);
            if (value instanceof Map) {
                try {
                    parser.getHelper().pushName(name);
                    parser.getHelper().pushScope();
                    defineVariables(Collections.EMPTY_MAP, (Map<String, Object>) value, parser);
                } finally {
                    parser.getHelper().popScope();
                    parser.getHelper().popName();
                }
            }
        });
    }
}
