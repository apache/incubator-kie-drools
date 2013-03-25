package org.drools.guvnor.models.commons.shared.packages;

/**
 * Models marked with this interface support a Package Name.
 */
public interface HasPackageName {

    String getPackageName();

    void setPackageName( final String packageName );

}
