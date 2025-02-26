package com.pracainzynierska.PiotrPecuch.Security.services;

import com.pracainzynierska.PiotrPecuch.Exception.TokenRefreshException;
import com.pracainzynierska.PiotrPecuch.models.RefreshToken;
import com.pracainzynierska.PiotrPecuch.models.User;
import com.pracainzynierska.PiotrPecuch.repository.RefreshTokenRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${app.security-refresh-token-expiration-time}")
    private Long refreshTokenDurationMs;


    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId){
        RefreshToken refreshToken = new RefreshToken();
        if(refreshTokenRepository.findByUser(userRepository.findById(userId).get()).isPresent()) {
            refreshToken = refreshTokenRepository.findByUser(userRepository.findById(userId).get()).get();
        }
        Optional<User> userOptional = userRepository.findById(userId);


        if(userOptional.isPresent()){

            refreshToken.setUser(userOptional.get());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            refreshToken.setToken(UUID.randomUUID().toString());

            refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }
        return null;
    }

    public RefreshToken getToken(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now().plus(2, ChronoUnit.HOURS)) < 0){
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),"Refresh token was expired. Please mage a new signin request");
        }
        return token;
    }



    @Transactional
    public void deleteByUserId(Long userId){
        Optional<User> userOptioanl = userRepository.findById(userId);
        userOptioanl.map(refreshTokenRepository::deleteByUser);
    }



}

