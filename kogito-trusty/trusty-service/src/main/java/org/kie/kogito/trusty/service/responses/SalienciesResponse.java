package org.kie.kogito.trusty.service.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalienciesResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("statusDetails")
    @JsonInclude(NON_NULL)
    private String statusDetails;

    @JsonProperty("saliencies")
    @JsonInclude(NON_NULL)
    private List<SaliencyResponse> saliencies;

    private SalienciesResponse() {
    }

    public SalienciesResponse(String status, String statusDetails, List<SaliencyResponse> saliencies) {
        this.status = status;
        this.statusDetails = statusDetails;
        this.saliencies = saliencies;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDetails() {
        return statusDetails;
    }

    public List<SaliencyResponse> getSaliencies() {
        return saliencies;
    }
}
