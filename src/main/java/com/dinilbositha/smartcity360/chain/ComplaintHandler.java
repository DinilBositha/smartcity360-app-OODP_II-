package com.dinilbositha.smartcity360.chain;

import com.dinilbositha.smartcity360.dto.ComplaintDTO;

/*
  Abstract base handler for the Chain of Responsibility complaint escalation pipeline.
  Represents a handler in the complaint escalation hierarchy.
 */
public abstract class ComplaintHandler {
    protected ComplaintHandler nextHandler;
    protected final long handlerId;
    protected final String handlerName;
    protected final int minSeverity;
    protected final int maxSeverity;

    public ComplaintHandler(long handlerId, String handlerName, int minSeverity, int maxSeverity) {
        this.handlerId = handlerId;
        this.handlerName = handlerName;
        this.minSeverity = minSeverity;
        this.maxSeverity = maxSeverity;
    }

    public ComplaintHandler setNext(ComplaintHandler next) {
        this.nextHandler = next;
        return next;
    }

    public ComplaintHandler getNext() {
        return nextHandler;
    }

    public long getHandlerId() {
        return handlerId;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public int getMinSeverity() {
        return minSeverity;
    }

    public int getMaxSeverity() {
        return maxSeverity;
    }

    public boolean canHandle(int severity) {
        return severity >= minSeverity && severity <= maxSeverity;
    }


      //Routes the complaint through the chain based on severity.

    public ComplaintHandler route(ComplaintDTO complaint) {
        if (canHandle(complaint.getSeverity())) {
            complaint.setAssignedHandlerId(this.handlerId);
            complaint.setHandlerName(this.handlerName);
            return this;
        }
        if (nextHandler != null) {
            return nextHandler.route(complaint);
        }
        complaint.setAssignedHandlerId(this.handlerId);
        complaint.setHandlerName(this.handlerName);
        return this;
    }


     // Escalates the complaint to the next authority in the chain.

    public ComplaintHandler escalate(ComplaintDTO complaint) {
        if (nextHandler != null) {
            complaint.setAssignedHandlerId(nextHandler.getHandlerId());
            complaint.setHandlerName(nextHandler.getHandlerName());
            complaint.setStatus("escalated");
            return nextHandler;
        }
        return null;
    }
}
