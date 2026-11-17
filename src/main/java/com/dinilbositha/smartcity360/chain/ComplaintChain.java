package com.dinilbositha.smartcity360.chain;

import com.dinilbositha.smartcity360.dto.ComplaintDTO;

/*
  Assembler and Manager for the Chain of Responsibility.
  Links: Local Staff -> Supervisor -> City Manager -> Emergency Director.
 */
public final class ComplaintChain {

    private static final ComplaintHandler CHAIN_ROOT;
    private static final ComplaintHandler LOCAL_STAFF;
    private static final ComplaintHandler SUPERVISOR;
    private static final ComplaintHandler CITY_MANAGER;
    private static final ComplaintHandler EMERGENCY_DIRECTOR;

    static {
        LOCAL_STAFF = new LocalStaffHandler();
        SUPERVISOR = new SupervisorHandler();
        CITY_MANAGER = new CityManagerHandler();
        EMERGENCY_DIRECTOR = new EmergencyDirectorHandler();

        // Build the Chain of Responsibility
        LOCAL_STAFF.setNext(SUPERVISOR)
                   .setNext(CITY_MANAGER)
                   .setNext(EMERGENCY_DIRECTOR);

        CHAIN_ROOT = LOCAL_STAFF;
    }

    private ComplaintChain() {}

    public static ComplaintHandler getChain() {
        return CHAIN_ROOT;
    }

    public static ComplaintHandler getHandlerById(long id) {
        if (id == 1L) return LOCAL_STAFF;
        if (id == 2L) return SUPERVISOR;
        if (id == 3L) return CITY_MANAGER;
        if (id == 4L) return EMERGENCY_DIRECTOR;
        return null;
    }

    public static ComplaintHandler route(ComplaintDTO complaint) {
        return CHAIN_ROOT.route(complaint);
    }
}
