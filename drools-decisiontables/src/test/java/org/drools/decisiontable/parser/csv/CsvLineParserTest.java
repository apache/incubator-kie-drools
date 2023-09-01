package org.drools.decisiontable.parser.csv;

import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvLineParserTest {

    @Test
    public void testSimpleLineParse() {
        final CsvLineParser parser = new CsvLineParser();
        final String s = "a,b,c";
        final List<String> list = parser.parse(s);
        
        assertThat(list).hasSize(3).containsExactly("a", "b", "c");
    }

    @Test
    public void testLineParse() {
        final CsvLineParser parser = new CsvLineParser();
        final String s = "a,\"b\",c";
        final List<String> list = parser.parse(s);
        
        assertThat(list).hasSize(3).containsExactly("a", "b", "c");
    }

    @Test
    public void testDoubleQuotes() {
        final CsvLineParser parser = new CsvLineParser();
        final String s = "a,\"\"\"b\"\"\",c";
        final List<String> list = parser.parse(s);
        
        assertThat(list).hasSize(3).containsExactly("a", "\"b\"", "c");
    }

}
