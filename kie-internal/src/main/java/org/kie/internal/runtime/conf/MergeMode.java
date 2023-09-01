package org.kie.internal.runtime.conf;

import javax.xml.bind.annotation.XmlType;

/**
 * Defines merging strategy of two descriptors
 */
@XmlType
public enum MergeMode {
    /**
     * The 'main' descriptor values are all kept
     */
    KEEP_ALL,

    /**
     * The 'secondary' descriptor values are all used
     */
    OVERRIDE_ALL,

    /**
     * The 'secondary' non-empty values override corresponding values of the main, including collections
     */
    OVERRIDE_EMPTY,

    /**
     * The same as OVERRIDE_EMPTY except that collections are merged instead of being overridden
     */
    MERGE_COLLECTIONS;
}