
package me.miksu.skriptvisuals.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public class EffHologram extends Effect {

    private Expression<String> text;
    private Expression<Location> location;
    private Expression<Number> duration;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(
            Expression<?>[] expressions,
            int matchedPattern,
            Kleenean isDelayed,
            ParseResult parser
    ) {
        text = (Expression<String>) expressions[0];
        location = (Expression<Location>) expressions[1];
        duration = (Expression<Number>) expressions[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        String message = text.getSingle(event);
        Location loc = location.getSingle(event);
        Number seconds = duration.getSingle(event);

        if (message == null || loc == null || seconds == null) {
            return;
        }

        World world = loc.getWorld();
        if (world == null) return;

        long ticks = Math.max(
                1,
                Math.min(1200, Math.round(seconds.doubleValue() * 20))
        );

        ArmorStand stand = world.spawn(
                loc.clone(),
                ArmorStand.class,
                entity -> {
                    entity.setInvisible(true);
                    entity.setMarker(true);
                    entity.setGravity(false);
                    entity.setSilent(true);
                    entity.setInvulnerable(true);
                    entity.setCustomName(
                            ChatColor.translateAlternateColorCodes('&', message)
                    );
                    entity.setCustomNameVisible(true);
                }
        );

        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(
                EffHologram.class
        );

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> {
                    if (stand.isValid()) {
                        stand.remove();
                    }
                },
                ticks
        );
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "show hologram";
    }
}
