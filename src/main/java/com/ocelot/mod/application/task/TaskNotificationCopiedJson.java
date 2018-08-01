package com.ocelot.mod.application.task;

import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.api.task.Task;
import com.ocelot.mod.Mod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class TaskNotificationCopiedJson extends Task {

	public TaskNotificationCopiedJson() {
		super(Mod.MOD_ID + ".notification_copy_json");
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) {
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			Notification notification = new Notification(Icons.EXPORT, TextFormatting.BOLD + "Export", "Json copied to Clipboard");
			notification.pushTo((EntityPlayerMP) player);
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) {
	}

	@Override
	public void processResponse(NBTTagCompound nbt) {
	}
}