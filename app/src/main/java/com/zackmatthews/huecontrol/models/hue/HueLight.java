package com.zackmatthews.huecontrol.models.hue;

/**
 * Created by zachmathews on 12/22/16.
 */
public class HueLight {

    private int id;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    private String type;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    private boolean isEnabled = true;

    public boolean isEnabled() { return isEnabled; }
    public void setIsEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }
}
