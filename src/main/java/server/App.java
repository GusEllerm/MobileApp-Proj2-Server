package server;

import server.database.DatabaseUtils;
import server.database.Routes;
import server.database.controller.GpsMapControler;


import static spark.Spark.*;

public class App {


    private static final int PORT = 4567;

    private static GpsMapControler gpsMapControler;


    private static void initialize() {
        gpsMapControler = new GpsMapControler();
        try {
            DatabaseUtils.checkIfDataBasesExistsElseCreate();
            //DatabaseUtils.resetDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initialize();

        port(PORT);

        /*
         * Set CORS headers.
         */
        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }
                    return "OK";
                });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        post(Routes.GPS_MAP, gpsMapControler.routePostGpsMap());
        get(Routes.GPS_MAP, gpsMapControler.routeGetGpsMap());
        get(Routes.GPS_MAP_ID, gpsMapControler.routeGetGpsMapsIds());

        patch(Routes.GPS_MAP_VOTE, gpsMapControler.routePatchVote());
        post(Routes.GPS_MAP_VOTE, gpsMapControler.routePostVote());


        get("*", (request, response) -> {
            response.status(404);
            return "Not Found";
        });
    }
}
