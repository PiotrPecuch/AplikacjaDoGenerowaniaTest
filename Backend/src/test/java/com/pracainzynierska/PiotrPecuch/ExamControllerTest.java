package com.pracainzynierska.PiotrPecuch;


import com.pracainzynierska.PiotrPecuch.Controllers.ExamController;
import com.pracainzynierska.PiotrPecuch.Security.services.UserDetailsImpl;
import com.pracainzynierska.PiotrPecuch.models.ERole;
import com.pracainzynierska.PiotrPecuch.models.Exam;
import com.pracainzynierska.PiotrPecuch.models.Role;
import com.pracainzynierska.PiotrPecuch.models.User;
import com.pracainzynierska.PiotrPecuch.payload.Requests.ExamAddRequest;
import com.pracainzynierska.PiotrPecuch.repository.ExamRepository;
import com.pracainzynierska.PiotrPecuch.repository.RoleRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ExamControllerTest {


    @InjectMocks
    private ExamController examController;

    @Mock
    private ExamRepository examRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@example.com", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testAddExam_Success() {
        String userEmail = "test@example.com";
        Long userId = 1L;
        ExamAddRequest examAddRequest = new ExamAddRequest();
        examAddRequest.setExamName("Nowy Egzamin");
        User user = new User(userId, userEmail, "password");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        user.setRoles(roles);
        UserDetailsImpl userDetails = new UserDetailsImpl(userId, userEmail, "password", roles);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(examRepository.findByExamNameAndUser(examAddRequest.getExamName(), user)).thenReturn(Optional.empty());
        ResponseEntity<?> response = examController.addExam(userDetails, examAddRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Dodano egzamin", response.getBody());
        verify(examRepository, times(1)).save(any(Exam.class));
    }

    @Test
    public void testAddExam_Conflict() {
        String userEmail = "test@example.com";
        Long userId = 1L;
        ExamAddRequest examAddRequest = new ExamAddRequest();
        examAddRequest.setExamName("Istniejący Egzamin");

        User user = new User(userId, userEmail, "password");
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(examRepository.findByExamNameAndUser(examAddRequest.getExamName(), user)).thenReturn(Optional.of(new Exam()));

        ResponseEntity<?> response = examController.addExam(new UserDetailsImpl(userId, userEmail, "password", new HashSet<>()), examAddRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Egzamin z podaną nazwą istnieje", response.getBody());
    }

    @Test
    public void testAddExam_UserNotFound() {

        String userEmail = "test@example.com";
        ExamAddRequest examAddRequest = new ExamAddRequest();
        examAddRequest.setExamName("New Exam");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));


        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        ResponseEntity<?> response = examController.addExam(new UserDetailsImpl(1L, userEmail, "password", roles), examAddRequest);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Użytkownik nie istnieje", response.getBody());
        verify(examRepository, times(0)).save(any(Exam.class));
    }

    @Test
    public void testDeleteExam_Success() {
        String userEmail = "test@example.com";
        Long examId = 1L;

        User user = new User(1L, userEmail, "password");
        Exam exam = new Exam();
        exam.setExamId(examId);
        exam.setUser(user);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        ResponseEntity<?> response = examController.deleteExam(new UserDetailsImpl(1L, userEmail, "password", new HashSet<>()), examId);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Usunięto pomyślnie", response.getBody());
        verify(examRepository, times(1)).delete(exam);
    }

    @Test
    public void testDeleteExam_ExamNotFound() {
        Long examId = 1L;
        String userEmail = "test@example.com";

        when(examRepository.findById(examId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = examController.deleteExam(new UserDetailsImpl(1L, userEmail, "password", new HashSet<>()), examId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Egzamin nie istnieje", response.getBody());
    }

    @Test
    public void testDeleteExam_UserNotFound() {
        //Given
        String userEmail = "test@example.com";
        Long examId = 1L;
        Exam exam = new Exam();
        exam.setExamId(examId);

        //When
        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        //Then
        ResponseEntity<?> response = examController.deleteExam(new UserDetailsImpl(1L, userEmail, "password", new HashSet<>()), examId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Użytkownik nie istnieje", response.getBody());
    }

    @Test
    public void testDeleteExam_NoPermission() {
        String userEmail = "test@example.com";
        String otherUserEmail = "other@example.com";
        Long examId = 1L;

        User owner = new User(2L, otherUserEmail, "password");
        Exam exam = new Exam();
        exam.setExamId(examId);
        exam.setUser(owner);

        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(new User(1L, userEmail, "password")));

        ResponseEntity<?> response = examController.deleteExam(new UserDetailsImpl(1L, userEmail, "password", new HashSet<>()), examId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Brak uprawnień do usunięcia egzaminu", response.getBody());
    }
}
