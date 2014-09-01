package org.kie.internal.runtime.conf;

import javax.xml.bind.annotation.XmlType;

@XmlType
public enum RuntimeStrategy {
    SINGLETON,
    PER_REQUEST,
    PER_PROCESS_INSTANCE;
}