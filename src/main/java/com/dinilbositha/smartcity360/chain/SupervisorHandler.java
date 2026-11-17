package com.dinilbositha.smartcity360.chain;


// Concrete Handler 2: District Supervisor
// Handles medium-severity issues or issues escalated from Local Staff (Severity 4 to 6).

public class SupervisorHandler extends ComplaintHandler {

    public SupervisorHandler() {
        super(2L, "Supervisor", 4, 6);
    }
}
