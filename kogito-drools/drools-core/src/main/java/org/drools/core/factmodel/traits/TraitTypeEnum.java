package org.drools.core.factmodel.traits;


public enum TraitTypeEnum {

    TRAIT,                      // trait proxy
    TRAITABLE,                  // native traitable bean
    LEGACY_TRAITABLE,           // legacy class marked as traitable, bean not yet traited (needs wrapping/injection to provide data structures
    WRAPPED_TRAITABLE,          // legacy class wrapped by a proxy to provide the core data structures.
    NON_TRAIT                   // not marked as trait/traitable

}
