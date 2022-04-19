package com.umashankar.kiratbroadbanduser.ModelClass;

public class Document {

    int docId;
    String docType;
    public Document() {}

    public Document(int docId, String docType) {
        this.docId = docId;
        this.docType = docType;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    @Override
    public String toString() {
        return this.docType; // What to display in the Spinner list.
    }

}
