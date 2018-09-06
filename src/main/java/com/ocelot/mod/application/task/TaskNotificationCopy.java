package com.ocelot.mod.application.task;

import com.mrcrayfish.device.api.app.Alphabet;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.api.task.Task;
import com.ocelot.mod.ModelCreator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class TaskNotificationCopy extends Task {

	private boolean valid;
	private IIcon icon;
	private String title;
	private String subTitle;

	public TaskNotificationCopy() {
		super(ModelCreator.MOD_ID + ".notification_copy");
	}

	public TaskNotificationCopy(String title, IIcon icon) {
		this(title, null, icon);
	}

	public TaskNotificationCopy(String title, String subTitle, IIcon icon) {
		super(ModelCreator.MOD_ID + ".notification_copy");
		this.valid = true;
		this.title = title;
		this.subTitle = subTitle;
		this.icon = icon;
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) {
		if (this.valid) {
			nbt.setString("title", this.title);
			if (this.subTitle != null) {
				nbt.setString("subTitle", this.subTitle);
			}

			NBTTagCompound tagIcon = new NBTTagCompound();
			tagIcon.setInteger("ordinal", icon.getOrdinal());
			tagIcon.setString("className", icon.getClass().getName());
			nbt.setTag("icon", tagIcon);
		}
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) {
		if (!nbt.hasKey("title", Constants.NBT.TAG_STRING))
			return;

		String title = nbt.getString("title");
		String subTitle = nbt.getString("subTitle");
		IIcon icon = Alphabet.QUESTION_MARK;

		{
			int ordinal = nbt.getCompoundTag("icon").getInteger("ordinal");
			String className = nbt.getCompoundTag("icon").getString("className");

			try {
				icon = (IIcon) Class.forName(className).getEnumConstants()[ordinal];
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (player instanceof EntityPlayerMP) {
			Notification notification = new Notification(icon, title, subTitle);
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