package application_package;

public class Data {
    String id;
    String generatedOTP;
    String timeStamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGeneratedOTP() {
        return generatedOTP;
    }
    public void setGeneratedOTP(String generatedOTP) {
        this.generatedOTP = generatedOTP;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
    public Data() {
    }
    public Data(String id, String generatedOTP) {
        this.id = id;
        this.generatedOTP = generatedOTP;

    }
}
