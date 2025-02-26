import api from "@/services/api.js";
import notificationService from "@/services/NotificationService.js";
import tokenServices from "@/services/TokenServices.js";
import TokenServices from "@/services/TokenServices.js";

class QuestionServices {
    async getAllQuestions(examId) {
        return TokenServices.isTokenValid().then(async () => {
            try {
                const response = await api.get("/question/getAllQuestions", {
                    params: { examId: examId },
                    headers: {
                        'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json',
                    },
                });

                return response.data;
            } catch (error) {
                notificationService.notification("error", error.message);
                throw error;
            }
        });
    }


    async addQuestions(examId, questionsArray) {
        try {
            const formData = new FormData();
            formData.append('examId', examId);
            formData.append('questionContent', questionsArray[0].questionContent);
            formData.append('questionPoints', questionsArray[0].questionPoints);

            if (questionsArray[0].questionAttachment) {
                formData.append('questionImage', questionsArray[0].questionAttachment);
            }

            questionsArray[0].answerList.forEach((answer, index) => {
                formData.append(`answerList[${index}].answerContent`, answer.answerContent);
                formData.append(`answerList[${index}].correct`, answer.correct);
                formData.append(`answerList[${index}].points`, answer.points);

                if (answer.attachment) {
                    formData.append(`answerList[${index}].attachment`, answer.attachment);
                }
            });

            await TokenServices.isTokenValid();

            const response = await api.post("/question/add", formData, {
                headers: {
                    'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                    'Content-Type': 'multipart/form-data',
                }
            });

            notificationService.notification("success",response.data);
        } catch (error) {
            console.error("Błąd przy dodawaniu pytania:", error);
            notificationService.notification("error", error.message || "An error occurred while adding the question.");
            throw error;
        }
    }



    async getAnswers(examName, questionContent, questionId) {
        try {
            await TokenServices.isTokenValid();

            const response = await api.get(`/question/answers/${examName}/${questionId}/${encodeURIComponent(questionContent)}`, {
                headers: {
                    'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                    'Content-Type': 'application/json'
                }
            });

            return response.data;

        } catch (error) {
            console.error('Błąd podczas pobierania odpowiedzi:', error);
            throw error;
        }
    }



    async deleteAnswer(examName, questionContent, questionId) {
        try {
            await TokenServices.isTokenValid();

            const response = await api.delete(`/question/delete/${examName}/${questionId}/${encodeURIComponent(questionContent)}`, {
                headers: {
                    'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                    'Content-Type': 'application/json'
                }
            });

            notificationService.notification("success","Usunięto pomyślnie")

        } catch (error) {
            console.error('Błąd usuwania odpowiedzi:', error);
            notificationService.notification("error", error.message || "An error occurred while deleting the answer.");
        }
    }



    async answersImport(examName, file) {
        try {
            const fileName = file.name;
            let fileExtension = fileName.split('.').pop().toLowerCase();

            if (!["ods", "xls", "xlsx"].includes(fileExtension)) {
                notificationService.notification("error", "Niepoprawny format pliku. Dozwolone rozszerzenia to: ods, xls, xlsx.");
                return;
            }

            if (fileExtension === "xlsx") {
                fileExtension = "xls";
            }

            const formData = new FormData();
            formData.append('file', file);
            formData.append('examName', examName);

            await TokenServices.isTokenValid();

            const response = await api.post(`/question/answers/import/${fileExtension}/${examName}`, formData, {
                headers: {
                    'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                    'Content-Type': 'multipart/form-data',
                },
            });

            notificationService.notification("success", "Importowano pomyślnie");

        } catch (error) {
            console.error('Błąd importowania odpowiedzi:', error);
            notificationService.notification("error", error.message || "An error occurred while importing answers.");
        }
    }

    async changePoints(questionId, ssoLogin, ssoPassword, points) {
        try {
            await TokenServices.isTokenValid();

            const response = await api.patch(
                `/question/answers/change/points/${encodeURIComponent(questionId)}/${encodeURIComponent(ssoLogin)}/${encodeURIComponent(ssoPassword)}`,
                null,
                {
                    params:{
                      points: points
                    },
                    headers: {
                        'Authorization': `Bearer ${TokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json',
                    },
                }
            );

            notificationService.notification("success", response.data);
            return response.data;

        } catch (error) {
            notificationService.notification("error", error.message);
            throw error;
        }
    }


}

export default new QuestionServices();



