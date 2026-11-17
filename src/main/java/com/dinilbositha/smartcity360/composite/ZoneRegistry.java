package com.dinilbositha.smartcity360.composite;


 // Factory providing the default composite topology of Metro City.

public final class ZoneRegistry {

    private ZoneRegistry() {}

    public static ZoneComposite createDefaultCity() {
        ZoneComposite city = new ZoneComposite("Metro City", "City");

        ZoneComposite north = new ZoneComposite("North District", "District");
        ZoneComposite northSt1 = new ZoneComposite("Commercial Avenue", "Street");
        northSt1.add(new BuildingLeaf("Central Mall", 450.0));
        northSt1.add(new BuildingLeaf("Grand Plaza Tower", 720.0));

        ZoneComposite northSt2 = new ZoneComposite("Tech Corridor", "Street");
        northSt2.add(new BuildingLeaf("Innovation Center", 310.0));
        northSt2.add(new BuildingLeaf("Data Center Alpha", 850.0));

        north.add(northSt1);
        north.add(northSt2);

        ZoneComposite south = new ZoneComposite("South District", "District");
        ZoneComposite southSt1 = new ZoneComposite("Harbor Way", "Street");
        southSt1.add(new BuildingLeaf("Port Logistics Hub", 620.0));
        southSt1.add(new BuildingLeaf("Customs Terminal", 180.0));

        south.add(southSt1);

        city.add(north);
        city.add(south);

        return city;
    }
}
