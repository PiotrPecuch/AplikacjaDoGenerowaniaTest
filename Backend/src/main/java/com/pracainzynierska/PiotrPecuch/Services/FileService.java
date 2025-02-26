package com.pracainzynierska.PiotrPecuch.Services;

import com.pracainzynierska.PiotrPecuch.models.*;
import com.pracainzynierska.PiotrPecuch.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;



@Slf4j
@Service
public class FileService {

    private final QuestionFileRepository questionFileRepository;
    private final AnswerFileRepository answerFileRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public FileService(QuestionFileRepository questionFileRepository, AnswerFileRepository answerFileRepository,
                       AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.questionFileRepository = questionFileRepository;
        this.answerFileRepository = answerFileRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    public void storeQuestionFiles(Long questionId, MultipartFile file) throws IOException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id " + questionId));


            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();
            byte[] data = file.getBytes();
            byte[] compressedData = CompressionUtil.compressToGZIP(data);

            QuestionFile questionFile = new QuestionFile(fileName, fileType, compressedData, question);
        questionFileRepository.save(questionFile);

    }

    public void storeAnswerFiles(Long answerId, MultipartFile file) throws IOException {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found with id " + answerId));

            byte[] data = file.getBytes();
            byte[] compressedData = CompressionUtil.compressToGZIP(data);

            AnswerFile answerFile = new AnswerFile(file.getOriginalFilename(), file.getContentType(), compressedData, answer);
            answerFileRepository.save(answerFile);
        }

    public AnswerFile getDecompressAnswerFile(Long answerId) throws IOException {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found with id " + answerId));

        AnswerFile file = answer.getAnswerFiles();
        if (file == null || file.getData() == null) {
            return null;
        }

        byte[] decompressedData = CompressionUtil.decompress(file.getData());
        file.setData(decompressedData);

        return file;
    }


    public byte[] getDecompressedQuestionFile(Long questionId) throws IOException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id " + questionId));

        QuestionFile questionFile = questionFileRepository.findByQuestion(question);

        return CompressionUtil.decompress(questionFile.getImageData());
    }
}
