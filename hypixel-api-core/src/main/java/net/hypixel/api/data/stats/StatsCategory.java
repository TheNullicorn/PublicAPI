package net.hypixel.api.data.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.hypixel.api.util.UnstableHypixelObject;

/**
 * A grouping of stats belonging to a specific mini-game or other area of the server.
 *
 * <h4 id="packages">Packages</h4>
 * Packages act as flags to indicate that something has been unlocked. For mini-games, flags
 * indicate which cosmetics the player has unlocked through the game's shop.
 * <p>
 * Some obscure packages (e.g. {@code achievement_flag_n}) are used internally by certain games, and
 * can be ignored by users of the API.
 */
public abstract class StatsCategory extends UnstableHypixelObject {

    protected StatsCategory(JsonElement raw) {
        super(raw);
    }

    /**
     * Retrieves all unlocked <a href="#package">packages</a> in the category.
     *
     * @return All packages unlocked by the player in the current category. May be empty.
     * @see #hasPackage(String)
     */
    public List<String> getPackages() {
        JsonArray jsonPackages = getArrayProperty("packages");
        if (jsonPackages.size() == 0) {
            return Collections.emptyList();
        }

        List<String> packages = new ArrayList<>(jsonPackages.size());
        for (JsonElement pkg : jsonPackages) {

            if (!pkg.isJsonPrimitive()) {
                packages.add(pkg.toString());
                continue;
            }

            // Otherwise, the value should already be convertable to a string.
            packages.add(stringifyPackage(pkg));
        }
        return packages;
    }

    /**
     * Checks if a <a href="#package">package</a> is unlocked in the current stat category.
     *
     * @param name The name/identifier of the package.
     * @return {@code true} if the package is unlocked/included. Otherwise {@code false}.
     * @see #getPackages()
     */
    public boolean hasPackage(String name) {
        JsonArray jsonPackages = getArrayProperty("packages");

        for (JsonElement pkg : jsonPackages) {
            if (name.equals(stringifyPackage(pkg))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Helper for converting {@link JsonElement elements} in the "packages" array into strings.
     * <p>
     * This returns the value of {@link JsonPrimitive#getAsString()} for primitive values, and the
     * value of {@code toString()} for any other types of elements (as a fall-back).
     *
     * @param pkg The package's JSON value.
     * @return The package's string name.
     */
    private static String stringifyPackage(JsonElement pkg) {
        // Packages *should* only be strings, so just use the string value.
        if (pkg.isJsonPrimitive()) {
            return pkg.getAsString();
        }

        // In case they ever include non-primitive packages, this converts them to strings anyway.
        return pkg.toString();
    }
}
