package json;

import java.util.List;

/**
 * Created by arahis on 4/25/17.
 */
public class JsonUsers {
    private List<String> names;

    public List<String> getNames() {
        return names;
    }

    @Override
    public String toString() {
        return "JsonUsers{" +
                "names=" + names +
                '}';
    }
}
