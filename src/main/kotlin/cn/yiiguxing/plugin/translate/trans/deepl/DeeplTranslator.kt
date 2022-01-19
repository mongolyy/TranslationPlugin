
package cn.yiiguxing.plugin.translate.trans.deepl

import cn.yiiguxing.plugin.translate.DEEPL_TRANSLATE_URL
import cn.yiiguxing.plugin.translate.trans.*
import com.intellij.openapi.diagnostic.Logger
import cn.yiiguxing.plugin.translate.ui.settings.TranslationEngine.DEEPL
import cn.yiiguxing.plugin.translate.util.Http
import cn.yiiguxing.plugin.translate.util.Settings
import cn.yiiguxing.plugin.translate.util.md5
import com.google.gson.Gson
import java.util.*
import javax.swing.Icon
import cn.yiiguxing.plugin.translate.util.i

/**
 * DeepL translator
 */
object DeeplTranslator : AbstractTranslator() {

    private val SUPPORTED_LANGUAGES: List<Lang> = listOf(
        Lang.CHINESE,
        Lang.ENGLISH,
        Lang.BULGARIAN,
        Lang.CZECH,
        Lang.DANISH,
        Lang.GERMAN,
        Lang.GREEK,
        Lang.SPANISH,
        Lang.ESTONIAN,
        Lang.FINNISH,
        Lang.HUNGARIAN,
        Lang.ITALIAN,
        Lang.JAPANESE,
        Lang.LITHUANIAN,
        Lang.LATVIAN,
        Lang.DUTCH,
        Lang.POLISH,
        Lang.PORTUGUESE,
        Lang.ROMANIAN,
        Lang.RUSSIAN,
        Lang.SLOVAK,
        Lang.SLOVENIAN,
        Lang.SWEDISH,
    )

    private val logger: Logger = Logger.getInstance(DeeplTranslator::class.java)

    override val id: String = DEEPL.id

    override val name: String = DEEPL.translatorName

    override val icon: Icon = DEEPL.icon

    override val intervalLimit: Int = DEEPL.intervalLimit

    override val contentLengthLimit: Int = DEEPL.contentLengthLimit

    override val primaryLanguage: Lang
        get() = DEEPL.primaryLanguage

    override val supportedSourceLanguages: List<Lang> = SUPPORTED_LANGUAGES
    override val supportedTargetLanguages: List<Lang> = SUPPORTED_LANGUAGES

    override fun checkConfiguration(force: Boolean): Boolean {
        if (force || Settings.deeplTranslateSettings.let { it.appId.isEmpty() || it.getAppKey().isEmpty() }) {
            return DEEPL.showConfigurationDialog()
        }

        return true
    }

    override fun doTranslate(text: String, srcLang: Lang, targetLang: Lang): Translation {
        return SimpleTranslateClient(this,
            DeeplTranslator::call,
            DeeplTranslator::parseTranslation
        ).execute(text, srcLang, targetLang)
    }

    private fun call(text: String, srcLang: Lang, targetLang: Lang): String {
        val settings = Settings.deeplTranslateSettings
        val appId = settings.appId
        val privateKey = settings.getAppKey()
        val salt = System.currentTimeMillis().toString()
        val sign = (appId + text + salt + privateKey).md5().lowercase(Locale.getDefault())

        return Http.postDataFrom(
            DEEPL_TRANSLATE_URL,
            "appid" to appId,
            "from" to srcLang.deeplLanguageCode,
            "to" to targetLang.deeplLanguageCode,
            "salt" to salt,
            "sign" to sign,
            "q" to text
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun parseTranslation(translation: String, original: String, srcLang: Lang, targetLang: Lang): Translation {
        logger.i("translate result: $translation")

        return Gson().fromJson(translation, DeeplTranslation::class.java).apply {
            if (!isSuccessful) {
                throw TranslateResultException(code, name)
            }
        }.toTranslation()
    }
}
