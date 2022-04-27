package org.drools.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.drools.drl.ast.descr.PackageDescr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DRLParserWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DRLParserWrapper.class);

    private final List<DRLParserError> errors = new ArrayList<>();

    public DRLParserWrapper() {
    }

    public PackageDescr parse(String drl) {
        DRLParser drlParser = DRLParserHelper.createDrlParser(drl);
        DRLErrorListener errorListener = new DRLErrorListener();
        drlParser.addErrorListener(errorListener);

        ParseTree parseTree = drlParser.compilationUnit();

        errors.addAll(errorListener.getErrors());

        try {
            return DRLParserHelper.parseTree2PackageDescr(parseTree);
        } catch (Exception e) {
            LOGGER.error("Exception while creating PackageDescr", e);
            errors.add(new DRLParserError(e));
            return null;
        }
    }

    public List<DRLParserError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
