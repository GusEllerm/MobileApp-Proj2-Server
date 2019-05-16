package server.database;

public class Routes {


    private Routes() {

    }

    public static final String BASE = "/api";

    public static final String GPS_MAP = BASE + "/gps_map";

    public static final String GPS_MAP_ID = GPS_MAP + "/list";

    public static final String GPS_MAP_VOTE = GPS_MAP + "/vote";
}
