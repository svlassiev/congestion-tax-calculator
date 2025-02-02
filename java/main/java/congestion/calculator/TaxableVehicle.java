package congestion.calculator;

/**
 * Vehicle contains different types of transport and contains information about toll taxation
 */
public enum TaxableVehicle {
    EMERGENCY(true),
    BUS(true),
    DIPLOMAT(true),
    MOTORCYCLE(true),
    MILITARY(true),
    FOREIGN(true),
    CAR(false),
    TRACTOR(false),
    UNKNOWN(false);

    private boolean tollFree;
    TaxableVehicle(boolean tollFree) {
        this.tollFree = tollFree;
    }

    public static TaxableVehicle getVehicle(String vehicle) {
        switch (vehicle.toUpperCase()) {
            case "MOTORCYCLE":
                return MOTORCYCLE;
            case "TRACTOR":
                return TRACTOR;
            case "EMERGENCY":
                return EMERGENCY;
            case "DIPLOMAT":
                return DIPLOMAT;
            case "FOREIGN":
                return FOREIGN;
            case "MILITARY":
                return MILITARY;
            case "BUS":
                return BUS;
            case "CAR":
                return CAR;
            default:
                return UNKNOWN;
        }
    }

    public boolean isTollFree() {
        return tollFree;
    }
}

