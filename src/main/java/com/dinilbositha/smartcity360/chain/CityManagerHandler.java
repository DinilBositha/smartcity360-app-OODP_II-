package com.dinilbositha.smartcity360.chain;

/*
 Concrete Handler 3: City Manager
 Handles high-severity civic issues or issues escalated from Supervisors (Severity 7 to 8).
*/
public class CityManagerHandler extends ComplaintHandler {

    public CityManagerHandler() {
        super(3L, "City Manager", 7, 8);
    }
}
