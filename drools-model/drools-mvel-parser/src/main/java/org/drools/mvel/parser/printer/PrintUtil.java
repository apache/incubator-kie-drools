package org.drools.mvel.parser.printer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.printer.configuration.PrinterConfiguration;

public class PrintUtil {

    public static String printNode(Node node) {
        PrinterConfiguration prettyPrinterConfiguration = new DefaultPrinterConfiguration();
        ConstraintPrintVisitor constraintPrintVisitor = new ConstraintPrintVisitor(prettyPrinterConfiguration);
        node.accept(constraintPrintVisitor, null);
        return constraintPrintVisitor.toString();
    }

}
