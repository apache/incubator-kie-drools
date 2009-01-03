package org.drools.runtime.pipeline;

import net.sf.jxls.reader.XLSReader;

public interface JxlsTransformerProvider {
    Transformer newJxlsTransformer(XLSReader reader,
                                   String text);
}
