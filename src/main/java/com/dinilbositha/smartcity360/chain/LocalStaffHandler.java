package com.dinilbositha.smartcity360.chain;


 //Concrete Handler 1: Front-line Local Staff
 //Handles basic, low-severity complaints (Severity 1 to 3).

public class LocalStaffHandler extends ComplaintHandler {

    public LocalStaffHandler() {
        super(1L, "Local Staff", 1, 3);
    }
}
