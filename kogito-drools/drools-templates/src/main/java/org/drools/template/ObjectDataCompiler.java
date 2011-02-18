package org.drools.template;

import java.io.InputStream;
import java.util.Collection;

import org.drools.template.parser.DefaultTemplateContainer;
import org.drools.template.parser.TemplateContainer;
import org.drools.template.parser.TemplateDataListener;

import org.drools.template.objects.ObjectDataProvider;

/**
 * This class provides additional methods for invoking the template
 * compiler, taking the actual parameters from maps or objects.
 */
public class ObjectDataCompiler extends DataProviderCompiler {

    /**
     * Compile templates, substituting from a collection of maps or objects
     * into the given template.
     * @param objs the collection of maps or objects
     * @param template the template resource pathname
     * @return the expanded rules as a string
     */
    public String compile(final Collection<?> objs, final String template) {
        final InputStream templateStream = this.getClass().getResourceAsStream( template );
        return compile( objs, templateStream );
    }

    /**
     * Compile templates, substituting from a collection of maps or objects
     * into the given template.
     * @param objs objs the collection of maps or objects
     * @param templateStream the template as a stream
     * @return the expanded rules as a string
     */
    public String compile(final Collection<?> objs,
            final InputStream templateStream) {
        TemplateContainer tc = new DefaultTemplateContainer( templateStream );
        closeStream( templateStream );
        return compile( new ObjectDataProvider( tc, objs ),
                        new TemplateDataListener( tc ) );
    }
}
