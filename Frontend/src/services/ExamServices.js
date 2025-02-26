import api from "@/services/api.js";
import notificationService from "@/services/NotificationService.js";
import NotificationService from "@/services/NotificationService.js";
import tokenServices from "@/services/TokenServices.js";
import TokenServices from "@/services/TokenServices.js";
import PDFService from "@/services/PDFService.js";
import router from "@/router/index.js";

class ExamServices {

    getAllExams() {
        return new Promise(async (resolve, reject) => {
            TokenServices.isTokenValid()
                .then(() => {

                    return api.get("/exam/getAllExams", {
                        headers: {
                            'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                            'Content-Type': 'application/json'
                        }
                    })
                        .then(response => {
                            resolve(response.data);
                        })
                        .catch(error => {
                            console.error('Błąd podczas pobierania egzaminów:', error);
                            reject(error);
                        });
                });
        })
    }

    deleteExam(examId) {
        return new Promise(async (resolve, reject) => {
            TokenServices.isTokenValid()
                .then(() => {
                    return api.delete('/exam/delete', {
                        params: {examId: examId},
                        headers: {
                            'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                            'Content-Type': 'application/json'
                        }
                    }).then(response => {
                        notificationService.notification("success", response.data);
                        resolve(response.data);
                    })
                        .catch(error => {
                            notificationService.notification("error", error.message);
                            reject(error);
                        });
                })
        });
    }


    addExam(examName) {
        return new Promise(async (resolve, reject) => {
            TokenServices.isTokenValid()
                .then(async () => {
                    return api.post('/exam/add', {examName: examName}, {
                        headers: {
                            'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                            'Content-Type': 'application/json'
                        }
                    }).then(response => {
                        notificationService.notification("success", `${response.data}`);
                        resolve(response.data);
                    })
                        .catch(error => {
                            if (error.response && error.response.status === 409) {
                                const errorMessage = error.response.data;
                                notificationService.notification("error", errorMessage);
                            } else {
                                console.log("Error", error);
                                notificationService.notification("error", error.message);
                            }
                            reject(error);
                        });
                })
        });
    }

    async generateExam(formData) {

        TokenServices.isTokenValid()
            .then(async () => {
                await api.post('/exam/generate', formData, {
                    headers: {
                        'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'multipart/form-data'
                    }
                }).then(response => {
                    notificationService.notification("success", `${response.data}`);
                })
                    .catch(error => {
                        if(error.response.data === null){
                            notificationService.notification("error", "Błąd podczas planowania");
                        }
                        notificationService.notification("error", error.response.data);
                        reject(error);
                    })
            })
    }


    async generatePDFExam(examToPDF, numberOfQuestions) {
        if(numberOfQuestions === 0){
            NotificationService.notification("error","Brak pytań w egzaminie")
            return;
        }
        try {
            TokenServices.isTokenValid().then(async () => {
                const response = await api.post(
                    `/exam/generatePDF?examId=${parseInt(examToPDF.examId, 10)}&numberOfQuestions=${parseInt(numberOfQuestions, 10)}`,
                    {},
                    {
                        headers: {
                            'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                            'Content-Type': 'application/json'
                        }
                    }
                )

                PDFService.generateExamPDF(response.data)
                return response.data;
            })} catch (error) {
            notificationService.notification("error", error.message);
            throw error;
        }
        return TokenServices.isTokenValid().then(async () => {
        });
    }


    getStartedExams() {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid().then(async () => {
                return api.get("/exam/getStartedExams", {
                    headers: {
                        'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        resolve(response.data);
                    })
                    .catch(error => {
                        console.error('Błąd podczas pobierania egzaminów:', error);
                        reject(error);
                    });
            })
        })
    }

    updateExamName(examId, newExamName) {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid().then(async () => {
                return api.patch("/exam/update/name", null, {
                    params: {
                        examId: examId,
                        name: newExamName
                    },
                    headers: {
                        'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        notificationService.notification("success", `${response.data}`);
                        resolve(response.data);
                    })
                    .catch(error => {
                        notificationService.notification("error", `${error.response.data}`);
                        console.error('Błąd podczas pobierania egzaminów:', error.response);
                        reject(error);
                    });
            })
        })
    }


    getFinishedExams() {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid()
                .then(() => {
                    return api.get("/exam/getFinishedExams", {
                        headers: {
                            'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                            'Content-Type': 'application/json'
                        }
                    });
                })
                .then(response => {
                    resolve(response.data);
                })
                .catch(error => {
                    console.error('Błąd podczas pobierania egzaminów:', error);
                    reject(error);
                });
        });
    }


    getStartedExamsDetails(generatedExamId) {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid()
                .then(() => {
                    return api.get("/exam/getStartedExamsDetails", {
                        params: {generatedExamId: generatedExamId},
                        headers: {
                            'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                            'Content-Type': 'application/json'
                        }
                    })
                        .then(response => {
                            resolve(response.data);
                        })
                        .catch(error => {
                            console.error('Błąd podczas pobierania egzaminów:', error);
                            reject(error);
                        });
                });
        })
    }

    getAllUserAnswers(generatedExamId, ssoLogin, ssoPassword) {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid()
                .then(() => {
                    return api.get("/exam/getAllUserAnswers", {
                        params: {
                            generatedExamId: generatedExamId,
                            ssoLogin: ssoLogin,
                            ssoPassword: ssoPassword
                        },
                        headers: {
                            'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                            'Content-Type': 'application/json'
                        }
                    })
                        .then(response => {
                            resolve(response.data);
                        })
                        .catch(error => {
                            console.error('Błąd podczas pobierania egzaminów:', error);
                            reject(error);
                        });
                });
        })
    }


    startExam(data) {
        return new Promise((resolve, reject) => {
            api.get("/student/startExam", {
                params: {ssoLogin: data.ssoLogin, ssoPassword: data.ssoPassword},
                headers: {'Content-Type': 'application/json'}
            })
                .then(response => {
                    resolve(response.data);
                })
                .catch(error => {
                    notificationService.notification("error", error.response.data)
                    reject(error);
                });
        });
    }

    getFirstQuestion(data) {
        return new Promise((resolve, reject) => {
            api.get("/student/getFirstQuestion", {
                params: {ssoLogin: data.ssoLogin, ssoPassword: data.ssoPassword},
                headers: {'Content-Type': 'multipart/form-data'}
            })
                .then(response => {
                    resolve(response.data);
                })
                .catch(error => {
                    reject(error);
                });
        });
    }

    async getNextQuestion(data, answerIdList, textAnswer) {
        return await api.post("/student/getNextQuestion", {
            answers: answerIdList,
            textAnswer: textAnswer
        }, {
            params: {
                ssoLogin: data.ssoLogin,
                ssoPassword: data.ssoPassword
            },
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.data)
            .catch(error => {
                console.error("Błąd ładowania kolejnego pytania:", error);
                throw error;
            });
    }

    endExam(data, userAlert) {
        return api.post("/student/endExam", null, {
            params: {
                ssoLogin: data.ssoLogin,
                ssoPassword: data.ssoPassword,
                userAlert: userAlert
            },
            headers: {'Content-Type': 'application/json'}
        })
            .then(async response => {
                sessionStorage.setItem("endExamResponse", JSON.stringify(response.data));
                await router.replace({path: '/exam/results'});

            })
    };

    archiveExam(item) {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid().then(async () => {
                return api.get("/exam/archive", {
                    params: {
                        generatedExamId: item.id
                    },
                    headers: {
                        'Authorization': `Bearer ${TokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        if (!response.data) {
                            throw new Error("Brak danych egzaminu do archiwizacji.");
                        }
                        console.log("Exam archive response:", response.data);
                        PDFService.archiveExam(response.data);
                        notificationService.notification("success", "Wygenerowano pomyślnie");
                        resolve(response.data);
                    })
                    .catch(error => {
                        console.error('Błąd podczas pobierania egzaminów:', error);
                        const errorMessage = error.response?.data || "Nieznany błąd sieci. Spróbuj ponownie później.";
                        notificationService.notification("error", errorMessage);
                        reject(error);
                    });
            }).catch(error => {
                notificationService.notification("error", "Błąd uwierzytelnienia. Proszę się ponownie zalogować.");
                reject(error);
            });
        });
    }

    sendNotification(item) {
        return new Promise((resolve, reject) => {
            TokenServices.isTokenValid().then(async () => {
                return api.get("/exam/sendGradeNotification", {
                    params: {
                        generatedExamId: item.id
                    },
                    headers: {
                        'Authorization': `Bearer ${tokenServices.getLocalAccessToken()}`,
                        'Content-Type': 'application/json'
                    }
                })
                    .then(response => {
                        notificationService.notification("success", response.data);
                        resolve(response.data);
                    })
                    .catch(error => {
                        notificationService.notification("error", `${error.response.data}`);
                        reject(error);
                    });
            })
        })
    }


}

export default new ExamServices();