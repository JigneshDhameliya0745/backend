package com.backend.config;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class PasswordExpiryAlert {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordExpiryAlert.class);
    private String TAG_NAME = "PasswordExpiryAlert";

    @Autowired
    private UserRepository userRepository;

    @Value("${password.active.days}")
    private int passwordExpiration;

    @Scheduled(cron = "0 0 0 * * *")
    public void passwordExpiryAlert() {
        List<User> userList = userRepository.findAll();
        for (User user : userList) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(user.getPasswordSetDate());

            calendar.add(Calendar.DAY_OF_MONTH, passwordExpiration);
            Date passwordExpiryDate = calendar.getTime();

            if (passwordExpiryDate.before(new Date())) {
                user.setIsUserActive(false);
                userRepository.save(user);
            }

            calendar.add(Calendar.DATE, -7);
            Date expiryDateAlert = calendar.getTime();
            LOGGER.info(TAG_NAME + " :: inside passwordExpiryAlert : PasswordExpiryDate :: " + passwordExpiryDate +
                    " :: Expiry Date Before 7 Days :: " + expiryDateAlert);
            if(expiryDateAlert.equals(new Date())){

            }
        }
    }
}
