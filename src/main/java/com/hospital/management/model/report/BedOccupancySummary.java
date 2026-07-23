package com.hospital.management.model.report;
import java.math.BigDecimal;
/** Immutable operational bed totals grouped by room type. */
public final class BedOccupancySummary {
    private final String roomType; private final long totalBeds,availableBeds,occupiedBeds,maintenanceBeds;
    private final BigDecimal occupancyPercentage;
    public BedOccupancySummary(String roomType,long totalBeds,long availableBeds,long occupiedBeds,long maintenanceBeds,BigDecimal occupancyPercentage){
        this.roomType=roomType;this.totalBeds=totalBeds;this.availableBeds=availableBeds;this.occupiedBeds=occupiedBeds;
        this.maintenanceBeds=maintenanceBeds;this.occupancyPercentage=occupancyPercentage==null?BigDecimal.ZERO.setScale(2):occupancyPercentage.setScale(2);
    }
    public String getRoomType(){return roomType;} public long getTotalBeds(){return totalBeds;}
    public long getAvailableBeds(){return availableBeds;} public long getOccupiedBeds(){return occupiedBeds;}
    public long getMaintenanceBeds(){return maintenanceBeds;} public BigDecimal getOccupancyPercentage(){return occupancyPercentage;}
}
