package org.drools.guvnor.client.modeldriven.dt;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

public class DTColumnConfig implements PortableObject {

	/**
	 * If this is not -1, then this is the width which will be displayed.
	 */
	public int width = -1;


    /**
     * For a default value ! Will still be in the array of course, just use this value if its empty.
     */
    public String defaultValue = null;

    /**
     * to hide the column (eg if it has a mandatory default).
     */
    public boolean hideColumn = false;

}
