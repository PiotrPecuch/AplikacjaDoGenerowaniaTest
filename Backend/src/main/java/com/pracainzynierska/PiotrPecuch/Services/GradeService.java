package com.pracainzynierska.PiotrPecuch.Services;


import com.pracainzynierska.PiotrPecuch.models.ExamStudentsInformations;
import com.pracainzynierska.PiotrPecuch.models.Question;
import com.pracainzynierska.PiotrPecuch.models.UserQuestionsAndAnswers;
import com.pracainzynierska.PiotrPecuch.repository.AnswerRepository;
import com.pracainzynierska.PiotrPecuch.repository.ExamStudentsInformationsRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserQuestionAndAnswersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class GradeService {

    private final ExamStudentsInformationsRepository examStudentsInformationsRepository;
    private final UserQuestionAndAnswersRepository userQuestionAndAnswersRepository;

    private final AnswerRepository answerRepository;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public GradeService(ExamStudentsInformationsRepository examStudentsInformationsRepository, UserQuestionAndAnswersRepository userQuestionAndAnswersRepository, AnswerRepository answerRepository) {
        this.examStudentsInformationsRepository = examStudentsInformationsRepository;
        this.userQuestionAndAnswersRepository = userQuestionAndAnswersRepository;
        this.answerRepository = answerRepository;
    }

    public void countPoints(ExamStudentsInformations examStudentsInformations){
        double sumOfPoints = 0.0;
        double totalPoints = 0.0;
        List<UserQuestionsAndAnswers> uqaaList = userQuestionAndAnswersRepository.findUserQuestionsAndAnswersByExamStudentsInformationsId(examStudentsInformations.getId());
        for(UserQuestionsAndAnswers uqaa : uqaaList){
            totalPoints += uqaa.getQuestion().getPoints();
            sumOfPoints += uqaa.getPoints();
        }
        examStudentsInformations.setPointsFromExam((Double.parseDouble((df.format(sumOfPoints)).replace(",", "."))));
        examStudentsInformations.setPercentageOfPoints(Double.parseDouble(df.format(sumOfPoints / totalPoints * 100).replace(",", ".")));
        examStudentsInformationsRepository.save(examStudentsInformations);



    }
    public void setPoints(ExamStudentsInformations examStudentsInformations) {
        List<UserQuestionsAndAnswers> uqaaList = userQuestionAndAnswersRepository.findUserQuestionsAndAnswersByExamStudentsInformationsId(examStudentsInformations.getId());
        double sumOfPoints = 0.0;
        double totalPoints = 0.0;
        for (UserQuestionsAndAnswers uqaa : uqaaList) {
            Question question = uqaa.getQuestion();
            totalPoints += question.getPoints();
            List<Long> correctAnswer = answerRepository.getPositiveAnswers(question.getQuestionId());
            List<Long> userAnswer = stringListToIntegerList(uqaa.getAnswerIdList());
            if(userAnswer == null){
                userAnswer = new ArrayList<>();
            }

            if (!uqaa.getQuestion().getAnswerList().isEmpty() && uqaa.getAnswerText() == null) {
                if ((correctAnswer.isEmpty() && uqaa.getQuestion().getAnswerList().size() >= 1 && uqaa.getAnswerText() == null)) {
                    sumOfPoints += question.getPoints();
                    uqaa.setPoints(Double.valueOf(question.getPoints()));
                    userQuestionAndAnswersRepository.save(uqaa);
                } else if (new HashSet<>(correctAnswer).containsAll(userAnswer)) {
                    List<Long> commonElements = new ArrayList<>(userAnswer);
                    commonElements.retainAll(correctAnswer);
                    double points = question.getPoints() * ((double) commonElements.size() / correctAnswer.size());
                    sumOfPoints += points;
                    uqaa.setPoints(Double.parseDouble((df.format(points).replace(",", "."))));
                    userQuestionAndAnswersRepository.save(uqaa);
                } else {
                    if (Objects.equals(correctAnswer, userAnswer)) {
                        sumOfPoints += question.getPoints();
                        uqaa.setPoints(Double.parseDouble((df.format(question.getPoints()).replace(",", "."))));
                        userQuestionAndAnswersRepository.save(uqaa);
                    }
                }
            } else if (uqaa.getAnswerText() != null || uqaa.getQuestion().getAnswerList() == null) {
                uqaa.setPoints(0.0);
                userQuestionAndAnswersRepository.save(uqaa);
            }
        }
        log.info(String.valueOf(sumOfPoints));
        log.info(String.valueOf(totalPoints));
        examStudentsInformations.setPointsFromExam((Double.parseDouble((df.format(sumOfPoints)).replace(",", "."))));
        examStudentsInformations.setPercentageOfPoints(Double.parseDouble(df.format(sumOfPoints / totalPoints * 100).replace(",", ".")));
        examStudentsInformations.setFinished(true);
        examStudentsInformationsRepository.save(examStudentsInformations);

    }


    public List<Long> stringListToIntegerList(String stringList) {
        try {
            if (stringList != null && !stringList.isEmpty()) {
                String[] elements = stringList.replaceAll("[\\[\\] ]", "").split(",");

                List<Long> integerList = new ArrayList<>();
                for (String element : elements) {
                    integerList.add(Long.parseLong(element));
                }
                return integerList;
            } else {
                return new ArrayList<>();
            }

        } catch (NumberFormatException e) {
            return null;
        }
    }


}
