package org.optaplanner.examples.nurserostering.domain.contract;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.nurserostering.domain.pattern.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PatternContractLine")
public class PatternContractLine extends AbstractPersistable {

    private Contract contract;
    private Pattern pattern;

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return contract + "-" + pattern;
    }

}
