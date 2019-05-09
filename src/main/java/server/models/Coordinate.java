package server.models;

public class Coordinate {

      private int id;
      private int sequence;
      private double longitude;
      private double latitude;
      private int fragmentId;

    /**
     * Initializer with all attributes
     * @param id the id of the coordinate
     * @param sequence the sequence number
     * @param longitude the longitude of the coordinate
     * @param latitude the latitude of the coordinate
     * @param fragmentId the fragment Id it belongs to
     */
      public Coordinate(int id, int sequence, double longitude, double latitude, int fragmentId) {
          this.id = id;
          this.sequence = sequence;
          this.longitude = longitude;
          this.latitude = latitude;
          this.fragmentId = fragmentId;
      }


      public Coordinate(int sequence, double longitude, double latitude) {
          this.sequence = sequence;
          this.latitude = latitude;
          this.longitude = longitude;
      }

    public void setId(int id) {
        this.id = id;
    }

    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getFragmentId() {
        return fragmentId;
    }

}
