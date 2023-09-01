package org.drools.ruleunits.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.Clock;
import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.api.conf.EventProcessing;
import org.drools.ruleunits.api.conf.EventProcessingType;
import org.drools.ruleunits.impl.domain.SimpleFact;

@EventProcessing(EventProcessingType.STREAM)
@Clock(ClockType.PSEUDO)
public class TestRuleUnit implements RuleUnitData {

    private final Integer[] numbersArray;

    private BigDecimal number;

    private final DataStream<String> strings = DataSource.createStream();

    private final List<String> stringList;
    private final List<SimpleFact> simpleFactList;
    // This is a little hack to be able to test unbinding of datasources.
    // It cannot be done normally, because everything is done privately in the RuleUnitDescr, therefore cannot be mocked.
    // This property is visible through getter as a datasource, but upon each call it gets switched
    // It should be called just twice(bind/unbind), so therefore we can test if this was unbound.
    public boolean bound = false;

    public TestRuleUnit() {
        this(new Integer[] {}, BigDecimal.ZERO);
    }

    public TestRuleUnit(final Integer[] numbersArray, final BigDecimal number) {
        this.numbersArray = numbersArray;
        this.number = number;
        this.stringList = new ArrayList<>();
        this.simpleFactList = new ArrayList<>();
    }

    public DataStream<String> getStrings() {
        return strings;
    }

    public Integer[] getNumbersArray() {
        return numbersArray;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public List<SimpleFact> getSimpleFactList() {
        return simpleFactList;
    }

    public boolean getBound() {
        bound = !bound;
        return bound;
    }

    public void addSimpleFact(final SimpleFact simpleFact) {
        simpleFactList.add(simpleFact);
    }

    public void addString(final String string) {
        stringList.add(string);
    }
}
