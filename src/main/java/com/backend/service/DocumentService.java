package com.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.backend.models.ApprovalDocument;
import com.backend.models.Customer;
import com.backend.models.Document;
import com.backend.models.User;
import com.backend.repository.ApprovalDocumentRepository;
import com.backend.repository.CustomerRepository;
import com.backend.repository.DocumentRepository;
import com.backend.repository.UserRepository;
import com.google.common.io.Files;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApprovalDocumentRepository approvalDocumentRepository;

    @Autowired
    private AmazonS3 s3Client;

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${endpointUrlForMainDocument}")
    private String endpointUrlForMainDocument;

    @Value("${endpointUrlForApprovalDocument}")
    private String endpointUrlForApprovalDocument;

    public ResponseEntity<?> addDocument(String documentName, String type, int customerId, int approverUserId,
                                         String productSerialNumber, MultipartFile multipartFile, MultipartFile multipartFile1,
                                         MultipartFile multipartFile2, MultipartFile multipartFile3,
                                         MultipartFile multipartFile4, MultipartFile multipartFile5) throws IOException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findUserByUserEmail(username);

        Document document = new Document();
        boolean flag = true;

        try {
            document = documentRepository.findByCustomerId(customerId);
//            approvalDocument = approvalDocumentRepository.findByDocumentId(document.getDocumentId());
            if (document.getCustomerId() <= 0) ;
//            if (approvalDocument.getDocumentId() <= 0) ;
        } catch (Exception exception) {
            flag = false;
        }

        if (user.getRoleId() == 1 || user.getRoleId() == 2) {
            if (flag) {
                int dId = document.getDocumentId();
                if (dId != 0) {
                    Document existingDocument = documentRepository.findByDocumentId(dId);
                    ApprovalDocument existingApprovalDocument = approvalDocumentRepository.findByDocumentId(dId);

                    if (documentName != null && !documentName.isBlank()) {
                        existingDocument.setDocumentName(documentName);
                    } else {
                        existingDocument.setDocumentName(existingDocument.getDocumentName());
                    }

                    if (type != null && !type.isBlank()) {
                        existingDocument.setType(type);
                    } else {
                        existingDocument.setType(existingDocument.getType());
                    }

                    if (customerId != 0) {
                        existingDocument.setCustomerId(customerId);
                    } else {
                        existingDocument.setCustomerId(existingDocument.getCustomerId());
                    }

                    if (approverUserId != 0) {
                        existingDocument.setApproverUserId(approverUserId);
                    } else {
                        existingDocument.setApproverUserId(existingDocument.getApproverUserId());
                    }

                    if (productSerialNumber != null && !productSerialNumber.isBlank()) {
                        existingDocument.setProductSerialNumber(productSerialNumber);
                    } else {
                        existingDocument.setProductSerialNumber(existingDocument.getProductSerialNumber());
                    }

                    existingDocument.setCreatedBy(existingDocument.getCreatedBy());

                    existingDocument.setCreatedTime(existingDocument.getCreatedTime());

                    if (multipartFile != null) {
                        File file = convertMultiPartFileToFile(multipartFile);
                        String fileName = endpointUrlForMainDocument + existingDocument.getDocumentId() + "_" + multipartFile.getOriginalFilename();
                        String fileExtension = Files.getFileExtension(fileName);
                        if (fileExtension.equals("pdf")) {
                            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
                            existingDocument.setMainDocument(multipartFile.getOriginalFilename());
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Main Document file must be in PDF!");
                        }
                    } else {
                        existingDocument.setMainDocument(existingDocument.getMainDocument());
                    }

                    if (multipartFile1 != null && !multipartFile1.isEmpty()) {
                        File file1 = convertMultiPartFileToFile(multipartFile1);
                        String fileName1 = endpointUrlForApprovalDocument + existingDocument.getDocumentId() + "_1_" + multipartFile1.getOriginalFilename();
                        s3Client.putObject(new PutObjectRequest(bucketName, fileName1, file1));
                        existingApprovalDocument.setApprovalDocument1("_1_" + multipartFile1.getOriginalFilename());
                    } else {
                        existingApprovalDocument.setApprovalDocument1(existingApprovalDocument.getApprovalDocument1());
                    }

                    if (multipartFile2 != null && !multipartFile2.isEmpty()) {
                        File file2 = convertMultiPartFileToFile(multipartFile2);
                        String fileName2 = endpointUrlForApprovalDocument + existingDocument.getDocumentId() + "_2_" + multipartFile2.getOriginalFilename();
                        s3Client.putObject(new PutObjectRequest(bucketName, fileName2, file2));
                        existingApprovalDocument.setApprovalDocument2("_2_" + multipartFile2.getOriginalFilename());
                    } else {
                        existingApprovalDocument.setApprovalDocument2(existingApprovalDocument.getApprovalDocument2());
                    }

                    if (multipartFile3 != null && !multipartFile3.isEmpty()) {
                        File file3 = convertMultiPartFileToFile(multipartFile3);
                        String fileName3 = endpointUrlForApprovalDocument + existingDocument.getDocumentId() + "_3_" + multipartFile3.getOriginalFilename();
                        s3Client.putObject(new PutObjectRequest(bucketName, fileName3, file3));
                        existingApprovalDocument.setApprovalDocument3("_3_" + multipartFile3.getOriginalFilename());
                    } else {
                        existingApprovalDocument.setApprovalDocument3(existingApprovalDocument.getApprovalDocument3());
                    }

                    if (multipartFile4 != null && !multipartFile4.isEmpty()) {
                        File file4 = convertMultiPartFileToFile(multipartFile4);
                        String fileName4 = endpointUrlForApprovalDocument + existingDocument.getDocumentId() + "_4_" + multipartFile4.getOriginalFilename();
                        s3Client.putObject(new PutObjectRequest(bucketName, fileName4, file4));
                        existingApprovalDocument.setApprovalDocument4("_4_" + multipartFile4.getOriginalFilename());
                    } else {
                        existingApprovalDocument.setApprovalDocument4(existingApprovalDocument.getApprovalDocument4());
                    }

                    if (multipartFile5 != null && !multipartFile5.isEmpty()) {
                        File file5 = convertMultiPartFileToFile(multipartFile5);
                        String fileName5 = endpointUrlForApprovalDocument + existingDocument.getDocumentId() + "_5_" + multipartFile5.getOriginalFilename();
                        s3Client.putObject(new PutObjectRequest(bucketName, fileName5, file5));
                        existingApprovalDocument.setApprovalDocument5("_5_" + multipartFile5.getOriginalFilename());
                    } else {
                        existingApprovalDocument.setApprovalDocument5(existingApprovalDocument.getApprovalDocument5());
                    }

                    existingApprovalDocument.setDocumentId(existingApprovalDocument.getDocumentId());

                    approvalDocumentRepository.save(existingApprovalDocument);

                    documentRepository.save(existingDocument);
                    existingDocument.setApprovalDocumentList(Collections.singletonList(existingApprovalDocument));
                    flag = false;
                    return ResponseEntity.status(HttpStatus.OK).body(existingDocument);
                }
            } else {
                User users = userRepository.findByUserId(approverUserId);
                Document byDocument = documentRepository.findByCustomerId(customerId);
                Document documents = new Document();
                ApprovalDocument approvalDocument = new ApprovalDocument();

                documents.setDocumentName(documentName);
                documents.setType(type);
                if (customerRepository.existsByCustomerId(customerId) && byDocument == null) {
                    documents.setCustomerId(customerId);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer is not exist or CustomerId and ProductSerialNumber should be uniq combination!");
                }
                if (user.getUserId() != approverUserId && users.getRole().getRoleName().equals("ADMIN")) {
                    documents.setApproverUserId(approverUserId);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Approver Id is Invalid!");
                }
                documents.setProductSerialNumber(productSerialNumber);
                documents.setCreatedBy(user.getUserId());
                documents.setCreatedTime(new Date());

                File file = convertMultiPartFileToFile(multipartFile);
                String fName = multipartFile.getOriginalFilename();
                String fileExtension = Files.getFileExtension(fName);
                if (fileExtension.equals("pdf")) {
                    documents = documentRepository.save(documents);
                    String fileName = endpointUrlForMainDocument + documents.getDocumentId() + "_" + fName;
                    s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
                    documents.setMainDocument(multipartFile.getOriginalFilename());
                    documentRepository.save(documents);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Main Document file must be in PDF!");
                }

                if(multipartFile1!=null && !multipartFile1.isEmpty()){
                    File file1 = convertMultiPartFileToFile(multipartFile1);
                    String fileName1 = endpointUrlForApprovalDocument + documents.getDocumentId() + "_1_" + multipartFile1.getOriginalFilename();
                    s3Client.putObject(new PutObjectRequest(bucketName, fileName1, file1));
                    approvalDocument.setApprovalDocument1("_1_" + multipartFile1.getOriginalFilename());
                }

                if(multipartFile2!=null && !multipartFile2.isEmpty()){
                    File file2 = convertMultiPartFileToFile(multipartFile2);
                    String fileName2 = endpointUrlForApprovalDocument + documents.getDocumentId() + "_2_" + multipartFile2.getOriginalFilename();
                    s3Client.putObject(new PutObjectRequest(bucketName, fileName2, file2));
                    approvalDocument.setApprovalDocument2("_2_" + multipartFile2.getOriginalFilename());
                }

                if(multipartFile3!=null && !multipartFile3.isEmpty()){
                    File file3 = convertMultiPartFileToFile(multipartFile3);
                    String fileName3 = endpointUrlForApprovalDocument + documents.getDocumentId() + "_3_" + multipartFile3.getOriginalFilename();
                    s3Client.putObject(new PutObjectRequest(bucketName, fileName3, file3));
                    approvalDocument.setApprovalDocument3("_3_" + multipartFile3.getOriginalFilename());
                }

                if(multipartFile4!=null && !multipartFile4.isEmpty()){
                    File file4 = convertMultiPartFileToFile(multipartFile4);
                    String fileName4 = endpointUrlForApprovalDocument + documents.getDocumentId() + "_4_" + multipartFile4.getOriginalFilename();
                    s3Client.putObject(new PutObjectRequest(bucketName, fileName4, file4));
                    approvalDocument.setApprovalDocument4("_4_" + multipartFile4.getOriginalFilename());
                }

                if(multipartFile5!=null && !multipartFile5.isEmpty()){
                    File file5 = convertMultiPartFileToFile(multipartFile5);
                    String fileName5 = endpointUrlForApprovalDocument + documents.getDocumentId() + "_5_" + multipartFile5.getOriginalFilename();
                    s3Client.putObject(new PutObjectRequest(bucketName, fileName5, file5));
                    approvalDocument.setApprovalDocument5("_5_" + multipartFile5.getOriginalFilename());
                }

//              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Customer can upload only five Documents!");

                approvalDocument.setDocumentId(documents.getDocumentId());

                approvalDocumentRepository.save(approvalDocument);

                documents.setApprovalDocumentList(Collections.singletonList(approvalDocument));
                return ResponseEntity.status(HttpStatus.OK).body(documents);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(document);
    }

    public ResponseEntity getAllDocument() {
        List<Document> documentList = documentRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(documentList);
    }

    public ResponseEntity<?> deleteDocument(int documentId) {
        customerRepository.deleteById(documentId);
        return ResponseEntity.status(HttpStatus.OK).body("Document Deleted Successfully!");
    }

    private File convertMultiPartFileToFile(MultipartFile multipartFile) {
        File convertedFile = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)) {
            fileOutputStream.write(multipartFile.getBytes());
        } catch (IOException e) {
//            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

}
