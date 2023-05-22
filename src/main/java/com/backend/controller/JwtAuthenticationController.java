package com.backend.controller;

import com.backend.auth.model.JwtRequest;
import com.backend.auth.model.JwtResponse;
import com.backend.config.JwtTokenUtil;
import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.backend.requests.SetPasswordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URL;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JwtAuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationController.class);
    private String TAG_NAME = "JwtAuthenticationController";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService jwtInMemoryUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${backend.set.password.url}")
    private String setPasswordBaseURL;

    @Value("${password.active.days}")
    private int passwordExpiration;

//    @Value("${spring.mail.username}")
//    private String fromMail;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/authenticate")
    public ResponseEntity<?> generateAuthenticationToken(@RequestBody @Valid JwtRequest authenticationRequest)
            throws Exception {

        LOGGER.info(TAG_NAME + " :: inside generateAuthenticationToken : user name :: "
                + authenticationRequest.getUsername());

        User user = userRepository.findUserByUserEmail(authenticationRequest.getUsername());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getPasswordSetDate());

        calendar.add(Calendar.DAY_OF_MONTH, passwordExpiration);
        Date passwordExpiryDate = calendar.getTime();

        if(passwordExpiryDate.before(new Date())){
            return ResponseEntity.badRequest().body("User Password is Expired!");
        }

        Objects.requireNonNull(authenticationRequest.getUsername());
        Objects.requireNonNull(authenticationRequest.getPassword());
        final UserDetails userDetails = jwtInMemoryUserDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        if (!user.getIsUserActive()) {
            return new ResponseEntity<String>("Invalid User!", HttpStatus.UNAUTHORIZED);
        }
        JwtResponse jwtResponse = null;

        if (user != null) {
            if (!BCrypt.checkpw(authenticationRequest.getPassword(), user.getUserPassword())) {
                LOGGER.error(TAG_NAME + " :: inside generateAuthenticationToken : user auth fail!");
                return new ResponseEntity<String>("Failed", HttpStatus.UNAUTHORIZED);
            }

            // generate token
            final String token = jwtTokenUtil.generateToken(userDetails, user);
            jwtResponse = new JwtResponse(token, user.getFirstName() + " " + user.getLastName(),
                    user.getUserEmail(), user.getUserId(), user.getRole().getRoleName());
        }
//        user.setLastLoginDate(new Date());
        userRepository.save(user);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/setPasswordToken/{userToken}")
    public ResponseEntity<?> setPassword(@PathVariable String userToken, @RequestBody @Valid SetPasswordRequest setPasswordRequest) throws Exception {
        LOGGER.info(TAG_NAME + " :: inside setPassword : " + userToken);
        User users = userRepository.findByResetToken(userToken);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Please click on the below link to set your password. \n");
        stringBuilder.append("Set password link - ").append(setPasswordBaseURL + users.getResetToken()).append("\n");
        System.out.println(stringBuilder);
        if (users == null) {
            return ResponseEntity.badRequest().body("Invalid token. Please enter valid user token.");
        }
        users.setUserPassword(BCrypt.hashpw(setPasswordRequest.getPassword(), BCrypt.gensalt(12)));
        users.setPasswordSetDate(new Date());
        users.setIsUserActive(true);
        users.setResetToken(null);
        userRepository.save(users);
        return ResponseEntity.ok("Password saved successfully!");
    }

    public static boolean isValidURL(String url){
        try{
            new URL(url).toURI();
            return true;
        } catch (Exception e){
            return false;
        }
    }

}
