package server.database.dao;

import server.database.DatabaseUtils;
import server.models.Coordinate;
import server.models.Fragment;
import server.models.GpsMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FragmentDomain {

    private CoordinateDomain coordinateDomain;

    public FragmentDomain() {
        coordinateDomain = new CoordinateDomain();
    }


    /**
     * Saves a fragment into the database and returns its unique id
     * @param fragment the fragment to save
     * @return an integer id of the fragment
     * @throws SQLException when there is an error saving
     */
    public int saveFragment(Fragment fragment) throws SQLException {
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO fragment (line_colour, " +
                        "map_id, zoom) VALUES (?, ?, ?)")
        ) {
            statement.setString(1, fragment.getLineColour());
            statement.setInt(2, fragment.getGpsMapId());
            statement.setInt(3, fragment.getZoom());
            statement.executeUpdate();
            try (ResultSet set = statement.getGeneratedKeys()) {
                if (set.next()) return set.getInt(1);
            }
        }
        return -1;
    }

    /**
     * Inserts a list of fragments and their coordinates into the database
     *
     * @param fragments the list of fragments to save
     * @return a count of successfully saved fragments
     * @throws SQLException if there is an error connecting or inserting into the database
     */
    public int saveAllFragments(List<Fragment> fragments, int mapId) throws SQLException {
        int count = 0;
        for (Fragment fragment : fragments) {
            fragment.setGpsMapId(mapId);
            int fragId = saveFragment(fragment);
            if (fragId > 0) {
                count++;
                boolean success = coordinateDomain.saveAllCoordinates(fragment.getCoordinates(), fragId);
                if (!success) System.out.println("Couldn't save coordinates for fragment with id : " + fragId);
            }
        }
        return count;
    }

    /**
     * Gets all the fragments and their related coordinates from the database for a given gps map
     *
     * @param gpsMap the given mao
     * @return a list of fragments
     * @throws SQLException when there is an error connection to the database or retrieving information.
     */
    public List<Fragment> getAllFragmentsFromGpsMap(GpsMap gpsMap) throws SQLException {
        List<Fragment> fragments = new ArrayList<>();
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM fragment WHERE map_id = ?")
        ) {
            statement.setInt(1, gpsMap.getId());
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    Fragment fragment = new Fragment(set.getInt("frag_id"),
                            set.getString("line_colour"),
                            gpsMap.getId(),
                            set.getInt("zoom"));
                    List<Coordinate> coordinates = coordinateDomain.getAllCoordinatesFromFragment(fragment);
                    fragment.setCoordinates(coordinates);
                    fragments.add(fragment);
                }
            }
        }
        return fragments;
    }
}
