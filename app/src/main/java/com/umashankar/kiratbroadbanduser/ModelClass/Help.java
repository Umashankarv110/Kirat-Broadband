package com.umashankar.kiratbroadbanduser.ModelClass;

public class Help {
    int questionId;
    String question, moduleName, answerDetails, fileName, filePath;

    public Help() {
    }

    public Help(int questionId, String question) {
        this.questionId = questionId;
        this.question = question;
    }


    public Help(String moduleName) {
        this.moduleName = moduleName;
    }

    public Help(int questionId, String question, String moduleName, String answerDetails, String fileName, String filePath) {
        this.questionId = questionId;
        this.question = question;
        this.moduleName = moduleName;
        this.answerDetails = answerDetails;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(String answerDetails) {
        this.answerDetails = answerDetails;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return this.getModuleName(); // What to display in the Spinner list.
    }
}
