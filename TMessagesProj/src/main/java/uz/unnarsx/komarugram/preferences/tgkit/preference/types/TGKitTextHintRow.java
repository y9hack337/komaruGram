package uz.unnarsx.komarugram.preferences.tgkit.preference.types;

import uz.unnarsx.komarugram.preferences.tgkit.preference.TGKitPreference;

public class TGKitTextHintRow extends TGKitPreference {
    public boolean divider;

    @Override
    public TGPType getType() {
        return TGPType.HINT;
    }
}
