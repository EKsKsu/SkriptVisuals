package me.miksu.skriptvisuals.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.miksu.skriptvisuals.HologramManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Locale;

public final class EffHologramAction extends Effect {

    private static HologramManager manager;

    private Expression<?>[] expressions;
    private int pattern;

    public static void setManager(HologramManager value) {
        manager = value;
    }

    @Override
    public boolean init(
            Expression<?>[] expressions,
            int matchedPattern,
            Kleenean isDelayed,
            SkriptParser.ParseResult parser
    ) {
        this.expressions = expressions;
        this.pattern = matchedPattern;
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (manager == null) {
            return;
        }

        try {
            switch (pattern) {
                case 0 -> manager.create(
                        string(0, event),
                        location(1, event)
                );

                case 1 -> manager.delete(string(0, event));

                case 2 -> manager.setRow(
                        string(1, event),
                        number(0, event),
                        string(2, event)
                );

                case 3 -> manager.addRow(
                        string(1, event),
                        string(0, event)
                );

                case 4 -> manager.insertRow(
                        string(2, event),
                        number(1, event),
                        string(0, event)
                );

                case 5 -> manager.removeRow(
                        string(1, event),
                        number(0, event)
                );

                case 6 -> manager.clearRows(string(0, event));

                case 7 -> manager.move(
                        string(0, event),
                        location(1, event)
                );

                case 8 -> manager.attach(
                        string(0, event),
                        player(1, event),
                        2.2
                );

                case 9 -> manager.detach(string(0, event));

                case 10 -> manager.setRowEffect(
                        string(1, event),
                        number(0, event),
                        string(2, event)
                );

                case 11 -> manager.setSpacing(
                        string(0, event),
                        decimal(1, event)
                );

                case 12 -> manager.setSeeThrough(
                        string(0, event),
                        bool(1, event)
                );

                case 13 -> manager.setShadow(
                        string(0, event),
                        bool(1, event)
                );

                case 14 -> manager.setBillboard(
                        string(0, event),
                        string(1, event)
                );

                default -> {
                }
            }
        } catch (RuntimeException exception) {
            // Invalid or missing expression values must not crash the server.
        }
    }

    private String string(int index, Event event) {
        Object value = expressions[index].getSingle(event);
        return value == null ? "" : value.toString();
    }

    private int number(int index, Event event) {
        Object value = expressions[index].getSingle(event);
        return value instanceof Number n ? n.intValue() : 0;
    }

    private double decimal(int index, Event event) {
        Object value = expressions[index].getSingle(event);
        return value instanceof Number n ? n.doubleValue() : 0;
    }

    private boolean bool(int index, Event event) {
        Object value = expressions[index].getSingle(event);
        return value instanceof Boolean b && b;
    }

    private Location location(int index, Event event) {
        Object value = expressions[index].getSingle(event);
        return value instanceof Location location ? location : null;
    }

    private Player player(int index, Event event) {
        Object value = expressions[index].getSingle(event);
        return value instanceof Player player ? player : null;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "manage hologram";
    }
}