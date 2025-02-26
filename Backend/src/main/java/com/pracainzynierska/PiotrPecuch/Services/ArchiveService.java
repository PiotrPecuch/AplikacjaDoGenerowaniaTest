package com.pracainzynierska.PiotrPecuch.Services;


import com.pracainzynierska.PiotrPecuch.models.ExamStudentsInformations;
import com.pracainzynierska.PiotrPecuch.models.GeneratedExam;
import com.pracainzynierska.PiotrPecuch.payload.Responses.ArchiveExamResponse;
import com.pracainzynierska.PiotrPecuch.payload.Responses.UserQuestionAndAnswersResponse;
import com.pracainzynierska.PiotrPecuch.repository.ExamStudentsInformationsRepository;
import com.pracainzynierska.PiotrPecuch.repository.UserQuestionAndAnswersRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArchiveService {

    private final ExamStudentsInformationsRepository esiRepository;
    private final UserQuestionAndAnswersRepository uqaaRepository;
    private final UQAAService uqaaService;

    public ArchiveService(ExamStudentsInformationsRepository esiRepository, UserQuestionAndAnswersRepository uqaaRepository, UQAAService uqaaService) {
        this.esiRepository = esiRepository;
        this.uqaaRepository = uqaaRepository;
        this.uqaaService = uqaaService;
    }

    public List<ArchiveExamResponse> archiveExam(GeneratedExam generatedExam) throws IOException {

        List<ExamStudentsInformations> esiList = esiRepository.findByGeneratedExam(generatedExam);
        List<ArchiveExamResponse> responseList = new ArrayList<>();
        for(ExamStudentsInformations examStudentsInformations : esiList){
            ArchiveExamResponse response = new ArchiveExamResponse();
            response.setStartDate(generatedExam.getStartDate());
            response.setEndDate(generatedExam.getEndDate());
            response.setSumOfPoints(examStudentsInformations.getPointsFromExam());
            response.setIndexNumber(examStudentsInformations.getIndexNumber());
            response.setFirstName(examStudentsInformations.getStudentName());
            response.setLastName(examStudentsInformations.getStudentLastName());
            response.setUqaaList(uqaaService.prepareUQAAList(examStudentsInformations.getSsoLogin(), examStudentsInformations.getSsoPassword(), generatedExam));
            responseList.add(response);
        }
        return responseList;
    }
}
