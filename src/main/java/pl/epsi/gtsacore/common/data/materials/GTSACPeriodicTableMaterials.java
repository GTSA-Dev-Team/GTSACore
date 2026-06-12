package pl.epsi.gtsacore.common.data.materials;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.Element;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.IngotProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTElements;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import pl.epsi.gtsacore.GTSubatomicCore;

public class GTSACPeriodicTableMaterials {

    public static Material MALZZIUM;

    public static Material OG_PLASMA;

    public static void register() {
        MALZZIUM = new Material.Builder(GTSubatomicCore.id("malzzium"))
                .ingot()
                .color(0x023812)
                .iconSet(MaterialIconSet.FINE)
                .element(GTSACElements.MALZZIUM)
                .langValue("Malzzium")
                .buildAndRegister();

    }

    public static void modifyMaterials() {
        addIngotFluid(GTMaterials.Scandium);
        addIngotFluid(GTMaterials.Germanium);
        addIngotFluid(GTMaterials.Selenium);
        addIngotFluid(GTMaterials.Rubidium);
        addIngotFluid(GTMaterials.Strontium);
        addIngotFluid(GTMaterials.Zirconium);
        addIngotFluid(GTMaterials.Technetium);
        addIngotFluid(GTMaterials.Tellurium);
        addIngotFluid(GTMaterials.Praseodymium);
        addIngotFluid(GTMaterials.Promethium);
        addIngotFluid(GTMaterials.Gadolinium);
        addIngotFluid(GTMaterials.Terbium);
        addIngotFluid(GTMaterials.Dysprosium);
        addIngotFluid(GTMaterials.Holmium);
        addIngotFluid(GTMaterials.Erbium);
        addIngotFluid(GTMaterials.Thulium);
        addIngotFluid(GTMaterials.Ytterbium);
        addIngotFluid(GTMaterials.Hafnium);
        addIngotFluid(GTMaterials.Rhenium);
        addIngotFluid(GTMaterials.Thallium);
        addIngotFluid(GTMaterials.Polonium);
        addIngotFluid(GTMaterials.Astatine);
        addIngotFluid(GTMaterials.Francium);
        addIngotFluid(GTMaterials.Radium);
        addIngotFluid(GTMaterials.Actinium);
        addIngotFluid(GTMaterials.Protactinium);
        addIngotFluid(GTMaterials.Neptunium);
        addIngotFluid(GTMaterials.Curium);
        addIngotFluid(GTMaterials.Berkelium);
        addIngotFluid(GTMaterials.Californium);
        addIngotFluid(GTMaterials.Einsteinium);
        addIngotFluid(GTMaterials.Fermium);
        addIngotFluid(GTMaterials.Mendelevium);
        addIngotFluid(GTMaterials.Nobelium);
        addIngotFluid(GTMaterials.Lawrencium);
        addIngotFluid(GTMaterials.Rutherfordium);
        addIngotFluid(GTMaterials.Dubnium);
        addIngotFluid(GTMaterials.Seaborgium);
        addIngotFluid(GTMaterials.Bohrium);
        addIngotFluid(GTMaterials.Hassium);
        addIngotFluid(GTMaterials.Meitnerium);
        addIngotFluid(GTMaterials.Roentgenium);
        addIngotFluid(GTMaterials.Copernicium);
        addIngotFluid(GTMaterials.Nihonium);
        addIngotFluid(GTMaterials.Flerovium);
        addIngotFluid(GTMaterials.Moscovium);
        addIngotFluid(GTMaterials.Livermorium);
        addIngotFluid(GTMaterials.Tennessine);
        addVariants(GTMaterials.Oganesson, true, true, true, true);
    }



    private static void addIngotFluid(Material material) {
        addIngot(material);
        addFluid(material);
    }

    private static void addIngot(Material material) {
        material.setProperty(PropertyKey.INGOT, new IngotProperty());
    }

    private static void addFluid(Material material) {
        FluidProperty prop = new FluidProperty();

        prop.getStorage().enqueueRegistration(FluidStorageKeys.LIQUID, new FluidBuilder());
        material.setProperty(PropertyKey.FLUID, prop);
    }

    private static void addVariants(Material material, boolean ingot, boolean fluid, boolean gas, boolean plasma) {

        if (ingot) {material.setProperty(PropertyKey.INGOT, new IngotProperty());}

        FluidProperty prop = new FluidProperty();

        if (fluid) { prop.getStorage().enqueueRegistration(FluidStorageKeys.LIQUID, new FluidBuilder()); }
        if (gas) { prop.getStorage().enqueueRegistration(FluidStorageKeys.GAS, new FluidBuilder()); }
        if (plasma) { prop.getStorage().enqueueRegistration(FluidStorageKeys.PLASMA, new FluidBuilder()); }

        if (fluid || gas || plasma) { material.setProperty(PropertyKey.FLUID, prop); }

    }

}
