package net.minecraftforge.legacyjavafixer.sort;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;

import java.util.List;
import java.util.Map;

/**
 * Used as a dummy plugin to add our cascading tweaker.
 * This allows us to provide this tweaker via the '-Dfml.coreMods.load' system property.
 * <p>
 * Created by covers1624 on 1/6/21.
 */
@IFMLLoadingPlugin.SortingIndex (-2147483648)
public class LegacyJavaSortDummyLoadingPlugin implements IFMLLoadingPlugin {

    public LegacyJavaSortDummyLoadingPlugin() {
        @SuppressWarnings ("unchecked")
        List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");
        // Add our tweaker to LaunchWrapper, this is done the same as FMLTweaker
        tweakClasses.add("net.minecraftforge.legacyjavafixer.sort.LegacyJavaSortTweaker");
    }

    //@formatter:off
    public String[] getLibraryRequestClass() { return new String[0]; } // 1.6.4 and bellow support.
    @Override public String[] getASMTransformerClass() { return new String[0]; }
    @Override public String getModContainerClass() { return null; }
    @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> map) { }
    @Override public String getAccessTransformerClass() { return null; }
    //@formatter:on
}
