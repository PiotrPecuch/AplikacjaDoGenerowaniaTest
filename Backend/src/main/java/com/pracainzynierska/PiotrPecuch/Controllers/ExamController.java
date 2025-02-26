package com.pracainzynierska.PiotrPecuch.Controllers;


import com.pracainzynierska.PiotrPecuch.Security.services.UserDetailsImpl;
import com.pracainzynierska.PiotrPecuch.Services.*;
import com.pracainzynierska.PiotrPecuch.models.Exam;
import com.pracainzynierska.PiotrPecuch.models.ExamStudentsInformations;
import com.pracainzynierska.PiotrPecuch.models.GeneratedExam;
import com.pracainzynierska.PiotrPecuch.models.User;
import com.pracainzynierska.PiotrPecuch.payload.Requests.ExamAddRequest;
import com.pracainzynierska.PiotrPecuch.payload.Requests.GenerateExamRequest;
import com.pracainzynierska.PiotrPecuch.payload.Responses.ArchiveExamResponse;
import com.pracainzynierska.PiotrPecuch.payload.Responses.ExamResponse;
import com.pracainzynierska.PiotrPecuch.payload.Responses.QuestionAndAnswerResponse;
import com.pracainzynierska.PiotrPecuch.payload.Responses.UserQuestionAndAnswersResponse;
import com.pracainzynierska.PiotrPecuch.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/exam")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
public class ExamController {

    public final ExamRepository examRepository;
    public final UserRepository userRepository;
    public final QuestionRepository questionRepository;
    public final GeneratedExamService generatedExamService;
    public final SingleSignOnService singleSignOnService;
    public final GeneratedExamRepository generatedExamRepository;
    public final ExamStudentsInformationsRepository examStudentsInformationsRepository;
    public final UserQuestionAndAnswersRepository questionAndAnswersRepository;
    private final UserQuestionAndAnswersRepository userQuestionAndAnswersRepository;
    private final GradeService gradeService;
    private final AnswerRepository answerRepository;
    private final CompressionUtil compressionUtil;
    private final ArchiveService archiveService;
    private final UQAAService uqaaService;
    private final EmailSenderService emailSenderService;
    private final ExamService examService;

    public ExamController(ExamRepository examRepository, UserRepository userRepository, QuestionRepository questionRepository, GeneratedExamService generatedExamService, SingleSignOnService singleSignOnService, GeneratedExamRepository generatedExamRepository, ExamStudentsInformationsRepository examStudentsInformationsRepository, UserQuestionAndAnswersRepository questionAndAnswersRepository, UserQuestionAndAnswersRepository userQuestionAndAnswersRepository, GradeService gradeService, AnswerRepository answerRepository, CompressionUtil compressionUtil, ArchiveService archiveService, UQAAService uqaaService, EmailSenderService emailSenderService, ExamService examService) {
        this.examRepository = examRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.generatedExamService = generatedExamService;
        this.singleSignOnService = singleSignOnService;
        this.generatedExamRepository = generatedExamRepository;
        this.examStudentsInformationsRepository = examStudentsInformationsRepository;
        this.questionAndAnswersRepository = questionAndAnswersRepository;
        this.userQuestionAndAnswersRepository = userQuestionAndAnswersRepository;
        this.gradeService = gradeService;
        this.answerRepository = answerRepository;
        this.compressionUtil = compressionUtil;
        this.archiveService = archiveService;

        this.uqaaService = uqaaService;
        this.emailSenderService = emailSenderService;
        this.examService = examService;
    }

    @GetMapping("/getAllExams")
    public ResponseEntity<?> getAllExams(@AuthenticationPrincipal UserDetailsImpl user) {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        if (userData.isPresent()) {
            List<ExamResponse> examList = new ArrayList<>();
            for (Exam exam : examRepository.findAllByUserOrderByCreationDateDescCreationTimeDesc(userData.get())) {
                examList.add(new ExamResponse(exam.getExamId(), exam.getExamName(), exam.getCreationTime(), exam.getCreationDate(),exam.getModificationTime(), exam.getModificationDate(), Math.toIntExact(questionRepository.countByExam(exam))));
            }
            return new ResponseEntity<>(examList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/update/name")
    public ResponseEntity<?> updateExamName(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam Long examId, @RequestParam String name){
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        Optional<Exam> examOptional = examRepository.findById(examId);
        if(!examRepository.existsExamByExamNameAndUser(name, userData.get())) {
            if (userData.isPresent() && examOptional.isPresent() && examOptional.get().getUser().equals(userData.get())) {
                Exam exam = examOptional.get();
                exam.setExamName(name);
                examRepository.save(exam);
                examService.updateModificationDate(exam);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Zaktualizowano pomyślnie");
            }
        }
        else {
            return ResponseEntity.badRequest().body("Egzamin z podaną nazwą istnieje");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Błąd podczas aktualizacji danych");
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteExam(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam Long examId) {
        try {
            Optional<Exam> examOptional = examRepository.findById(examId);

            if (!examOptional.isPresent()) {
                return new ResponseEntity<>("Egzamin nie istnieje", HttpStatus.BAD_REQUEST);
            }
            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if (!userOptional.isPresent()) {
                return new ResponseEntity<>("Użytkownik nie istnieje", HttpStatus.BAD_REQUEST);
            }

            if (examOptional.get().getUser().equals(userOptional.get())) {
                Exam exam = examOptional.get();
                examRepository.delete(exam);
                return new ResponseEntity<>("Usunięto pomyślnie", HttpStatus.ACCEPTED);
            }

            return new ResponseEntity<>("Brak uprawnień do usunięcia egzaminu", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Wystąpił błąd", HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("/add")
    public ResponseEntity<?> addExam(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody ExamAddRequest examAddRequest) {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());

        if (!userData.isPresent()) {
            return new ResponseEntity<>("Użytkownik nie istnieje", HttpStatus.BAD_REQUEST);
        }

        Optional<Exam> examOptional = examRepository.findByExamNameAndUser(examAddRequest.getExamName(), userData.get());
        if (examOptional.isPresent()) {
            return new ResponseEntity<>("Egzamin z podaną nazwą istnieje", HttpStatus.CONFLICT);
        }

        Exam savedExam = new Exam();
        savedExam.setExamName(examAddRequest.getExamName());
        savedExam.setUser(userData.get());
        savedExam.setCreationDate(LocalDate.now());
        savedExam.setCreationTime(Time.valueOf(LocalTime.now()));
        examRepository.save(savedExam);

        return new ResponseEntity<>("Dodano egzamin", HttpStatus.CREATED);
    }



    @PostMapping("generate")
    public ResponseEntity<?> generateExam(@AuthenticationPrincipal UserDetailsImpl user, @ModelAttribute GenerateExamRequest generateExamRequest) {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        if (userData.isPresent()) {
                Exam exam = examRepository.findExamByExamNameAndUser(generateExamRequest.getExamName(), userData.get());
                GeneratedExam generatedExam = generatedExamService.generateExam(exam, generateExamRequest.getDescription(), generateExamRequest.getStartDate(), generateExamRequest.getEndDate(), generateExamRequest.getSolutionTime());
                return singleSignOnService.generateStudentData(generateExamRequest.getStudentList(), exam, generatedExam, generateExamRequest.getNumberOfQuestions());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono egzaminu");
    }

    @PostMapping("generatePDF")
    public ResponseEntity<?> generateExamPDF(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam Long examId, @RequestParam Integer numberOfQuestions) throws IOException {
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        if (userData.isPresent()) {
            List<QuestionAndAnswerResponse> questionList = generatedExamService.preparePDFExam(examRepository.findById(examId).get(), numberOfQuestions);

            return ResponseEntity.status(HttpStatus.CREATED).body(questionList);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono egzaminu");
    }

    @GetMapping("/getStartedExams")
    public List<GeneratedExam> getStartedExams(@AuthenticationPrincipal UserDetailsImpl user) {
        return generatedExamRepository.getStartedExamsByUserId(user.getId());
    }

    @GetMapping("/getFinishedExams")
    public List<GeneratedExam> getFinishedExam(@AuthenticationPrincipal UserDetailsImpl user) {
        return generatedExamRepository.getFinishedExamByUserId(user.getId());
    }

    @GetMapping("getStartedExamsDetails")
    public ResponseEntity<?> getStartedExamsDetails(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam Long generatedExamId) {
        Optional<GeneratedExam> generatedExam = generatedExamRepository.findById(generatedExamId);
        if (generatedExam.isPresent()) {
            if (generatedExamService.isGeneratedExamOwner(user.getEmail(), generatedExamId)) {
                return ResponseEntity.status(HttpStatus.OK).body(examStudentsInformationsRepository.findByGeneratedExam(generatedExam.get()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Brak uprawnień");
        } else {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/archive")
    public ResponseEntity<?> archiveExam(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam Long generatedExamId) throws IOException {
        Optional<GeneratedExam> generatedExam = generatedExamRepository.findById(generatedExamId);
        if (generatedExam.isPresent()) {
            if (generatedExamService.isGeneratedExamOwner(user.getEmail(), generatedExamId)) {
                List<ArchiveExamResponse> archiveExamResponses = archiveService.archiveExam(generatedExam.get());
                return ResponseEntity.status(HttpStatus.OK).body(archiveExamResponses);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Brak uprawnień");
        } else {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("getAllUserAnswers")
    public ResponseEntity<?> getAllUserAnswers(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam Long generatedExamId,
                                               @RequestParam String ssoLogin, @RequestParam String ssoPassword) throws IOException {

        Optional<GeneratedExam> generatedExam = generatedExamRepository.findById(generatedExamId);
        if (generatedExam.isPresent() && generatedExamService.isGeneratedExamOwner(user.getEmail(), generatedExamId)) {
            List<UserQuestionAndAnswersResponse> uqaar = uqaaService.prepareUQAAList(ssoLogin, ssoPassword, generatedExam.get());
            return ResponseEntity.status(HttpStatus.OK).body(uqaar);
        }else {
            return new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("sendGradeNotification")
    public ResponseEntity<?> sendGradeNotification(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam Long generatedExamId) {
        List<ExamStudentsInformations> esiList = examStudentsInformationsRepository.findByGeneratedExam(generatedExamRepository.findById(generatedExamId).get());
        emailSenderService.sendNotification(user.getEmail(),esiList);

        return ResponseEntity.status(HttpStatus.OK).body("Wysłano powiadomienia mailowe");
    }
}
