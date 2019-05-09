package server.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

public class WebUtils {


    private WebUtils() {

    }

    /**
     * Parse an object to JSON
     *
     * @param object The object to parse
     * @return The JSON
     */
    public static String CreateJsonFromModel(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }


    /**
     * Parses a string from a request given a Type t
     * @param body the body from the request
     * @param c the class type
     * @return an object from json
     */
    public static Object parseBody(String body, Class c) {
        Gson gson = new Gson();
        return gson.fromJson(body, c);
    }
}
