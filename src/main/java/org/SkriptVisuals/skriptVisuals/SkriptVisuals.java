package me.miksu.skriptvisuals;

import ch.njol.skript.Skript;
import me.miksu.skriptvisuals.effects.EffHologram;
import me.miksu.skriptvisuals.effects.EffHologramAction;
import me.miksu.skriptvisuals.effects.EffParticleAt;
import me.miksu.skriptvisuals.effects.EffParticleCircle;
import me.miksu.skriptvisuals.effects.EffParticleLine;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkriptVisuals extends JavaPlugin {

    private HologramManager hologramManager;

    @Override
    public void onEnable() {
        if (!getServer().getPluginManager().isPluginEnabled("Skript")) {
            getLogger().severe("Skript is required!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            Skript.registerAddon(this);

            hologramManager = new HologramManager(this);
            EffHologramAction.setManager(hologramManager);

            registerParticleEffects();
            registerHologramEffects();

            getLogger().info("SkriptVisuals enabled successfully!");
            getLogger().info("Particle effects and hologram management are ready.");

        } catch (Exception exception) {
            getLogger().severe("Failed to initialize SkriptVisuals.");
            exception.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerParticleEffects() {
        Skript.registerEffect(
                EffParticleAt.class,
                "spawn %number% [of] %string% particles at %location%"
        );

        Skript.registerEffect(
                EffParticleCircle.class,
                "spawn [a] %string% particle circle at %location% with radius %number% and %number% points"
        );

        Skript.registerEffect(
                EffParticleLine.class,
                "draw [a] %string% particle line from %location% to %location% with %number% points"
        );

        Skript.registerEffect(
                EffHologram.class,
                "show hologram %string% at %location% for %number% seconds"
        );
    }

    private void registerHologramEffects() {
        Skript.registerEffect(
                EffHologramAction.class,

                "create [a] hologram with id %string% at %location%",

                "delete hologram %string%",

                "set hologram row %number% of %string% to %string%",

                "add hologram row %string% to %string%",

                "insert hologram row %string% at %number% of %string%",

                "remove hologram row %number% of %string%",

                "clear hologram rows of %string%",

                "move hologram %string% to %location%",

                "attach hologram %string% above %player%",

                "detach hologram %string%",

                "set hologram row %number% of %string% effect to %string%",

                "set hologram %string% spacing to %number%",

                "set hologram %string% see-through to %boolean%",

                "set hologram %string% shadow to %boolean%",

                "set hologram %string% billboard to %string%"
        );
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    @Override
    public void onDisable() {
        if (hologramManager != null) {
            hologramManager.deleteAll();
            hologramManager = null;
        }

        getLogger().info("SkriptVisuals disabled.");
    }
}