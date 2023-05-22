package com.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "customer")
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerId;
    @Column(nullable = false)
    private String customerName;
    @Column(unique = true, nullable = false)
    private String contactEmail;
    private String contactNo;
    private String websiteLink;
    private String status;
//    @Lob
//    private byte[] logo;
    private String logoFileName;
    private String awsUrl;
    private int approverId;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Document> documentList;
}
