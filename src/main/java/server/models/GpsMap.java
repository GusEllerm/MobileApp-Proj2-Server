package server.models;


import java.util.List;

public class GpsMap {

    private int id;
    private String mapType;
    private int fragmentAmount;
    private String mapTitle;
    private String category;
    private int votes;
    private String imageData;
    private List<Fragment> fragments;

    /**
     * Initializer
     * @param id the id of the gps map in the database
     * @param mapType the map type
     * @param fragmentAmount the amount of fragments in the fragments list
     * @param mapTitle the map title
     * @param category the type of category
     * @param votes the number of votes for the image / map
     */
    public GpsMap(int id, String mapType, int fragmentAmount, String mapTitle, String category, int votes, String imageData) {
        this.id = id;
        this.mapType = mapType;
        this.fragmentAmount = fragmentAmount;
        this.mapTitle = mapTitle;
        this.category = category;
        this.votes = votes;
        this.imageData = imageData;
    }

    public GpsMap(String mapType, int fragmentAmount, String mapTitle, String category, int votes, List<Fragment> fragments) {
        this.mapType = mapType;
        this.fragmentAmount = fragmentAmount;
        this.mapTitle = mapTitle;
        this.category = category;
        this.votes = votes;
        this.fragments = fragments;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public int getFragmentAmount() {
        return fragmentAmount;
    }

    public int getVotes() {
        return votes;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public String getMapTitle() {
        return mapTitle;
    }

    public String getMapType() {
        return mapType;
    }

    public String getImageDataAsString() {
        if (imageData == null) return "";
        else return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setFragmentAmount(int fragmentAmount) {
        this.fragmentAmount = fragmentAmount;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public void setMapTitle(String mapTitle) {
        this.mapTitle = mapTitle;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
