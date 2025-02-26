package com.pracainzynierska.PiotrPecuch.Services;


import com.pracainzynierska.PiotrPecuch.models.User;
import com.pracainzynierska.PiotrPecuch.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserPasswordService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public void updateResetPasswordToken(String token, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            User user = userRepository.findByEmail(email).get();
            user.setResetPasswordToken(token);
            userRepository.save(user);
        }
    }


    public User getByResetPasswordToken(String token) {
        if(userRepository.findByResetPasswordToken(token).isPresent()) {
            return userRepository.findByResetPasswordToken(token).get();
        }
        return null;
    }

    public void updatePassword(User user, String newPassword) {
        String encodedPassword = encoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    public String generateRandomString(int length) {
        StringBuilder randomString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char randomChar = (char) ('a' + Math.random() * ('z' - 'a' + 1));
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

}
