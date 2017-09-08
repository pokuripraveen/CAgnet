package com.unique.agent.utils;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.UiModeManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.CaptioningManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by praveenpokuri on 17/07/17.
 */

public class SystemUtils {
    /**
     * Default constructor.
     */
    private SystemUtils() {
        super();
    }

    private static void validateContext(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Provided Context must not be null");
        }
    }

    /**
     * Retrieve a {@link TelephonyManager} for handling management the telephony features of the device.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link TelephonyManager}.
     */
    public static TelephonyManager getTelephonyManager(final Context context) {
        validateContext(context);
        return  (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * Retrieve a {@link WindowManager} for accessing the system's window manager.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link WindowManager}.
     */
    public static WindowManager getWindowManager(final Context context) {
        validateContext(context);
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * Retrieve a {@link ConnectivityManager} for handling management of network connections.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link ConnectivityManager}.
     */
    public static ConnectivityManager getConnectivityManager(final Context context) {
        validateContext(context);
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Retrieve a {@link UiModeManager} for controlling UI modes.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link UiModeManager}.
     */
    public static UiModeManager getUiModeManager(final Context context) {
        validateContext(context);
        return (UiModeManager)context.getSystemService(Context.UI_MODE_SERVICE);
    }

    /**
     * Retrieve a {@link LocationManager} for controlling location updates.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link LocationManager}.
     */
    public static LocationManager getLocationManager(final Context context) {
        validateContext(context);
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Retrieve a {@link PowerManager} for controlling power management,
     * including "wake locks," which let you keep the device on while you're running long tasks.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link PowerManager}.
     */
    public static PowerManager getPowerManager(final Context context) {
        validateContext(context);
        return (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    /**
     * Retrieve a {@link AlarmManager} for receiving intents at a time of your choosing.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link AlarmManager}.
     */
    public static AlarmManager getAlarmManager(final Context context) {
        validateContext(context);
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Retrieve a {@link LayoutInflater} for inflating layout resources in this context.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link LayoutInflater}.
     */
    public static LayoutInflater getLayoutInflater(final Context context) {
        validateContext(context);
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Retrieve a {@link AudioManager} for handling management of volume, ringer modes and audio routing.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link AudioManager}.
     */
    public static AudioManager getAudioManager(final Context context) {
        validateContext(context);
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Retrieve a {@link MediaRouter} for controlling and managing routing of media.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link MediaRouter}.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static MediaRouter getMediaRouter(final Context context) {
        validateContext(context);
        return (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
    }

    /**
     * Retrieve a {@link InputMethodManager} for accessing input methods.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link InputMethodManager}.
     */
    public static InputMethodManager getInputMethodManager(final Context context) {
        validateContext(context);
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * Retrieve a {@link ClipboardManager} for accessing and modifying ClipboardManager for accessing
     * and modifying the contents of the global clipboard.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link ClipboardManager}.
     */
    public static ClipboardManager getClipboardManager(final Context context) {
        validateContext(context);
        return (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * Retrieve a {@link CaptioningManager} for obtaining captioning properties
     * and listening for changes in captioning preferences.
     *
     * @param context Context of the callee.
     *
     * @return Instance of the {@link CaptioningManager}.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static CaptioningManager getCaptioningManager(final Context context) {
        validateContext(context);
        return (CaptioningManager) context.getSystemService(Context.CAPTIONING_SERVICE);
    }


}
