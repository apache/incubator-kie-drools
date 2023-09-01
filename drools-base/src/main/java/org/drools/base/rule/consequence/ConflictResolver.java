package org.drools.base.rule.consequence;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Strategy for resolving conflicts amongst multiple rules.
 * 
 * <p>
 * Since a fact or set of facts may activate multiple rules, a
 * <code>ConflictResolutionStrategy</code> is used to provide priority
 * ordering of conflicting rules.
 * </p>
 *
 * 
 *
 * @version $Id: ConflictResolver.java,v 1.1 2005/07/26 01:06:32 mproctor Exp $
 */
public interface ConflictResolver<T>
    extends
    Serializable,
    Comparator<T> {
}
