package com.dinilbositha.smartcity360.chain;


 //Concrete Handler 4: Emergency Director
 //Handles critical emergencies and safety hazards (Severity 9 to 10).

public class EmergencyDirectorHandler extends ComplaintHandler {

    public EmergencyDirectorHandler() {
        super(4L, "Emergency Director", 9, 10);
    }
}
