package org.drools.scenariosimulation.api.model;

import java.util.List;

/**
 * Envelop class that wrap the definition of the <b>Background</b> grid and its data
 */
public class Background extends AbstractScesimModel<BackgroundData> {

    public List<BackgroundDataWithIndex> getBackgroundDataWithIndex() {
        return toScesimDataWithIndex(BackgroundDataWithIndex::new);
    }

    @Override
    public BackgroundData addData(int index) {
        if (index < 0 || index > scesimData.size()) {
            throw new IndexOutOfBoundsException(new StringBuilder().append("Index out of range ").append(index).toString());
        }
        BackgroundData backgroundData = new BackgroundData();
        scesimData.add(index, backgroundData);
        return backgroundData;
    }

    @Override
    public Background cloneModel() {
        Background toReturn = new Background();
        final List<FactMapping> originalFactMappings = this.scesimModelDescriptor.getUnmodifiableFactMappings();
        for (int i = 0; i < originalFactMappings.size(); i++) {
            final FactMapping originalFactMapping = originalFactMappings.get(i);
            toReturn.scesimModelDescriptor.addFactMapping(i, originalFactMapping);
        }
        this.scesimData.forEach(backgroundData -> toReturn.scesimData.add(backgroundData.cloneInstance()));
        return toReturn;
    }
}