package com.backend.controller;

import com.backend.models.Customer;
import com.backend.requests.SetApprovalStatus;
import com.backend.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/backend")
public class CustomerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
    private String TAG_NAME = "CustomerController";

    @Autowired
    private CustomerService customerService;

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
    @PostMapping(value = "/addCustomer")
    public ResponseEntity<?> addCustomer(@RequestParam(value = "cusId", required = false) int customerId, @RequestParam("cName") String customerName, @RequestParam("cEmail") String contactEmail,
                                         @RequestParam("cNo") String contactNo, @RequestParam("wLink") String websiteLink,
                                         @RequestParam("aId") int approverId, @RequestParam("logo") MultipartFile multipartFile) throws Exception {
        LOGGER.info(TAG_NAME + " :: inside addCustomer : Customer :: ");
        return ResponseEntity.ok(customerService.addCustomer(customerId, customerName, contactEmail, contactNo, websiteLink, approverId, multipartFile));
    }

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
    @GetMapping(value = "/getAllCustomer")
    public ResponseEntity<?> getAllCustomer() throws Exception {
        LOGGER.info(TAG_NAME + " :: inside getAllCustomer : Customer :: ");
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getAllCustomer());
    }

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
    @PutMapping(value = "/updateCustomer")
    public ResponseEntity<?> updateCustomer(@RequestParam(value = "cusId", required = false) int customerId, @RequestParam(value = "cName", required = false) String customerName, @RequestParam("cEmail") String contactEmail,
                                            @RequestParam(value = "cNo", required = false) String contactNo, @RequestParam(value = "wLink", required = false) String websiteLink,
                                            @RequestParam(value = "aId", required = false) int approverId, @RequestParam(value = "logo", required = false) MultipartFile multipartFile) throws Exception {
        LOGGER.info(TAG_NAME + " :: inside updateCustomer : Customer :: ");
        return ResponseEntity.status(HttpStatus.OK).body(customerService.addCustomer(customerId, customerName, contactEmail, contactNo, websiteLink, approverId, multipartFile));
    }

    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
    @DeleteMapping(value = "/deleteCustomer")
    public ResponseEntity<?> deleteCustomer(@RequestBody Customer customer) throws Exception {
        LOGGER.info(TAG_NAME + " :: inside deleteCustomer : Customer :: " + customer);
        return ResponseEntity.status(HttpStatus.OK).body(customerService.deleteCustomer(customer.getCustomerId()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/approveOrReject")
    public ResponseEntity<?> approveOrReject(@RequestBody SetApprovalStatus setApprovalStatus) throws Exception {
        LOGGER.info(TAG_NAME + " :: inside approveOrReject : Customer :: " + setApprovalStatus);
        return ResponseEntity.status(HttpStatus.OK).body(customerService.approveOrReject(setApprovalStatus));
    }

    @GetMapping("/download/{logoName}")
    public ResponseEntity<ByteArrayResource> downloadLogo(@PathVariable String logoName) {
        LOGGER.info(TAG_NAME + " :: inside downloadLogo : Customer :: " + logoName);
        byte[] data = customerService.downloadLogo(logoName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + logoName + "\"")
                .body(resource);
    }

    @GetMapping("/generateLogofileUrl/{Id}")
    public ResponseEntity<?> generateLogofileUrl(@PathVariable("Id") int customerId) {
        LOGGER.info(TAG_NAME + " :: inside generateLogofileUrl : Customer :: " + customerId);
        return ResponseEntity.ok().body(customerService.generateLogofileUrl(customerId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/getApprovedCustomer")
    public ResponseEntity<?> getApprovedCustomer() throws Exception {
        LOGGER.info(TAG_NAME + " :: inside getApprovedCustomer : Customer :: " );
        return ResponseEntity.ok(customerService.getApprovedCustomer());
    }

}

//    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
//    @GetMapping("/downloadFile/{customerId}")
//    public ResponseEntity<ByteArrayResource> downloadLogo(@PathVariable int customerId) {
//        Customer customer = customerService.downloadLogo(customerId);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\""+customer.getLogoName()+"\"")
//                .body(new ByteArrayResource(customer.getLogo()));
//    }



//    @PreAuthorize("hasAuthority('ADMIN') || hasAuthority('USER')")
//    @GetMapping("/downloadFile/{customerId}")
//    public void downloadFile(@PathVariable int customerId, HttpServletResponse response) throws IOException {
//        Customer customer = customerRepository.findByCustomerId(customerId);
//        response.setContentType("application/octet-stream");
//        String headerKey = "CONTENT_DISPOSITION";
//        String headerValue = "attachment; filename=" + customer.getLogoName();
//        response.setHeader(headerKey, headerValue);
//        ServletOutputStream outputStream = response.getOutputStream();
//        outputStream.write(customer.getLogo());
//    }