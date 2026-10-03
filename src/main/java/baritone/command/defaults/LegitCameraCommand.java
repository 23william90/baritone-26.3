/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.command.defaults;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.exception.CommandException;
import baritone.api.command.exception.CommandInvalidTypeException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class LegitCameraCommand extends Command {

    public LegitCameraCommand(IBaritone baritone) {
        super(baritone, "legitcamera", "legitcameramovement", "legitcam", "legitlook", "legit");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            boolean newState = !Baritone.settings().legitCameraMovement.value;
            Baritone.settings().legitCameraMovement.value = newState;
            Baritone.settings().legitMovement.value = newState;
            logDirect("Legit camera movement is now " + (newState ? "§aENABLED§r (Smooth human-like camera, no snapping)" : "§cDISABLED§r (Default instant snap)"));
            return;
        }

        String sub = args.getString().toLowerCase(Locale.ROOT);
        switch (sub) {
            case "on":
            case "true":
            case "enable":
            case "1": {
                Baritone.settings().legitCameraMovement.value = true;
                Baritone.settings().legitMovement.value = true;
                logDirect("Legit camera movement is now §aENABLED§r (Smooth human-like camera, no snapping)");
                break;
            }
            case "off":
            case "false":
            case "disable":
            case "0": {
                Baritone.settings().legitCameraMovement.value = false;
                Baritone.settings().legitMovement.value = false;
                logDirect("Legit camera movement is now §cDISABLED§r (Default instant snap)");
                break;
            }
            case "speed": {
                args.requireMin(1);
                try {
                    float speed = Float.parseFloat(args.getString());
                    if (speed <= 0.0f) {
                        throw new CommandInvalidTypeException(args.consumed(), "a positive number");
                    }
                    Baritone.settings().legitCameraSpeed.value = speed;
                    logDirect("Legit camera max speed set to: §b" + speed + "§r deg/tick");
                } catch (NumberFormatException e) {
                    throw new CommandInvalidTypeException(args.consumed(), "a valid float number");
                }
                break;
            }
            case "smooth":
            case "smoothing": {
                args.requireMin(1);
                try {
                    float smooth = Float.parseFloat(args.getString());
                    if (smooth <= 0.0f || smooth > 1.0f) {
                        throw new CommandInvalidTypeException(args.consumed(), "a number between 0.01 and 1.0");
                    }
                    Baritone.settings().legitCameraSmoothing.value = smooth;
                    logDirect("Legit camera smoothing factor set to: §b" + smooth + "§r");
                } catch (NumberFormatException e) {
                    throw new CommandInvalidTypeException(args.consumed(), "a valid float number");
                }
                break;
            }
            case "status":
            case "info": {
                boolean enabled = Baritone.settings().legitCameraMovement.value;
                float speed = Baritone.settings().legitCameraSpeed.value;
                float smooth = Baritone.settings().legitCameraSmoothing.value;
                boolean assist = Baritone.settings().legitCameraTurnAssist.value;
                logDirect("§6=== Legit Camera Movement Status ===");
                logDirect("Status: " + (enabled ? "§aENABLED" : "§cDISABLED"));
                logDirect("Max Speed: §b" + speed + " deg/tick");
                logDirect("Smoothing Factor: §b" + smooth);
                logDirect("Corner Turn Assist: §b" + (assist ? "§aENABLED" : "§cDISABLED"));
                break;
            }
            default:
                logDirect("Unknown option: " + sub);
                logDirect("Usage: #" + label + " [on|off|speed <deg/tick>|smooth <factor>|status]");
                break;
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        if (args.hasExactlyOne()) {
            try {
                String prefix = args.getString().toLowerCase(Locale.ROOT);
                return Stream.of("on", "off", "speed", "smooth", "status")
                        .filter(s -> s.startsWith(prefix));
            } catch (Exception ignored) {
            }
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Configure legit smooth camera movement";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Hijacks the camera so that all Baritone movements (pathing, breaking, placing, mining, looking) smoothly glide using human-like ease-out curves instead of robotic instant snapping, while maintaining 100% path accuracy.",
                "",
                "Usage:",
                "> #legitcamera - Toggle legit camera movement on/off",
                "> #legitcamera on - Enable legit camera movement",
                "> #legitcamera off - Disable legit camera movement",
                "> #legitcamera speed <val> - Set max turn speed (default 45.0 deg/tick)",
                "> #legitcamera smooth <val> - Set ease-out factor (default 0.65)",
                "> #legitcamera status - Show current settings"
        );
    }
}
