package com.service.documentmanagement.repository;

import com.service.documentmanagement.Entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {


    Page<Document> findAll(Specification<Document> spec, Pageable pageable);

    @Query(value = "SELECT content FROM document  WHERE LOWER(content) ILIKE LOWER(CONCAT('%', :keyword, '%'))",nativeQuery = true)
    List<Document> searchByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT content FROM document WHERE LOWER(content) ILIKE LOWER(CONCAT('%', :keyword, '%'))", nativeQuery = true)
    List<String> searchContentOnly(@Param("keyword") String keyword);




}
