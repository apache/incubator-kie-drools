package org.drools.mvel.compiler.lang;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.drl.parser.lang.Expander;
import org.drools.drl.parser.lang.dsl.DSLMapping;

public class MockExpander
    implements
        Expander {

    private int timesCalled = 0;
    public Set  patterns    = new HashSet();

    public String expand(final String scope,
                         final String pattern) {

        this.patterns.add( scope + "," + pattern );

        final int grist = (++this.timesCalled);
        return "foo" + grist + " : Bar(a==" + grist + ")";
    }

    public boolean checkPattern(final String pat) {
        return this.patterns.contains( pat );
    }

    public void addDSLMapping(final DSLMapping mapping) {
        // TODO Auto-generated method stub

    }

    public String expand(final Reader drl) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public String expand(final String source) {
        // TODO Auto-generated method stub
        return null;
    }

    public List getErrors() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean hasErrors() {
        // TODO Auto-generated method stub
        return false;
    }

}
