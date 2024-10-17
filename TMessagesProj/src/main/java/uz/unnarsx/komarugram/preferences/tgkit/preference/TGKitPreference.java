package uz.unnarsx.komarugram.preferences.tgkit.preference;

import uz.unnarsx.komarugram.preferences.tgkit.preference.types.TGPType;

abstract public class TGKitPreference {
    public String title;
    public boolean isAvailable = true;

    abstract public TGPType getType();
}
