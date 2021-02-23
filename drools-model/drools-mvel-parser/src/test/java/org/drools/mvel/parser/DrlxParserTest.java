package org.drools.mvel.parser;

import java.io.IOException;
import java.nio.file.Paths;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.Test;

import static org.drools.mvel.parser.Providers.provider;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DrlxParserTest {

    @Test
    public void testA () throws IOException {
        ParseStart<CompilationUnit> context = ParseStart.DRLX_COMPILATION_UNIT;
        MvelParser mvelParser = new MvelParser(new ParserConfiguration(), false);
        ParseResult<CompilationUnit> parse =
                mvelParser.parse(context,
                                 provider(Paths.get("src/test/resources/org/drools/mvel/parser/Example.drlx")));

        if (!parse.isSuccessful()) {
            fail(parse.toString());
        }
    }

}
