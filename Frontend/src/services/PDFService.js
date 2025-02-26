import {jsPDF} from "jspdf"
import autoTable from 'jspdf-autotable'
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
import JSZip from "jszip";


class PDFGenerator {


    generatePdf(data) {
        const doc = new jsPDF;
        doc.text("Loginy i hasla uczniów", 14, 20)
        const tableData = data.map(item => [String(item.indexNumber), item.ssoLogin, item.ssoPassword]);

        autoTable(doc, {
            head: [['Nr. indexu', 'login', 'haslo']],
            columnStyles: {
                halign: "center"
            },
            body: tableData,
            theme: "grid",
            showHead: "firstPage",
            bodyStyles: {
                fillColor: [255, 255, 255],
                fontSize: 15,
                minCellHeight: 15,
                valign: "middle"

            }
        })
        doc.save("loginy.pdf");
    }

    generateExamPDF(response) {
        console.log(response)
        const questions = response;
        const doc = new jsPDF();

        doc.setFontSize(10);
        doc.text("Imie i Nazwisko: ____________________________ Numer Indeksu: _____________________ Data: _____________", 10, 20);
        doc.text("Ocena: _________", 165, 10);

        const tableData = [];

        questions.forEach((item, index) => {
            const question = item.question;

            let questionText = `${index + 1}. ${question.questionContent}`;

            if (question.questionFile) {
                if (question.questionFile.contentType === "text/plain") {
                    const fileText = decodeURIComponent(escape(atob(question.questionFile.imageData)));
                    console.log("Plik:", question.questionFile);
                    questionText += `\n${fileText}]`;
                } else if (question.questionFile.contentType !== "text/plain") {
                    questionText += `nieobsługiwany typ pliku`;
                }
            }

            tableData.push([
                questionText,
                question.points,
                ''
            ]);

            let answersText = "";
            let answers = "";

            if (item.answer && item.answer.length === 0) {
                answersText = '\n\n\n\n\n\n\n\n';
            } else if (item.answer && item.answer.length > 0) {
                answers = item.answer.map((answer, answerIndex) => {
                    return "Cokolwiek tutaj";
                }).join('\n');

            } else {
                answersText = 'Brak odpowiedzi';
            }

            tableData.push([answersText || answers, question.points, ""]);

        });

        // Ustawienia szerokości kolumn
        const pageWidth = doc.internal.pageSize.width;
        const questionWidth = pageWidth * 0.7;
        const maxPointsWidth = pageWidth * 0.1;
        const scoreWidth = pageWidth * 0.1;

        autoTable(doc, {
            head: [['Pytanie', 'Uzyskano', 'Maks.']],
            body: tableData.map((row, index) => {
                if (index % 2 === 0) {
                    return [
                        row[0],
                        row[2],
                        row[1]
                    ];
                } else {
                    return [
                        { content: row[0], colSpan: 3 },
                    ];
                }
            }),
            startY: 25,
            theme: 'grid',
            columnStyles: {
                0: {
                    cellWidth: questionWidth,
                    fontSize: 10,
                    overflow: 'linebreak'
                },
                1: {
                    cellWidth: maxPointsWidth,
                    fontSize: 10,
                    halign: 'center'
                },
                2: {
                    cellWidth: scoreWidth,
                    fontSize: 10,
                    halign: 'center'
                }
            },
            bodyStyles: {
                fontSize: 12,
                valign: 'middle',
            },
            margin: { top: 20, bottom: 20 },
            showHead: 'firstPage',
            autoSize: true
        });

        doc.save("exam.pdf");
    }



    archiveExam(dataList) {
        console.log(dataList);

        const formatDate = (isoDate) => {
            const date = new Date(isoDate);
            return date.toLocaleDateString('pl-PL', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
            });
        };

        const renderUqaaList = (uqaaList) => {
            if (!uqaaList || uqaaList.length === 0) return [{ text: 'Brak pytań.', margin: [0, 10, 0, 10] }];

            return uqaaList.flatMap((item, index) => [
                { text: `Pytanie nr. ${index + 1}`, margin: [0, 5, 0, 5], bold: true },
                { text: `Treść pytania: ${item.question.questionContent}`, margin: [0, 5, 0, 5] },
                { text: `Plik pytania: ${item.question?.questionFile?.filename || 'Brak pliku'}`, margin: [0, 5, 0, 5] },
                { text: "Odpowiedzi użytkownika:", margin: [0, 10, 0, 10], bold: true },
                renderUserAnswers(item.userAnswers),
                { text: "Poprawne odpowiedzi:", margin: [0, 10, 0, 10], bold: true },
                renderCorrectAnswers(item.correctAnswers),
                { text: `-----------------------------------------------------------------------------------------------------------------------------------------------------------`, margin: [0, 5, 0, 5] },
            ]);
        };

        const renderUserAnswers = (userAnswers) => {
            if (!userAnswers || userAnswers.length === 0) {
                return [{ text: "Brak odpowiedzi użytkownika", margin: [0, 10, 0, 10] }];
            }
            return userAnswers.map(item => {
                if (item.answerFiles !== null) {
                    const contentType = item.answerFiles.contentType;
                    // Sprawdzamy, czy plik jest obrazem
                    if (contentType.startsWith("image")) {
                        const imageBase64 = `data:${contentType};base64,${item.answerFiles.data}`;
                        return { image: imageBase64, width: 150, margin: [0, 10, 0, 10] };
                    }
                    // Jeśli plik to PDF lub GIF, dodajemy do ZIP
                    else if (contentType === "application/pdf" || contentType === "image/gif") {
                        fileNames.add({
                            filename: item.answerFiles.filename,
                            data: item.answerFiles.data
                        });
                        return { text: `Plik ${item.answerFiles.filename} dodany do ZIP`, margin: [0, 10, 0, 10] };
                    }
                } else {
                    return { text: item.answerContent || "Brak treści odpowiedzi", margin: [0, 10, 0, 10] };
                }
            });
        };

        const renderCorrectAnswers = (correctAnswers) => {
            if (!correctAnswers || correctAnswers.length === 0) {
                return [{ text: "Brak poprawnych odpowiedzi / Odpowiedź pisemna", margin: [0, 10, 0, 10] }];
            }
            return correctAnswers.map(item => {
                if (item.answerFiles !== null) {
                    const contentType = item.answerFiles.contentType;
                    // Sprawdzamy, czy plik jest obrazem
                    if (contentType.startsWith("image")) {
                        const imageBase64 = `data:${contentType};base64,${item.answerFiles.data}`;
                        return { image: imageBase64, width: 150, margin: [0, 10, 0, 10] };
                    }
                    // Jeśli plik to PDF lub GIF, dodajemy do ZIP
                    else if (contentType === "application/pdf" || contentType === "image/gif") {
                        fileNames.add({
                            filename: item.answerFiles.filename,
                            data: item.answerFiles.data
                        });
                        return { text: `Plik ${item.answerFiles.filename} dodany do ZIP`, margin: [0, 10, 0, 10] };
                    }
                } else {
                    return { text: item.answerContent || "Brak treści odpowiedzi", margin: [0, 10, 0, 10] };
                }
            });
        };

        const fileNames = new Set();

        const content = dataList.flatMap(data => {
            data.uqaaList.forEach(item => {
                if (item.question?.questionFile) {
                    const contentType = item.question.questionFile.contentType;
                    // Sprawdzamy, czy plik jest obrazem i dodajemy do PDF
                    if (contentType.startsWith("image")) {
                        fileNames.add({
                            filename: item.question.questionFile.filename,
                            data: item.question.questionFile.imageData
                        });
                    }
                    // Jeśli plik to PDF lub GIF, dodajemy do ZIP
                    else if (contentType === "application/pdf" || contentType === "image/gif") {
                        fileNames.add({
                            filename: item.question.questionFile.filename,
                            data: item.question.questionFile.imageData
                        });
                    }
                }
            });

            return [
                { text: `Imię i nazwisko: ${data.firstName} ${data.lastName}`, fontSize: 15, margin: [0, 10, 0, 5] },
                { text: `Numer indeksu: ${data.indexNumber}`, fontSize: 14, margin: [0, 0, 0, 5] },
                { text: `Suma punktów: ${data.sumOfPoints} / ${data.uqaaList[0].percentageOfPoints}%`, margin: [0, 0, 0, 10] },
                { text: `Pytania i odpowiedzi:`, style: 'header' },
                ...renderUqaaList(data.uqaaList),
                { text: '', pageBreak: 'after' }
            ];
        });

        const documentDefinition = {
            content: [
                { text: 'Zarchiwizowane odpowiedzi użytkowników', fontSize: 18, bold: true, margin: [0, 0, 0, 10] },
                { text: `Data rozpoczęcia: ${formatDate(dataList[0].startDate)}`, margin: [0, 0, 0, 5] },
                { text: `Data zakończenia: ${formatDate(dataList[0].endDate)}`, margin: [0, 0, 0, 10] },
                ...content,
            ],
            styles: {
                header: {
                    fontSize: 14,
                    bold: true,
                    margin: [0, 10, 0, 10],
                },
            },
        };

        pdfMake.createPdf(documentDefinition).getBase64(function (base64) {
            const zip = new JSZip();

            // Dodajemy PDF z odpowiedziami
            zip.file('egzamin-odpowiedzi.pdf', base64, { base64: true });

            // Dodajemy pliki PDF i GIF do ZIP
            fileNames.forEach(file => {
                zip.file(file.filename, file.data, { base64: true });
            });

            zip.generateAsync({ type: 'blob' }).then(function (content) {
                saveAs(content, 'raport_i_pliki.zip');
            });
        });
    }


}

export default new PDFGenerator();