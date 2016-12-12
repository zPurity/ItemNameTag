package com.SapphireDesigns.ItemNameTag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

	private ArrayList<Player> inNaming = new ArrayList<>();

	@Override
	public void onEnable() {

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		Bukkit.getPluginManager().registerEvents(this, this);

		Bukkit.getConsoleSender().sendMessage("§3Item§bName§3Tag §aHas been enabled!");

	}

	private boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("ItemNameTag")) {

			if (sender.hasPermission("ItemNameTag.Command")) {

				if (sender instanceof Player) {
					Player p = (Player) sender;

					if (args.length == 0) {
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("Main.help1")));
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("Main.help2")));
						return true;
					}

					if (args.length == 1) {

						Player t = Bukkit.getPlayer(args[0]);

						if (t != null) {
							p.sendMessage(
									ChatColor.translateAlternateColorCodes('&', getConfig().getString("Main.help2")));
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									getConfig().getString("Messages.Player-Offline").replace("%player%", args[0])));
						}
						return true;
					}

					if (args.length == 2) {

						Player t = Bukkit.getPlayer(args[0]);
						int amt = Integer.parseInt(args[1]);
						
						if (t != null) {

							if (isInt(args[1])) {

								for (int i = 0; i < amt; i++) {
									t.getInventory().addItem(renameTag());
								}

								t.sendMessage(ChatColor.translateAlternateColorCodes('&',
										getConfig().getString("Messages.Receive").replace("%sender%", p.getName())
												.replace("%amount%", args[1])));
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										getConfig().getString("Messages.Sender").replace("%amount%", args[1])
												.replace("%target%", args[0])));
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										getConfig().getString("Messages.Invalid-Number")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									getConfig().getString("Messages.Player-Offline").replace("%player%", args[0])));
						}

						return true;

					}

				} else {

					if (args.length == 0) {
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("Main.help1")));
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', getConfig().getString("Main.help2")));
						return true;
					}

					if (args.length == 1) {

						Player t = Bukkit.getPlayer(args[0]);

						if (t != null) {
							sender.sendMessage(
									ChatColor.translateAlternateColorCodes('&', getConfig().getString("Main.help2")));
						} else {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
									getConfig().getString("Messages.Player-Offline").replace("%player%", args[0])));
						}
						return true;
					}

					if (args.length == 2) {

						Player t = Bukkit.getPlayer(args[0]);
						int amt = Integer.parseInt(args[1]);

						if (t != null) {

							if (isInt(args[1])) {

								for (int i = 0; i < amt; i++) {
									t.getInventory().addItem(renameTag());
								}

							} else {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
										getConfig().getString("Messages.Invalid-Number")));
							}
						} else {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
									getConfig().getString("Messages.Player-Offline").replace("%player%", args[0])));
						}
						return true;

					}
				}
			} else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						getConfig().getString("Messages.No-Permission-Command")));
			}
		}

		return true;
	}

	private ItemStack renameTag() {

		ItemStack tag = new ItemStack(Material.getMaterial(getConfig().getString("ItemNameTag.Item")), 1,
				(short) getConfig().getInt("ItemNameTag.Item-Data"));
		ItemMeta tagmeta = tag.getItemMeta();
		tagmeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("ItemNameTag.Name")));
		ArrayList<String> lore = new ArrayList<>();
		for (String all : getConfig().getStringList("ItemNameTag.Lore")) {
			lore.add(ChatColor.translateAlternateColorCodes('&', all));
		}
		tagmeta.setLore(lore);
		tag.setItemMeta(tagmeta);

		return tag;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {

		Player p = e.getPlayer();

		if (inNaming.contains(p)) {
			p.getInventory().addItem(renameTag());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {

		Player p = e.getPlayer();

		if (e.getMessage().contains(getConfig().getString("Cancel"))) {

			if (inNaming.contains(p)) {
				e.setCancelled(true);
				inNaming.remove(p);

				for (String all : getConfig().getStringList("Messages.Cancel-Message")) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', all));
				}

				p.getInventory().addItem(renameTag());

			}

		} else if (inNaming.contains(p)) {

			e.setCancelled(true);
			
			List<String> blockedItems = getConfig().getStringList("Blocked-Items");
			
			if (p.getItemInHand().getType().equals(Material.AIR)) {

				p.sendMessage(
						ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.Nothing-In-Hand")));

			} 
			
			else if(blockedItems.contains(p.getItemInHand().getType().name().toString())) {
				
				p.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cYour not allowed to rename that item!"));
				
			}
			
			else {

				ItemStack is = p.getItemInHand();
				ItemMeta ismeta = is.getItemMeta();
				is.setItemMeta(ismeta);
				p.updateInventory();

				String item = is.getType().toString();

				inNaming.remove(p);

				p.sendMessage(
						ChatColor.translateAlternateColorCodes('&', getConfig().getString("Messages.Renamed-Message"))
								.replace("%item%", item).replace("%name%", ChatColor.translateAlternateColorCodes('&', e.getMessage())));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e) {

		Player p = e.getPlayer();

		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (p.getItemInHand().isSimilar(renameTag())) {

				if (p.hasPermission("ItemNameTag.Use")) {

					if (inNaming.contains(p)) {
						e.setCancelled(true);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								getConfig().getString("Messages.Finish-Renaming-Before-Use")));
					} else {

						if (p.getItemInHand().getAmount() > 1) {
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);

							for (String all : getConfig().getStringList("Messages.Activate-Message")) {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', all));
							}

						} else if (p.getItemInHand().getAmount() == 1) {
							p.setItemInHand(null);

							for (String all : getConfig().getStringList("Messages.Activate-Message")) {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', all));
							}
						}

						inNaming.add(p);
					}

				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',
							getConfig().getString("Messages.No-Permission-Use")));
				}

			} else {
				return;
			}
		}
	}

	@Override
	public void onDisable() {

		Bukkit.getConsoleSender().sendMessage("§3Item§bName§3Tag §aHas been disabled!");
	}
}
