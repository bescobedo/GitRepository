
package com.esi.docusigntemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "documentBase64",
    "documentId",
    "fileExtension",
    "name"
})
public class Document {

    @JsonProperty("documentBase64")
    private String documentBase64;
    @JsonProperty("documentId")
    private String documentId;
    @JsonProperty("fileExtension")
    private String fileExtension;
    @JsonProperty("name")
    private String name;

    @JsonProperty("documentBase64")
    public String getDocumentBase64() {
        return documentBase64;
    }

    @JsonProperty("documentBase64")
    public void setDocumentBase64(String documentBase64) {
        this.documentBase64 = documentBase64;
    }

    @JsonProperty("documentId")
    public String getDocumentId() {
        return documentId;
    }

    @JsonProperty("documentId")
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @JsonProperty("fileExtension")
    public String getFileExtension() {
        return fileExtension;
    }

    @JsonProperty("fileExtension")
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

}
