import json.JsonMessages;
import model.Message;

public class GetThread implements Runnable {

    private Session session;

    public GetThread(Session session) {
        this.session = session;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                JsonMessages messages = session.getMessages();
                if (messages != null) {
                    for (Message m : messages.getList()) {
                        System.out.println(m);
                    }
                }
                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
