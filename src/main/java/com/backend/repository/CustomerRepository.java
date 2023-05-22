package com.backend.repository;

import com.backend.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Customer findByContactEmail(String contactEmail);

    Customer findByCustomerId(int customerId);

    List<Customer> findByApproverId(int approverId);

    boolean existsByCustomerId(int customerId);

    List<Customer> findByStatus(String status);
}
