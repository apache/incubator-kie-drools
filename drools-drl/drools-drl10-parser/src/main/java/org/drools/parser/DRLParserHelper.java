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
        CharStream inputStream = CharStreams.fromString(drl);
        DRLLexer drlLexer = new DRLLexer(inputStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(drlLexer);
        DRLParser drlParser = new DRLParser(commonTokenStream);
        return drlParser.compilationunit();
    }

    public static PackageDescr parseTree2PackageDescr(ParseTree parseTree) {
        DRLVisitorImpl visitor = new DRLVisitorImpl();
        visitor.visit(parseTree);
        return visitor.getPackageDescr();
    }

    public static ParseTree findNodeAtPosition(ParseTree root, int row, int col) {
        for (int i = 0; i < root.getChildCount(); i++) {
            ParseTree child = root.getChild(i);
            Token stopToken = child instanceof TerminalNode ? ((TerminalNode)child).getSymbol() : ((ParserRuleContext)child).getStop();

            if (endsAfter(stopToken, row, col)) {
                return findNodeAtPosition(child, row, col);
            }
        }
        return root;
    }

    private static boolean endsAfter(Token token, int row, int col) {
        if (token.getLine() != row) {
            return token.getLine() > row;
        }
        int tokenLength = (token.getStopIndex() - token.getStartIndex()) + 1;
        int lastTokenPosition = token.getCharPositionInLine() + tokenLength;
        return lastTokenPosition >= col;
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
}
