package server.models;


import java.util.List;

public class Fragment {

    private int id;
    private String lineColour;
    private int gpsMapId;
    private int zoom;
    private List<Coordinate> coordinates;

    /**
     * Initializer
     * @param id the id of the fragment from the database
     * @param lineColour the line colour as string
     * @param gpsMapId the id of the gps map
     * @param zoom the zoom level
     */
    public Fragment(int id, String lineColour, int gpsMapId, int zoom) {
        this.id = id;
        this.lineColour = lineColour;
        this.gpsMapId = gpsMapId;
        this.zoom = zoom;
    }

    public Fragment(String lineColour, int zoom, List<Coordinate> coordinates) {
        this.lineColour = lineColour;
        this.zoom = zoom;
        this.coordinates = coordinates;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setCoordinates(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public void setGpsMapId(int gpsMapId) {
        this.gpsMapId = gpsMapId;
    }

    public void setLineColour(String lineColour) {
        this.lineColour = lineColour;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public int getId() {
        return id;
    }

    public int getGpsMapId() {
        return gpsMapId;
    }

    public int getZoom() {
        return zoom;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public String getLineColour() {
        return lineColour;
    }


}
