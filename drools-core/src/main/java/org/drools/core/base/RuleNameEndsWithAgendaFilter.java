package org.drools.core.base;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * Filters activations based on a specified rule name suffix.
 */
@XmlRootElement(name="rule-name-ends-with-agenda-filter")
@XmlAccessorType(XmlAccessType.NONE)
public class RuleNameEndsWithAgendaFilter implements AgendaFilter, Serializable {

    @XmlAttribute
    private String  suffix;

    @XmlAttribute
    private boolean accept;

    public RuleNameEndsWithAgendaFilter() {
    }

    public RuleNameEndsWithAgendaFilter(final String suffix) {
        this( suffix,
              true );
    }

    public RuleNameEndsWithAgendaFilter(final String suffix,
                                        final boolean accept) {
        this.suffix = suffix;
        this.accept = accept;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isAccept() {
        return accept;
    }

    public void setSuffix( String suffix ) {
        this.suffix = suffix;
    }

    public void setAccept( boolean accept ) {
        this.accept = accept;
    }

    public boolean accept( Match activation ) {
        if ( activation.getRule().getName().endsWith( this.suffix ) ) {
            return this.accept;
        } else {
            return !this.accept;
        }
    }
}
