package com.pracainzynierska.PiotrPecuch.Controllers;

import com.pracainzynierska.PiotrPecuch.Exception.TokenRefreshException;
import com.pracainzynierska.PiotrPecuch.Security.jwt.JwtUtils;
import com.pracainzynierska.PiotrPecuch.Security.services.RefreshTokenService;
import com.pracainzynierska.PiotrPecuch.Security.services.UserDetailsImpl;
import com.pracainzynierska.PiotrPecuch.Services.UserPasswordService;
import com.pracainzynierska.PiotrPecuch.models.ERole;
import com.pracainzynierska.PiotrPecuch.models.RefreshToken;
import com.pracainzynierska.PiotrPecuch.models.Role;
import com.pracainzynierska.PiotrPecuch.models.User;
import com.pracainzynierska.PiotrPecuch.payload.Requests.LoginRequest;
import com.pracainzynierska.PiotrPecuch.payload.Requests.SignupRequest;
import com.pracainzynierska.PiotrPecuch.payload.Requests.TokenRefreshRequest;
import com.pracainzynierska.PiotrPecuch.payload.Responses.JwtResponse;
import com.pracainzynierska.PiotrPecuch.payload.Responses.MessageResponse;
import com.pracainzynierska.PiotrPecuch.payload.Responses.TokenRefreshResponse;
import com.pracainzynierska.PiotrPecuch.repository.QuestionRepository;
import com.pracainzynierska.PiotrPecuch.repository.RefreshTokenRepository;
import com.pracainzynierska.PiotrPecuch.repository.RoleRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final RefreshTokenService refreshTokenService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final QuestionRepository questionRepository;
    private final UserPasswordService userPasswordService;

    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository, QuestionRepository questionRepository, UserPasswordService userPasswordService, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.questionRepository = questionRepository;
        this.userPasswordService = userPasswordService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        if (!userRepository.existsByEmail(loginRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Błędne dane logowania");
        } else {
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
            if (userOptional.isPresent()) {
                if (refreshTokenRepository.findByUser(userOptional.get()).isPresent()) {
                    refreshTokenService.deleteByUserId(userOptional.get().getId());
                }
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                String jwt = jwtUtils.generateJwtToken(userDetails.getEmail());

                List<String> roles = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

                return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                        userDetails.getEmail(), roles));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::getToken)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateJwtToken(user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Nie ma tokenu w bazie danych!!"));

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Podany adres E-mail jest zajęty!"));
        }

        User user = new User(signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_USER));

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Zarejestrowana pomyślnie!"));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> processResetPassword(@RequestParam String token, @RequestParam String password) {
        if (userPasswordService.getByResetPasswordToken(token) != null) {
            User user = userPasswordService.getByResetPasswordToken(token);
            userPasswordService.updatePassword(user, password);
            return ResponseEntity.ok().body("Hasło zmieniono poprawnie");
        }else {
            return ResponseEntity.badRequest().body("Podany token, jest nieaktualny ");
        }
    }
}