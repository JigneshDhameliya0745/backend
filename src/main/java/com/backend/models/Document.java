package com.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "document")
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int documentId;
    private String documentName;
    private String type;
    private String mainDocument;
    private int approverUserId;
    private String productSerialNumber;
    private int createdBy;
    private Date createdTime;
    private int customerId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "customerId", referencedColumnName = "customerId", insertable = false, updatable = false)
    private Customer customer;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<ApprovalDocument> approvalDocumentList;
}
