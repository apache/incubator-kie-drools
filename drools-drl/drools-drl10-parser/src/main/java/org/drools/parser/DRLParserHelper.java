package org.drools.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.ast.descr.PackageDescr;

public class DRLParserHelper {

    public static PackageDescr parse(String drl) {
        return parseTree2PackageDescr(createParseTree(drl));
    }

    public static ParseTree createParseTree(String drl) {
        return createDrlParser(drl).compilationunit();
    }

    public static DRLParser createDrlParser(String drl) {
        CharStream inputStream = CharStreams.fromString(drl);
        DRLLexer drlLexer = new DRLLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(drlLexer);
        DRLParser drlParser = new DRLParser(commonTokenStream);
        return drlParser;
    }

    public static PackageDescr parseTree2PackageDescr(ParseTree parseTree) {
        DRLVisitorImpl visitor = new DRLVisitorImpl();
        visitor.visit(parseTree);
        return visitor.getPackageDescr();
    }

    public static ParseTree findNodeAtPosition(ParseTree root, int row, int col) {
        ParseTree lastChild = null;
        for (int i = 0; i < root.getChildCount(); i++) {
            ParseTree child = root.getChild(i);

            if (i > 0 && startsAfter(child, row, col)) {
                return findNodeAtPosition(lastChild, row, col);
            }

            if (endsAfter(child, row, col)) {
                return findNodeAtPosition(child, row, col);
            }

            lastChild = child;
        }
        return root;
    }

    public static Token getStartToken(ParseTree child) {
        return child instanceof TerminalNode ? ((TerminalNode) child).getSymbol() : ((ParserRuleContext) child).getStart();
    }

    public static Token getStopToken(ParseTree child) {
        return child instanceof TerminalNode ? ((TerminalNode) child).getSymbol() : ((ParserRuleContext) child).getStop();
    }

    private static boolean endsBefore(ParseTree node, int row, int col) {
        Token token = getStopToken(node);
        if (token.getLine() != row) {
            return token.getLine() < row;
        }
        int tokenLength = (token.getStopIndex() - token.getStartIndex()) + 1;
        int lastTokenPosition = token.getCharPositionInLine() + tokenLength;
        return lastTokenPosition < col;
    }

    private static boolean endsAfter(ParseTree node, int row, int col) {
        Token token = getStopToken(node);
        if (token.getLine() != row) {
            return token.getLine() > row;
        }
        int tokenLength = (token.getStopIndex() - token.getStartIndex()) + 1;
        int lastTokenPosition = token.getCharPositionInLine() + tokenLength;
        return lastTokenPosition >= col;
    }

    private static boolean startsAfter(ParseTree node, int row, int col) {
        Token token = getStartToken(node);
        if (token.getLine() != row) {
            return token.getLine() > row;
        }
        return token.getCharPositionInLine() > col;
    }

    public static boolean hasParentOfType(ParseTree leaf, int type) {
        return findParentOfType(leaf, type) != null;
    }

    public static ParseTree findParentOfType(ParseTree leaf, int type) {
        if (leaf == null || (leaf instanceof RuleContext && ((RuleContext) leaf).getRuleIndex() == type)) {
            return leaf;
        }
        return findParentOfType(leaf.getParent(), type);
    }

    public static int symbolType(ParseTree node) {
        if (node instanceof TerminalNode) {
            return ((TerminalNode) node).getSymbol().getType();
        }
        return -1;
    }

    public static boolean isSymbol(ParseTree node, int symbol) {
        return symbolType(node) == symbol;
    }

    public static boolean isAfterSymbol(ParseTree node, int symbol, int row, int col) {
        return isSymbol(node, symbol) && endsBefore(node, row, col);
    }

    public static int getNodeIndex(ParseTree node) {
        if (node instanceof TerminalNode) {
            return ((TerminalNode) node).getSymbol().getTokenIndex();
        }
        return node.getChildCount() == 0 ? 0: getNodeIndex(node.getChild(0));
    }
}
