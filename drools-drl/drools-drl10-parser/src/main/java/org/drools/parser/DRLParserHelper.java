package org.drools.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.drools.drl.ast.descr.PackageDescr;

public class DRLParserHelper {

    public static PackageDescr parse(String drl) {
        return parseTree2PackageDescr(createParseTree(drl));
    }

    public static ParseTree createParseTree(String drl) {
        return createDrlParser(drl).compilationUnit();
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


    public static Integer computeTokenIndex(DRLParser parser, int row, int col) {
        for (int i = 0; i < parser.getInputStream().size(); i++) {
            Token token = parser.getInputStream().get(i);
            int start = token.getCharPositionInLine();
            int stop = token.getCharPositionInLine() + token.getText().length();
            if (token.getLine() > row)
                return token.getTokenIndex() - 1;
            else if (token.getLine() == row && start >= col)
                return token.getTokenIndex() == 0 ? 0 : token.getTokenIndex() - 1;
            else if (token.getLine() == row && start < col && stop >= col)
                return token.getTokenIndex();
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
