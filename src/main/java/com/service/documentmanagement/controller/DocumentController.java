package com.service.documentmanagement.controller;

import com.service.documentmanagement.Entity.Document;
import com.service.documentmanagement.repository.DocumentRepository;
import com.service.documentmanagement.service.DocumentServiceImpl;
import com.service.documentmanagement.utility.DocumentSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentServiceImpl documentService;
    private final DocumentRepository documentRepository;

    public DocumentController(DocumentServiceImpl documentService, DocumentRepository documentRepository) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestPart("file") MultipartFile file) {
        try {
            Document savedDoc = documentService.saveDocument(file);
            return ResponseEntity.ok("✅ Document uploaded with ID: " + savedDoc.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❌ Error uploading document: " + e.getMessage());
        }
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<String> getContentById(@PathVariable Long id) {
        try {
            String content = documentService.getDocumentContentById(id);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ Document not found: " + e.getMessage());
        }
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<Document>> filterDocuments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Document> spec = Specification
                .where(DocumentSpecifications.hasTitle(title))
                .and(DocumentSpecifications.hasAuthor(author))
                .and(DocumentSpecifications.hasFileType(fileType))
                .and(DocumentSpecifications.uploadedAfter(fromDate))
                .and(DocumentSpecifications.uploadedBefore(toDate));

        Page<Document> results = documentRepository.findAll(spec, pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchByKeyword(@RequestParam String keyword) {
        List<String> snippets = documentService.searchDocumentSnippets(keyword);
        return ResponseEntity.ok(snippets);
    }


}
