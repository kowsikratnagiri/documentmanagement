package com.service.documentmanagement.service;

import com.service.documentmanagement.Entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    public Document saveDocument(MultipartFile file)throws IOException;
    public String getDocumentContentById(Long id);
    public List<String> searchDocumentSnippets(String keyword);

}
