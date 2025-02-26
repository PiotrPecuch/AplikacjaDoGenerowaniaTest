package com.pracainzynierska.PiotrPecuch.Services;


import com.pracainzynierska.PiotrPecuch.models.Exam;
import com.pracainzynierska.PiotrPecuch.models.Question;
import com.pracainzynierska.PiotrPecuch.models.User;
import com.pracainzynierska.PiotrPecuch.repository.AnswerRepository;
import com.pracainzynierska.PiotrPecuch.repository.ExamRepository;
import com.pracainzynierska.PiotrPecuch.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.stereotype.Service;


import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public ExamService(ExamRepository examRepository, AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public void updateModificationDate(Exam exam){
        LocalDate currentDate = LocalDate.now();
        Time currentTime = Time.valueOf(LocalTime.now());
        exam.setModificationDate(currentDate);
        exam.setModificationTime(currentTime);
        examRepository.save(exam);
    }

    @Transactional
    public void deleteExamsByUser(User user) {
        List<Exam> exams = examRepository.findAllByUser(user);
        for (Exam exam : exams) {
            List<Question> questions = exam.getQuestionList();

            for (Question question : questions) {
                answerRepository.deleteAllByQuestion(question);
            }

            questionRepository.deleteByExam(exam);
        }

        examRepository.deleteExamsByUser(user);
    }
}
