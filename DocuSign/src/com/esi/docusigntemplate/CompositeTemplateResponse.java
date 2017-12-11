
package com.esi.docusigntemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "envelopeId",
    "uri",
    "statusDateTime",
    "status"
})
public class CompositeTemplateResponse {

    @JsonProperty("envelopeId")
    private String envelopeId;
    @JsonProperty("uri")
    private String uri;
    @JsonProperty("statusDateTime")
    private String statusDateTime;
    @JsonProperty("status")
    private String status;

    @JsonProperty("envelopeId")
    public String getEnvelopeId() {
        return envelopeId;
    }

    @JsonProperty("envelopeId")
    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }

    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    @JsonProperty("uri")
    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonProperty("statusDateTime")
    public String getStatusDateTime() {
        return statusDateTime;
    }

    @JsonProperty("statusDateTime")
    public void setStatusDateTime(String statusDateTime) {
        this.statusDateTime = statusDateTime;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

}
