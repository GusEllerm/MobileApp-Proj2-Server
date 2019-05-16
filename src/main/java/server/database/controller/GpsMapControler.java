package server.database.controller;

import server.database.WebUtils;
import server.database.dao.GpsMapDomain;
import server.database.enums.OrderType;
import server.database.viewmodel.gpsmap.GetGpsMapViewModel;
import server.models.GpsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;
import java.util.List;

public class GpsMapControler {

    private static final int ID_LIMIT = 50;
    private static final int DEFAULT_PAGE = 1;


    private GpsMapDomain gpsMapDomain;

    public GpsMapControler() {
        gpsMapDomain = new GpsMapDomain();
    }

    public Route routeGetGpsMap() {return getGpsMap;}
    /**
     * route for GETting a gps map
     */
    private Route getGpsMap = (Request req, Response res) -> {
        GpsMap map;
        try {
            String stringId = req.queryParamOrDefault("id", "");
            if (stringId.isEmpty()) throw new Exception("Cannot find id parameter");

            int id = Integer.parseInt(stringId);
            System.out.println(String.format("Getting maps with id : %s", id));
            map = gpsMapDomain.getGpsMapFromId(id);
            if (map == null) {
                System.out.println(String.format("Cannot find gps map with given id : %d in the database", id));
                res.status(404);
                return "";
            }
            GetGpsMapViewModel gpsMapVM = GetGpsMapViewModel.createGpsMapViewModel(map);
            res.status(200);
            return WebUtils.CreateJsonFromModel(gpsMapVM);
        } catch (SQLException sqle) {
            System.out.println("Error : " + sqle.getMessage());
            res.status(500);
            return "";
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            res.status(400);
            return "";
        }
    };


    public Route routeGetGpsMapsIds() {return getGetGpsMapIds;}
    /**
     * Gets all the unique map ids up to a limit given.
     */
    private Route getGetGpsMapIds = (Request req, Response res) -> {
        String category = req.queryParamOrDefault("category", "");
        // can be "newest" "oldest", "most_votes", "least_votes"
        OrderType order = OrderType.getOrderTypeFromString(req.queryParamOrDefault("order", ""));
        String strLimit = req.queryParamOrDefault("limit", "");
        String strPage = req.queryParamOrDefault("page", "");
        int page = DEFAULT_PAGE;
        int limit = ID_LIMIT;
        try {
            limit = Integer.parseInt(strLimit);
            page = Integer.parseInt(strPage);
        } catch (Exception e) {
            //can't parse integer given using default limit
        }
        res.status(200);
        return WebUtils.CreateJsonFromModel(gpsMapDomain.getGpsMapIds(category, order, page, limit));

    };


    public Route routePostGpsMap() {return postGpsMap;}
    /**
     *  Route for POSTing a new gps map
     */
    private Route postGpsMap = (Request req, Response res) -> {
        GpsMap gpsMap;
        if (req.body().isEmpty()) {
            res.status(400);
            return "";
        }
        try {
            gpsMap = (GpsMap) WebUtils.parseBody(req.body(), GpsMap.class);
            gpsMap.setVotes(0); //reset votes
            System.out.println("Saving gps map");
            int id = gpsMapDomain.insertGpsMap(gpsMap);
            res.status(201);
            return String.format("{ id : %d}", id);
        } catch (SQLException e) {
            e.printStackTrace();
            res.status(500);
            return "";
        }
    };

    public Route routePostVote() {return postVote;}
    /**
     * Adds a vote to a map
     */
    private Route postVote = (Request req, Response res) -> {
        String strId = req.queryParamOrDefault("id", "");
        if (strId.isEmpty()) {
            res.status(400);
            return "";
        }
        try {
            int id = Integer.parseInt(strId);
            int votes = gpsMapDomain.addVote(id, 1);
            res.status(200);
            return String.format("{ votes : %d}", votes);
        } catch (SQLException e) {
            e.printStackTrace();
            res.status(500);
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            res.status(400);
            return "";
        }
    };

    public Route routePatchVote() {return patchVote;}
    /**
     * Removes a vote to a map
     */
    private Route patchVote = (Request req, Response res) -> {
        String strId = req.queryParamOrDefault("id", "");
        if (strId.isEmpty()) {
            res.status(400);
            return "";
        }
        try {
            int id = Integer.parseInt(strId);
            int votes = gpsMapDomain.addVote(id, -1);
            res.status(200);
            return String.format("{ votes : %d}", votes);
        } catch (SQLException e) {
            e.printStackTrace();
            res.status(500);
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            res.status(400);
            return "";
        }
    };
}
