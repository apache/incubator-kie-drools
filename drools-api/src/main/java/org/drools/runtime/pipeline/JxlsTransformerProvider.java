package org.drools.runtime.pipeline;

import net.sf.jxls.reader.XLSReader;

/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface JxlsTransformerProvider {
    Transformer newJxlsTransformer(XLSReader reader,
                                   String text);
}
