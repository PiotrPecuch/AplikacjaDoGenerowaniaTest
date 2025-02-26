package com.pracainzynierska.PiotrPecuch.Controllers;


import com.pracainzynierska.PiotrPecuch.Services.GradeService;
import com.pracainzynierska.PiotrPecuch.Services.QuestionService;
import com.pracainzynierska.PiotrPecuch.models.ExamStudentsInformations;
import com.pracainzynierska.PiotrPecuch.models.UserQuestionsAndAnswers;
import com.pracainzynierska.PiotrPecuch.payload.Requests.UserAnswerRequest;
import com.pracainzynierska.PiotrPecuch.payload.Responses.QuestionAndAnswerResponse;
import com.pracainzynierska.PiotrPecuch.repository.ExamStudentsInformationsRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserQuestionAndAnswersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
@RequestMapping("/api/student")
public class StudentExamController {

    private final ExamStudentsInformationsRepository examStudentsInformationsRepository;
    private final UserQuestionAndAnswersRepository userQuestionAndAnswersRepository;

    private final QuestionService questionService;
    private final GradeService gradeService;

    public StudentExamController(ExamStudentsInformationsRepository examStudentsInformationsRepository, UserQuestionAndAnswersRepository questionAndAnswersRepository, QuestionService questionService, GradeService gradeService) {
        this.examStudentsInformationsRepository = examStudentsInformationsRepository;
        this.userQuestionAndAnswersRepository = questionAndAnswersRepository;
        this.questionService = questionService;
        this.gradeService = gradeService;
    }

    @GetMapping("/startExam")
    public ResponseEntity<?> getQuestionByStudent(@RequestParam String ssoLogin, @RequestParam String ssoPassword) {
        log.info("czy istnieje {}", examStudentsInformationsRepository.existsBySsoLoginAndSsoPassword(ssoLogin, ssoPassword));
        Optional<ExamStudentsInformations> examStudentsInformations = examStudentsInformationsRepository.findBySsoLoginAndSsoPassword(ssoLogin, ssoPassword);
        if(examStudentsInformations.isPresent()){
            if(examStudentsInformationsRepository.existsBySsoLoginAndSsoPassword(ssoLogin, ssoPassword)) {
                LocalDateTime startDate = examStudentsInformations.get().getGeneratedExam().getStartDate();
                LocalDateTime endDate = examStudentsInformations.get().getGeneratedExam().getEndDate();
                LocalDateTime now = LocalDateTime.now();
                if(now.isAfter(startDate) && now.isBefore(endDate)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("examDescription", examStudentsInformations.get().getGeneratedExam().getExamDescription());
                    response.put("numberOfQuestion", userQuestionAndAnswersRepository.countByExamStudentsInformations(examStudentsInformations.get()));
                    response.put("time", examStudentsInformations.get().getGeneratedExam().getSoultionTime());

                    return ResponseEntity.ok(response);
                }
                return ResponseEntity.badRequest().body("Zła data dołączania");
            }
            return ResponseEntity.badRequest().body("Złe dane logowania");
        }
        return ResponseEntity.badRequest().body("Złe dane logowania");
    }





    @GetMapping("/getFirstQuestion")
    public ResponseEntity<?> getFirstQuestion(@RequestParam String ssoLogin, @RequestParam String ssoPassword) throws IOException {
        Optional<ExamStudentsInformations> examStudentsInformations = examStudentsInformationsRepository.findBySsoLoginAndSsoPassword(ssoLogin, ssoPassword);
        QuestionAndAnswerResponse questionResponse = questionService.getQuestionAndAnswers(ssoLogin, ssoPassword);

        if (questionResponse != null && !examStudentsInformations.get().getStarted() && examStudentsInformations.isPresent()) {
            examStudentsInformations.get().setStarted(true);
            examStudentsInformationsRepository.save(examStudentsInformations.get());
            return ResponseEntity.ok(questionResponse);
        } else if (examStudentsInformations.get().getStarted()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Egzamin został już rozpoczęty, brak możliwości powrotu");
        }else {
            return ResponseEntity.badRequest().body("Bad request");
        }
    }



    @PostMapping("/getNextQuestion")
    public ResponseEntity<?> getNextQuestion(@RequestParam String ssoLogin,
                                             @RequestParam String ssoPassword,
                                             @RequestBody UserAnswerRequest answerRequest) throws IOException {
        Optional<ExamStudentsInformations> examStudentsInformations = examStudentsInformationsRepository.findBySsoLoginAndSsoPassword(ssoLogin, ssoPassword);
        Optional<UserQuestionsAndAnswers> questionsAndAnswers = (userQuestionAndAnswersRepository.getFirstQuestionBySSOLoginAndSSOPassword(examStudentsInformations.get().getId()));
        if (questionsAndAnswers.isPresent() && examStudentsInformations.isPresent() && !examStudentsInformations.get().getCheated()) {
            UserQuestionsAndAnswers uqaa = userQuestionAndAnswersRepository.getUserQuestionsAndAnswersByExamStudentsInformationsIdAndQuestion(examStudentsInformations.get().getId(), questionsAndAnswers.get().getQuestion());
            if(answerRequest.getTextAnswer().isEmpty()){
                uqaa.setAnswerIdList(answerRequest.getAnswers().toString());
                uqaa.setAnswerText(null);
            } else{
                uqaa.setAnswerText(answerRequest.getTextAnswer());
                uqaa.setAnswerIdList(null);
            }
            userQuestionAndAnswersRepository.save(uqaa);
            QuestionAndAnswerResponse questionResponse = questionService.getQuestionAndAnswers(ssoLogin, ssoPassword);
            return ResponseEntity.ok(questionResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("next question error 1");
    }




    @PostMapping("/endExam")
    public ResponseEntity<?> endExam(@RequestParam String ssoLogin,
                                     @RequestParam String ssoPassword,
                                     @RequestParam Boolean userAlert){
        Optional<ExamStudentsInformations> examStudentsInformations = examStudentsInformationsRepository.findBySsoLoginAndSsoPassword(ssoLogin, ssoPassword);
        if(LocalDateTime.now().minusWeeks(2).isBefore(examStudentsInformations.get().getGeneratedExam().getEndDate())) {
            if(examStudentsInformations.get().getCheated() && examStudentsInformations.isPresent()){
                return ResponseEntity.ok().body(examStudentsInformations);
            }
            if(userAlert){
                examStudentsInformations.get().setCheated(true);
                examStudentsInformationsRepository.save(examStudentsInformations.get());
            }else if(!examStudentsInformations.get().getFinished()) {
                gradeService.setPoints(examStudentsInformations.get());
                return ResponseEntity.ok().body(examStudentsInformations);
            }
            return ResponseEntity.ok().body(examStudentsInformations);
        }
        else {
            return ResponseEntity.ok().body("Czas na sprawdzenie wyników minął");
        }
    }
}
