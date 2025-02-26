package com.pracainzynierska.PiotrPecuch.Services;

import com.pracainzynierska.PiotrPecuch.models.*;
import com.pracainzynierska.PiotrPecuch.repository.ExamStudentsInformationsRepository;
import com.pracainzynierska.PiotrPecuch.repository.GeneratedExamRepository;
import com.pracainzynierska.PiotrPecuch.repository.QuestionRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserQuestionAndAnswersRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@Service
public class SingleSignOnService {

    private final QuestionRepository questionRepository;
    private final ExamStudentsInformationsRepository examStudentsInformationsRepository;
    private final UserQuestionAndAnswersRepository userQuestionAndAnswersRepository;
    private final GeneratedExamRepository generatedExamRepository;

    @Value("${sso.password.length}")
    Integer passwordLength;

    Random random = new Random();

    public SingleSignOnService(QuestionRepository questionRepository, ExamStudentsInformationsRepository examStudentsInformationsRepository, UserQuestionAndAnswersRepository userQuestionAndAnswersRepository, GeneratedExamRepository generatedExamRepository) {
        this.questionRepository = questionRepository;
        this.examStudentsInformationsRepository = examStudentsInformationsRepository;
        this.userQuestionAndAnswersRepository = userQuestionAndAnswersRepository;
        this.generatedExamRepository = generatedExamRepository;
    }


    public ResponseEntity<?> generateStudentData(MultipartFile file, Exam exam, GeneratedExam generatedExam, Integer nummberOfQuestions) {
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();

            assert fileName != null;
            if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                Workbook workbook = fileName.endsWith(".xlsx") ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);

                int totalRows = sheet.getPhysicalNumberOfRows();

                if (totalRows == 0) {
                    throw new IllegalArgumentException("Arkusz jest pusty.");
                }

                for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);

                    if (row == null || row.getCell(0) == null || row.getCell(1) == null || row.getCell(2) == null) {
                        continue;
                    }

                    try {
                        String firstName = row.getCell(0).getStringCellValue();
                        String lastName = row.getCell(1).getStringCellValue();
                        Long number = Math.round(row.getCell(2).getNumericCellValue());

                        String login = generateLogin(generatedExam, firstName, lastName, number, exam.getExamName());
                        String password = generatePassword();

                        ExamStudentsInformations studentsInformations = examStudentsInformationsRepository.save(
                                new ExamStudentsInformations(0, login, password, number, firstName, lastName, false, false, false, 0.0, 0.0, generatedExam, null)
                        );
                        generateQuestions(studentsInformations, nummberOfQuestions, exam);
                    } catch (Exception e) {
                        System.err.println("Błąd podczas przetwarzania wiersza: " + e.getMessage());
                    }
                }
                return ResponseEntity.ok().body("Utworzono egzamin");
            } else if (fileName.endsWith(".ods")) {
                OdfSpreadsheetDocument document = OdfSpreadsheetDocument.loadDocument(inputStream);
                OdfTable table = document.getTableList().get(0);
                for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
                    String firstName = table.getCellByPosition(0, rowIndex).getStringValue();
                    String lastName = table.getCellByPosition(1, rowIndex).getStringValue();
                    Long number = Math.round(table.getCellByPosition(2, rowIndex).getDoubleValue());
                    String login = generateLogin(generatedExam, firstName, lastName, number, exam.getExamName());
                    String password = generatePassword();
                    ExamStudentsInformations studentsInformations = examStudentsInformationsRepository.save(new ExamStudentsInformations(0, login, password, number, firstName, lastName, false, false, false, 0.0, 0.0, generatedExam, null));
                    generateQuestions(studentsInformations, nummberOfQuestions, exam);
                }
                return ResponseEntity.ok().body("Utworzono egzamin");
            } else {
                generatedExamRepository.delete(generatedExam);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (IOException e) {
            generatedExamRepository.delete(generatedExam);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas ladowania pliku");
        } catch (Exception e) {
            generatedExamRepository.delete(generatedExam);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas ladowania pliku");
        }
    }

    private String generateLogin(GeneratedExam generatedExam, String firstName, String lastName, Long number, String examName) {
        String firstPart = firstName.substring(0, 1).toLowerCase();
        Integer randomNameStartNumber = random.nextInt(0, lastName.length() - 1);
        Integer randomExamStartNumber = random.nextInt(0, examName.length() - 1);
        String lastPart = lastName.length() >= 2 ? lastName.substring(randomNameStartNumber, randomNameStartNumber + 1).toLowerCase() : lastName.toLowerCase();
        String examPart = !examName.isEmpty() ? examName.substring(randomExamStartNumber, randomExamStartNumber + 1).toLowerCase() : "";
        System.out.println("Wartosc indexu " + number % 1000);
        String baseLogin = String.format("%s%s%s%s%s", generatedExam.getId(), firstPart, lastPart, number % 1000, examPart);
        List<Character> characters = new ArrayList<>();
        for (char c : baseLogin.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);

        StringBuilder shuffledLogin = new StringBuilder();
        for (Character character : characters) {
            shuffledLogin.append(character);
        }
        return shuffledLogin.toString();
    }

    private String generatePassword() {
        String lettersAndDigits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String specialCharacters = "!@#$%^&*()";
        String characters = lettersAndDigits + specialCharacters;

        StringBuilder password = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        char lastChar = password.charAt(password.length() - 1);
        if (specialCharacters.indexOf(lastChar) != -1) {
            password.setCharAt(password.length() - 1, lettersAndDigits.charAt(random.nextInt(lettersAndDigits.length())));
        }

        return password.toString();
    }


    public void generateQuestions(ExamStudentsInformations examStudentsInformations, Integer numberOfQuestions, Exam exam) {
        List<Question> questionList = questionRepository.findAllByExam(exam);
        if (!questionList.isEmpty()) {
            Collections.shuffle(questionList);
            List<Question> randomQuestionList = questionList.subList(0, numberOfQuestions);
            for (Question question1 : randomQuestionList) {
                userQuestionAndAnswersRepository.save(new UserQuestionsAndAnswers(0, question1, null, null, 0.0, examStudentsInformations));
            }
        }
    }


}
