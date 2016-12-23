package com.zackmatthews.huecontrol.models.hue;

/**
 * Created by zachmathews on 12/22/16.
 */
public class HueLight {
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private int id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
