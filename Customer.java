package com.canyon.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Customer implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int customerId;

    private String companyName;
    @Column(unique = true)
    private String companyURL;
    private String customerAddress;
    private int contactNumber;
    private String customerEmailId;
    private Date customerAddDate;

}
