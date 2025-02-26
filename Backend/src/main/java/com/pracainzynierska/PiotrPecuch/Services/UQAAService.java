package com.pracainzynierska.PiotrPecuch.Services;


import com.pracainzynierska.PiotrPecuch.models.*;
import com.pracainzynierska.PiotrPecuch.payload.Responses.UserQuestionAndAnswersResponse;
import com.pracainzynierska.PiotrPecuch.repository.AnswerRepository;
import com.pracainzynierska.PiotrPecuch.repository.ExamStudentsInformationsRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserQuestionAndAnswersRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UQAAService {

    private final ExamStudentsInformationsRepository examStudentsInformationsRepository;
    private final UserQuestionAndAnswersRepository userQuestionAndAnswersRepository;
    private final GradeService gradeService;
    private final QuestionService questionService;
    private final AnswerRepository answerRepository;
    private final CompressionUtil compressionUtil;

    public UQAAService(ExamStudentsInformationsRepository examStudentsInformationsRepository, UserQuestionAndAnswersRepository userQuestionAndAnswersRepository, GradeService gradeService, QuestionService questionService, AnswerRepository answerRepository, CompressionUtil compressionUtil) {
        this.examStudentsInformationsRepository = examStudentsInformationsRepository;

        this.userQuestionAndAnswersRepository = userQuestionAndAnswersRepository;
        this.gradeService = gradeService;
        this.questionService = questionService;
        this.answerRepository = answerRepository;
        this.compressionUtil = compressionUtil;
    }


    public List<UserQuestionAndAnswersResponse> prepareUQAAList(String ssoLogin, String ssoPassword, GeneratedExam generatedExam) throws IOException {
        List<UserQuestionAndAnswersResponse> uqaar = new ArrayList<>();
        ExamStudentsInformations esInfo = examStudentsInformationsRepository.findBySsoLoginAndSsoPasswordAndGeneratedExam(ssoLogin, ssoPassword, generatedExam);

        List<UserQuestionsAndAnswers> userQuestionsAndAnswersList = userQuestionAndAnswersRepository.findUserQuestionsAndAnswersByExamStudentsInformationsId(esInfo.getId());
        for (UserQuestionsAndAnswers userQuestionsAndAnswers : userQuestionsAndAnswersList) {
            UserQuestionAndAnswersResponse responseCreator = new UserQuestionAndAnswersResponse();
            Question question = userQuestionsAndAnswers.getQuestion();
            if (question.getQuestionFile() != null) {
                QuestionFile questionFile = question.getQuestionFile();
                questionFile.setImageData(CompressionUtil.decompress(questionFile.getImageData()));
                question.setQuestionFile(questionFile);
                userQuestionsAndAnswers.setQuestion(question);

            }
            if (userQuestionsAndAnswers.getAnswerIdList() == null) {

                List<Answer> answerList = new ArrayList<>();
                Answer answer = new Answer();
                answer.setAnswerContent(userQuestionsAndAnswers.getAnswerText());
                answer.setQuestion(userQuestionsAndAnswers.getQuestion());
                answerList.add(answer);
                responseCreator.setPoints(userQuestionsAndAnswers.getPoints());
                responseCreator.setCorrectAnswers(null);
                responseCreator.setUserAnswers(answerList);



            }

            List<Long> answerIngerList = gradeService.stringListToIntegerList(userQuestionsAndAnswers.getAnswerIdList());
            List<Long> correctAnswer = answerRepository.getPositiveAnswers(userQuestionsAndAnswers.getQuestion().getQuestionId());

// Poprawne odpowiedzi
            if (correctAnswer != null) {
                List<Answer> answerList = answerRepository.findAllById(correctAnswer);
                for (Answer answer : answerList) {
                    if (answer.getAnswerFiles() != null) {
                        AnswerFile answerFile = answer.getAnswerFiles();
                        System.out.println(answerFile.getFilename());
                        byte[] decompressedData = CompressionUtil.decompress(answerFile.getData()); // Zdekodowane dane
                        answerFile.setData(decompressedData); // Zapisanie zdekodowanych danych
                        answer.setAnswerFiles(answerFile); // Uaktualnienie answerFile w odpowiedzi
                    }
                }
                responseCreator.setCorrectAnswers(answerList);
            } else {
                responseCreator.setUserAnswers(null);
            }


            //odpowiedzi uzytkownika
            if (answerIngerList != null) {
                List<Answer> userAnswerList = answerRepository.findAllById(answerIngerList);
                for (Answer userAnswer : userAnswerList) {
                    AnswerFile answerFile = userAnswer.getAnswerFiles();  // Przypisanie do zmiennej
                    if (answerFile != null) {  // Sprawdzanie null raz
                        System.out.println(answerFile.getFilename());
                        answerFile.setData(CompressionUtil.decompress(answerFile.getData()));
                        userAnswer.setAnswerFiles(answerFile);  // Aktualizacja answerFile w userAnswer
                    }
                }
                responseCreator.setUserAnswers(userAnswerList);
                responseCreator.setUserTextAnswer(
                        userQuestionAndAnswersRepository.findByQuestionAndExamStudentsInformations(question, esInfo).getAnswerText()
                );
            } else {
                responseCreator.setUserAnswers(null);
            }


            responseCreator.setId(userQuestionsAndAnswers.getUserQuestionsAndAnswersId());
            responseCreator.setQuestion(userQuestionsAndAnswers.getQuestion());
            responseCreator.setPercentageOfPoints(userQuestionsAndAnswers.getExamStudentsInformations().getPercentageOfPoints());
            responseCreator.setPoints(userQuestionsAndAnswers.getPoints());
            uqaar.add(responseCreator);
        }
        return uqaar;
    }
}
