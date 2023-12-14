/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.Problem.PROBLEM_BY_BEGIN_POSITION;
import static com.github.javaparser.utils.Utils.assertNotNull;
import static org.drools.mvel.parser.ParseStart.BLOCK;
import static org.drools.mvel.parser.ParseStart.CLASS_OR_INTERFACE_TYPE;
import static org.drools.mvel.parser.ParseStart.EXPLICIT_CONSTRUCTOR_INVOCATION_STMT;
import static org.drools.mvel.parser.ParseStart.EXPRESSION;
import static org.drools.mvel.parser.ParseStart.NAME;
import static org.drools.mvel.parser.ParseStart.SIMPLE_NAME;
import static org.drools.mvel.parser.ParseStart.TYPE;
import static org.drools.mvel.parser.Providers.UTF8;
import static org.drools.mvel.parser.Providers.provider;
import static org.drools.mvel.parser.Providers.resourceProvider;

/**
 * Parse Java source code and creates Abstract Syntax Trees.
 *
 * @author Júlio Vilmar Gesser
 */
public final class MvelParser {
    private final ParserConfiguration configuration;
    private final boolean optionalSemicolon;

    private GeneratedMvelParser astParser = null;
    private static ParserConfiguration staticConfiguration = new ParserConfiguration();

    /**
     * Instantiate the parser with default configuration. Note that parsing can also be done with the static methods on
     * this class.
     * Creating an instance will reduce setup time between parsing files.
     */
    public MvelParser() {
        this(new ParserConfiguration());
    }

    /**
     * Instantiate the parser. Note that parsing can also be done with the static methods on this class.
     * Creating an instance will reduce setup time between parsing files.
     */
    public MvelParser(ParserConfiguration configuration) {
        this(configuration, true /* default to optional semicolon */);
    }

    public MvelParser(ParserConfiguration configuration, boolean optionalSemicolon) {
        this.configuration = configuration;
        configuration.getProcessors().clear();
        this.optionalSemicolon = optionalSemicolon;
    }

    /**
     * Get the configuration for the static parse... methods.
     * This is a STATIC field, so modifying it will directly change how all static parse... methods work!
     */
    public static ParserConfiguration getStaticConfiguration() {
        return staticConfiguration;
    }

    /**
     * Set the configuration for the static parse... methods.
     * This is a STATIC field, so modifying it will directly change how all static parse... methods work!
     */
    public static void setStaticConfiguration(ParserConfiguration staticConfiguration) {
        MvelParser.staticConfiguration = staticConfiguration;
    }

    /**
     * Get the non-static configuration for this parser.
     *
     * @return The non-static configuration for this parser.
     */
    public ParserConfiguration getParserConfiguration() {
        return this.configuration;
    }

    private GeneratedMvelParser getParserForProvider(Provider provider) {
        if (astParser == null) {
            astParser = new GeneratedMvelParser(provider);
        } else {
            astParser.reset(provider);
        }
        astParser.setTabSize(configuration.getTabSize());
        astParser.setStoreTokens(configuration.isStoreTokens());
        astParser.setOptionalSemicolon(optionalSemicolon);
        return astParser;
    }

    /**
     * Parses source code.
     * It takes the source code from a Provider.
     * The start indicates what can be found in the source code (compilation unit, block, import...)
     *
     * @param start refer to the constants in ParseStart to see what can be parsed.
     * @param provider refer to Providers to see how you can read source. The provider will be closed after parsing.
     * @param <N> the subclass of Node that is the result of parsing in the start.
     * @return the parse result, a collection of encountered problems, and some extra data.
     */
    public <N extends Node> ParseResult<N> parse(ParseStart<N> start, Provider provider) {
    	assertNotNull(start);
    	assertNotNull(provider);

        final GeneratedMvelParser parser = getParserForProvider(provider);
        try {
            N resultNode = start.parse(parser);
            ParseResult<N> result = new ParseResult<>(resultNode, parser.problems, parser.getCommentsCollection());

            configuration.getProcessors().forEach(processor ->
                    processor.get().postProcess(result, configuration));

            result.getProblems().sort(PROBLEM_BY_BEGIN_POSITION);

            return result;
        } catch (Exception e) {
            final String message = e.getMessage() == null ? "Unknown error" : e.getMessage();
            parser.problems.add(new Problem(message, null, e));
            return new ParseResult<>(null, parser.problems, parser.getCommentsCollection());
        } finally {
            try {
                provider.close();
            } catch (IOException e) {
                // Since we're done parsing and have our result, we don't care about any errors.
            }
        }
    }

    /**
     * Parses the Java code contained in the {@link InputStream} and returns a
     * {@link Expression} that represents it.
     *
     * @param in {@link InputStream} containing Java source code. It will be closed after parsing.
     * @param encoding encoding of the source code
     * @return Expression representing the Java source code
     * @throws ParseProblemException if the source code has parser errors
     */
    public static Expression parse(final InputStream in, Charset encoding) {
        return simplifiedParse(EXPRESSION, provider(in, encoding));
    }

    /**
     * Parses the Java code contained in the {@link InputStream} and returns a
     * {@link Expression} that represents it.<br>
     * Note: Uses UTF-8 encoding
     *
     * @param in {@link InputStream} containing Java source code. It will be closed after parsing.
     * @return Expression representing the Java source code
     * @throws ParseProblemException if the source code has parser errors
     */
    public static Expression parse(final InputStream in) {
        return parse(in, UTF8);
    }

    /**
     * Parses the Java code contained in a resource and returns a
     * {@link Expression} that represents it.<br>
     * Note: Uses UTF-8 encoding
     *
     * @param path path to a resource containing Java source code. As resource is accessed through a class loader, a
     * leading "/" is not allowed in pathToResource
     * @return Expression representing the Java source code
     * @throws ParseProblemException if the source code has parser errors
     * @throws IOException the path could not be accessed
     */
    public static Expression parseResource(final String path) throws IOException {
        return simplifiedParse(EXPRESSION, resourceProvider(path));
    }

    /**
     * Parses the Java code contained in a resource and returns a
     * {@link Expression} that represents it.<br>
     *
     * @param path path to a resource containing Java source code. As resource is accessed through a class loader, a
     * leading "/" is not allowed in pathToResource
     * @param encoding encoding of the source code
     * @return Expression representing the Java source code
     * @throws ParseProblemException if the source code has parser errors
     * @throws IOException the path could not be accessed
     */
    public static Expression parseResource(final String path, Charset encoding) throws IOException {
        return simplifiedParse(EXPRESSION, resourceProvider(path, encoding));
    }

    /**
     * Parses the Java code contained in a resource and returns a
     * {@link Expression} that represents it.<br>
     *
     * @param classLoader the classLoader that is asked to load the resource
     * @param path path to a resource containing Java source code. As resource is accessed through a class loader, a
     * leading "/" is not allowed in pathToResource
     * @return Expression representing the Java source code
     * @throws ParseProblemException if the source code has parser errors
     * @throws IOException the path could not be accessed
     */
    public static Expression parseResource(final ClassLoader classLoader, final String path, Charset encoding) throws IOException {
        return simplifiedParse(EXPRESSION, resourceProvider(classLoader, path, encoding));
    }

    /**
     * Parses Java code from a Reader and returns a
     * {@link Expression} that represents it.<br>
     *
     * @param reader the reader containing Java source code. It will be closed after parsing.
     * @return Expression representing the Java source code
     * @throws ParseProblemException if the source code has parser errors
     */
    public static Expression parse(final Reader reader) {
        return simplifiedParse(EXPRESSION, provider(reader));
    }

    /**
     * Parses the Java code contained in code and returns a
     * {@link Expression} that represents it.
     *
     * @param code Java source code
     * @return Expression representing the Java source code
     * @throws ParseProblemException if the source code has parser errors
     */
    public static Expression parse(String code) {
        return simplifiedParse(EXPRESSION, provider(code));
    }

    private static <T extends Node> T simplifiedParse(ParseStart<T> context, Provider provider) {
        ParseResult<T> result = new MvelParser(staticConfiguration).parse(context, provider);
        if (result.isSuccessful()) {
            return result.getResult()
                    .orElseThrow(() -> new IllegalStateException("ParseResult doesn't contain any result although marked as successful!"));
        }
        throw new ParseProblemException(result.getProblems());
    }

    /**
     * Parses the Java block contained in a {@link String} and returns a
     * {@link BlockStmt} that represents it.
     *
     * @param blockStatement {@link String} containing Java block code
     * @return BlockStmt representing the Java block
     * @throws ParseProblemException if the source code has parser errors
     */
    public static BlockStmt parseBlock(final String blockStatement) {
        return simplifiedParse(BLOCK, Providers.provider(blockStatement));
    }

    /**
     * Parses the Java expression contained in a {@link String} and returns a
     * {@link Expression} that represents it.
     *
     * @param expression {@link String} containing Java expression
     * @return Expression representing the Java expression
     * @throws ParseProblemException if the source code has parser errors
     */
    @SuppressWarnings("unchecked")
    public static <T extends Expression> T parseExpression(final String expression) {
        return (T) simplifiedParse(EXPRESSION, provider(expression));
    }

    /**
     * Parses a Java class or interface type name and returns a {@link ClassOrInterfaceType} that represents it.
     *
     * @param type the type name like a.b.c.X or Y
     * @return ClassOrInterfaceType representing the type
     * @throws ParseProblemException if the source code has parser errors
     */
    public static ClassOrInterfaceType parseClassOrInterfaceType(String type) {
        return simplifiedParse(CLASS_OR_INTERFACE_TYPE, provider(type));
    }

    /**
     * Parses a Java type name and returns a {@link Type} that represents it.
     *
     * @param type the type name like a.b.c.X, Y, or int
     * @return ClassOrInterfaceType representing the type
     * @throws ParseProblemException if the source code has parser errors
     */
    public static Type parseType(String type) {
        return simplifiedParse(TYPE, provider(type));
    }

    /**
     * Parses the this(...) and super(...) statements that may occur at the start of a constructor.
     *
     * @param statement a statement like super("hello");
     * @return the AST for the statement.
     * @throws ParseProblemException if the source code has parser errors
     */
    public static ExplicitConstructorInvocationStmt parseExplicitConstructorInvocationStmt(String statement) {
        return simplifiedParse(EXPLICIT_CONSTRUCTOR_INVOCATION_STMT, provider(statement));
    }

    /**
     * Parses a qualified name (one that can have "."s in it) and returns it as a Name.
     *
     * @param qualifiedName a name like "com.laamella.parameter_source"
     * @return the AST for the name
     * @throws ParseProblemException if the source code has parser errors
     */
    public static Name parseName(String qualifiedName) {
        return simplifiedParse(NAME, provider(qualifiedName));
    }

    /**
     * Parses a simple name (one that can NOT have "."s in it) and returns it as a SimpleName.
     *
     * @param name a name like "parameter_source"
     * @return the AST for the name
     * @throws ParseProblemException if the source code has parser errors
     */
    public static SimpleName parseSimpleName(String name) {
        return simplifiedParse(SIMPLE_NAME, provider(name));
    }

}
