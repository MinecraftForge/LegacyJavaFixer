package net.minecraftforge.legacyjavafixer.sort;

import cpw.mods.fml.relauncher.ServerLaunchWrapper;

/**
 * Created by covers1624 on 1/6/21.
 */
public class ServerWrapper extends ClientWrapper {

    public static void main(String[] args) {
        injectDummyCoreMod();
        ServerLaunchWrapper.main(args);
    }
}
