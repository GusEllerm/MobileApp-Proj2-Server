package server.database.enums;

public enum OrderType {

    NEWEST("newest", " ORDER BY map_id DESC"),
    OLDEST("oldest", " ORDER BY map_id ASC"),
    MOST_VOTES("most_votes", " ORDER BY votes DESC"),
    LEAST_VOTES("least_votes", " ORDER BY votes ASC");

    private final String type;
    private final String sql;

    OrderType(String type, String sql) {this.type = type; this.sql = sql;}

    public String getType() {return type;}

    public String getSql() {
        return sql;
    }

    public static OrderType getOrderTypeFromString(String string) {
        for (OrderType ot : OrderType.values()) {
            if (ot.getType().equalsIgnoreCase(string)) {
                return ot;
            }
        }
        return NEWEST;
    }


}
