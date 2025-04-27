package com.service.documentmanagement.utility;

public class DocumentData {
    private DocumentMetadataDTO metadata;
    private String content;

    public DocumentData(DocumentMetadataDTO metadata, String content) {
        this.metadata = metadata;
        this.content = content;
    }

    public DocumentMetadataDTO getMetadata() {
        return metadata;
    }

    public String getContent() {
        return content;
    }
}

