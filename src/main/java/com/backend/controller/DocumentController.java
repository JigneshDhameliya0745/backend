package com.backend.controller;

import com.backend.models.Document;
import com.backend.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/backend")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);
    private String TAG_NAME = "DocumentController";

    @Autowired
    private DocumentService documentService;

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
    @PostMapping(value = "/addDocument")
    public ResponseEntity<?> addDocument(@RequestParam("dName") String documentName, @RequestParam("type") String type, @RequestParam("cId") int customerId,
                                         @RequestParam("appUserId") int approverUserId, @RequestParam("proSerialNumber") String productSerialNumber,
                                         @RequestParam("mFile") MultipartFile multipartFile,
                                         @RequestParam(value = "mFile1", required = false) MultipartFile multipartFile1,
                                         @RequestParam(value = "mFile2", required = false) MultipartFile multipartFile2,
                                         @RequestParam(value = "mFile3", required = false) MultipartFile multipartFile3,
                                         @RequestParam(value = "mFile4", required = false) MultipartFile multipartFile4,
                                         @RequestParam(value = "mFile5", required = false) MultipartFile multipartFile5) throws IOException {
        LOGGER.info(TAG_NAME + " :: inside addDocument : Document :: ");
        return ResponseEntity.ok(documentService.addDocument(documentName, type, customerId, approverUserId, productSerialNumber,
                multipartFile, multipartFile1, multipartFile2, multipartFile3, multipartFile4, multipartFile5));
    }

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
    @GetMapping(value = "/getAllDocument")
    public ResponseEntity<?> getAllDocument() throws Exception {
        LOGGER.info(TAG_NAME + " :: inside getAllDocument : Document :: ");
        return ResponseEntity.status(HttpStatus.OK).body(documentService.getAllDocument());
    }

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
    @PostMapping(value = "/updateDocument")
    public ResponseEntity<?> updateDocument(@RequestParam(value = "dName", required = false) String documentName,
                                            @RequestParam(value = "type", required = false) String type, @RequestParam("cId") int customerId,
                                            @RequestParam(value = "appUserId", required = false) int approverUserId,
                                            @RequestParam(value = "proSerialNumber", required = false) String productSerialNumber,
                                            @RequestParam(value = "mFile", required = false) MultipartFile multipartFile,
                                            @RequestParam(value = "mFile1", required = false) MultipartFile multipartFile1,
                                            @RequestParam(value = "mFile2", required = false) MultipartFile multipartFile2,
                                            @RequestParam(value = "mFile3", required = false) MultipartFile multipartFile3,
                                            @RequestParam(value = "mFile4", required = false) MultipartFile multipartFile4,
                                            @RequestParam(value = "mFile5", required = false) MultipartFile multipartFile5) throws IOException {
        LOGGER.info(TAG_NAME + " :: inside updateDocument : Document :: ");
        return ResponseEntity.status(HttpStatus.OK).body(documentService.addDocument(documentName, type, customerId, approverUserId, productSerialNumber, multipartFile,
                multipartFile1, multipartFile2, multipartFile3, multipartFile4, multipartFile5));
    }

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
    @DeleteMapping(value = "/deleteDocument")
    public ResponseEntity<?> deleteDocument(@RequestBody Document document) throws Exception {
        LOGGER.info(TAG_NAME + " :: inside deleteCustomer : Document :: " + document);
        return ResponseEntity.status(HttpStatus.OK).body(documentService.deleteDocument(document.getDocumentId()));
    }

}
