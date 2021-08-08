package com.ilm9001.cosmetics.listeners;

import com.ilm9001.cosmetics.Cosmetics;
import com.ilm9001.cosmetics.util.Cosmetic;
import com.ilm9001.cosmetics.util.CosmeticType;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class ArmorSlotClickListener implements Listener {
   
   @EventHandler
   public void onInventoryClick(InventoryClickEvent e) {
      Inventory inv = e.getClickedInventory();
      HumanEntity player = e.getWhoClicked();
      
      if(e.getSlotType() == InventoryType.SlotType.ARMOR && inv != null) { //only apply to armor slots
         ItemStack itemInSlot = e.getCurrentItem();
         ItemStack itemForSlot = e.getCursor();
         NamespacedKey key = new NamespacedKey(Cosmetics.getInstance(), "cosmetic-type");
         PersistentDataContainer metaContainer;
   
         if (itemForSlot != null && itemForSlot.hasItemMeta()) {
            metaContainer = itemForSlot.getItemMeta().getPersistentDataContainer();
         } else return;
         
         Byte type = metaContainer.get(key, PersistentDataType.BYTE);
         CosmeticType cosmeticType;
         
         if(type != null) {
            Optional<CosmeticType> optional = CosmeticType.getFromID(type);
            cosmeticType = optional.orElse(null); //autogenerated suggestion, "If a value is present, returns the value, otherwise returns other."
         } else return;
         
         if(cosmeticType == Cosmetic.getCosmeticFromItemStack(itemForSlot).getType() && e.getSlot() == 36+cosmeticType.getID()) {
            /*verify that item has metadata indicating cosmetic-type
             and that the cosmetic-type matches armor slot & slot type
             */
            if(itemInSlot == null || itemInSlot.equals(new ItemStack(Material.AIR))) {
               player.setItemOnCursor(null);
               Bukkit.getScheduler().runTask(Cosmetics.getInstance(), () -> inv.setItem(e.getSlot(), itemForSlot));
            } else {
               Bukkit.getScheduler().runTask(Cosmetics.getInstance(), () -> inv.setItem(e.getSlot(), itemForSlot));
               Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), () -> player.setItemOnCursor(itemInSlot),1);
            }
         }
      }
   }
   
   @EventHandler
   public void onCreativeInventoryClick(InventoryCreativeEvent e) {
      InventoryClickEvent event = new InventoryClickEvent(e.getView(),e.getSlotType(),e.getSlot(),e.getClick(),e.getAction());
      //lazy way, but less repetitive code i guess
      Bukkit.getServer().getPluginManager().callEvent(event);
   }
}
