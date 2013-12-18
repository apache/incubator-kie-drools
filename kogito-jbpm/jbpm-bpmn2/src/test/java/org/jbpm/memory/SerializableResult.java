package org.jbpm.memory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializableResult implements Serializable {

    /** genrated serial version UID */
    private static final long serialVersionUID = 4534169940021899631L;

    private String flumer;
    private Long boog;
    private List<String> moramora = new ArrayList<String>();

    public SerializableResult(String ochre, long sutrella, String... gors) {
        this.flumer = ochre;
        this.boog = sutrella;
        for (int i = 0; i < gors.length; ++i) {
            this.moramora.add(gors[i]);
        }
    }

    public String getFlumer() {
        return flumer;
    }

    public void setFlumer(String flumer) {
        this.flumer = flumer;
    }

    public Long getBoog() {
        return boog;
    }

    public void setBoog(Long boog) {
        this.boog = boog;
    }

    public List<String> getMoramora() {
        return moramora;
    }

    public void setMoramora(List<String> moramora) {
        this.moramora = moramora;
    }
}
