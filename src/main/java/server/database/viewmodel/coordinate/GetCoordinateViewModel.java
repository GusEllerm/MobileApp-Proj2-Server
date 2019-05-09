package server.database.viewmodel.coordinate;

import server.models.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class GetCoordinateViewModel {

    private int id;
    private int sequence;
    private double longitude;
    private double latitude;
    private int fragmentId;

    private GetCoordinateViewModel(int id, int sequence, double longitude, double latitude, int fragmentId) {
        this.id = id;
        this.sequence = sequence;
        this.longitude = longitude;
        this.latitude = latitude;
        this.fragmentId = fragmentId;
    }


    public static GetCoordinateViewModel createCoordinateViewModel(Coordinate coordinate) {
        return new GetCoordinateViewModel(
                coordinate.getId(),
                coordinate.getSequence(),
                coordinate.getLongitude(),
                coordinate.getLatitude(),
                coordinate.getFragmentId()
        );
    }

    public static List<GetCoordinateViewModel> createListFromCoordinates(List<Coordinate> coordinates) {
        List<GetCoordinateViewModel> coordinatesVM = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            coordinatesVM.add(createCoordinateViewModel(coordinate));
        }
        return coordinatesVM;
    }
}
