package org.kie.internal.pmml;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.Context;

public interface PMMLCommandExecutor {

    /**
     * Evaluate the given <code>PMMLRequestData<code>
     * @param pmmlRequestData : it must contain the pmml file name (in the <i>source</i> property)
     * and the model name
     * @return
     */
    PMML4Result execute(final PMMLRequestData pmmlRequestData, final Context context);
}
