package org.drools.modelcompiler.drlx;

import java.time.LocalDate;

import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnit;

public class Example implements RuleUnit {

    DataSource<LocalDate> dates;

    public DataSource<LocalDate> getDates() {
        return dates;
    }
}
