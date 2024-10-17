package uz.unnarsx.komarugram.core

import uz.unnarsx.komarugram.core.configs.komarugramChatsConfig
import uz.unnarsx.komarugram.core.configs.komarugramCameraConfig

// I've created this so CG features can be injected in a source file with 1 line only (maybe)
// Because manual editing of drklo's sources harms your mental health.
object CGFeatureHooks {

    @JvmStatic
    fun setFlashLight(b: Boolean) {
        // ...
        komarugramCameraConfig.whiteBackground = b
    }

    @JvmStatic
    fun switchNoAuthor(b: Boolean) {
        // ...
        komarugramChatsConfig.noAuthorship = b
    }

    @JvmStatic
    fun switchGifSpoilers(b: Boolean) {
        // ...
        komarugramChatsConfig.gifSpoilers = b
    }

}