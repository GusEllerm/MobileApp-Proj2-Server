package server.database.viewmodel.gpsmap;



import server.database.viewmodel.fragment.GetFragmentViewModel;
import server.models.GpsMap;

import java.util.List;

public class GetGpsMapViewModel {

    private int id;
    private String mapType;
    private int fragmentAmount;
    private String mapTitle;
    private String category;
    private int votes;
    private List<GetFragmentViewModel> fragments;

    private GetGpsMapViewModel(int id, String mapType, int fragmentAmount, String mapTitle, String category, int votes,
                               List<GetFragmentViewModel> fragments) {
        this.id = id;
        this.mapType = mapType;
        this.fragmentAmount = fragmentAmount;
        this.mapTitle = mapTitle;
        this.category = category;
        this.votes = votes;
        this.fragments = fragments;
    }

    public static GetGpsMapViewModel createGpsMapViewModel(GpsMap map) {
        return new GetGpsMapViewModel(
                map.getId(),
                map.getMapType(),
                map.getFragmentAmount(),
                map.getMapTitle(),
                map.getCategory(),
                map.getVotes(),
                GetFragmentViewModel.createListFromFragments(map.getFragments())
        );
    }
}
