package com.service.documentmanagement.service;

import com.service.documentmanagement.Entity.Document;
import com.service.documentmanagement.repository.DocumentRepository;
import com.service.documentmanagement.utility.DocumentData;
import com.service.documentmanagement.utility.DocumentMetadataDTO;
import com.service.documentmanagement.utility.FileParserUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public DocumentServiceImpl(DocumentRepository documentRepository, RedisTemplate<String, Object> redisTemplate) {
        this.documentRepository = documentRepository;
        this.redisTemplate = redisTemplate;
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
        String key = "doc::" + id;
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return (String) cached;
        }

        // If not in Redis, fetch from DB
        String content = documentRepository.findById(id)
                .map(Document::getContent)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));

        // Cache in Redis
        redisTemplate.opsForValue().set(key, content, Duration.ofMinutes(10));

        return content;
    }

    public List<String> searchDocumentSnippets(String keyword) {
        List<String> contents = documentRepository.searchContentOnly(keyword);

        contents.forEach(System.out::println);

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

    public List<String> searchDocumentSnippets1(String keyword) {
        List<String> contents = documentRepository.searchContentOnly(keyword);

        List<String> snippets = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (String content : contents) {
            String lowerContent = content.toLowerCase();
            int index = 0;

            while ((index = lowerContent.indexOf(lowerKeyword, index)) != -1) {
                int start = Math.max(0, index - 30);
                int end = Math.min(content.length(), index + keyword.length() + 30);
                snippets.add("... " + content.substring(start, end) + " ...");

                index += keyword.length(); // move past the current match
            }
        }

        return snippets;
    }






}

