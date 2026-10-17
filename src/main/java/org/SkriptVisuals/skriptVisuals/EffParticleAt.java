
package me.miksu.skriptvisuals.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.Locale;

public class EffParticleAt extends Effect {

    private Expression<Number> amount;
    private Expression<String> particle;
    private Expression<Location> location;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(
            Expression<?>[] expressions,
            int matchedPattern,
            Kleenean isDelayed,
            ParseResult parser
    ) {
        amount = (Expression<Number>) expressions[0];
        particle = (Expression<String>) expressions[1];
        location = (Expression<Location>) expressions[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Number n = amount.getSingle(event);
        String name = particle.getSingle(event);
        Location loc = location.getSingle(event);

        if (n == null || name == null || loc == null || loc.getWorld() == null) {
            return;
        }

        try {
            Particle type = Particle.valueOf(name.toUpperCase(Locale.ROOT));
            int count = Math.max(0, Math.min(10000, n.intValue()));

            loc.getWorld().spawnParticle(
                    type, loc, count, 0.25, 0.25, 0.25, 0
            );
        } catch (IllegalArgumentException ex) {
            // Invalid particle name.
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "spawn particles at location";
    }
}
