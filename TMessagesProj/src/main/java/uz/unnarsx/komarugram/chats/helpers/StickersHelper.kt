package uz.unnarsx.cherrygram.chats.helpers

import android.content.res.AssetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.MessageObject
import org.telegram.tgnet.TLRPC
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

object StickersHelper: CoroutineScope by MainScope() {

    private var SET_IDS = listOf<String>()

    suspend fun getStickerSetIDs() = withContext(Dispatchers.IO) {
        try {
            SET_IDS = URL("https://raw.githubusercontent.com/arsLan4k1390/Cherrygram/main/stickers.txt").readText().lines()
//            Log.d("SetsDownloader", SET_IDS.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun gitFetcher(document: Long): Boolean {
        return SET_IDS.contains(document.toString())
    }

    private fun isGitSetId(document: TLRPC.Document): Boolean {
        return gitFetcher(MessageObject.getStickerSetId(document))
    }

    // Locally stored IDs
    private val iDs = arrayOf(683462835916767409L, 1510769529645432834L, 8106175868352593928L, 5835129661968875533L,
        5149354467191160831L, 5091996690789957635L, 7131267980628852734L, 7131267980628852733L, 3346563080237613068L,
        6055278067666911223L, 5062008833983905790L, 1169953291908415506L, 6055278067666911216L, 4331929539736240157L,
        5091996690789957649L, 9087292238668496936L, 6417088260173987842L, 8728063708061761539L, 4238900539514945542L,
        4008340909736329215, 3560771065137332232
    )

    private fun isLocalSetId(document: TLRPC.Document): Boolean = iDs.any { setID: Long ->
        setID == MessageObject.getStickerSetId(document)
    }

    fun setToBlock(document: TLRPC.Document): Boolean {
        return isGitSetId(document) || isLocalSetId(document)
    }

    //Get sticker from assets
    fun copyStickerFromAssets() {
        try {
            val outFile = File(ApplicationLoader.applicationContext.getExternalFilesDir(null), "stickers/cherrygram.webm")
            if (outFile.exists()) return
            outFile.parentFile?.mkdirs()
            val am: AssetManager = ApplicationLoader.applicationContext.assets
            val `in` = am.open("cherrygram.webm")
            val out: OutputStream = FileOutputStream(outFile)
            copyFile(`in`, out)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

}