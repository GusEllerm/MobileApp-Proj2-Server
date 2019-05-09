package server.database.dao;

import server.database.DatabaseUtils;
import server.models.Fragment;
import server.models.GpsMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GpsMapDomain {

    private FragmentDomain fragmentDomain;


    public GpsMapDomain() {
        fragmentDomain = new FragmentDomain();
    }

    /**
     * Get a map from id and all its relating fragments and coordinates
     * @param id the id to search the database for
     * @return a GpsMap object
     * @throws SQLException if the object doesnt exist or there is a problem connecting to the database.
     */
    public GpsMap getGpsMapFromId(int id) throws SQLException {
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM gps_map WHERE map_id = ?")
        ) {
            statement.setInt(1, id);
            try (ResultSet set = statement.executeQuery()) {
                if(set.next()) {
                    GpsMap map = new GpsMap(set.getInt("map_id"),
                            set.getString("map_type"),
                            set.getInt("fragment_amount"),
                            set.getString("map_title"),
                            set.getString("category"),
                            set.getInt("votes"));
                    List<Fragment> fragments = fragmentDomain.getAllFragmentsFromGpsMap(map);
                    map.setFragments(fragments);
                    return map;
                } else return null;
            }
        }
    }

    /**
     * Inserts a gps map into the database
     * @param gpsMap the map to save
     * @return the unique integer if of the saved map
     * @throws SQLException when there is an error connecting or saving to the dtabase
     */
    public int insertGpsMap(GpsMap gpsMap) throws SQLException {
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO gps_map (map_type, fragment_amount, " +
                        "map_title, category, votes) VALUES (?, ?, ?, ?, ?)")
        ) {
            statement.setString(1, gpsMap.getMapType());
            statement.setInt(2, gpsMap.getFragmentAmount());
            statement.setString(3, gpsMap.getMapTitle());
            statement.setString(4, gpsMap.getCategory());
            statement.setInt(5, gpsMap.getVotes());
            statement.executeUpdate();
            try (ResultSet set = statement.getGeneratedKeys()) {
                if (set.next()) {
                    gpsMap.setId(set.getInt(1));
                    System.out.println("Saved map successfully");
                    int saved = fragmentDomain.saveAllFragments(gpsMap.getFragments(), gpsMap.getId());
                    System.out.println(String.format("Saved %d fragments out of %d", saved, gpsMap.getFragmentAmount()));
                }
            }
            return gpsMap.getId();
        }
    }
}
