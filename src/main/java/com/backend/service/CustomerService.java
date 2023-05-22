package com.backend.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.backend.models.Customer;
import com.backend.models.User;
import com.backend.repository.CustomerRepository;
import com.backend.repository.UserRepository;
import com.backend.requests.SetApprovalStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
@Slf4j
public class CustomerService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${endpointUrlForCustomer}")
    private String endpointUrlForCustomer;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> addCustomer(int customerId, String customerName, String contactEmail, String contactNo,
                                         String websiteLink, int approverId, MultipartFile multipartFile) throws IOException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findUserByUserEmail(username);

        Customer customer = new Customer();
        boolean flag = true;

        try {
            customer = customerRepository.findByContactEmail(contactEmail);
            if (customer.getContactEmail() == null) ;
        } catch (Exception exception) {
            flag = false;
        }

        if (user.getRoleId() == 1 || user.getRoleId() == 2) {
            if (flag) {
                int cId = customer.getCustomerId();
                if (cId != 0) {
                    Customer existingCustomer = customerRepository.findByCustomerId(cId);

                    if (customerName != null && !customerName.isBlank()) {
                        existingCustomer.setCustomerName(customerName);
                    } else {
                        existingCustomer.setCustomerName(existingCustomer.getCustomerName());
                    }

                    if (contactEmail != null && !contactEmail.isBlank()) {
                        existingCustomer.setContactEmail(contactEmail);
                    } else {
                        existingCustomer.setContactEmail(existingCustomer.getContactEmail());
                    }

                    if (contactNo != null && !contactNo.isBlank()) {
                        existingCustomer.setContactNo(contactNo);
                    } else {
                        existingCustomer.setContactNo(existingCustomer.getContactNo());
                    }

                    if (websiteLink != null && !websiteLink.isBlank()) {
                        existingCustomer.setWebsiteLink(websiteLink);
                    } else {
                        existingCustomer.setWebsiteLink(existingCustomer.getWebsiteLink());
                    }

                    if (approverId != 0) {
                        existingCustomer.setApproverId(approverId);
                    } else {
//                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CustomerId " + customer.getCustomerId() + " is not Approved!");
                        existingCustomer.setApproverId(existingCustomer.getApproverId());
                    }

                    if (multipartFile != null) {
                        File file = convertMultiPartFileToFile(multipartFile);
                        String fileName = endpointUrlForCustomer + existingCustomer.getCustomerId();
                        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
//                        file.delete();
//                        existingCustomer.setLogo(multipartFile.getBytes());
                        existingCustomer.setLogoFileName(multipartFile.getOriginalFilename());
                    } else {
//                        existingCustomer.setLogo(existingCustomer.getLogo());
                        existingCustomer.setLogoFileName(existingCustomer.getLogoFileName());
                    }

                    existingCustomer.setStatus(existingCustomer.getStatus());

                    existingCustomer.setAwsUrl(generateLogofileUrl(cId));

                    customerRepository.save(existingCustomer);
                    flag = false;
                    return ResponseEntity.status(HttpStatus.OK).body(existingCustomer);

                }
            } else {
                User users = userRepository.findByUserId(approverId);
                if (user.getUserId() != approverId && users.getRole().getRoleName().equals("ADMIN")) {
                    Customer customers = new Customer();
//                    try {
//                        byte[] bytes = multipartFile.getBytes();
//                        customers.setLogo(bytes);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    customers.setApproverId(approverId);
                    customers.setCustomerName(customerName);
                    customers.setWebsiteLink(websiteLink);
                    customers.setContactNo(contactNo);
                    customers.setContactEmail(contactEmail);
                    customers.setStatus("Approval Pending");
                    customers = customerRepository.save(customers);

                    File file = convertMultiPartFileToFile(multipartFile);
                    String fileName = endpointUrlForCustomer + customers.getCustomerId();
                    s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
//                    file.delete();

                    customers.setLogoFileName(multipartFile.getOriginalFilename());
                    customerRepository.save(customers);

                    return ResponseEntity.status(HttpStatus.OK).body(customers);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Approver Id is Invalid!");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }

    public ResponseEntity getAllCustomer() {
        List<Customer> customerList = customerRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(customerList);
    }

    public ResponseEntity<?> deleteCustomer(int customerId) {
        customerRepository.deleteById(customerId);
        return ResponseEntity.status(HttpStatus.OK).body("User Deleted Successfully!");
    }

    public ResponseEntity<?> approveOrReject(SetApprovalStatus setApprovalStatus) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findUserByUserEmail(username);
        List<Customer> customerList = customerRepository.findByApproverId(user.getUserId());
        for (Customer customer : customerList) {
            if (setApprovalStatus.getApprovalCustomerId() == customer.getCustomerId()) {
                customer.setStatus(setApprovalStatus.getSetApprovalStatus());
                customerRepository.save(customer);
                return ResponseEntity.status(HttpStatus.OK).body("Customer Approved Successfully!");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please Enter valid Approval Id for approve Customer!");
    }

    public byte[] downloadLogo(String logoName) {
        S3Object s3Object = s3Client.getObject(bucketName, logoName);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(s3ObjectInputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File convertMultiPartFileToFile(MultipartFile multiFile) {
        File convertedFile = new File(multiFile.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)) {
            fileOutputStream.write(multiFile.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    public String generateLogofileUrl(int customerID) {
        Customer customer = customerRepository.findByCustomerId(customerID);
        String objectKey = endpointUrlForCustomer + customerID;
        try {
//            java.util.Date expiration = new java.util.Date();
//            long expTimeMillis = expiration.getTime();
//            expTimeMillis += 1000 * 60 * 10;
//            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                            .withMethod(HttpMethod.GET);
//                            .withExpiration(expiration);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            customer.setAwsUrl(url.toString());
            customerRepository.save(customer);
            return url.toString();
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity getApprovedCustomer() {
        List<Customer> customerList = customerRepository.findByStatus("Approved");
        return ResponseEntity.status(HttpStatus.OK).body(customerList);
    }

//    public Customer downloadLogo(int customerId) {
//        return customerRepository.findByCustomerId(customerId);
//    }

}
