package com.pracainzynierska.PiotrPecuch.Services;

import com.pracainzynierska.PiotrPecuch.models.Answer;
import com.pracainzynierska.PiotrPecuch.models.Exam;
import com.pracainzynierska.PiotrPecuch.models.Question;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class XlsParser {

    private final QuestionService questionService;

    public XlsParser(QuestionService questionService) {
        this.questionService = questionService;
    }

    public void parseXls(Exam exam, MultipartFile file) throws Exception {
        log.info("1");

        List<Question> questions = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);


            Sheet sheet = workbook.getSheetAt(0);
            log.info("stop 2");

            for (Row row : sheet) {
                if (row.getPhysicalNumberOfCells() < 2) {
                    continue;
                }

                Cell questionCell = row.getCell(0);
                String questionContent = questionCell != null ? questionCell.getStringCellValue() : null;
                if (questionContent == null || questionContent.trim().isEmpty()) {
                    continue;
                }
                log.info("3");

                int points;
                Cell pointsCell = row.getCell(1);
                try {
                    points = (int) pointsCell.getNumericCellValue();
                    if (points > 50) {
                        points = 50;
                    }
                } catch (Exception e) {
                    points = 5;
                }

                List<Answer> answers = new ArrayList<>();


                for (int i = 2; i < row.getPhysicalNumberOfCells(); i += 2) {

                    if (i < row.getPhysicalNumberOfCells()) {
                        Cell answerCell = row.getCell(i);

                        String answerContent = getString(answerCell);

                        if (answerContent == null || answerContent.trim().isEmpty()) {
                            continue;
                        }

                        boolean isCorrect = false;

                        if (i + 1 < row.getPhysicalNumberOfCells()) {
                            Cell correctCell = row.getCell(i + 1);
                            if (correctCell != null) {
                                String correctValue = null;
                                if (correctCell.getCellType() == CellType.STRING) {
                                    correctValue = correctCell.getStringCellValue();
                                } else if (correctCell.getCellType() == CellType.NUMERIC) {
                                    correctValue = String.valueOf(Math.round(correctCell.getNumericCellValue()));
                                }
                                System.out.println(correctValue);

                                if ("1".equals(correctValue) || "true".equalsIgnoreCase(correctValue)) {
                                    isCorrect = true;
                                }
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
            throw new Exception("Błąd podczas parsowania pliku XLS/XLSX: " + e.getMessage(), e);
        }

    }

    private static String getString(Cell answerCell) {
        String answerContent = null;
        if (answerCell != null) {
            if (answerCell.getCellType() == CellType.STRING) {
                answerContent = answerCell.getStringCellValue();
            } else if (answerCell.getCellType() == CellType.NUMERIC) {
                answerContent = String.valueOf(answerCell.getNumericCellValue());
            }
        }
        return answerContent;
    }
}
