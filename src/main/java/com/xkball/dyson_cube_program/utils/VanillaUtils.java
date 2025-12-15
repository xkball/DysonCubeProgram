package com.xkball.dyson_cube_program.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.xkball.dyson_cube_program.DysonCubeProgram;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class VanillaUtils {
    
    public static final boolean DEBUG = SharedConstants.IS_RUNNING_WITH_JDWP;
    public static final Direction[] DIRECTIONS = Direction.values();
    public static final Identifier MISSING_TEXTURE = Identifier.withDefaultNamespace("missingno");
    public static final int TRANSPARENT = ColorUtils.getColor(255, 255, 255, 0);
    public static final int GUI_GRAY = ColorUtils.getColor(30, 30, 30, 200);
    
    public static Identifier modRL(String path) {
        return rLOf(DysonCubeProgram.MODID, path);
    }
    
    public static Identifier rLOf(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
    
    public static EquipmentSlot equipmentSlotFromHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }
    
    public static void runCommand(String command, LivingEntity livingEntity) {
        // Raise permission level to 2, akin to what vanilla sign does
        var level = livingEntity.level();
        var server = livingEntity.level().getServer();
        if (server != null && level instanceof ServerLevel serverLevel) {
            CommandSourceStack cmdSrc = livingEntity.createCommandSourceStackForNameResolution(serverLevel).withPermission(LevelBasedPermissionSet.GAMEMASTER);
            server.getCommands().performPrefixedCommand(cmdSrc, command);
        }
    }
    
    public static void runCommand(String command, MinecraftServer server, UUID playerUUID) {
        var player = server.getPlayerList().getPlayer(playerUUID);
        if (player != null) {
            server.getCommands().performPrefixedCommand(player.createCommandSourceStack().withPermission(LevelBasedPermissionSet.GAMEMASTER), command);
        }
    }
    
    @SuppressWarnings("SuspiciousNameCombination")
    public static Vec2 rotate90FromBlockCenterYP(Vec2 point, int times) {
        times = times % 4;
        if (times == 0) return point;
        var x = point.x;
        var y = point.y;
        if (times == 1) return new Vec2(16 - y, x);
        if (times == 2) return new Vec2(16 - x, 16 - y);
        return new Vec2(y, 16 - x);
    }
    
    public static Component getName(Block block) {
        var rl = BuiltInRegistries.BLOCK.getKey(block);
        return Component.translatable("block." + rl.getNamespace() + "." + rl.getPath());
    }
    
    public static String md5(String input) {
        try {
            var md = MessageDigest.getInstance("MD5");
            var bytes = md.digest(input.getBytes());
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String base64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }
    
    public static byte[] unBase64(String str){
        return Base64.decodeBase64(str);
    }
    
    public static byte[] gzip(String str){
        try(var byteOut = new ByteArrayOutputStream()){
            try(GZIPOutputStream gzip = new GZIPOutputStream(byteOut)) {
                gzip.write(str.getBytes(StandardCharsets.UTF_8));
            }
            return byteOut.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static byte[] unGzip(byte[] bytes) {
        try(GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            return gzip.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String removeAfterLastCharOf(String str,char c){
        return str.substring(0,str.lastIndexOf(c));
    }
    
    public static List<String> searchStartWith(String key, Collection<String> src){
        var startWithList = new ArrayList<String>();
        for (var str : src) {
            var searchEntry = str.toLowerCase();
            if (searchEntry.startsWith(key)) startWithList.add(str);
        }
        startWithList.sort(String::compareTo);
        return startWithList;
    }
    
    public static List<String> searchInLowerCase(String key, Collection<String> src) {
        key = key.toLowerCase();
        var startWithList = new ArrayList<String>();
        var containsList = new ArrayList<String>();
        for (var str : src) {
            var searchEntry = str.toLowerCase();
            if (searchEntry.startsWith(key)) startWithList.add(str);
            else if (searchEntry.contains(key)) containsList.add(str);
        }
        startWithList.sort(String::compareTo);
        containsList.sort(String::compareTo);
        startWithList.addAll(containsList);
        return startWithList;
    }
    
    public static List<String> search(String key, Collection<String> src) {
        var startWithList = new ArrayList<String>();
        var containsList = new ArrayList<String>();
        for (var str : src) {
            if (str.startsWith(key)) startWithList.add(str);
            else if (str.contains(key)) containsList.add(str);
        }
        startWithList.sort(String::compareTo);
        containsList.sort(String::compareTo);
        startWithList.addAll(containsList);
        return startWithList;
    }
    
    public static JsonElement readJsonFromResource(Resource resource) throws IOException {
        try(var reader = resource.openAsReader()){
            return JsonParser.parseReader(reader);
        }
    }
    
    @Nullable
    public static <T> T pickRandom(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(RandomSource.create().nextInt(list.size()));
    }
    
}
