package net.minecraftforge.lex.legacyjavafixer;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.base.Throwables;
import com.google.common.primitives.Ints;

import cpw.mods.fml.common.launcher.FMLInjectionAndSortingTweaker;
import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class LegacyJavaFixer implements ITweaker
{
	public LegacyJavaFixer()
	{
        @SuppressWarnings("unchecked")
        ListIterator<ITweaker> itr = ((List<ITweaker>)Launch.blackboard.get("Tweaks")).listIterator();
        ITweaker replacement = new SortReplacement();
        while (itr.hasNext())
        {
        	ITweaker t = itr.next();
        	FMLRelaunchLog.log.info("[LegacyJavaFixer] Tweaker: " + t);
        	if (t instanceof FMLInjectionAndSortingTweaker)
        	{
        		itr.set(replacement);
        		FMLRelaunchLog.info("[LegacyJavaFixer] Replacing tweaker %s with %s", t, replacement);
        	}
        }
	}

	public static class SortReplacement implements ITweaker
	{
		private boolean hasRun = false;
    	Class<?> wrapperCls = null;
    	Field wrapperField = null;
    	Map<String, Integer> tweakSorting = null;
    	
    	SortReplacement()
    	{
        	try 
        	{
        		wrapperCls = Class.forName("cpw.mods.fml.relauncher.CoreModManager$FMLPluginWrapper", false, SortReplacement.class.getClassLoader());
        		wrapperField = wrapperCls.getDeclaredField("sortIndex");
        		wrapperField.setAccessible(true);
        		tweakSorting = ReflectionHelper.getPrivateValue(CoreModManager.class, null, "tweakSorting");
        	}
        	catch (Exception e)
        	{
        		e.printStackTrace();
        		Throwables.propagate(e);
        	}
    	}
    	
		@Override
		public void injectIntoClassLoader(LaunchClassLoader classLoader)
		{
			if (!hasRun)
			{
				FMLRelaunchLog.log.info("[LegacyJavaFixer] Replacing sort");
				sort();
				URL is = FMLInjectionAndSortingTweaker.class.getResource("/cpw/mods/fml/common/launcher/TerminalTweaker.class");
				if (is != null)
				{
					FMLRelaunchLog.log.info("[LegacyJavaFixer] Detected TerminalTweaker");
		            @SuppressWarnings("unchecked")
		            List<String> newTweaks = (List<String>) Launch.blackboard.get("TweakClasses");
		            newTweaks.add("cpw.mods.fml.common.launcher.TerminalTweaker");
				}
			}
			hasRun = true;
		}
		//Copied from FML's fixed version.
        @SuppressWarnings("unchecked")
		private void sort()
		{
	        List<ITweaker> tweakers = (List<ITweaker>) Launch.blackboard.get("Tweaks");
	        // Basically a copy of Collections.sort pre 8u20, optimized as we know we're an array list.
	        // Thanks unhelpful fixer of http://bugs.java.com/view_bug.do?bug_id=8032636
	        ITweaker[] toSort = tweakers.toArray(new ITweaker[tweakers.size()]);
	        Arrays.sort(toSort, new Comparator<ITweaker>()
    		{
	            @Override
	            public int compare(ITweaker o1, ITweaker o2)
	            {
	                return Ints.saturatedCast((long)getIndex(o1) - (long)getIndex(o2));
	            }
	            private int getIndex(ITweaker t)
	            {
	            	try
	            	{
	            		if (t instanceof SortReplacement) return Integer.MIN_VALUE;
	            		if (wrapperCls.isInstance(t)) return wrapperField.getInt(t);
	            		if (tweakSorting.containsKey(t.getClass().getName())) return tweakSorting.get(t.getClass().getName());
	            	}
	            	catch (Exception e)
	            	{
	            		Throwables.propagate(e);
	            	}
            		return 0;
	            }
	        });
	        // Basically a copy of Collections.sort, optimized as we know we're an array list.
	        // Thanks unhelpful fixer of http://bugs.java.com/view_bug.do?bug_id=8032636
	        for (int j = 0; j < toSort.length; j++) {
	            tweakers.set(j, toSort[j]);
	        }
		}

		@Override public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile){}
		@Override public String[] getLaunchArguments() { return new String[0]; }
		@Override public String getLaunchTarget(){ return ""; }
	}



	//Blah
	@Override public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile){}
	@Override public String[] getLaunchArguments() { return new String[0]; }
	@Override public String getLaunchTarget(){ return ""; }
	@Override public void injectIntoClassLoader(LaunchClassLoader classLoader){}
}
