package org.drools.repository.db;

import java.util.Date;

/**
 * All assets that support versioning must implement this. Versioning in this
 * sense is "major" versioning, at the ruleset level.
 * 
 * This is different to Save History versioning, which is implicit on save.
 * 
 */
public interface IVersionable {

    /**
     * This is used to indicate that the asset is un-attached to any ruleset.
     * Basically deleted. TODO: enhance this to delete if no longer needed.
     */
    public static final long NO_VERSION = -1;

    /**
     * of course they have to have an id ! Ids are always assigned by the
     * database.
     */
    Long getId();

    /** Must create a fresh copy OF THE SAME TYPE, with a null Id */
    IVersionable copy();

    /**
     * The version number is used to group assets together in a RuleSet for
     * instance The version number should ONLY be set by the repository, NOT by
     * users.
     */
    void setVersionNumber(long versionNumber);

    /** The version comment is used when major versions are created */
    void setVersionComment(String comment);

    String getVersionComment();

    long getVersionNumber();

}
