package com.amary.amarywebview.sample

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amary.amarywebview.AmaryWebView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.defaultTheme -> {
                AmaryWebView(this).titleDefault("The Finest Artist")
                    .webViewJavaScriptEnabled(true)
                    .show("http://thefinestartist.com")
            }
            R.id.redTheme -> {
                AmaryWebView(this)
                    .theme(R.style.RedTheme)
                    .titleDefault("Bless This Stuff")
                    .webViewBuiltInZoomControls(true)
                    .webViewDisplayZoomControls(true)
                    .dividerHeight(0)
                    .gradientDivider(false)
                    //          .setCustomAnimations(
                    //              R.anim.activity_open_enter,
                    //              R.anim.activity_open_exit,
                    //              R.anim.activity_close_enter,
                    //              R.anim.activity_close_exit)
                    .injectJavaScript(
                        "javascript: document.getElementById('msg').innerHTML='Hello "
                                + "TheFinestArtist"
                                + "!';"
                    )
                    .show("http://www.blessthisstuff.com")
            }
            R.id.blueTheme -> {
                AmaryWebView(this)
                    //          .theme(R.style.AmaryWebViewTheme)
                    .titleDefault("Vimeo")
                    .showUrl(false)
                    .statusBarColorRes(R.color.bluePrimaryDark)
                    .toolbarColorRes(R.color.bluePrimary)
                    //          .titleColorRes(R.color.finestWhite)
                    .urlColorRes(R.color.bluePrimaryLight)
                    //          .iconDefaultColorRes(R.color.finestWhite)
                    //          .progressBarColorRes(R.color.finestWhite)
                    //          .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                    //          .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                    //          .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                    .showSwipeRefreshLayout(true)
                    .swipeRefreshColorRes(R.color.bluePrimaryDark)
                    //          .menuSelector(R.drawable.selector_light_theme)
                    .menuTextGravity(Gravity.CENTER)
                    //          .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                    .dividerHeight(0)
                    .gradientDivider(false)
                    //          .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                    .show("http://example.com")
            }
            R.id.blackTheme -> {
                AmaryWebView(this) //          .theme(R.style.AmaryWebViewTheme)
                    .titleDefault("Dribbble")
                    .toolbarScrollFlags(0)
                    .statusBarColorRes(R.color.blackPrimaryDark)
                    .toolbarColorRes(R.color.blackPrimary) //          .titleColorRes(R.color.finestWhite)
                    .urlColorRes(R.color.blackPrimaryLight) //          .iconDefaultColorRes(R.color.finestWhite)
                    //          .progressBarColorRes(R.color.finestWhite)
                    .swipeRefreshColorRes(R.color.blackPrimaryDark) //          .menuSelector(R.drawable.selector_light_theme)
                    .menuTextGravity(Gravity.CENTER_VERTICAL or Gravity.END) //          .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                    .dividerHeight(0)
                    .gradientDivider(false) //          .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                    //          .setCustomAnimations(
                    //              R.anim.slide_left_in, R.anim.hold, R.anim.hold, R.anim.slide_right_out)
                    //          .setCustomAnimations(
                    //              R.anim.fade_in_fast,
                    //              R.anim.fade_out_medium,
                    //              R.anim.fade_in_medium,
                    //              R.anim.fade_out_fast)
                    .disableIconBack(true)
                    .disableIconClose(true)
                    .disableIconForward(true)
                    .disableIconMenu(true)
                    .show("https://dribbble.com")
            }
        }
    }
}