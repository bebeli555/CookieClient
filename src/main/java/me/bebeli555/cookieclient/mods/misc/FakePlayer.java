package me.bebeli555.cookieclient.mods.misc;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.gui.Group;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class FakePlayer extends Mod {
	public static EntityOtherPlayerMP fakePlayer;
	
	public FakePlayer() {
		super(Group.MISC, "FakePlayer", "Creates a fakeplayer at ur location");
	}
	
	@Override
	public void onEnabled() {
		if (mc.player == null) {
			disable();
			return;
		}
		
		fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("6ab32213-179a-4c41-8ab9-66789121e051"), "bebeli555"));
		fakePlayer.copyLocationAndAnglesFrom(mc.player);
		fakePlayer.rotationYawHead = mc.player.rotationYawHead;
		mc.world.addEntityToWorld(-100, fakePlayer);
	}
	
	@Override
	public void onDisabled() {
		if (mc.world != null && fakePlayer != null) {
			mc.world.removeEntity(fakePlayer);
		}
	}
}
