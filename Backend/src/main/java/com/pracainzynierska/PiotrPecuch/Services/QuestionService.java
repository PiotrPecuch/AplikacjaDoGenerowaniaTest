package com.pracainzynierska.PiotrPecuch.Services;

import com.pracainzynierska.PiotrPecuch.models.Answer;
import com.pracainzynierska.PiotrPecuch.models.ExamStudentsInformations;
import com.pracainzynierska.PiotrPecuch.models.Question;
import com.pracainzynierska.PiotrPecuch.models.UserQuestionsAndAnswers;
import com.pracainzynierska.PiotrPecuch.payload.Responses.QuestionAndAnswerResponse;
import com.pracainzynierska.PiotrPecuch.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamStudentsInformationsRepository examStudentsInformationsRepository;
    private final UserQuestionAndAnswersRepository userQuestionAndAnswersRepository;
    private final AnswerRepository answerRepository;
    private final FileService fileService;

    public QuestionService(QuestionRepository questionRepository, ExamStudentsInformationsRepository examStudentsInformationsRepository, UserQuestionAndAnswersRepository userQuestionAndAnswersRepository,  AnswerRepository answerRepository, FileService fileService) {
        this.questionRepository = questionRepository;
        this.examStudentsInformationsRepository = examStudentsInformationsRepository;
        this.userQuestionAndAnswersRepository = userQuestionAndAnswersRepository;
        this.answerRepository = answerRepository;
        this.fileService = fileService;
    }

    @Transactional
    public void saveQuestions(List<Question> questions) {
        for (Question question : questions) {
            for (Answer answer : question.getAnswerList()) {
                answer.setQuestion(question);
            }
            questionRepository.save(question);
        }
    }

    public QuestionAndAnswerResponse getQuestionAndAnswers(String ssoLogin, String ssoPassword) throws IOException {
        QuestionAndAnswerResponse response = new QuestionAndAnswerResponse();
        try {
            Optional<ExamStudentsInformations> examStudentsInformations = examStudentsInformationsRepository.findBySsoLoginAndSsoPassword(ssoLogin, ssoPassword);

            Optional<UserQuestionsAndAnswers> questionsAndAnswers = userQuestionAndAnswersRepository.getFirstQuestionBySSOLoginAndSSOPassword(examStudentsInformations.get().getId());

            if (questionsAndAnswers.isPresent()) {
                List<Answer> answerList = answerRepository.findAnswerByQuestion(questionsAndAnswers.get().getQuestion());
                if (questionsAndAnswers.get().getQuestion().getQuestionFile() != null) {
                    questionsAndAnswers.get().getQuestion().getQuestionFile().setImageData(fileService.getDecompressedQuestionFile(questionsAndAnswers.get().getQuestion().getQuestionId()));
                }
                for (Answer answer : answerList) {
                    answer.setCorrect(null);
                    if (answer.getAnswerFiles() != null) {
                        answer.setAnswerFiles(fileService.getDecompressAnswerFile(answer.getAnswerId()));
                    }
                }
                response.setQuestion(questionsAndAnswers.get().getQuestion());
                response.setAnswer(answerList);

                return response;
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

}