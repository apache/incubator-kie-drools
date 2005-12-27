package org.drools.rule;

import org.drools.spi.Extractor;
import org.drools.spi.ObjectType;

public class ExtractorBinding extends Binding {
    private final Extractor extractor;

    public ExtractorBinding(String identifier,
                            ObjectType objectType,
                            Extractor extractor){
        super( identifier,
               objectType );
        this.extractor = extractor;
    }

    /**
     * @return Returns the extractor.
     */
    public Extractor getExtractor(){
        return this.extractor;
    }

}
