
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

public class EffParticleLine extends Effect {

    private Expression<String> particle;
    private Expression<Location> start;
    private Expression<Location> end;
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
        start = (Expression<Location>) expressions[1];
        end = (Expression<Location>) expressions[2];
        points = (Expression<Number>) expressions[3];
        return true;
    }

    @Override
    protected void execute(Event event) {
        String name = particle.getSingle(event);
        Location a = start.getSingle(event);
        Location b = end.getSingle(event);
        Number p = points.getSingle(event);

        if (name == null || a == null || b == null || p == null) {
            return;
        }

        World world = a.getWorld();

        if (world == null || !world.equals(b.getWorld())) {
            return;
        }

        try {
            Particle type = Particle.valueOf(name.toUpperCase(Locale.ROOT));
            int count = Math.max(2, Math.min(500, p.intValue()));

            for (int i = 0; i < count; i++) {
                double t = (double) i / (count - 1);

                double x = a.getX() + (b.getX() - a.getX()) * t;
                double y = a.getY() + (b.getY() - a.getY()) * t;
                double z = a.getZ() + (b.getZ() - a.getZ()) * t;

                Location point = new Location(world, x, y, z);
                world.spawnParticle(type, point, 1, 0, 0, 0, 0);
            }
        } catch (IllegalArgumentException ex) {
            // Invalid particle name.
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "draw particle line";
    }
}
