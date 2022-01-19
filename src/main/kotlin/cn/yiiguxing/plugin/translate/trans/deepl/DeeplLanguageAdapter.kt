package cn.yiiguxing.plugin.translate.trans.deepl

import cn.yiiguxing.plugin.translate.trans.BaseLanguageAdapter
import cn.yiiguxing.plugin.translate.trans.Lang

object DeeplLanguageAdapter : BaseLanguageAdapter() {

    override fun getAdaptedLanguages(): Map<String, Lang> = mapOf(
        "zh" to Lang.CHINESE,
    )

}

val Lang.deeplLanguageCode: String
    get() = DeeplLanguageAdapter.getLanguageCode(this)

@Suppress("unused")
fun Lang.Companion.fromAliLanguageCode(code: String): Lang {
    return DeeplLanguageAdapter.getLanguage(code)
}
