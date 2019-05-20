package server.database.dao;

import server.database.DatabaseUtils;
import server.database.enums.OrderType;
import server.models.Fragment;
import server.models.GpsMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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


    public int patchGpsMap(GpsMap gpsMap) throws SQLException {
        GpsMap databaseMap = getGpsMapFromId(gpsMap.getId());
        if (!isGpsMapEqual(databaseMap, gpsMap)) {
            try (
                    Connection connection = DatabaseUtils.getDatabaseConnection();
                    PreparedStatement statement = connection.prepareStatement("UPDATE gps_map SET map_type = ?, map_title = ?, " +
                            "fragment_amount = ?, category = ? WHERE map_id = ?")
            ) {
                statement.setString(1, gpsMap.getMapType());
                statement.setString(2, gpsMap.getMapTitle());
                statement.setInt(3, gpsMap.getFragmentAmount());
                statement.setString(4, gpsMap.getCategory());
                statement.setInt(5, gpsMap.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new SQLException(e.getMessage());
            }
        }
        fragmentDomain.updateFragments(gpsMap, databaseMap);
        return gpsMap.getId();
    }


    public List<Integer> getGpsMapIds(String category, OrderType order, int page, int limit) {
        List<Integer> mapIds = new ArrayList<>();
        String query = "";
        int offset = (page - 1) * limit;
        if (!category.isEmpty()) {
            query += " WHERE lower(category) = " + category.toLowerCase();
        }
        query += order.getSql();
        query += " LIMIT " + limit + " OFFSET " + offset;
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT map_id FROM gps_map" + query)
        ) {
            try (ResultSet set = statement.executeQuery()) {
                while(set.next()) {
                    mapIds.add(set.getInt("map_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return mapIds;
    }


    public int addVote(int id, int amount) throws SQLException {
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement1 = connection.prepareStatement("SELECT votes FROM gps_map WHERE map_id = ?");
                PreparedStatement statement2 = connection.prepareStatement("UPDATE gps_map SET votes = ? WHERE map_id = ?")
        ) {
            statement1.setInt(1, id);
            try (ResultSet set = statement1.executeQuery()) {
                if (set.next()) {
                    int votes = set.getInt("votes");
                    votes += amount;
                    statement2.setInt(1, votes);
                    statement2.setInt(2, id);
                    statement2.executeUpdate();
                    return votes;
                } else throw new SQLException("Cannot find gps map with id");
            }
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }


    private boolean isGpsMapEqual(GpsMap obj1, GpsMap obj2) {
        return (obj1.getFragments() == obj2.getFragments() && obj1.getCategory().equals(obj2.getCategory()) &&
                obj1.getMapType().equals(obj2.getMapTitle()) && obj1.getMapTitle().equals(obj2.getMapTitle()));
    }

}





















