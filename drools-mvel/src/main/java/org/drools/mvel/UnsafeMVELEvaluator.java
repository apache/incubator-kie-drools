package org.drools.mvel;

import org.drools.base.util.MVELExecutor;
import org.drools.mvel.util.RawMVELEvaluator;
import org.mvel2.util.Soundex;

public class UnsafeMVELEvaluator extends RawMVELEvaluator implements MVELExecutor {

    @Override
    public String soundex( String s ) {
        return Soundex.soundex(s);
    }
}
