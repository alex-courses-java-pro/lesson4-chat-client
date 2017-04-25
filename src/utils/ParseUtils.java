package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Created by arahis on 4/20/17.
 */
public class ParseUtils {

    private static Gson gson = new GsonBuilder().create();

    public static <T> T fromJson(String json, Class<T> t) {
        return gson.fromJson(json, t);
    }

    public static String toJson(Object obj, Type t) {
        return gson.toJson(obj, t);
    }

}
