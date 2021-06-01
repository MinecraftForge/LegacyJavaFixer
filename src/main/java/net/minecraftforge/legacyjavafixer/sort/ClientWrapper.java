package net.minecraftforge.legacyjavafixer.sort;

import net.minecraft.launchwrapper.Launch;

/**
 * Created by covers1624 on 1/6/21.
 */
public class ClientWrapper {

    private static final String FML_COREMODS_SYS_PROP = "fml.coreMods.load";
    private static final String DUMMY_PLUGIN = "net.minecraftforge.legacyjavafixer.sort.LegacyJavaSortDummyLoadingPlugin";

    public static void main(String[] args) {
        injectDummyCoreMod();
        Launch.main(args);
    }

    protected static void injectDummyCoreMod() {
        String coreMods = System.getProperty(FML_COREMODS_SYS_PROP, ""); // Grab the existing sys prop.
        if (!coreMods.isEmpty()) {
            coreMods = DUMMY_PLUGIN + "," + coreMods; // Coremods were provided already, append to the beginning.
        } else {
            coreMods = DUMMY_PLUGIN;
        }
        //Set the sys prop back.
        System.setProperty(FML_COREMODS_SYS_PROP, coreMods);
    }
}
