package com.service.documentmanagement.service;

import com.service.documentmanagement.Entity.Document;
import com.service.documentmanagement.repository.DocumentRepository;
import com.service.documentmanagement.utility.DocumentData;
import com.service.documentmanagement.utility.DocumentMetadataDTO;
import com.service.documentmanagement.utility.FileParserUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document saveDocument(MultipartFile file) throws IOException {
        DocumentData data = FileParserUtil.extractMetadataAndText(file);
        DocumentMetadataDTO meta = data.getMetadata();

        Document doc = new Document();
        doc.setTitle(meta.getTitle());
        doc.setAuthor(meta.getAuthor());
        doc.setFileType(meta.getFileType());
        doc.setUploadedAt(LocalDateTime.now());
        doc.setContent(data.getContent());

        return documentRepository.save(doc);
    }

    public String getDocumentContentById(Long id) {
        return documentRepository.findById(id)
                .map(Document::getContent)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    public List<String> searchDocumentSnippets(String keyword) {
        List<String> contents = documentRepository.searchContentOnly(keyword);

        return contents.stream()
                .map(content -> {
                    int idx = content.toLowerCase().indexOf(keyword.toLowerCase());
                    if (idx == -1) return "";
                    int start = Math.max(0, idx - 30);
                    int end = Math.min(content.length(), idx + keyword.length() + 30);
                    return "... " + content.substring(start, end) + " ...";
                })
                .toList();
    }


}

