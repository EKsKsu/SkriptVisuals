package me.miksu.skriptvisuals;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class HologramManager {

    private static final double DEFAULT_SPACING = 0.27;
    private static final int MAX_ROWS = 100;
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacySection();

    private final JavaPlugin plugin;
    private final Map<String, Hologram> holograms = new HashMap<>();

    public HologramManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean create(String id, Location location) {
        if (id == null || location == null || location.getWorld() == null) {
            return false;
        }

        String key = normalize(id);

        if (key.isBlank() || holograms.containsKey(key)) {
            return false;
        }

        Hologram hologram = new Hologram(key, location.clone());
        holograms.put(key, hologram);
        hologram.refresh();
        return true;
    }

    public boolean delete(String id) {
        Hologram hologram = get(id);

        if (hologram == null) {
            return false;
        }

        holograms.remove(hologram.id);
        hologram.destroy();
        return true;
    }

    public boolean exists(String id) {
        return get(id) != null;
    }

    public boolean setRow(String id, int row, String text) {
        Hologram hologram = get(id);

        if (hologram == null || row < 1 || row > MAX_ROWS || text == null) {
            return false;
        }

        while (hologram.rows.size() < row) {
            hologram.rows.add("");
        }

        hologram.rows.set(row - 1, text);
        hologram.refresh();
        return true;
    }

    public boolean addRow(String id, String text) {
        Hologram hologram = get(id);

        if (hologram == null || text == null || hologram.rows.size() >= MAX_ROWS) {
            return false;
        }

        hologram.rows.add(text);
        hologram.refresh();
        return true;
    }

    public boolean insertRow(String id, int row, String text) {
        Hologram hologram = get(id);

        if (hologram == null || text == null
                || row < 1 || row > hologram.rows.size() + 1
                || hologram.rows.size() >= MAX_ROWS) {
            return false;
        }

        hologram.rows.add(row - 1, text);
        shiftEffectsAfterInsert(hologram, row - 1);
        hologram.refresh();
        return true;
    }

    public boolean removeRow(String id, int row) {
        Hologram hologram = get(id);

        if (hologram == null || row < 1 || row > hologram.rows.size()) {
            return false;
        }

        hologram.rows.remove(row - 1);
        shiftEffectsAfterRemove(hologram, row - 1);
        hologram.refresh();
        return true;
    }

    public boolean clearRows(String id) {
        Hologram hologram = get(id);

        if (hologram == null) {
            return false;
        }

        hologram.rows.clear();
        hologram.rowEffects.clear();
        hologram.refresh();
        return true;
    }

    public boolean move(String id, Location location) {
        Hologram hologram = get(id);

        if (hologram == null || location == null || location.getWorld() == null) {
            return false;
        }

        hologram.location = location.clone();
        hologram.attachedPlayer = null;
        hologram.stopFollowing();
        hologram.refresh();
        return true;
    }

    public boolean attach(String id, Player player, double offset) {
        Hologram hologram = get(id);

        if (hologram == null || player == null || !player.isOnline()
                || !Double.isFinite(offset) || offset < -5 || offset > 10) {
            return false;
        }

        hologram.attachedPlayer = player.getUniqueId();
        hologram.playerOffset = offset;
        hologram.startFollowing();
        return true;
    }

    public boolean detach(String id) {
        Hologram hologram = get(id);

        if (hologram == null) {
            return false;
        }

        hologram.attachedPlayer = null;
        hologram.stopFollowing();
        return true;
    }

    public boolean setRowEffect(String id, int row, String effect) {
        Hologram hologram = get(id);

        if (hologram == null || row < 1 || row > hologram.rows.size()
                || effect == null) {
            return false;
        }

        String normalized = effect.toUpperCase(Locale.ROOT);

        if (!List.of("NONE", "RAINBOW", "PULSE", "WAVE", "FADE")
                .contains(normalized)) {
            return false;
        }

        if ("NONE".equals(normalized)) {
            hologram.rowEffects.remove(row - 1);
        } else {
            hologram.rowEffects.put(row - 1, normalized);
        }

        hologram.refresh();
        return true;
    }

    public boolean setSpacing(String id, double spacing) {
        Hologram hologram = get(id);

        if (hologram == null || !Double.isFinite(spacing)
                || spacing < 0.1 || spacing > 2.0) {
            return false;
        }

        hologram.spacing = spacing;
        hologram.refresh();
        return true;
    }

    public boolean setSeeThrough(String id, boolean enabled) {
        Hologram hologram = get(id);

        if (hologram == null) {
            return false;
        }

        hologram.seeThrough = enabled;
        hologram.refresh();
        return true;
    }

    public boolean setShadow(String id, boolean enabled) {
        Hologram hologram = get(id);

        if (hologram == null) {
            return false;
        }

        hologram.shadow = enabled;
        hologram.refresh();
        return true;
    }

    public boolean setBillboard(String id, String mode) {
        Hologram hologram = get(id);

        if (hologram == null || mode == null) {
            return false;
        }

        try {
            hologram.billboard = Display.Billboard.valueOf(
                    mode.toUpperCase(Locale.ROOT)
            );
            hologram.refresh();
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public void deleteAll() {
        for (Hologram hologram : new ArrayList<>(holograms.values())) {
            hologram.destroy();
        }

        holograms.clear();
    }

    private void shiftEffectsAfterInsert(Hologram hologram, int index) {
        Map<Integer, String> updated = new HashMap<>();

        hologram.rowEffects.forEach((key, value) ->
                updated.put(key >= index ? key + 1 : key, value));

        hologram.rowEffects.clear();
        hologram.rowEffects.putAll(updated);
    }

    private void shiftEffectsAfterRemove(Hologram hologram, int index) {
        Map<Integer, String> updated = new HashMap<>();

        hologram.rowEffects.forEach((key, value) -> {
            if (key < index) {
                updated.put(key, value);
            } else if (key > index) {
                updated.put(key - 1, value);
            }
        });

        hologram.rowEffects.clear();
        hologram.rowEffects.putAll(updated);
    }

    private Hologram get(String id) {
        return id == null ? null : holograms.get(normalize(id));
    }

    private String normalize(String id) {
        return id.trim().toLowerCase(Locale.ROOT);
    }

    private final class Hologram {

        private final String id;
        private final List<String> rows = new ArrayList<>();
        private final Map<Integer, String> rowEffects = new HashMap<>();
        private final List<TextDisplay> displays = new ArrayList<>();

        private Location location;
        private UUID attachedPlayer;
        private double playerOffset = 2.2;
        private double spacing = DEFAULT_SPACING;
        private boolean seeThrough = true;
        private boolean shadow;
        private Display.Billboard billboard = Display.Billboard.CENTER;

        private BukkitTask followTask;
        private BukkitTask animationTask;
        private long animationTick;

        private Hologram(String id, Location location) {
            this.id = id;
            this.location = location;
        }

        private void refresh() {
            removeDisplays();

            if (location.getWorld() == null) {
                return;
            }

            for (int i = 0; i < rows.size(); i++) {
                final int rowIndex = i;

                Location rowLocation = location.clone().add(
                        0,
                        (rows.size() - 1 - rowIndex) * spacing,
                        0
                );

                TextDisplay display = location.getWorld().spawn(
                        rowLocation,
                        TextDisplay.class,
                        entity -> {
                            entity.setPersistent(false);
                            entity.setInvulnerable(true);
                            entity.setGravity(false);
                            entity.setSilent(true);
                            entity.setBillboard(billboard);
                            entity.setSeeThrough(seeThrough);
                            entity.setShadowed(shadow);
                            entity.setDefaultBackground(false);
                            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
                            entity.setAlignment(TextDisplay.TextAlignment.CENTER);
                            entity.setLineWidth(400);
                            entity.setText(LEGACY.serialize(textForRow(rowIndex)));

                            entity.setTransformation(new Transformation(
                                    new Vector3f(0, 0, 0),
                                    new AxisAngle4f(0, 0, 0, 1),
                                    new Vector3f(1, 1, 1),
                                    new AxisAngle4f(0, 0, 0, 1)
                            ));
                        }
                );

                displays.add(display);
            }

            startAnimation();
        }

        private net.kyori.adventure.text.Component textForRow(int index) {
            String text = rows.get(index);
            String effect = rowEffects.getOrDefault(index, "NONE");

            if ("RAINBOW".equals(effect)) {
                String[] colors = {"§c", "§6", "§e", "§a", "§b", "§d"};
                StringBuilder result = new StringBuilder();

                for (int i = 0; i < text.length(); i++) {
                    result.append(colors[(i + (int) (animationTick / 2))
                            % colors.length]);
                    result.append(text.charAt(i));
                }

                text = result.toString();
            } else if ("PULSE".equals(effect)) {
                String[] colors = {"§4", "§c", "§6", "§c"};
                text = colors[(int) (animationTick / 5 % colors.length)] + text;
            } else if ("WAVE".equals(effect)) {
                text = (animationTick % 20 < 10 ? "§b" : "§3") + text;
            } else if ("FADE".equals(effect)) {
                text = (animationTick % 30 < 15 ? "§7" : "§f") + text;
            }

            return LEGACY.deserialize(text);
        }

        private void startAnimation() {
            if (animationTask != null) {
                animationTask.cancel();
                animationTask = null;
            }

            if (rowEffects.isEmpty()) {
                return;
            }

            animationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                animationTick++;

                for (int i = 0; i < displays.size() && i < rows.size(); i++) {
                    TextDisplay display = displays.get(i);

                    if (display.isValid()) {
                        display.text(textForRow(i));
                    }
                }
            }, 2L, 2L);
        }

        private void startFollowing() {
            stopFollowing();

            followTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (attachedPlayer == null) {
                    stopFollowing();
                    return;
                }

                Player player = Bukkit.getPlayer(attachedPlayer);

                if (player == null || !player.isOnline()) {
                    return;
                }

                if (!player.getWorld().equals(location.getWorld())) {
                    location = player.getLocation().add(0, playerOffset, 0);
                    refresh();
                    return;
                }

                location = player.getLocation().add(0, playerOffset, 0);

                for (int i = 0; i < displays.size(); i++) {
                    TextDisplay display = displays.get(i);

                    if (display.isValid()) {
                        display.teleport(location.clone().add(
                                0,
                                (rows.size() - 1 - i) * spacing,
                                0
                        ));
                    }
                }
            }, 1L, 1L);
        }

        private void stopFollowing() {
            if (followTask != null) {
                followTask.cancel();
                followTask = null;
            }
        }

        private void removeDisplays() {
            for (TextDisplay display : displays) {
                if (display.isValid()) {
                    display.remove();
                }
            }

            displays.clear();
        }

        private void destroy() {
            stopFollowing();

            if (animationTask != null) {
                animationTask.cancel();
                animationTask = null;
            }

            removeDisplays();
        }
    }
}