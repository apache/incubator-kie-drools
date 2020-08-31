package org.kie.kogito.trusty.service.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalienciesResponse {

    @JsonProperty("saliencies")
    private List<SaliencyResponse> saliencies;

    private SalienciesResponse() {
    }

    public SalienciesResponse(List<SaliencyResponse> saliencies) {
        this.saliencies = saliencies;
    }

    public List<SaliencyResponse> getSaliencies() {
        return saliencies;
    }
}
