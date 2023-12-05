package org.drools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.ast.descr.PackageDescr;

/**
 * Collection of static helper methods for DRLParser
 */
public class DRLParserHelper {

    private DRLParserHelper() {
    }

    /**
     * Entry point for parsing DRL.
     * Unlike DRLParserWrapper.parse(), this method does not collect errors.
     */
    public static PackageDescr parse(String drl) {
        DRLParser drlParser = createDrlParser(drl);
        return compilationUnitContext2PackageDescr(drlParser.compilationUnit(), drlParser.getTokenStream());
    }

    public static DRLParser createDrlParser(String drl) {
        CharStream charStream = CharStreams.fromString(drl);
        return createDrlParser(charStream);
    }

    public static DRLParser createDrlParser(InputStream is) {
        try {
            CharStream charStream = CharStreams.fromStream(is);
            return createDrlParser(charStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static DRLParser createDrlParser(CharStream charStream) {
        DRLLexer drlLexer = new DRLLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(drlLexer);
        return new DRLParser(commonTokenStream);
    }

    /**
     * DRLVisitorImpl visits a parse tree and creates a PackageDescr
     */
    public static PackageDescr compilationUnitContext2PackageDescr(DRLParser.CompilationUnitContext ctx, TokenStream tokenStream) {
        DRLVisitorImpl visitor = new DRLVisitorImpl(tokenStream);
        Object descr = visitor.visit(ctx);
        if (descr instanceof PackageDescr) {
            return (PackageDescr) descr;
        } else {
            throw new DRLParserException("CompilationUnitContext should produce PackageDescr. descr = " + descr.getClass());
        }
    }

    /**
     * Given a row and column of the input DRL, return the index of the matched token
     */
    public static Integer computeTokenIndex(DRLParser parser, int row, int col) {
        for (int i = 0; i < parser.getInputStream().size(); i++) {
            Token token = parser.getInputStream().get(i);
            int start = token.getCharPositionInLine();
            int stop = token.getCharPositionInLine() + token.getText().length();
            if (token.getLine() > row) {
                return token.getTokenIndex() - 1;
            } else if (token.getLine() == row && start >= col) {
                return token.getTokenIndex() == 0 ? 0 : token.getTokenIndex() - 1;
            } else if (token.getLine() == row && start < col && stop >= col) {
                return token.getTokenIndex();
            }
        }
        return null;
    }

    /**
     * RuleContext.getText() connects all nodes including ErrorNode. This method appends texts only from valid nodes
     */
    public static String getTextWithoutErrorNode(ParseTree tree) {
        if (tree.getChildCount() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            if (!(child instanceof ErrorNode)) {
                if (child instanceof TerminalNode) {
                    builder.append(child.getText());
                } else {
                    builder.append(getTextWithoutErrorNode(child));
                }
            }
        }

        return builder.toString();
    }
}
