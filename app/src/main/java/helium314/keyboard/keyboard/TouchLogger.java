// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.keyboard;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Local-only log of key touches, used to calibrate a per-thumb touch model.
 * One CSV line per key press: time, pointer id, key code, key bounds (x, y, w, h) and touch position (x, y).
 * Never written for password fields. Never leaves the device.
 * File: Android/data/&lt;package&gt;/files/touchlog.csv
 */
public final class TouchLogger {
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor();
    private static File sFile;
    private static volatile boolean sEnabled = true;

    private TouchLogger() {}

    public static void init(final Context context) {
        try {
            File dir = context.getExternalFilesDir(null);
            if (dir == null) dir = context.getFilesDir();
            sFile = new File(dir, "touchlog.csv");
        } catch (Exception e) {
            sFile = null;
        }
    }

    public static void setEnabled(final boolean enabled) {
        sEnabled = enabled;
    }

    public static void log(final long time, final int pointerId, final int code,
            final int keyX, final int keyY, final int keyW, final int keyH,
            final int touchX, final int touchY) {
        final File file = sFile;
        if (!sEnabled || file == null) return;
        final String line = time + "," + pointerId + "," + code + "," + keyX + "," + keyY + ","
                + keyW + "," + keyH + "," + touchX + "," + touchY + "\n";
        sExecutor.execute(() -> {
            try (BufferedWriter w = new BufferedWriter(new FileWriter(file, true))) {
                w.write(line);
            } catch (IOException ignored) {
            }
        });
    }
}
