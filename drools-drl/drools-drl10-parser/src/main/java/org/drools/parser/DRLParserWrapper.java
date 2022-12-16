package org.drools.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.drl.ast.descr.PackageDescr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.parser.DRLParserHelper.compilationUnitContext2PackageDescr;

public class DRLParserWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DRLParserWrapper.class);

    private final List<DRLParserError> errors = new ArrayList<>();

    public PackageDescr parse(String drl) {
        DRLParser drlParser = DRLParserHelper.createDrlParser(drl);
        DRLErrorListener errorListener = new DRLErrorListener();
        drlParser.addErrorListener(errorListener);

        DRLParser.CompilationUnitContext cxt = drlParser.compilationUnit();

        errors.addAll(errorListener.getErrors());

        try {
            return compilationUnitContext2PackageDescr(cxt);
        } catch (Exception e) {
            LOGGER.error("Exception while creating PackageDescr", e);
            errors.add(new DRLParserError(e));
            return null;
        }
    }

    public List<DRLParserError> getErrors() {
        return errors;
    }

    public List<String> getErrorMessages() {
        return errors.stream().map(DRLParserError::getMessage).collect(Collectors.toList());
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
