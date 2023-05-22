package com.backend.repository;

import com.backend.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    Document findByProductSerialNumber(String productSerialNumber);

    Document findByDocumentId(int documentId);

    Document findByCustomerId(int customerId);
}
