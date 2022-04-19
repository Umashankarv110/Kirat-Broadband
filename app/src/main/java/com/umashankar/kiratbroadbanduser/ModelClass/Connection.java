package com.umashankar.kiratbroadbanduser.ModelClass;

public class Connection {

    int connectionId;
    String connectionType;

    public Connection() {}

    public Connection(int connectionId, String connectionType) {
        this.connectionId = connectionId;
        this.connectionType = connectionType;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public String toString() {
        return this.connectionType; // What to display in the Spinner list.
    }


}
