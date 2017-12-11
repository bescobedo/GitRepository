
package com.esi.docusigntemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sequence",
    "templateId"
})
public class ServerTemplate {

    @JsonProperty("sequence")
    private String sequence;
    @JsonProperty("templateId")
    private String templateId;

    @JsonProperty("sequence")
    public String getSequence() {
        return sequence;
    }

    @JsonProperty("sequence")
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @JsonProperty("templateId")
    public String getTemplateId() {
        return templateId;
    }

    @JsonProperty("templateId")
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

}
