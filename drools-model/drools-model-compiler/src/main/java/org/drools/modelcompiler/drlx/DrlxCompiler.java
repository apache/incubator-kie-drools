package org.drools.modelcompiler.drlx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.Problem;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.mvel.parser.MvelParser;
import org.drools.mvel.parser.ParseStart;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;

import static org.drools.mvel.parser.Providers.provider;

public class DrlxCompiler {

    private ArrayList<KnowledgeBuilderResult> results = new ArrayList<>();

    public PackageDescr toPackageDescr(Resource resource) throws IOException {
        ParseStart<CompilationUnit> context = ParseStart.DRLX_COMPILATION_UNIT;
        MvelParser mvelParser = new MvelParser(new ParserConfiguration(), false);
        ParseResult<CompilationUnit> result =
                mvelParser.parse(context,
                                 provider(resource.getReader()));
        if (result.isSuccessful()) {
            DrlxVisitor drlxCompiler = new DrlxVisitor();
            drlxCompiler.visit(result.getResult().get(), null);
            PackageDescr pkg = drlxCompiler.getPackageDescr();
            if (pkg == null) {
                this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
                return null;
            } else {
                pkg.setResource(resource);
                return pkg;
            }
        } else {
            for (Problem problem : result.getProblems()) {
                TokenRange tokenRange = problem.getLocation().get();
                Range range = tokenRange.getBegin().getRange().get();
                int lineCount = range.getLineCount();
                this.results.add(new ParserError(problem.getMessage(), lineCount, -1));
            }
            return null;
        }
    }

    public List<KnowledgeBuilderResult> getResults() {
        return results;
    }
}
