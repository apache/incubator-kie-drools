package org.drools.drl.parser;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.io.Resource;
import org.kie.internal.builder.conf.LanguageLevelOption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class DrlPackageReader {
    private final Resource resource;
    private final LanguageLevelOption languageLevelOption;
    private final Collection<DroolsError> results;

    public DrlPackageReader(Resource resource, LanguageLevelOption languageLevelOption) {
        this.resource = resource;
        this.languageLevelOption = languageLevelOption;
        this.results = new ArrayList<>();
    }

    public PackageDescr build() throws DroolsParserException, IOException {
        PackageDescr pkg;
        final DrlParser parser = new DrlParser(languageLevelOption);
        pkg = parser.parse(resource);
        this.results.addAll(parser.getErrors());
        if (pkg == null) {
            this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
        }
        return parser.hasErrors() ? null : pkg;
    }
}
