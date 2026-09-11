package pl.epsi.gtsacore.util;

public class ChemistryUtil {

    public static int getNumOrbitals(int electronCount) {
        int num = electronCount;
        int orbitalCounter = 0;
        while (num > 0) {
            orbitalCounter++;
            int value = getOrbitalSlots(orbitalCounter);
            num -= value;
        }

        return orbitalCounter;
    }

    public static int getOrbitalSlots(int orbital) {
        return 2 * orbital * orbital;
    }

}
