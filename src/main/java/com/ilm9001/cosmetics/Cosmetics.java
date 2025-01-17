package com.ilm9001.cosmetics;

import com.ilm9001.cosmetics.commands.GiveCosmetic;
import com.ilm9001.cosmetics.commands.GiveCosmeticTabComplete;
import com.ilm9001.cosmetics.commands.RefreshCosmeticsList;
import com.ilm9001.cosmetics.listeners.ArmorSlotClickListener;
import com.ilm9001.cosmetics.listeners.RightClickEventListener;
import com.ilm9001.cosmetics.summon.CosmeticFactory;
import com.ilm9001.cosmetics.util.Cosmetic;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Cosmetics extends JavaPlugin {
   private static Cosmetics instance;
   private static CosmeticFactory cosmeticFactory;
   private static List<Cosmetic> cosmeticList;
   
   private static FileConfiguration cosmetics;
   private static File cosmeticsFile;
   
   // Static abuse? No, Static abusage.
   
   @Override
   public void onEnable() {
      instance = this;
      
      this.saveDefaultConfig();
      this.createCosmetics();
      
      if(cosmeticFactory == null) {
         cosmeticFactory = new CosmeticFactory();
         cosmeticList = cosmeticFactory.getCosmeticsFromConfig();
      }
      int pluginId = 12993;
      Metrics metrics = new Metrics(this,pluginId);
      
      // probably a good idea to eventually move these elsewhere to work as a method
      this.getCommand("givecosmetic").setExecutor(new GiveCosmetic());
      this.getCommand("givecosmetic").setTabCompleter(new GiveCosmeticTabComplete());
      this.getCommand("refreshcosmetics").setExecutor(new RefreshCosmeticsList());
      getServer().getPluginManager().registerEvents(new RightClickEventListener(), this);
      getServer().getPluginManager().registerEvents(new ArmorSlotClickListener(), this);
   }
   
   @Override
   public void onDisable() {
   }
   
   public static JavaPlugin getInstance() { return instance; }
   public static CosmeticFactory getCosmeticFactory() { return cosmeticFactory; }
   
   /**
    * Get cached List of Cosmetic's. Use CosmeticFactory#getCosmeticsFromConfig() for a non-cached list!
    * @return List of cached (read from config at load) Cosmetic's
    */
   public static List<Cosmetic> getCachedCosmeticList() { return cosmeticList; }
   
   /**
    * Called from CosmeticFactory#getCosmeticsFromConfig(), refreshes cache and updates cosmetic list if new ones are found
    * in the config!
    * @param cList List of Cosmetic's from CosmeticFactory#getCosmeticsFromConfig()
    */
   public static void setCachedCosmeticList(List<Cosmetic> cList) { cosmeticList = cList;}
   
   /**
    * Return cosmetics.yml FileConfiguration.
    *
    * @return FileConfiguration of cosmetics.yml
    */
   public static FileConfiguration getCosmetics() {
      return cosmetics;
   }
   
   /**
    * Reloads cosmetics file for use in CosmeticFactory.
    */
   public static void refreshCosmetics() {
      try {
         cosmetics.load(cosmeticsFile);
      } catch (IOException | InvalidConfigurationException e) {
         e.printStackTrace();
      }
   }
   
   // https://www.spigotmc.org/wiki/config-files/
   private void createCosmetics() {
      cosmeticsFile = new File(getDataFolder(), "cosmetics.yml");
      if (!cosmeticsFile.exists()) {
         cosmeticsFile.getParentFile().mkdirs();
         saveResource("cosmetics.yml", false);
      }
      
      cosmetics = new YamlConfiguration();
      try {
         cosmetics.load(cosmeticsFile);
      } catch (IOException | InvalidConfigurationException e) {
         e.printStackTrace();
      }
   }
   
}
