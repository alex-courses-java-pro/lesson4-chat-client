package json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Message;

public class JsonMessages {
    private final List<Message> messages;

    public JsonMessages(List<Message> sourceList, int fromIndex) {
        this.messages = new ArrayList<>();
        for (int i = fromIndex; i < sourceList.size(); i++)
            messages.add(sourceList.get(i));
    }

    public List<Message> getList() {
        return Collections.unmodifiableList(messages);
    }

    @Override
    public String toString() {
        return "JsonMessages{" +
                "list=" + messages +
                '}';
    }
}
