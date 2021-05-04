package me.bebeli555.cookieclient.mods.misc;

import me.bebeli555.cookieclient.Mod;
import me.bebeli555.cookieclient.events.bus.EventHandler;
import me.bebeli555.cookieclient.events.bus.Listener;
import me.bebeli555.cookieclient.events.other.PacketEvent;
import me.bebeli555.cookieclient.gui.Group;
import me.bebeli555.cookieclient.gui.Mode;
import me.bebeli555.cookieclient.gui.Setting;
import me.bebeli555.cookieclient.utils.Timer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;

//Auto generated code mostly
public class PacketCanceller extends Mod {
	private static Timer timer = new Timer();
	
	public static Setting serverPackets = new Setting(Mode.LABEL, "ServerPackets", true, "Server packets (S)", "Cancelling these will make the client not receive them");
		public static Setting sPacketAdvancementInfo = new Setting(serverPackets, Mode.BOOLEAN, "SPacketAdvancementInfo" , false);
		public static Setting sPacketAnimation = new Setting(serverPackets, Mode.BOOLEAN, "SPacketAnimation" , false);
		public static Setting sPacketBlockAction = new Setting(serverPackets, Mode.BOOLEAN, "SPacketBlockAction" , false);
		public static Setting sPacketBlockBreakAnim = new Setting(serverPackets, Mode.BOOLEAN, "SPacketBlockBreakAnim" , false);
		public static Setting sPacketBlockChange = new Setting(serverPackets, Mode.BOOLEAN, "SPacketBlockChange" , false);
		public static Setting sPacketCamera = new Setting(serverPackets, Mode.BOOLEAN, "SPacketCamera" , false);
		public static Setting sPacketChangeGameState = new Setting(serverPackets, Mode.BOOLEAN, "SPacketChangeGameState" , false);
		public static Setting sPacketChat = new Setting(serverPackets, Mode.BOOLEAN, "SPacketChat" , false);
		public static Setting sPacketChunkData = new Setting(serverPackets, Mode.BOOLEAN, "SPacketChunkData" , false);
		public static Setting sPacketCloseWindow = new Setting(serverPackets, Mode.BOOLEAN, "SPacketCloseWindow" , false);
		public static Setting sPacketCollectItem = new Setting(serverPackets, Mode.BOOLEAN, "SPacketCollectItem" , false);
		public static Setting sPacketCombatEvent = new Setting(serverPackets, Mode.BOOLEAN, "SPacketCombatEvent" , false);
		public static Setting sPacketConfirmTransaction = new Setting(serverPackets, Mode.BOOLEAN, "SPacketConfirmTransaction" , false);
		public static Setting sPacketCooldown = new Setting(serverPackets, Mode.BOOLEAN, "SPacketCooldown" , false);
		public static Setting sPacketCustomPayload = new Setting(serverPackets, Mode.BOOLEAN, "SPacketCustomPayload" , false);
		public static Setting sPacketCustomSound = new Setting(serverPackets, Mode.BOOLEAN, "SPacketCustomSound" , false);
		public static Setting sPacketDestroyEntities = new Setting(serverPackets, Mode.BOOLEAN, "SPacketDestroyEntities" , false);
		public static Setting sPacketDisconnect = new Setting(serverPackets, Mode.BOOLEAN, "SPacketDisconnect" , false);
		public static Setting sPacketDisplayObjective = new Setting(serverPackets, Mode.BOOLEAN, "SPacketDisplayObjective" , false);
		public static Setting sPacketEffect = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEffect" , false);
		public static Setting sPacketEntity = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntity" , false);
			public static Setting s15PacketEntityRelMove = new Setting(sPacketEntity, Mode.BOOLEAN, "S15PacketEntityRelMove" , false);
			public static Setting s16PacketEntityLook = new Setting(sPacketEntity, Mode.BOOLEAN, "S16PacketEntityLook" , false);
			public static Setting s17PacketEntityLookMove = new Setting(sPacketEntity, Mode.BOOLEAN, "S17PacketEntityLookMove" , false);
		public static Setting sPacketEntityAttach = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntityAttach" , false);
		public static Setting sPacketEntityEffect = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntityEffect" , false);
		public static Setting sPacketEntityEquipment = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntityEquipment" , false);
		public static Setting sPacketEntityHeadLook = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntityHeadLook" , false);
		public static Setting sPacketEntityMetadata = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntityMetadata" , false);
		public static Setting sPacketEntityProperties = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntityProperties" , false);
		public static Setting sPacketEntityStatus = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntityStatus" , false);
		public static Setting sPacketEntityTeleport = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntityTeleport" , false);
		public static Setting sPacketEntityVelocity = new Setting(serverPackets, Mode.BOOLEAN, "SPacketEntityVelocity" , false);
		public static Setting sPacketExplosion = new Setting(serverPackets, Mode.BOOLEAN, "SPacketExplosion" , false);
		public static Setting sPacketHeldItemChange = new Setting(serverPackets, Mode.BOOLEAN, "SPacketHeldItemChange" , false);
		public static Setting sPacketJoinGame = new Setting(serverPackets, Mode.BOOLEAN, "SPacketJoinGame" , false);
		public static Setting sPacketKeepAlive = new Setting(serverPackets, Mode.BOOLEAN, "SPacketKeepAlive" , false);
		public static Setting sPacketMaps = new Setting(serverPackets, Mode.BOOLEAN, "SPacketMaps" , false);
		public static Setting sPacketMoveVehicle = new Setting(serverPackets, Mode.BOOLEAN, "SPacketMoveVehicle" , false);
		public static Setting sPacketMultiBlockChange = new Setting(serverPackets, Mode.BOOLEAN, "SPacketMultiBlockChange" , false);
		public static Setting sPacketOpenWindow = new Setting(serverPackets, Mode.BOOLEAN, "SPacketOpenWindow" , false);
		public static Setting sPacketParticles = new Setting(serverPackets, Mode.BOOLEAN, "SPacketParticles" , false);
		public static Setting sPacketPlayerAbilities = new Setting(serverPackets, Mode.BOOLEAN, "SPacketPlayerAbilities" , false);
		public static Setting sPacketPlayerListHeaderFooter = new Setting(serverPackets, Mode.BOOLEAN, "SPacketPlayerListHeaderFooter" , false);
		public static Setting sPacketPlayerListItem = new Setting(serverPackets, Mode.BOOLEAN, "SPacketPlayerListItem" , false);
		public static Setting sPacketPlayerPosLook = new Setting(serverPackets, Mode.BOOLEAN, "SPacketPlayerPosLook" , false);
		public static Setting sPacketRecipeBook = new Setting(serverPackets, Mode.BOOLEAN, "SPacketRecipeBook" , false);
		public static Setting sPacketRemoveEntityEffect = new Setting(serverPackets, Mode.BOOLEAN, "SPacketRemoveEntityEffect" , false);
		public static Setting sPacketResourcePackSend = new Setting(serverPackets, Mode.BOOLEAN, "SPacketResourcePackSend" , false);
		public static Setting sPacketRespawn = new Setting(serverPackets, Mode.BOOLEAN, "SPacketRespawn" , false);
		public static Setting sPacketScoreboardObjective = new Setting(serverPackets, Mode.BOOLEAN, "SPacketScoreboardObjective" , false);
		public static Setting sPacketSelectAdvancementsTab = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSelectAdvancementsTab" , false);
		public static Setting sPacketServerDifficulty = new Setting(serverPackets, Mode.BOOLEAN, "SPacketServerDifficulty" , false);
		public static Setting sPacketSetExperience = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSetExperience" , false);
		public static Setting sPacketSetPassengers = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSetPassengers" , false);
		public static Setting sPacketSetSlot = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSetSlot" , false);
		public static Setting sPacketSignEditorOpen = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSignEditorOpen" , false);
		public static Setting sPacketSoundEffect = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSoundEffect" , false);
		public static Setting sPacketSpawnExperienceOrb = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSpawnExperienceOrb" , false);
		public static Setting sPacketSpawnGlobalEntity = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSpawnGlobalEntity" , false);
		public static Setting sPacketSpawnMob = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSpawnMob" , false);
		public static Setting sPacketSpawnObject = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSpawnObject" , false);
		public static Setting sPacketSpawnPainting = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSpawnPainting" , false);
		public static Setting sPacketSpawnPlayer = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSpawnPlayer" , false);
		public static Setting sPacketSpawnPosition = new Setting(serverPackets, Mode.BOOLEAN, "SPacketSpawnPosition" , false);
		public static Setting sPacketStatistics = new Setting(serverPackets, Mode.BOOLEAN, "SPacketStatistics" , false);
		public static Setting sPacketTabComplete = new Setting(serverPackets, Mode.BOOLEAN, "SPacketTabComplete" , false);
		public static Setting sPacketTeams = new Setting(serverPackets, Mode.BOOLEAN, "SPacketTeams" , false);
		public static Setting sPacketTimeUpdate = new Setting(serverPackets, Mode.BOOLEAN, "SPacketTimeUpdate" , false);
		public static Setting sPacketTitle = new Setting(serverPackets, Mode.BOOLEAN, "SPacketTitle" , false);
		public static Setting sPacketUnloadChunk = new Setting(serverPackets, Mode.BOOLEAN, "SPacketUnloadChunk" , false);
		public static Setting sPacketUpdateBossInfo = new Setting(serverPackets, Mode.BOOLEAN, "SPacketUpdateBossInfo" , false);
		public static Setting sPacketUpdateHealth = new Setting(serverPackets, Mode.BOOLEAN, "SPacketUpdateHealth" , false);
		public static Setting sPacketUpdateScore = new Setting(serverPackets, Mode.BOOLEAN, "SPacketUpdateScore" , false);
		public static Setting sPacketUpdateTileEntity = new Setting(serverPackets, Mode.BOOLEAN, "SPacketUpdateTileEntity" , false);
		public static Setting sPacketUseBed = new Setting(serverPackets, Mode.BOOLEAN, "SPacketUseBed" , false);
		public static Setting sPacketWindowItems = new Setting(serverPackets, Mode.BOOLEAN, "SPacketWindowItems" , false);
		public static Setting sPacketWindowProperty = new Setting(serverPackets, Mode.BOOLEAN, "SPacketWindowProperty" , false);
		public static Setting sPacketWorldBorder = new Setting(serverPackets, Mode.BOOLEAN, "SPacketWorldBorder" , false);
	public static Setting clientPackets = new Setting(Mode.LABEL, "ClientPackets", true, "Client packets (C)", "Cancelling these will make the client not send them to server");
		public static Setting cPacketAnimation = new Setting(clientPackets, Mode.BOOLEAN, "CPacketAnimation" , false);
		public static Setting cPacketChatMessage = new Setting(clientPackets, Mode.BOOLEAN, "CPacketChatMessage" , false);
		public static Setting cPacketClickWindow = new Setting(clientPackets, Mode.BOOLEAN, "CPacketClickWindow" , false);
		public static Setting cPacketClientSettings = new Setting(clientPackets, Mode.BOOLEAN, "CPacketClientSettings" , false);
		public static Setting cPacketClientStatus = new Setting(clientPackets, Mode.BOOLEAN, "CPacketClientStatus" , false);
		public static Setting cPacketCloseWindow = new Setting(clientPackets, Mode.BOOLEAN, "CPacketCloseWindow" , false);
		public static Setting cPacketConfirmTeleport = new Setting(clientPackets, Mode.BOOLEAN, "CPacketConfirmTeleport" , false);
		public static Setting cPacketConfirmTransaction = new Setting(clientPackets, Mode.BOOLEAN, "CPacketConfirmTransaction" , false);
		public static Setting cPacketCreativeInventoryAction = new Setting(clientPackets, Mode.BOOLEAN, "CPacketCreativeInventoryAction" , false);
		public static Setting cPacketCustomPayload = new Setting(clientPackets, Mode.BOOLEAN, "CPacketCustomPayload" , false);
		public static Setting cPacketEnchantItem = new Setting(clientPackets, Mode.BOOLEAN, "CPacketEnchantItem" , false);
		public static Setting cPacketEntityAction = new Setting(clientPackets, Mode.BOOLEAN, "CPacketEntityAction" , false);
		public static Setting cPacketHeldItemChange = new Setting(clientPackets, Mode.BOOLEAN, "CPacketHeldItemChange" , false);
		public static Setting cPacketInput = new Setting(clientPackets, Mode.BOOLEAN, "CPacketInput" , false);
		public static Setting cPacketKeepAlive = new Setting(clientPackets, Mode.BOOLEAN, "CPacketKeepAlive" , false);
		public static Setting cPacketPlayer = new Setting(clientPackets, Mode.BOOLEAN, "CPacketPlayer" , false);
			public static Setting cPacketPlayerPosition = new Setting(cPacketPlayer, Mode.BOOLEAN, "CPacketPlayer.Position" , false);
			public static Setting cPacketPlayerPositionRotation = new Setting(cPacketPlayer, Mode.BOOLEAN, "CPacketPlayer.PositionRotation" , false);
			public static Setting cPacketPlayerRotation = new Setting(cPacketPlayer, Mode.BOOLEAN, "CPacketPlayer.Rotation" , false);
		public static Setting cPacketPlayerAbilities = new Setting(clientPackets, Mode.BOOLEAN, "CPacketPlayerAbilities" , false);
		public static Setting cPacketPlayerDigging = new Setting(clientPackets, Mode.BOOLEAN, "CPacketPlayerDigging" , false);
		public static Setting cPacketPlayerTryUseItem = new Setting(clientPackets, Mode.BOOLEAN, "CPacketPlayerTryUseItem" , false);
		public static Setting cPacketPlayerTryUseItemOnBlock = new Setting(clientPackets, Mode.BOOLEAN, "CPacketPlayerTryUseItemOnBlock" , false);
		public static Setting cPacketRecipeInfo = new Setting(clientPackets, Mode.BOOLEAN, "CPacketRecipeInfo" , false);
		public static Setting cPacketResourcePackStatus = new Setting(clientPackets, Mode.BOOLEAN, "CPacketResourcePackStatus" , false);
		public static Setting cPacketSeenAdvancements = new Setting(clientPackets, Mode.BOOLEAN, "CPacketSeenAdvancements" , false);
		public static Setting cPacketSpectate = new Setting(clientPackets, Mode.BOOLEAN, "CPacketSpectate" , false);
		public static Setting cPacketSteerBoat = new Setting(clientPackets, Mode.BOOLEAN, "CPacketSteerBoat" , false);
		public static Setting cPacketTabComplete = new Setting(clientPackets, Mode.BOOLEAN, "CPacketTabComplete" , false);
		public static Setting cPacketUpdateSign = new Setting(clientPackets, Mode.BOOLEAN, "CPacketUpdateSign" , false);
		public static Setting cPacketUseEntity = new Setting(clientPackets, Mode.BOOLEAN, "CPacketUseEntity" , false);
		public static Setting cPacketVehicleMove = new Setting(clientPackets, Mode.BOOLEAN, "CPacketVehicleMove" , false);
	public static Setting logPackets = new Setting(Mode.BOOLEAN, "LogPackets", false, "Logs the received and sent packets", "In console or mc chat");
		public static Setting logPacketsMode = new Setting(logPackets, "Mode", "Chat", new String[]{"Chat", "Sends them in mc-chat", "Only works every x seconds or spam would be too high", "Set the amount below"}, new String[]{"Console", "Sends them in console with System.out.println()"});
			public static Setting chatDelay = new Setting(logPacketsMode, "Chat", Mode.INTEGER, "Delay", 250, "Timer delay for waiting to send another packet message");
			
	public PacketCanceller() {
		super(Group.MISC, "PacketCanceller", "Cancel packets. Includes every packet in minecraft 1.12.2", "Also allows you to see received and sent packets");
	}
	
	@EventHandler
	private Listener<PacketEvent> packetEvent = new Listener<>(event -> {		
		//Cancel packets
		if (cPacketAnimation.booleanValue() && event.packet instanceof CPacketAnimation) {
			event.cancel();
		} else if (cPacketChatMessage.booleanValue() && event.packet instanceof CPacketChatMessage) {
			event.cancel();
		} else if (cPacketClickWindow.booleanValue() && event.packet instanceof CPacketClickWindow) {
			event.cancel();
		} else if (cPacketClientSettings.booleanValue() && event.packet instanceof CPacketClientSettings) {
			event.cancel();
		} else if (cPacketClientStatus.booleanValue() && event.packet instanceof CPacketClientStatus) {
			event.cancel();
		} else if (cPacketCloseWindow.booleanValue() && event.packet instanceof CPacketCloseWindow) {
			event.cancel();
		} else if (cPacketConfirmTeleport.booleanValue() && event.packet instanceof CPacketConfirmTeleport) {
			event.cancel();
		} else if (cPacketConfirmTransaction.booleanValue() && event.packet instanceof CPacketConfirmTransaction) {
			event.cancel();
		} else if (cPacketCreativeInventoryAction.booleanValue() && event.packet instanceof CPacketCreativeInventoryAction) {
			event.cancel();
		} else if (cPacketCustomPayload.booleanValue() && event.packet instanceof CPacketCustomPayload) {
			event.cancel();
		} else if (cPacketEnchantItem.booleanValue() && event.packet instanceof CPacketEnchantItem) {
			event.cancel();
		} else if (cPacketEntityAction.booleanValue() && event.packet instanceof CPacketEntityAction) {
			event.cancel();
		} else if (cPacketHeldItemChange.booleanValue() && event.packet instanceof CPacketHeldItemChange) {
			event.cancel();
		} else if (cPacketInput.booleanValue() && event.packet instanceof CPacketInput) {
			event.cancel();
		} else if (cPacketKeepAlive.booleanValue() && event.packet instanceof CPacketKeepAlive) {
			event.cancel();
		} else if (cPacketPlayer.booleanValue() && event.packet instanceof CPacketPlayer) {
			event.cancel();
		} else if (cPacketPlayerPosition.booleanValue() && event.packet instanceof CPacketPlayer.Position) {
			event.cancel();
		} else if (cPacketPlayerPositionRotation.booleanValue() && event.packet instanceof CPacketPlayer.PositionRotation) {
			event.cancel();
		} else if (cPacketPlayerRotation.booleanValue() && event.packet instanceof CPacketPlayer.Rotation) {
			event.cancel();
		} else if (cPacketPlayerAbilities.booleanValue() && event.packet instanceof CPacketPlayerAbilities) {
			event.cancel();
		} else if (cPacketPlayerDigging.booleanValue() && event.packet instanceof CPacketPlayerDigging) {
			event.cancel();
		} else if (cPacketPlayerTryUseItem.booleanValue() && event.packet instanceof CPacketPlayerTryUseItem) {
			event.cancel();
		} else if (cPacketPlayerTryUseItemOnBlock.booleanValue() && event.packet instanceof CPacketPlayerTryUseItemOnBlock) {
			event.cancel();
		} else if (cPacketRecipeInfo.booleanValue() && event.packet instanceof CPacketRecipeInfo) {
			event.cancel();
		} else if (cPacketResourcePackStatus.booleanValue() && event.packet instanceof CPacketResourcePackStatus) {
			event.cancel();
		} else if (cPacketSeenAdvancements.booleanValue() && event.packet instanceof CPacketSeenAdvancements) {
			event.cancel();
		} else if (cPacketSpectate.booleanValue() && event.packet instanceof CPacketSpectate) {
			event.cancel();
		} else if (cPacketSteerBoat.booleanValue() && event.packet instanceof CPacketSteerBoat) {
			event.cancel();
		} else if (cPacketTabComplete.booleanValue() && event.packet instanceof CPacketTabComplete) {
			event.cancel();
		} else if (cPacketUpdateSign.booleanValue() && event.packet instanceof CPacketUpdateSign) {
			event.cancel();
		} else if (cPacketUseEntity.booleanValue() && event.packet instanceof CPacketUseEntity) {
			event.cancel();
		} else if (cPacketVehicleMove.booleanValue() && event.packet instanceof CPacketVehicleMove) {
			event.cancel();
		} else if (sPacketAdvancementInfo.booleanValue() && event.packet instanceof SPacketAdvancementInfo) {
			event.cancel();
		} else if (sPacketAnimation.booleanValue() && event.packet instanceof SPacketAnimation) {
			event.cancel();
		} else if (sPacketBlockAction.booleanValue() && event.packet instanceof SPacketBlockAction) {
			event.cancel();
		} else if (sPacketBlockBreakAnim.booleanValue() && event.packet instanceof SPacketBlockBreakAnim) {
			event.cancel();
		} else if (sPacketBlockChange.booleanValue() && event.packet instanceof SPacketBlockChange) {
			event.cancel();
		} else if (sPacketCamera.booleanValue() && event.packet instanceof SPacketCamera) {
			event.cancel();
		} else if (sPacketChangeGameState.booleanValue() && event.packet instanceof SPacketChangeGameState) {
			event.cancel();
		} else if (sPacketChat.booleanValue() && event.packet instanceof SPacketChat) {
			event.cancel();
		} else if (sPacketChunkData.booleanValue() && event.packet instanceof SPacketChunkData) {
			event.cancel();
		} else if (sPacketCloseWindow.booleanValue() && event.packet instanceof SPacketCloseWindow) {
			event.cancel();
		} else if (sPacketCollectItem.booleanValue() && event.packet instanceof SPacketCollectItem) {
			event.cancel();
		} else if (sPacketCombatEvent.booleanValue() && event.packet instanceof SPacketCombatEvent) {
			event.cancel();
		} else if (sPacketConfirmTransaction.booleanValue() && event.packet instanceof SPacketConfirmTransaction) {
			event.cancel();
		} else if (sPacketCooldown.booleanValue() && event.packet instanceof SPacketCooldown) {
			event.cancel();
		} else if (sPacketCustomPayload.booleanValue() && event.packet instanceof SPacketCustomPayload) {
			event.cancel();
		} else if (sPacketCustomSound.booleanValue() && event.packet instanceof SPacketCustomSound) {
			event.cancel();
		} else if (sPacketDestroyEntities.booleanValue() && event.packet instanceof SPacketDestroyEntities) {
			event.cancel();
		} else if (sPacketDisconnect.booleanValue() && event.packet instanceof SPacketDisconnect) {
			event.cancel();
		} else if (sPacketDisplayObjective.booleanValue() && event.packet instanceof SPacketDisplayObjective) {
			event.cancel();
		} else if (sPacketEffect.booleanValue() && event.packet instanceof SPacketEffect) {
			event.cancel();
		} else if (sPacketEntity.booleanValue() && event.packet instanceof SPacketEntity) {
			event.cancel();
		} else if (s15PacketEntityRelMove.booleanValue() && event.packet instanceof SPacketEntity.S15PacketEntityRelMove) {
			event.cancel();
		} else if (s16PacketEntityLook.booleanValue() && event.packet instanceof SPacketEntity.S16PacketEntityLook) {
			event.cancel();
		} else if (s17PacketEntityLookMove.booleanValue() && event.packet instanceof SPacketEntity.S17PacketEntityLookMove) {
			event.cancel();
		} else if (sPacketEntityAttach.booleanValue() && event.packet instanceof SPacketEntityAttach) {
			event.cancel();
		} else if (sPacketEntityEffect.booleanValue() && event.packet instanceof SPacketEntityEffect) {
			event.cancel();
		} else if (sPacketEntityEquipment.booleanValue() && event.packet instanceof SPacketEntityEquipment) {
			event.cancel();
		} else if (sPacketEntityHeadLook.booleanValue() && event.packet instanceof SPacketEntityHeadLook) {
			event.cancel();
		} else if (sPacketEntityMetadata.booleanValue() && event.packet instanceof SPacketEntityMetadata) {
			event.cancel();
		} else if (sPacketEntityProperties.booleanValue() && event.packet instanceof SPacketEntityProperties) {
			event.cancel();
		} else if (sPacketEntityStatus.booleanValue() && event.packet instanceof SPacketEntityStatus) {
			event.cancel();
		} else if (sPacketEntityTeleport.booleanValue() && event.packet instanceof SPacketEntityTeleport) {
			event.cancel();
		} else if (sPacketEntityVelocity.booleanValue() && event.packet instanceof SPacketEntityVelocity) {
			event.cancel();
		} else if (sPacketExplosion.booleanValue() && event.packet instanceof SPacketExplosion) {
			event.cancel();
		} else if (sPacketHeldItemChange.booleanValue() && event.packet instanceof SPacketHeldItemChange) {
			event.cancel();
		} else if (sPacketJoinGame.booleanValue() && event.packet instanceof SPacketJoinGame) {
			event.cancel();
		} else if (sPacketKeepAlive.booleanValue() && event.packet instanceof SPacketKeepAlive) {
			event.cancel();
		} else if (sPacketMaps.booleanValue() && event.packet instanceof SPacketMaps) {
			event.cancel();
		} else if (sPacketMoveVehicle.booleanValue() && event.packet instanceof SPacketMoveVehicle) {
			event.cancel();
		} else if (sPacketMultiBlockChange.booleanValue() && event.packet instanceof SPacketMultiBlockChange) {
			event.cancel();
		} else if (sPacketOpenWindow.booleanValue() && event.packet instanceof SPacketOpenWindow) {
			event.cancel();
		} else if (sPacketParticles.booleanValue() && event.packet instanceof SPacketParticles) {
			event.cancel();
		} else if (sPacketPlayerAbilities.booleanValue() && event.packet instanceof SPacketPlayerAbilities) {
			event.cancel();
		} else if (sPacketPlayerListHeaderFooter.booleanValue() && event.packet instanceof SPacketPlayerListHeaderFooter) {
			event.cancel();
		} else if (sPacketPlayerListItem.booleanValue() && event.packet instanceof SPacketPlayerListItem) {
			event.cancel();
		} else if (sPacketPlayerPosLook.booleanValue() && event.packet instanceof SPacketPlayerPosLook) {
			event.cancel();
		} else if (sPacketRecipeBook.booleanValue() && event.packet instanceof SPacketRecipeBook) {
			event.cancel();
		} else if (sPacketRemoveEntityEffect.booleanValue() && event.packet instanceof SPacketRemoveEntityEffect) {
			event.cancel();
		} else if (sPacketResourcePackSend.booleanValue() && event.packet instanceof SPacketResourcePackSend) {
			event.cancel();
		} else if (sPacketRespawn.booleanValue() && event.packet instanceof SPacketRespawn) {
			event.cancel();
		} else if (sPacketScoreboardObjective.booleanValue() && event.packet instanceof SPacketScoreboardObjective) {
			event.cancel();
		} else if (sPacketSelectAdvancementsTab.booleanValue() && event.packet instanceof SPacketSelectAdvancementsTab) {
			event.cancel();
		} else if (sPacketServerDifficulty.booleanValue() && event.packet instanceof SPacketServerDifficulty) {
			event.cancel();
		} else if (sPacketSetExperience.booleanValue() && event.packet instanceof SPacketSetExperience) {
			event.cancel();
		} else if (sPacketSetPassengers.booleanValue() && event.packet instanceof SPacketSetPassengers) {
			event.cancel();
		} else if (sPacketSetSlot.booleanValue() && event.packet instanceof SPacketSetSlot) {
			event.cancel();
		} else if (sPacketSignEditorOpen.booleanValue() && event.packet instanceof SPacketSignEditorOpen) {
			event.cancel();
		} else if (sPacketSoundEffect.booleanValue() && event.packet instanceof SPacketSoundEffect) {
			event.cancel();
		} else if (sPacketSpawnExperienceOrb.booleanValue() && event.packet instanceof SPacketSpawnExperienceOrb) {
			event.cancel();
		} else if (sPacketSpawnGlobalEntity.booleanValue() && event.packet instanceof SPacketSpawnGlobalEntity) {
			event.cancel();
		} else if (sPacketSpawnMob.booleanValue() && event.packet instanceof SPacketSpawnMob) {
			event.cancel();
		} else if (sPacketSpawnObject.booleanValue() && event.packet instanceof SPacketSpawnObject) {
			event.cancel();
		} else if (sPacketSpawnPainting.booleanValue() && event.packet instanceof SPacketSpawnPainting) {
			event.cancel();
		} else if (sPacketSpawnPlayer.booleanValue() && event.packet instanceof SPacketSpawnPlayer) {
			event.cancel();
		} else if (sPacketSpawnPosition.booleanValue() && event.packet instanceof SPacketSpawnPosition) {
			event.cancel();
		} else if (sPacketStatistics.booleanValue() && event.packet instanceof SPacketStatistics) {
			event.cancel();
		} else if (sPacketTabComplete.booleanValue() && event.packet instanceof SPacketTabComplete) {
			event.cancel();
		} else if (sPacketTeams.booleanValue() && event.packet instanceof SPacketTeams) {
			event.cancel();
		} else if (sPacketTimeUpdate.booleanValue() && event.packet instanceof SPacketTimeUpdate) {
			event.cancel();
		} else if (sPacketTitle.booleanValue() && event.packet instanceof SPacketTitle) {
			event.cancel();
		} else if (sPacketUnloadChunk.booleanValue() && event.packet instanceof SPacketUnloadChunk) {
			event.cancel();
		} else if (sPacketUpdateBossInfo.booleanValue() && event.packet instanceof SPacketUpdateBossInfo) {
			event.cancel();
		} else if (sPacketUpdateHealth.booleanValue() && event.packet instanceof SPacketUpdateHealth) {
			event.cancel();
		} else if (sPacketUpdateScore.booleanValue() && event.packet instanceof SPacketUpdateScore) {
			event.cancel();
		} else if (sPacketUpdateTileEntity.booleanValue() && event.packet instanceof SPacketUpdateTileEntity) {
			event.cancel();
		} else if (sPacketUseBed.booleanValue() && event.packet instanceof SPacketUseBed) {
			event.cancel();
		} else if (sPacketWindowItems.booleanValue() && event.packet instanceof SPacketWindowItems) {
			event.cancel();
		} else if (sPacketWindowProperty.booleanValue() && event.packet instanceof SPacketWindowProperty) {
			event.cancel();
		} else if (sPacketWorldBorder.booleanValue() && event.packet instanceof SPacketWorldBorder) {
			event.cancel();
		}
		
		//Log packets
		if (logPackets.booleanValue()) {
			String packetName = event.packet.getClass().getName();
			if (packetName.contains(".S")) {
				packetName = packetName.substring(packetName.indexOf(".S") + 1);
			} else if (packetName.contains(".C")) {
				packetName = packetName.substring(packetName.indexOf(".C") + 1);
			}
			
			packetName = packetName.replace("$", ".");
			
			if (event.isCancelled()) {
				packetName = "Cancelled: " + packetName;
			}
			
			if (logPacketsMode.stringValue().equals("Chat") && timer.hasPassed(chatDelay.intValue())) {
				sendMessage(packetName, false);
				timer.reset();
			} else {
				System.out.println(packetName);
			}
		}
	});
}