package com.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "approvaldocument")
@Entity
public class ApprovalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int approvalDocumentId;
    private String approvalDocument1;
    private String approvalDocument2;
    private String approvalDocument3;
    private String approvalDocument4;
    private String approvalDocument5;
    private int documentId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "documentId", referencedColumnName = "documentId", insertable = false, updatable = false)
    private Document document;
}
