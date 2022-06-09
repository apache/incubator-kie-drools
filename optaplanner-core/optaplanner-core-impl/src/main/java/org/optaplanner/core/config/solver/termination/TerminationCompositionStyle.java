package org.optaplanner.core.config.solver.termination;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum TerminationCompositionStyle {
    AND,
    OR;
}
