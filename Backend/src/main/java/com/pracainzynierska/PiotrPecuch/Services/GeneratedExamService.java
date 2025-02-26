package com.pracainzynierska.PiotrPecuch.Services;


import com.pracainzynierska.PiotrPecuch.models.*;
import com.pracainzynierska.PiotrPecuch.payload.Responses.QuestionAndAnswerResponse;
import com.pracainzynierska.PiotrPecuch.repository.GeneratedExamRepository;
import com.pracainzynierska.PiotrPecuch.repository.QuestionRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class GeneratedExamService {


    private final GeneratedExamRepository generatedExamRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final CompressionUtil compressionUtil;

    public GeneratedExamService(GeneratedExamRepository generatedExamRepository, UserRepository userRepository, QuestionRepository questionRepository, CompressionUtil compressionUtil) {
        this.generatedExamRepository = generatedExamRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.compressionUtil = compressionUtil;
    }

    public GeneratedExam generateExam(Exam exam, String description, LocalDateTime startDate, LocalDateTime endDate, Integer solutionTime) {

        GeneratedExam generatedExam = new GeneratedExam();
        generatedExam.setExam(exam);
        generatedExam.setStartDate(startDate);
        generatedExam.setEndDate(endDate);
        generatedExam.setSoultionTime(solutionTime);
        generatedExam.setExamDescription(description);
        generatedExamRepository.save(generatedExam);


        return generatedExam;
    }


    public boolean isGeneratedExamOwner(String userEmail, Long generatedExamId) {
        Exam exam = new Exam();
        User user = new User();
        if (userRepository.findByEmail(userEmail).isPresent()) {
            user = userRepository.findByEmail(userEmail).get();
        }
        if (generatedExamRepository.findById(generatedExamId).isPresent()) {
            exam = generatedExamRepository.findById(generatedExamId).get().getExam();
        }
        return exam.getUser() == user;
    }


    public List<QuestionAndAnswerResponse> preparePDFExam(Exam exam, Integer numberOfQuestions) throws IOException {
        List<QuestionAndAnswerResponse> qaar = new ArrayList<>();

        List<Question> questionList = questionRepository.findAllByExam(exam);
        if (!questionList.isEmpty()) {
            Collections.shuffle(questionList);
            int indexNumer = 0;

            while (qaar.size() < numberOfQuestions && indexNumer < questionList.size()) {
                Question currentQuestion = questionList.get(indexNumer);

                if (currentQuestion.getQuestionFile() != null) {
                    indexNumer++;
                    continue;
                }


                QuestionAndAnswerResponse questionAndAnswerResponse = new QuestionAndAnswerResponse();
                questionAndAnswerResponse.setQuestion(currentQuestion);

                List<Answer> validAnswers = currentQuestion.getAnswerList().stream()
                        .filter(answer -> answer.getAnswerFiles() == null)
                        .collect(Collectors.toList());

                if (validAnswers.isEmpty() && currentQuestion.getAnswerList().isEmpty()) {
                    questionAndAnswerResponse.setAnswer(Collections.emptyList());
                } else {
                    questionAndAnswerResponse.setAnswer(validAnswers);
                }

                qaar.add(questionAndAnswerResponse);

                indexNumer++;
            }
        }
        return qaar;
    }


}
