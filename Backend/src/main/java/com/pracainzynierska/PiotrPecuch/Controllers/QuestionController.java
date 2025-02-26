package com.pracainzynierska.PiotrPecuch.Controllers;

import com.pracainzynierska.PiotrPecuch.Security.services.UserDetailsImpl;
import com.pracainzynierska.PiotrPecuch.Services.*;
import com.pracainzynierska.PiotrPecuch.models.*;
import com.pracainzynierska.PiotrPecuch.payload.Requests.AddQuestionRequest;
import com.pracainzynierska.PiotrPecuch.payload.Requests.AnswerDTO;
import com.pracainzynierska.PiotrPecuch.payload.Responses.AnswerResponse;
import com.pracainzynierska.PiotrPecuch.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/question")
@Slf4j
public class QuestionController {

    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final FileService fileService;

    private final OdsParser odsParser;

    private final XlsParser xlsParser;
    private final ExamStudentsInformationsRepository examStudentsInformationsRepository;
    private final UserQuestionAndAnswersRepository userQuestionAndAnswersRepository;
    private final GradeService gradeService;
    private final ExamService examService;

    public QuestionController(ExamRepository examRepository, UserRepository userRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, FileService fileService, OdsParser odsParser, XlsParser xlsParser, ExamStudentsInformationsRepository examStudentsInformationsRepository, UserQuestionAndAnswersRepository userQuestionAndAnswersRepository, GradeService gradeService, ExamService examService) {
        this.examRepository = examRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.fileService = fileService;
        this.odsParser = odsParser;
        this.xlsParser = xlsParser;
        this.examStudentsInformationsRepository = examStudentsInformationsRepository;
        this.userQuestionAndAnswersRepository = userQuestionAndAnswersRepository;
        this.gradeService = gradeService;
        this.examService = examService;
    }


    @GetMapping("/getAllQuestions")
    public ResponseEntity<?> getAllExams(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam Long examId) {
        Optional<Exam> exam = examRepository.findById(examId);
        Optional<User> userData = userRepository.findById(user.getId());
        if ((exam.isPresent() && userData.isPresent()) && (exam.get().getUser() == userData.get())) {
            if (Objects.equals(user.getId(), exam.get().getUser().getId())) {
                return ResponseEntity.ok().body(questionRepository.findAllByExam(exam.get()));
            } else {
                return ResponseEntity.badRequest().body("Not found");
            }
        }
        else {
            return ResponseEntity.badRequest().body("Not found");
        }
    }



    @PostMapping("/add")
    public ResponseEntity<String> addQuestion(@ModelAttribute AddQuestionRequest questionDto) throws IOException {
        log.info("Question points {}", questionDto.getQuestionPoints());
        log.info(String.valueOf(questionDto.getQuestionImage()));

        Optional<Exam> exam = examRepository.findById(questionDto.getExamId());
        if (exam.isEmpty()) {
            return ResponseEntity.badRequest().body("Egzamin nie istnieje.");
        }
        examService.updateModificationDate(exam.get());
        Question newQuestion = new Question();
        newQuestion.setQuestionContent(questionDto.getQuestionContent());
        newQuestion.setPoints(questionDto.getQuestionPoints());
        newQuestion.setExam(exam.get());

        Question savedQuestion = questionRepository.save(newQuestion);

        if (questionDto.getQuestionImage() != null && !questionDto.getQuestionImage().isEmpty()) {
            fileService.storeQuestionFiles(savedQuestion.getQuestionId(),questionDto.getQuestionImage());
            questionRepository.save(savedQuestion);
        }

        for (AnswerDTO answerDto : questionDto.getAnswerList()) {
            log.info(answerDto.getAnswerContent());
            Answer newAnswer = new Answer();
            if(answerDto.getAnswerContent()!=null && !answerDto.getAnswerContent().isEmpty() || !(answerDto.getAttachment() == null)) {
                newAnswer.setAnswerContent(answerDto.getAnswerContent());
                newAnswer.setCorrect(answerDto.getCorrect());
                newAnswer.setQuestion(savedQuestion);
            }
            Answer savedAnswer = answerRepository.save(newAnswer);

            if (answerDto.getAttachment() != null && !answerDto.getAttachment().isEmpty()) {
                MultipartFile answerFile = answerDto.getAttachment();
                try {
                    fileService.storeAnswerFiles(savedAnswer.getAnswerId(), answerFile);
                } catch (IOException e) {
                    return ResponseEntity.status(500).body("Błąd przy zapisie pliku odpowiedzi: " + e.getMessage());
                }
            }
        }

        return ResponseEntity.ok("Pytanie zostało dodane pomyślnie.");
    }



    @GetMapping("/answers/{examName}/{questionId}/{questionContent}")
    public ResponseEntity<?> getAnswers(@AuthenticationPrincipal UserDetailsImpl user,
                                        @PathVariable String examName,@PathVariable Long questionId,
                                        @PathVariable String questionContent) throws IOException {
        log.info(questionContent);
        Optional<User> userData = userRepository.findByEmail(user.getEmail());
        Question question = new Question();
        if(userData.isPresent()) {
            examRepository.findByExamNameAndUser(examName,userData.get());
            question = questionRepository.findByExamAndQuestionContentAndQuestionId(
                    examRepository.findExamByExamNameAndUser(examName, userData.get()),
                    questionContent,questionId).orElseThrow(() -> new RuntimeException("Question not found"));
        }


        byte[] questionFile = null;
        String questionFileType = null;
        QuestionFile questionFileEntity = question.getQuestionFile();
        if (questionFileEntity != null) {
            questionFile = CompressionUtil.decompress(questionFileEntity.getImageData());
            questionFileType = questionFileEntity.getContentType();
        }


        List<Answer> answers = answerRepository.findAnswerByQuestion(question);
        for (Answer answer : answers) {
            if (answer.getAnswerFiles() != null) {
                AnswerFile answerFile = answer.getAnswerFiles();
                answerFile.setData(CompressionUtil.decompress(answer.getAnswerFiles().getData()));
                answer.setAnswerFiles(answerFile);
            }
        }
        AnswerResponse response = new AnswerResponse(answers, questionFile, questionFileType);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/delete/{examName}/{questionId}/{questionContent}")
    public ResponseEntity<?> deleteQuestion(@AuthenticationPrincipal UserDetailsImpl user,
                                            @PathVariable String examName,@PathVariable Long questionId,
                                            @PathVariable String questionContent){

        try{
            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            if(userOptional.isPresent()) {
                Exam exam = examRepository.findExamByExamNameAndUser(examName,userOptional.get());
                Optional<Question> questionToDelete = questionRepository.findByExamAndQuestionContentAndQuestionId(
                        exam,
                        questionContent,questionId);
                if(questionToDelete.isPresent()){
                    questionRepository.delete(questionToDelete.get());
                    examService.updateModificationDate(exam);
                    return ResponseEntity.ok().body("Usunięto pytanie");
                }
                else {
                    return ResponseEntity.badRequest().body("Nie znaleziono pytania");
                }
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
        return ResponseEntity.badRequest().body("Coś poszło nie tak");
    }

    @PostMapping("/answers/import/xls/{examName}")
    public ResponseEntity<?> importAnswersXLS(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable String examName, @RequestParam MultipartFile file){
        try {
            Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
            Exam exam = examRepository.findExamByExamNameAndUser(examName,userOptional.get());
            if(userOptional.isPresent()) {
                xlsParser.parseXls(exam, file);
                examService.updateModificationDate(exam);
                return ResponseEntity.ok("Plik został przetworzony pomyślnie");
            }else{
                return ResponseEntity.badRequest().body("Nie znaleziono użytkownika");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd przetwarzania pliku: " + e.getMessage());
        }
    }

    @PostMapping("/answers/import/ods/{examName}")
    public ResponseEntity<?> importAnswersODS(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable String examName, @RequestParam MultipartFile file) throws Exception {
        log.info("Przetwarzanie w endpoint");
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        Exam exam = examRepository.findExamByExamNameAndUser(examName,userOptional.get());
            if(userOptional.isPresent()) {
                odsParser.parseOds(exam, file);
                examService.updateModificationDate(exam);
                return ResponseEntity.ok("Plik został przetworzony pomyślnie");
            }
            else{
                return ResponseEntity.badRequest().body("Nie znaleziono użytkownika");
            }
    }

    @PatchMapping("/answers/change/points/{questionId}/{ssoLogin}/{ssoPassword}")
    public ResponseEntity<?> changePoints(@AuthenticationPrincipal UserDetailsImpl user, @PathVariable Long questionId, @PathVariable String ssoLogin, @PathVariable String ssoPassword, @RequestParam Double points){
        Optional<ExamStudentsInformations> esi = examStudentsInformationsRepository.findBySsoLoginAndSsoPassword(ssoLogin, ssoPassword);
        log.info("{} {} {} {} {} {}",user.getEmail(), questionId, ssoLogin, ssoPassword, points, esi.get().getId());
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if(esi.isPresent() && userOptional.isPresent() && esi.get().getGeneratedExam().getExam().getUser().equals(userOptional.get())){
            Optional<UserQuestionsAndAnswers> uqaar = userQuestionAndAnswersRepository.findByExamStudentsInformationsIdAndQuestion(esi.get().getId(),questionRepository.findById(questionId).get());
            if(uqaar.isPresent()){
                uqaar.get().setPoints(points);
                userQuestionAndAnswersRepository.save(uqaar.get());
                gradeService.countPoints(esi.get());
                return ResponseEntity.ok().body("Pomyślnie zmieniono punkty");
            }
        }
        return ResponseEntity.badRequest().body("Wystąpił problem podczas zmiany punktów.");
    }
}


