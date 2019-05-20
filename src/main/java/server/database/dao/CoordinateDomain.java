package server.database.dao;

import server.database.DatabaseUtils;
import server.models.Coordinate;
import server.models.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoordinateDomain {

    /**
     * Get all coordinates related to a fragment
     * @param fragment the fragment to retrieve coordinates for
     * @return a list of coordinates
     * @throws SQLException when there is an error connecting to database or parsing information retrieved
     */
    public List<Coordinate> getAllCoordinatesFromFragment(Fragment fragment) throws SQLException {
        List<Coordinate> coordinates = new ArrayList<>();
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM coords WHERE frag_id = ?")
        ) {
            statement.setInt(1, fragment.getId());
            try (ResultSet set = statement.executeQuery()) {
                while(set.next()) {
                    Coordinate coordinate = new Coordinate(set.getInt("coords_id"),
                            set.getInt("sequence"),
                            set.getDouble("long"),
                            set.getDouble("lat"),
                            fragment.getId());
                    coordinates.add(coordinate);
                }
            }
        }
        return coordinates;
    }

    /**
     * Inserts a list of coordinates into the database
     * @param coordinates the list of coordinates to save
     * @param fragId the id of the fragment the coordinates relate to
     * @return a boolean whether the insertion was successfully
     */
    public boolean saveAllCoordinates(List<Coordinate> coordinates, int fragId) {
        StringBuffer items = new StringBuffer();
        int size = coordinates.size();
        for (int i = 0; i < size; i++) {
            items.append("(?, ?, ?, ?)");
            if (i < size - 1) {
                items.append(",");
            }
        }
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT INTO coords (sequence, long, lat, " +
                        "frag_id) VALUES " + items.toString())
        ) {
            for (int i = 0; i < size; i++) {
                int index = i * 4;
                Coordinate co = coordinates.get(i);
                statement.setInt(1 + index, co.getSequence());
                statement.setDouble(2 + index, co.getLongitude());
                statement.setDouble(3 + index, co.getLatitude());
                statement.setInt(4 + index, fragId);
            }
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteCoordinate(Coordinate coordinate) throws SQLException {
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM coords WHERE coords_id = ?")
        ) {
            statement.setInt(1, coordinate.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void updateCoordinate(Coordinate coordinate) throws SQLException {
        try (
                Connection connection = DatabaseUtils.getDatabaseConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE coords SET long = ?, lat = ?, " +
                        "sequence = ? WHERE coords_id = ?")
        ) {
            statement.setDouble(1, coordinate.getLongitude());
            statement.setDouble(2, coordinate.getLatitude());
            statement.setInt(3, coordinate.getSequence());
            statement.setInt(4, coordinate.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void updateCoordinates(Fragment updateFragment, Fragment oldFragment) throws SQLException {
        List<Coordinate> oldCoords = oldFragment.getCoordinates();
        List<Coordinate> coordinatesToSave = new ArrayList<>();
        for (Coordinate coord : updateFragment.getCoordinates()) {
            Coordinate oldCoord = getCoordinateFromList(coord, oldCoords);
            if (oldCoord != null) {
                // update old coordinate
                if (!isCoordinateEqual(oldCoord, coord)) {
                    updateCoordinate(coord);
                }
            } else {
                // insert coordinate
                coord.setFragmentId(updateFragment.getId());
                coordinatesToSave.add(coord);
            }
        }
        saveAllCoordinates(coordinatesToSave, updateFragment.getId());
        for (Coordinate leftOverCoordinate : oldCoords) {
            deleteCoordinate(leftOverCoordinate);
        }
    }


    private Coordinate getCoordinateFromList(Coordinate coordinate, List<Coordinate> list) {
        for (Coordinate coordFromList : list) {
            if (coordFromList.getId() == coordFromList.getId()) return coordFromList;
        }
        return null;
    }

    private boolean isCoordinateEqual(Coordinate obj1, Coordinate obj2) {
        return (obj1.getLatitude() == obj2.getLatitude() && obj1.getLatitude() == obj2.getLongitude() &&
                obj1.getSequence() == obj2.getSequence());
    }
}
