package com.pracainzynierska.PiotrPecuch.Services;


import com.pracainzynierska.PiotrPecuch.models.Answer;
import com.pracainzynierska.PiotrPecuch.models.Exam;
import com.pracainzynierska.PiotrPecuch.models.Question;
import lombok.extern.slf4j.Slf4j;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OdsParser {
    private final QuestionService questionService;

    public OdsParser(QuestionService questionService) {
        this.questionService = questionService;
    }

    public void parseOds(Exam exam, MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.loadDocument(is);
            OdfTable table = ods.getTableList().get(0);

            List<Question> questions = new ArrayList<>();

            for (OdfTableRow row : table.getRowList()) {
                if (row.getCellCount() < 2) {
                    continue;
                }

                String questionContent = row.getCellByIndex(0).getStringValue();
                if (questionContent == null || questionContent.trim().isEmpty()) {
                    continue;
                }

                int points;
                try {
                    points = Integer.parseInt(row.getCellByIndex(1).getStringValue());
                    if(points > 50){
                        points = 50;
                    }
                } catch (NumberFormatException e) {
                    points = 5;
                }

                List<Answer> answers = new ArrayList<>();

                for (int i = 2; i < row.getCellCount(); i += 2) {

                    if (i < row.getCellCount()) {
                        String answerContent = row.getCellByIndex(i).getStringValue();
                        if (answerContent == null || answerContent.trim().isEmpty()) {
                            continue;
                        }

                        boolean isCorrect = false;
                        if (i + 1 < row.getCellCount()) {
                            String correctValue = row.getCellByIndex(i + 1).getStringValue();

                            if ("1".equals(correctValue) || "true".equalsIgnoreCase(correctValue)) {
                                isCorrect = true;
                            }
                        }
                        answers.add(new Answer(answerContent, isCorrect, null));
                    }
                }

                if (!answers.isEmpty()) {
                    Question question = new Question(questionContent, points, answers, null, exam);
                    for (Answer answer : answers) {
                        answer.setQuestion(question);
                    }
                    questions.add(question);
                }
            }

            questionService.saveQuestions(questions);
        } catch (Exception e) {

            throw new Exception("Błąd podczas parsowania pliku ODS: " + e.getMessage(), e);
        }
    }
}


