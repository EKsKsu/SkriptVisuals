
package me.miksu.skriptvisuals.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.util.Locale;

public class EffParticleCircle extends Effect {

    private Expression<String> particle;
    private Expression<Location> location;
    private Expression<Number> radius;
    private Expression<Number> points;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(
            Expression<?>[] expressions,
            int matchedPattern,
            Kleenean isDelayed,
            ParseResult parser
    ) {
        particle = (Expression<String>) expressions[0];
        location = (Expression<Location>) expressions[1];
        radius = (Expression<Number>) expressions[2];
        points = (Expression<Number>) expressions[3];
        return true;
    }

    @Override
    protected void execute(Event event) {
        String name = particle.getSingle(event);
        Location center = location.getSingle(event);
        Number r = radius.getSingle(event);
        Number p = points.getSingle(event);

        if (name == null || center == null || r == null || p == null) {
            return;
        }

        World world = center.getWorld();
        if (world == null) return;

        try {
            Particle type = Particle.valueOf(name.toUpperCase(Locale.ROOT));
            double size = Math.max(0, Math.min(100, r.doubleValue()));
            int count = Math.max(3, Math.min(500, p.intValue()));

            for (int i = 0; i < count; i++) {
                double angle = 2 * Math.PI * i / count;
                double x = Math.cos(angle) * size;
                double z = Math.sin(angle) * size;

                Location point = center.clone().add(x, 0, z);
                world.spawnParticle(type, point, 1, 0, 0, 0, 0);
            }
        } catch (IllegalArgumentException ex) {
            // Invalid particle name.
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "spawn particle circle";
    }
}
