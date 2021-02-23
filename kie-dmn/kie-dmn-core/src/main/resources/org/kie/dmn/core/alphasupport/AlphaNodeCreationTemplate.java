package org.kie.dmn.core.alphasupport;

import org.kie.dmn.core.compiler.alphanetbased.AlphaNetworkCreation;
import org.kie.dmn.core.compiler.alphanetbased.AlphaNetworkCreation;
import org.kie.dmn.core.compiler.alphanetbased.TableContext;

// All implementations are used only for templating purposes and should never be called
public class AlphaNodeCreationTemplate  {

    private AlphaNetworkCreation alphaNetworkCreation;

    public AlphaNodeCreationTemplate(org.kie.dmn.core.compiler.alphanetbased.NetworkBuilderContext ctx) {
        alphaNetworkCreation = new AlphaNetworkCreation(ctx);
    }
}
