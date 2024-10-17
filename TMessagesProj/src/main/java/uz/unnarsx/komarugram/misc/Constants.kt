package uz.unnarsx.komarugram.misc

import org.telegram.messenger.BuildConfig

object Constants {

    var CG_VERSION = BuildConfig.BUILD_VERSION_STRING_CHERRY
    var CG_AUTHOR = "Updates: @komarugram"

    const val komarugram_Owner = 6815290979L // komarugram Owner (Arslan)
    const val komarugram_Channel = 1555706985L // komarugram Channel
    const val komarugram_Support = 1555706985L // komarugram Support Group
    const val komarugram_APKs = 1555706985L // komarugram APKs
    const val komarugram_Beta = 1555706985L // komarugram Beta APKs
    const val komarugram_Archive = 1555706985L // komarugram Archive

    const val CHERRY_EMOJI_ID = 5190867769951799110L // komarugram logo
    const val CHERRY_EMOJI_ID_14 = 5190867769951799110L // komarugram logo (bra)
    const val PROFILE_BACKGROUND_COLOR_ID_GREEN_BLUE = 12 // Blue-Green gradient
    const val PROFILE_BACKGROUND_COLOR_ID_RED = 14 // Red-Pink gradient
    const val REPLY_BACKGROUND_COLOR_ID = 13 // Red-Pink gradient

    /** Firebase remote Config start **/
    const val Videomessages_Resolution = "videomessages_resolution"
    const val Is_Donate_Screen_Available = "is_donate_screen_available"
    /** Firebase remote Config finish **/

}