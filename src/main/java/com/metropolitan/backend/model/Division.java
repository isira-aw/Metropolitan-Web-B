package com.metropolitan.backend.model;

public enum Division {
    CENTRAL_AC("Central AC"),
    ELEVATORS_AND_TRAVELATORS("Elevators and Travelators"),
    FIRE_DETECTION_AND_PROTECTION("Fire Detection & Protection"),
    GENERATOR("Generator"),
    SOLAR("Solar"),
    ELV("ELV");

    private final String displayName;

    Division(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Division fromDisplayName(String displayName) {
        for (Division division : Division.values()) {
            if (division.getDisplayName().equals(displayName)) {
                return division;
            }
        }
        return null;
    }
}
