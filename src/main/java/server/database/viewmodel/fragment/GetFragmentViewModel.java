package server.database.viewmodel.fragment;

import server.database.viewmodel.coordinate.GetCoordinateViewModel;
import server.models.Fragment;

import java.util.ArrayList;
import java.util.List;

public class GetFragmentViewModel {

    private int id;
    private String lineColour;
    private int gpsMapId;
    private int zoom;
    private List<GetCoordinateViewModel> coordinates;

    private GetFragmentViewModel(int id, String lineColour, int gpsMapId, int zoom, List<GetCoordinateViewModel> coordinates) {
        this.id = id;
        this.lineColour = lineColour;
        this.gpsMapId = gpsMapId;
        this.zoom = zoom;
        this.coordinates = coordinates;
    }

    public static GetFragmentViewModel createFragmentViewModel(Fragment fragment) {
        return new GetFragmentViewModel(
                fragment.getId(),
                fragment.getLineColour(),
                fragment.getGpsMapId(),
                fragment.getZoom(),
                GetCoordinateViewModel.createListFromCoordinates(fragment.getCoordinates()));
    }

    public static List<GetFragmentViewModel> createListFromFragments(List<Fragment> fragments) {
        List<GetFragmentViewModel> fragmentsVM = new ArrayList<>();
        for (Fragment fragment : fragments) {
            fragmentsVM.add(createFragmentViewModel(fragment));
        }
        return fragmentsVM;
    }

}
