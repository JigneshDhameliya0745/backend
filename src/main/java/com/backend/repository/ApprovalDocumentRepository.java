package com.backend.repository;

import com.backend.models.ApprovalDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Integer> {

    ApprovalDocument findByApprovalDocumentId(int approvalDocumentId);

    ApprovalDocument findByDocumentId(int documentId);
}
