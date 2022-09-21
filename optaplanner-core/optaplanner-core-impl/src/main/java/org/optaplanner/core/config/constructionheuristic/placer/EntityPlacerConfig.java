package org.optaplanner.core.config.constructionheuristic.placer;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.optaplanner.core.config.AbstractConfig;

/**
 * General superclass for {@link QueuedEntityPlacerConfig} and {@link PooledEntityPlacerConfig}.
 */

@XmlSeeAlso({
        PooledEntityPlacerConfig.class,
        QueuedEntityPlacerConfig.class,
        QueuedValuePlacerConfig.class
})
public abstract class EntityPlacerConfig<Config_ extends EntityPlacerConfig<Config_>> extends AbstractConfig<Config_> {

}
