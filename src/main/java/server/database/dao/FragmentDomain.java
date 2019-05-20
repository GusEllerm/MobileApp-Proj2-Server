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

    public void updateFragment(Fragment frag) throws SQLException {
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE fragment SET line_colour = ?, zoom = ? " +
                        "WHERE frag_id = ?")
        ) {
            statement.setString(1, frag.getLineColour());
            statement.setInt(2, frag.getZoom());
            statement.setInt(3, frag.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void deleteFragment(Fragment fragment) throws SQLException {
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM fragment WHERE frag_id = ?")
        ) {
            statement.setInt(1, fragment.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }


    public void updateFragments(GpsMap update, GpsMap old) throws SQLException{
        List<Fragment> oldFragments = old.getFragments();
        for (Fragment fragment : update.getFragments()) {
            Fragment oldFrag = getFragmentFromList(fragment, oldFragments);
            if (oldFrag != null) {
                // updating fragment
                if (!isFragmentEqual(fragment, oldFrag)) {
                    updateFragment(fragment);
                }
                coordinateDomain.updateCoordinates(fragment, oldFrag);
                oldFragments.remove(oldFrag);
            } else {
                // new fragment
                fragment.setGpsMapId(update.getId());
                int fragId = saveFragment(fragment);
                fragment.setId(fragId);
                if (fragId > 0) coordinateDomain.saveAllCoordinates(fragment.getCoordinates(), fragId);
            }
        }
        for (Fragment leftOverFragment : oldFragments) {
            deleteFragment(leftOverFragment);
        }
    }


    private boolean isFragmentEqual(Fragment obj1, Fragment obj2) {
        return (obj1.getLineColour().equals(obj2.getLineColour()) && obj1.getZoom() == obj2.getZoom());
    }

    private Fragment getFragmentFromList(Fragment fragment, List<Fragment> fragments) {
        for (Fragment fragFromList : fragments) {
            if (fragFromList.getId() == fragment.getId()) return fragFromList;
        }
        return null;
    }
}
