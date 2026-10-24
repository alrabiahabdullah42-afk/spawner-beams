package com.example.addon.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.core.BlockPos;

public class SpawnerBeams extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> height = sgGeneral.add(new IntSetting.Builder()
        .name("height")
        .description("How tall the beam is, in blocks.")
        .defaultValue(64)
        .min(1)
        .sliderRange(1, 256)
        .build());

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("Only draw beams for spawners within this many blocks.")
        .defaultValue(128.0)
        .min(1.0)
        .sliderRange(16.0, 512.0)
        .build());

    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("Beam color.")
        .defaultValue(new SettingColor(255, 40, 40, 255))
        .build());

    public SpawnerBeams() {
        super(Categories.World, "spawner-beams", "Draws a tall beam above every mob spawner.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.player == null || mc.level == null) return;

        double maxDistSq = range.get() * range.get();

        for (BlockEntity be : Utils.blockEntities()) {
            if (!(be instanceof SpawnerBlockEntity)) continue;

            BlockPos pos = be.getBlockPos();
            if (mc.player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > maxDistSq) continue;

            double cx = pos.getX() + 0.5;
            double cz = pos.getZ() + 0.5;
            double y1 = pos.getY() + 1.0;
            double y2 = y1 + height.get();

            // four close lines side by side so the beam looks thicker than 1 pixel
            double o = 0.12;
            event.renderer.line(cx - o, y1, cz - o, cx - o, y2, cz - o, color.get());
            event.renderer.line(cx + o, y1, cz - o, cx + o, y2, cz - o, color.get());
            event.renderer.line(cx - o, y1, cz + o, cx - o, y2, cz + o, color.get());
            event.renderer.line(cx + o, y1, cz + o, cx + o, y2, cz + o, color.get());
        }
    }
}
