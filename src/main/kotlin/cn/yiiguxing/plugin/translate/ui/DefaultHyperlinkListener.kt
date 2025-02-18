package cn.yiiguxing.plugin.translate.ui

import com.intellij.ui.BrowserHyperlinkListener
import javax.swing.event.HyperlinkEvent

open class DefaultHyperlinkListener : BrowserHyperlinkListener() {

    override fun hyperlinkActivated(hyperlinkEvent: HyperlinkEvent) {
        if (!DefaultHyperlinkHandler.handleHyperlinkActivated(hyperlinkEvent)) {
            super.hyperlinkActivated(hyperlinkEvent)
        }
    }

    companion object : DefaultHyperlinkListener()
}