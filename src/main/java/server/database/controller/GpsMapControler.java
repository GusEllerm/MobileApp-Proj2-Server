package server.database.controller;

import server.database.WebUtils;
import server.database.dao.GpsMapDomain;
import server.database.viewmodel.gpsmap.GetGpsMapViewModel;
import server.models.GpsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;

public class GpsMapControler {


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
            map = gpsMapDomain.getGpsMapFromId(id);
            if (map == null) throw new Exception(String.format("Cannot find gps map with given id : %d in the database", id));
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
        } catch (Exception e) {
            e.printStackTrace();
            res.status(400);
            return "";
        }
    };
}
