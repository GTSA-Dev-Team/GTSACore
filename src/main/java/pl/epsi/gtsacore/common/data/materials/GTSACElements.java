package pl.epsi.gtsacore.common.data.materials;

import com.gregtechceu.gtceu.api.data.chemical.Element;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

public class GTSACElements {

    public static Element MALZZIUM;

    public static void init() {
        MALZZIUM = create("malzzium", 122, 167, "Mz");
    }

    private static Element create(String name, long protons, long neutrons, String symbol) {
        return create(name, protons, neutrons, -1L, (String) null, name, symbol, false);
    }

    private static Element create(String id, long protons, long neutrons, long halfLife, String decayTo, String name,
                                  String symbol, boolean isIsotope) {
        Element element = new Element(protons, neutrons, halfLife, decayTo, name, symbol, isIsotope);
        GTRegistries.ELEMENTS.register(id, element);
        return element;
    }
}
