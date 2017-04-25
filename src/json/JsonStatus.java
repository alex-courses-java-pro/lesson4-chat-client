package json;

/**
 * Created by arahis on 4/25/17.
 */
public class JsonStatus {
    private String username;
    private String status;

    public JsonStatus(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "JsonStatus{" +
                "username='" + username + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
