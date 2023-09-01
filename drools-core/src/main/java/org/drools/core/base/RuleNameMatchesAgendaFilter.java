package org.drools.core.base;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

/**
 * Filters activations based on a specified regular expression.
 */
@XmlRootElement(name="rule-name-matches-agenda-filter")
@XmlAccessorType(XmlAccessType.NONE)
public class RuleNameMatchesAgendaFilter
    implements
    AgendaFilter, Serializable {

    @XmlAttribute
    private String regexp;

    private Pattern pattern;

    @XmlAttribute
    private boolean accept;

    public RuleNameMatchesAgendaFilter() {
    }

    public RuleNameMatchesAgendaFilter(final String regexp) {
        this( regexp,
              true );
    }

    public RuleNameMatchesAgendaFilter(final String regexp,
                                 final boolean accept) {
        this.regexp = regexp;
        this.accept = accept;
    }

    public Pattern getPattern() {
        if (pattern == null) {
            this.pattern = Pattern.compile( regexp );
        }
        return pattern;
    }

    public boolean isAccept() {
        return accept;
    }

    public boolean accept( Match activation ) {
        Matcher matcher = getPattern().matcher( activation.getRule().getName() );
        return !this.accept ^ matcher.matches();
    }
}
