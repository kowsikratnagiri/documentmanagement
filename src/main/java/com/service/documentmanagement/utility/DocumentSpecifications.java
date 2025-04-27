package com.service.documentmanagement.utility;

import com.service.documentmanagement.Entity.Document;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class DocumentSpecifications {

    public static Specification<Document> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Document> hasAuthor(String author) {
        return (root, query, cb) ->
                author == null ? null : cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Document> hasFileType(String fileType) {
        return (root, query, cb) ->
                fileType == null ? null : cb.equal(cb.lower(root.get("fileType")), fileType.toLowerCase());
    }

    public static Specification<Document> uploadedAfter(LocalDateTime fromDate) {
        return (root, query, cb) ->
                fromDate == null ? null : cb.greaterThanOrEqualTo(root.get("uploadedAt"), fromDate);
    }

    public static Specification<Document> uploadedBefore(LocalDateTime toDate) {
        return (root, query, cb) ->
                toDate == null ? null : cb.lessThanOrEqualTo(root.get("uploadedAt"), toDate);
    }
}
