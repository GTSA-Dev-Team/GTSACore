package pl.epsi.gtsacore.integration.jei.element;

import pl.epsi.gtsacore.util.ChemistryUtil;
import pl.epsi.gtsacore.util.Option;

public record ElementInfo(String name, Option<String> isotopeName, int electronCount, int protonCount, int neutronCount, int orbitalCount) {

    // All
    public ElementInfo(String name, String isotopeName, int electronCount, int protonCount, int neutronCount) {
        this(name, Option.some(isotopeName), electronCount, protonCount, neutronCount, ChemistryUtil.getNumOrbitals(electronCount));
    }

    // No isotope
    public ElementInfo(String name, int electronCount, int protonCount, int neutronCount) {
        this(name, Option.none(), electronCount, protonCount, neutronCount, ChemistryUtil.getNumOrbitals(electronCount));
    }

    // No proton count, isotope
    public ElementInfo(String name, String isotopeName, int electronCount, int neutronCount) {
        this(name, Option.some(isotopeName), electronCount, electronCount, neutronCount, ChemistryUtil.getNumOrbitals(electronCount));
    }

    // No proton count, no isotope
    public ElementInfo(String name, int electronCount, int neutronCount) {
        this(name, Option.none(), electronCount, electronCount, neutronCount, ChemistryUtil.getNumOrbitals(electronCount));
    }

}
