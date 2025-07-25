package com.totemfletching;

import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;

class EntTrail {
    private final GameObject tileObject;
    private final ObjectComposition composition;
    private final String name;

    public EntTrail(GameObject tileObject, ObjectComposition composition, String name) {
        this.tileObject = tileObject;
        this.composition = composition;
        this.name = name;
    }

    public GameObject getTileObject() {
        return tileObject;
    }

    public ObjectComposition getComposition() {
        return composition;
    }

    public String getName() {
        return name;
    }
}
