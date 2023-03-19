package com.thefinestartist.finestwebview

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.MailTo
import android.net.Uri
import android.os.Build.*
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.Animation.*
import android.view.animation.AnimationUtils
import android.webkit.DownloadListener
import android.webkit.WebChromeClient
import android.webkit.WebSettings.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.thefinestartist.finestwebview.enums.ProgressBarPosition
import com.thefinestartist.finestwebview.listeners.BroadCastManager.Companion.onDownloadStart
import com.thefinestartist.finestwebview.listeners.BroadCastManager.Companion.onLoadResource
import com.thefinestartist.finestwebview.listeners.BroadCastManager.Companion.onPageCommitVisible
import com.thefinestartist.finestwebview.listeners.BroadCastManager.Companion.onPageFinished
import com.thefinestartist.finestwebview.listeners.BroadCastManager.Companion.onPageStarted
import com.thefinestartist.finestwebview.listeners.BroadCastManager.Companion.onProgressChanged
import com.thefinestartist.finestwebview.listeners.BroadCastManager.Companion.onReceivedTitle
import com.thefinestartist.finestwebview.listeners.BroadCastManager.Companion.onReceivedTouchIconUrl
import com.thefinestartist.finestwebview.listeners.BroadCastManager.Companion.unregister
import com.thefinestartist.finestwebview.utils.BitmapUtil.getColoredBitmap
import com.thefinestartist.finestwebview.utils.BitmapUtil.getGradientBitmap
import com.thefinestartist.finestwebview.utils.ColorUtil.disableColor
import com.thefinestartist.finestwebview.utils.DisplayUtil.getHeight
import com.thefinestartist.finestwebview.utils.DisplayUtil.getStatusBarHeight
import com.thefinestartist.finestwebview.utils.DisplayUtil.getWidth
import com.thefinestartist.finestwebview.utils.TypefaceUtil
import com.thefinestartist.finestwebview.utils.UnitConverter.dpToPx
import com.thefinestartist.finestwebview.utils.UrlParser.getHost
import com.thefinestartist.finestwebview.utils.orEmpty
import com.thefinestartist.finestwebview.views.ShadowLayout
import kotlin.math.abs

class FinestWebViewActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

  private var key = 0

  private var rtl = false

  private var statusBarColor = 0

  private var toolbarColor = 0
  private var toolbarScrollFlags = 0

  private var iconDefaultColor = 0
  private var iconDisabledColor = 0
  private var iconPressedColor = 0
  private var iconSelector = 0

  private var showIconClose = false
  private var disableIconClose = false
  private var showIconBack = false
  private var disableIconBack = false
  private var showIconForward = false
  private var disableIconForward = false
  private var showIconMenu = false
  private var disableIconMenu = false

  private var showSwipeRefreshLayout = false
  private var swipeRefreshColor = 0
  private var swipeRefreshColors: IntArray? = null

  private var showDivider = false
  private var gradientDivider = false
  private var dividerColor = 0
  private var dividerHeight = 0f

  private var showProgressBar = false
  private var progressBarColor = 0
  private var progressBarHeight = 0f
  private var progressBarPosition: ProgressBarPosition? = null

  private var titleDefault: String? = null
  private var updateTitleFromHtml = false
  private var titleSize = 0f
  private var titleFont: String? = null
  private var finestWebViewTitleColor = 0

  private var showUrl = false
  private var urlSize = 0f
  private var urlFont: String? = null
  private var urlColor = 0

  private var menuColor = 0
  private var menuDropShadowColor = 0
  private var menuDropShadowSize = 0f
  private var menuSelector = 0

  private var menuTextSize = 0f
  private var menuTextFont: String? = null
  private var menuTextColor = 0

  private var menuTextGravity = 0
  private var menuTextPaddingLeft = 0f
  private var menuTextPaddingRight = 0f

  private var showMenuRefresh = false
  private var stringResRefresh = 0
  private var showMenuFind = false
  private var stringResFind = 0
  private var showMenuShareVia = false
  private var stringResShareVia = 0
  private var showMenuCopyLink = false
  private var stringResCopyLink = 0
  private var showMenuOpenWith = false
  private var stringResOpenWith = 0

  private var animationCloseEnter = 0
  private var animationCloseExit = 0

  private var backPressToClose = false
  private var stringResCopiedToClipboard = 0

  private var webViewSupportZoom: Boolean? = null
  private var webViewMediaPlaybackRequiresUserGesture: Boolean? = null
  private var webViewBuiltInZoomControls: Boolean? = null
  private var webViewDisplayZoomControls: Boolean? = null
  private var webViewAllowFileAccess: Boolean? = null
  private var webViewAllowContentAccess: Boolean? = null
  private var webViewLoadWithOverviewMode: Boolean? = null
  private var webViewSaveFormData: Boolean? = null
  private var webViewTextZoom: Int? = null
  private var webViewUseWideViewPort: Boolean? = null
  private var webViewSupportMultipleWindows: Boolean? = null
  private var webViewLayoutAlgorithm: LayoutAlgorithm? = null
  private var webViewStandardFontFamily: String? = null
  private var webViewFixedFontFamily: String? = null
  private var webViewSansSerifFontFamily: String? = null
  private var webViewSerifFontFamily: String? = null
  private var webViewCursiveFontFamily: String? = null
  private var webViewFantasyFontFamily: String? = null
  private var webViewMinimumFontSize: Int? = null
  private var webViewMinimumLogicalFontSize: Int? = null
  private var webViewDefaultFontSize: Int? = null
  private var webViewDefaultFixedFontSize: Int? = null
  private var webViewLoadsImagesAutomatically: Boolean? = null
  private var webViewBlockNetworkImage: Boolean? = null
  private var webViewBlockNetworkLoads: Boolean? = null
  private var webViewJavaScriptEnabled: Boolean? = null
  private var webViewAllowUniversalAccessFromFileURLs: Boolean? = null
  private var webViewAllowFileAccessFromFileURLs: Boolean? = null
  private var webViewGeolocationDatabasePath: String? = null
  private var webViewAppCacheEnabled: Boolean? = null
  private var webViewAppCachePath: String? = null
  private var webViewDatabaseEnabled: Boolean? = null
  private var webViewDomStorageEnabled: Boolean? = null
  private var webViewGeolocationEnabled: Boolean? = null
  private var webViewJavaScriptCanOpenWindowsAutomatically: Boolean? = null
  private var webViewDefaultTextEncodingName: String? = null
  private var webViewUserAgentString: String? = null
  private var webViewNeedInitialFocus: Boolean? = null
  private var webViewCacheMode: Int? = null
  private var webViewMixedContentMode: Int? = null
  private var webViewOffscreenPreRaster: Boolean? = null

  private var injectJavaScript: String? = null

  private var mimeType: String? = null
  private var encoding: String? = null
  private var data: String? = null
  private var url: String? = null
  private var coordinatorLayout: CoordinatorLayout? = null
  private var appBar: AppBarLayout? = null
  private var toolbar: Toolbar? = null
  private var toolbarLayout: RelativeLayout? = null
  private var title: TextView? = null
  private var urlTv: TextView? = null
  private var close: AppCompatImageButton? = null
  private var back: AppCompatImageButton? = null
  private var forward: AppCompatImageButton? = null
  private var more: AppCompatImageButton? = null
  private var swipeRefreshLayout: SwipeRefreshLayout? = null
  private var webView: WebView? = null
  private var gradient: View? = null
  private var divider: View? = null
  private var progressBar: ProgressBar? = null
  private var menuLayout: RelativeLayout? = null
  private var shadowLayout: ShadowLayout? = null
  private var menuBackground: LinearLayout? = null
  private var menuRefresh: LinearLayout? = null
  private var menuRefreshTv: TextView? = null
  private var menuFind: LinearLayout? = null
  private var menuFindTv: TextView? = null
  private var menuShareVia: LinearLayout? = null
  private var menuShareViaTv: TextView? = null
  private var menuCopyLink: LinearLayout? = null
  private var menuCopyLinkTv: TextView? = null
  private var menuOpenWith: LinearLayout? = null
  private var menuOpenWithTv: TextView? = null
  private var webLayout: FrameLayout? = null

  private var downloadListener = DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
    onDownloadStart(this@FinestWebViewActivity, key, url, userAgent, contentDisposition, mimetype, contentLength)
  }

  @SuppressLint("ResourceType")
  private fun initializeOptions() {
    val intent = intent ?: return
    val finestWebView = intent.getSerializableExtra("FinestWebView") as FinestWebView?

    // set theme before resolving attributes depending on those
    setTheme((finestWebView?.theme.orEmpty()))

    // resolve themed attributes
    val typedValue = TypedValue()
    val typedArray = obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimaryDark, R.attr.colorPrimary, R.attr.colorAccent, android.R.attr.textColorPrimary, android.R.attr.textColorSecondary, android.R.attr.selectableItemBackground, android.R.attr.selectableItemBackgroundBorderless))
    val colorPrimaryDark = typedArray.getColor(0, ContextCompat.getColor(this, R.color.finestGray))
    val colorPrimary = typedArray.getColor(1, ContextCompat.getColor(this, R.color.finestWhite))
    val colorAccent = typedArray.getColor(2, ContextCompat.getColor(this, R.color.finestBlack))
    val textColorPrimary = typedArray.getColor(3, ContextCompat.getColor(this, R.color.finestBlack))
    val textColorSecondary = typedArray.getColor(4, ContextCompat.getColor(this, R.color.finestSilver))
    val selectableItemBackground = typedArray.getResourceId(5, 0)
    val selectableItemBackgroundBorderless = typedArray.getResourceId(6, 0)
    typedArray.recycle()
    key = finestWebView?.key.orEmpty()
    rtl = finestWebView?.rtl ?: resources.getBoolean(R.bool.is_right_to_left)
    statusBarColor = finestWebView?.statusBarColor ?: colorPrimaryDark
    toolbarColor = finestWebView?.toolbarColor ?: colorPrimary
    toolbarScrollFlags = finestWebView?.toolbarScrollFlags ?: (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
    iconDefaultColor = finestWebView?.iconDefaultColor ?: colorAccent
    iconDisabledColor = finestWebView?.iconDisabledColor ?: disableColor(iconDefaultColor)
    iconPressedColor = finestWebView?.iconPressedColor ?: iconDefaultColor
    iconSelector = finestWebView?.iconSelector ?: selectableItemBackgroundBorderless
    showIconClose = finestWebView?.showIconClose ?: true
    disableIconClose = finestWebView?.disableIconClose ?: false
    showIconBack = finestWebView?.showIconBack ?: true
    disableIconBack = finestWebView?.disableIconBack ?: false
    showIconForward = finestWebView?.showIconForward ?: true
    disableIconForward = finestWebView?.disableIconForward ?: false
    showIconMenu = finestWebView?.showIconMenu ?: true
    disableIconMenu = finestWebView?.disableIconMenu ?: false
    showSwipeRefreshLayout = finestWebView?.showSwipeRefreshLayout ?: true
    swipeRefreshColor = finestWebView?.swipeRefreshColor ?: colorAccent
    if (finestWebView?.swipeRefreshColors != null) {
      val colors = finestWebView.swipeRefreshColors?.size?.let { IntArray(it) }
      finestWebView.swipeRefreshColors?.forEachIndexed { index, i ->
        colors?.set(index, i)
      }
      swipeRefreshColors = colors
    }
    showDivider = finestWebView?.showDivider ?: true
    gradientDivider = finestWebView?.gradientDivider ?: true
    dividerColor = finestWebView?.dividerColor ?: ContextCompat.getColor(this, R.color.finestBlack10)
    dividerHeight = finestWebView?.dividerHeight ?: resources.getDimension(R.dimen.defaultDividerHeight)
    showProgressBar = finestWebView?.showProgressBar ?: true
    progressBarColor = finestWebView?.progressBarColor ?: colorAccent
    progressBarHeight = finestWebView?.progressBarHeight ?: resources.getDimension(R.dimen.defaultProgressBarHeight)
    progressBarPosition = finestWebView?.progressBarPosition ?: ProgressBarPosition.BOTTOM_OF_TOOLBAR
    titleDefault = finestWebView?.titleDefault
    updateTitleFromHtml = finestWebView?.updateTitleFromHtml ?: true
    titleSize = finestWebView?.titleSize ?: resources.getDimension(R.dimen.defaultTitleSize)
    titleFont = finestWebView?.titleFont ?: "Roboto-Medium.ttf"
    finestWebViewTitleColor = finestWebView?.titleColor ?: textColorPrimary
    showUrl = finestWebView?.showUrl ?: true
    urlSize = finestWebView?.urlSize ?: resources.getDimension(R.dimen.defaultUrlSize)
    urlFont = finestWebView?.urlFont ?: "Roboto-Regular.ttf"
    urlColor = finestWebView?.urlColor ?: textColorSecondary
    menuColor = finestWebView?.menuColor ?: ContextCompat.getColor(this, R.color.finestWhite)
    menuDropShadowColor = finestWebView?.menuDropShadowColor ?: ContextCompat.getColor(this, R.color.finestBlack10)
    menuDropShadowSize = finestWebView?.menuDropShadowSize ?: resources.getDimension(R.dimen.defaultMenuDropShadowSize)
    menuSelector = finestWebView?.menuSelector ?: selectableItemBackground
    menuTextSize = finestWebView?.menuTextSize ?: resources.getDimension(R.dimen.defaultMenuTextSize)
    menuTextFont = finestWebView?.menuTextFont ?: "Roboto-Regular.ttf"
    menuTextColor = finestWebView?.menuTextColor ?: ContextCompat.getColor(this, R.color.finestBlack)
    menuTextGravity = finestWebView?.menuTextGravity ?: (Gravity.CENTER_VERTICAL or Gravity.START)
    menuTextPaddingLeft = finestWebView?.menuTextPaddingLeft ?: (if (rtl) resources.getDimension(R.dimen.defaultMenuTextPaddingRight) else resources.getDimension(R.dimen.defaultMenuTextPaddingLeft))
    menuTextPaddingRight = finestWebView?.menuTextPaddingRight ?: (if (rtl) resources.getDimension(R.dimen.defaultMenuTextPaddingLeft) else resources.getDimension(R.dimen.defaultMenuTextPaddingRight))
    showMenuRefresh = finestWebView?.showMenuRefresh ?: true
    stringResRefresh = finestWebView?.stringResRefresh ?: R.string.refresh
    showMenuFind = finestWebView?.showMenuFind ?: false
    stringResFind = finestWebView?.stringResFind ?: R.string.find
    showMenuShareVia = finestWebView?.showMenuShareVia ?: true
    stringResShareVia = finestWebView?.stringResShareVia ?: R.string.share_via
    showMenuCopyLink = finestWebView?.showMenuCopyLink ?: true
    stringResCopyLink = finestWebView?.stringResCopyLink ?: R.string.copy_link
    showMenuOpenWith = finestWebView?.showMenuOpenWith ?: true
    stringResOpenWith = finestWebView?.stringResOpenWith ?: R.string.open_with
    animationCloseEnter = finestWebView?.animationCloseEnter ?: R.anim.modal_activity_close_enter
    animationCloseExit = finestWebView?.animationCloseExit ?: R.anim.modal_activity_close_exit
    backPressToClose = finestWebView?.backPressToClose ?: false
    stringResCopiedToClipboard = finestWebView?.stringResCopiedToClipboard ?: R.string.copied_to_clipboard
    webViewSupportZoom = finestWebView?.webViewSupportZoom
    webViewMediaPlaybackRequiresUserGesture = finestWebView?.webViewMediaPlaybackRequiresUserGesture
    webViewBuiltInZoomControls = finestWebView?.webViewBuiltInZoomControls ?: false
    webViewDisplayZoomControls = finestWebView?.webViewDisplayZoomControls ?: false
    webViewAllowFileAccess = finestWebView?.webViewAllowFileAccess ?: true
    webViewAllowContentAccess = finestWebView?.webViewAllowContentAccess
    webViewLoadWithOverviewMode = finestWebView?.webViewLoadWithOverviewMode ?: true
    webViewSaveFormData = finestWebView?.webViewSaveFormData
    webViewTextZoom = finestWebView?.webViewTextZoom
    webViewUseWideViewPort = finestWebView?.webViewUseWideViewPort
    webViewSupportMultipleWindows = finestWebView?.webViewSupportMultipleWindows
    webViewLayoutAlgorithm = finestWebView?.webViewLayoutAlgorithm
    webViewStandardFontFamily = finestWebView?.webViewStandardFontFamily
    webViewFixedFontFamily = finestWebView?.webViewFixedFontFamily
    webViewSansSerifFontFamily = finestWebView?.webViewSansSerifFontFamily
    webViewSerifFontFamily = finestWebView?.webViewSerifFontFamily
    webViewCursiveFontFamily = finestWebView?.webViewCursiveFontFamily
    webViewFantasyFontFamily = finestWebView?.webViewFantasyFontFamily
    webViewMinimumFontSize = finestWebView?.webViewMinimumFontSize
    webViewMinimumLogicalFontSize = finestWebView?.webViewMinimumLogicalFontSize
    webViewDefaultFontSize = finestWebView?.webViewDefaultFontSize
    webViewDefaultFixedFontSize = finestWebView?.webViewDefaultFixedFontSize
    webViewLoadsImagesAutomatically = finestWebView?.webViewLoadsImagesAutomatically
    webViewBlockNetworkImage = finestWebView?.webViewBlockNetworkImage
    webViewBlockNetworkLoads = finestWebView?.webViewBlockNetworkLoads
    webViewJavaScriptEnabled = finestWebView?.webViewJavaScriptEnabled ?: true
    webViewAllowUniversalAccessFromFileURLs = finestWebView?.webViewAllowUniversalAccessFromFileURLs
    webViewAllowFileAccessFromFileURLs = finestWebView?.webViewAllowFileAccessFromFileURLs
    webViewGeolocationDatabasePath = finestWebView?.webViewGeolocationDatabasePath
    webViewAppCacheEnabled = finestWebView?.webViewAppCacheEnabled ?: true
    webViewAppCachePath = finestWebView?.webViewAppCachePath
    webViewDatabaseEnabled = finestWebView?.webViewDatabaseEnabled
    webViewDomStorageEnabled = finestWebView?.webViewDomStorageEnabled ?: true
    webViewGeolocationEnabled = finestWebView?.webViewGeolocationEnabled
    webViewJavaScriptCanOpenWindowsAutomatically = finestWebView?.webViewJavaScriptCanOpenWindowsAutomatically
    webViewDefaultTextEncodingName = finestWebView?.webViewDefaultTextEncodingName
    webViewUserAgentString = finestWebView?.webViewUserAgentString
    webViewNeedInitialFocus = finestWebView?.webViewNeedInitialFocus
    webViewCacheMode = finestWebView?.webViewCacheMode
    webViewMixedContentMode = finestWebView?.webViewMixedContentMode
    webViewOffscreenPreRaster = finestWebView?.webViewOffscreenPreRaster
    injectJavaScript = finestWebView?.injectJavaScript
    mimeType = finestWebView?.mimeType
    encoding = finestWebView?.encoding
    data = finestWebView?.data
    url = finestWebView?.url
  }

  private fun bindViews() {
    coordinatorLayout = findViewById<View>(R.id.coordinatorLayout) as CoordinatorLayout
    appBar = findViewById<View>(R.id.appBar) as AppBarLayout
    toolbar = findViewById<View>(R.id.toolbar) as Toolbar
    toolbarLayout = findViewById<View>(R.id.toolbarLayout) as RelativeLayout
    title = findViewById<View>(R.id.title) as TextView
    urlTv = findViewById<View>(R.id.url) as TextView
    close = findViewById<View>(R.id.close) as AppCompatImageButton
    back = findViewById<View>(R.id.back) as AppCompatImageButton
    forward = findViewById<View>(R.id.forward) as AppCompatImageButton
    more = findViewById<View>(R.id.more) as AppCompatImageButton
    close?.setOnClickListener(this)
    back?.setOnClickListener(this)
    forward?.setOnClickListener(this)
    more?.setOnClickListener(this)
    swipeRefreshLayout = findViewById<View>(R.id.swipeRefreshLayout) as SwipeRefreshLayout
    gradient = findViewById(R.id.gradient)
    divider = findViewById(R.id.divider)
    progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
    menuLayout = findViewById<View>(R.id.menuLayout) as RelativeLayout
    shadowLayout = findViewById<View>(R.id.shadowLayout) as ShadowLayout
    menuBackground = findViewById<View>(R.id.menuBackground) as LinearLayout
    menuRefresh = findViewById<View>(R.id.menuRefresh) as LinearLayout
    menuRefreshTv = findViewById<View>(R.id.menuRefreshTv) as TextView
    menuFind = findViewById<View>(R.id.menuFind) as LinearLayout
    menuFindTv = findViewById<View>(R.id.menuFindTv) as TextView
    menuShareVia = findViewById<View>(R.id.menuShareVia) as LinearLayout
    menuShareViaTv = findViewById<View>(R.id.menuShareViaTv) as TextView
    menuCopyLink = findViewById<View>(R.id.menuCopyLink) as LinearLayout
    menuCopyLinkTv = findViewById<View>(R.id.menuCopyLinkTv) as TextView
    menuOpenWith = findViewById<View>(R.id.menuOpenWith) as LinearLayout
    menuOpenWithTv = findViewById<View>(R.id.menuOpenWithTv) as TextView
    webLayout = findViewById<View>(R.id.webLayout) as FrameLayout
    webView = WebView(this)
    webLayout?.addView(webView)
  }

  private fun layoutViews() {
    setSupportActionBar(toolbar)
    run { // AppBar
      var toolbarHeight = resources.getDimension(R.dimen.toolbarHeight)
      if (!gradientDivider) {
        toolbarHeight += dividerHeight
      }
      val params = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toolbarHeight.toInt())
      appBar?.layoutParams = params
      coordinatorLayout?.requestLayout()
    }
    run { // Toolbar
      val toolbarHeight = resources.getDimension(R.dimen.toolbarHeight)
      val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toolbarHeight.toInt())
      toolbarLayout?.minimumHeight = toolbarHeight.toInt()
      toolbarLayout?.layoutParams = params
      coordinatorLayout?.requestLayout()
    }
    run { // TextViews
      val maxWidth = maxWidth
      title?.maxWidth = maxWidth
      urlTv?.maxWidth = maxWidth
      requestCenterLayout()
    }
    run { // Icons
      updateIcon(close, if (rtl) R.drawable.more else R.drawable.close)
      updateIcon(back, R.drawable.back)
      updateIcon(forward, R.drawable.forward)
      updateIcon(more, if (rtl) R.drawable.close else R.drawable.more)
    }
    run { // Divider
      if (gradientDivider) {
        val toolbarHeight = resources.getDimension(R.dimen.toolbarHeight)
        val params = gradient?.layoutParams as CoordinatorLayout.LayoutParams
        params.setMargins(0, toolbarHeight.toInt(), 0, 0)
        gradient?.layoutParams = params
      }
    }
    run { // ProgressBar
      progressBar?.minimumHeight = progressBarHeight.toInt()
      val params = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, progressBarHeight.toInt())
      val toolbarHeight = resources.getDimension(R.dimen.toolbarHeight)
      when (progressBarPosition) {
        ProgressBarPosition.TOP_OF_TOOLBAR -> params.setMargins(0, 0, 0, 0)
        ProgressBarPosition.BOTTOM_OF_TOOLBAR -> params.setMargins(0, toolbarHeight.toInt() - progressBarHeight.toInt(), 0, 0)
        ProgressBarPosition.TOP_OF_WEBVIEW -> params.setMargins(0, toolbarHeight.toInt(), 0, 0)
        ProgressBarPosition.BOTTOM_OF_WEBVIEW -> params.setMargins(0, getHeight(this) - progressBarHeight.toInt(), 0, 0)
      }
      progressBar?.layoutParams = params
    }
    run { // WebLayout
      val toolbarHeight = resources.getDimension(R.dimen.toolbarHeight)
      val statusBarHeight = getStatusBarHeight(this)
      val screenHeight = getHeight(this)
      var webLayoutMinimumHeight = screenHeight - toolbarHeight - statusBarHeight
      if (showDivider && !gradientDivider) {
        webLayoutMinimumHeight -= dividerHeight
      }
      webLayout?.minimumHeight = webLayoutMinimumHeight.toInt()
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun initializeViews() {
    setSupportActionBar(toolbar)
    run { // StatusBar
      val window = window
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.statusBarColor = statusBarColor
    }
    run { // AppBar
      appBar?.addOnOffsetChangedListener(this)
    }
    run { // Toolbar
      toolbar?.setBackgroundColor(toolbarColor)
      val params = toolbar?.layoutParams as AppBarLayout.LayoutParams
      params.scrollFlags = toolbarScrollFlags
      toolbar?.layoutParams = params
    }
    run { // TextViews
      title?.text = titleDefault
      title?.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
      title?.typeface = TypefaceUtil[this, titleFont.orEmpty()]
      title?.setTextColor(titleColor)
      urlTv?.visibility = if (showUrl) View.VISIBLE else View.GONE
      urlTv?.text = getHost(url.orEmpty())
      urlTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, urlSize)
      urlTv?.typeface = TypefaceUtil[this, urlFont.orEmpty()]
      urlTv?.setTextColor(urlColor)
      requestCenterLayout()
    }
    run { // Icons
      close?.setBackgroundResource(iconSelector)
      back?.setBackgroundResource(iconSelector)
      forward?.setBackgroundResource(iconSelector)
      more?.setBackgroundResource(iconSelector)
      close?.visibility = if (showIconClose) View.VISIBLE else View.GONE
      close?.isEnabled = !disableIconClose
      if ((showMenuRefresh || showMenuFind || showMenuShareVia || showMenuCopyLink || showMenuOpenWith) && showIconMenu) {
        more?.visibility = View.VISIBLE
      } else {
        more?.visibility = View.GONE
      }
      more?.isEnabled = !disableIconMenu
    }
    run { // WebView
      webView?.webChromeClient = MyWebChromeClient()
      webView?.webViewClient = MyWebViewClient()
      webView?.setDownloadListener(downloadListener)
      val settings = webView?.settings
      settings?.apply {
        webViewSupportZoom?.let { setSupportZoom(it) }
        if (webViewMediaPlaybackRequiresUserGesture != null) {
          mediaPlaybackRequiresUserGesture = webViewMediaPlaybackRequiresUserGesture.orEmpty()
        }
        if (webViewBuiltInZoomControls != null) {
          builtInZoomControls = webViewBuiltInZoomControls.orEmpty()
          if (webViewBuiltInZoomControls as Boolean) { // Remove NestedScrollView to enable BuiltInZoomControls
            (webView?.parent as ViewGroup).removeAllViews()
            swipeRefreshLayout?.addView(webView)
            swipeRefreshLayout?.removeViewAt(1)
          }
        }
        webViewDisplayZoomControls?.let { displayZoomControls = it }
        webViewAllowFileAccess?.let { allowFileAccess = it }
        webViewAllowContentAccess?.let { allowContentAccess = it }
        webViewLoadWithOverviewMode?.let { loadWithOverviewMode = it }
        webViewSaveFormData?.let { saveFormData = it }
        webViewTextZoom?.let { textZoom = it }
        webViewUseWideViewPort?.let { useWideViewPort = it }
        webViewSupportMultipleWindows?.let { setSupportMultipleWindows(it) }
        webViewLayoutAlgorithm?.let { layoutAlgorithm = it }
        webViewStandardFontFamily?.let { standardFontFamily = it }
        webViewFixedFontFamily?.let { fixedFontFamily = it }
        webViewSansSerifFontFamily?.let { sansSerifFontFamily = it }
        webViewSerifFontFamily?.let { serifFontFamily = it }
        webViewCursiveFontFamily?.let { cursiveFontFamily = it  }
        webViewFantasyFontFamily?.let { fantasyFontFamily = it }
        webViewMinimumFontSize?.let { minimumFontSize = it }
        webViewMinimumLogicalFontSize?.let { minimumLogicalFontSize = it }
        webViewDefaultFontSize?.let { defaultFontSize = it }
        webViewDefaultFixedFontSize?.let { defaultFixedFontSize = it }
        webViewLoadsImagesAutomatically?.let { loadsImagesAutomatically = it }
        webViewBlockNetworkImage?.let { blockNetworkImage = it }
        webViewBlockNetworkLoads?.let { blockNetworkLoads = it }
        webViewJavaScriptEnabled?.let { javaScriptEnabled = it }
        webViewAllowUniversalAccessFromFileURLs?.let { allowUniversalAccessFromFileURLs = it }
        webViewAllowFileAccessFromFileURLs?.let { allowFileAccessFromFileURLs = it }
        webViewGeolocationDatabasePath?.let { setGeolocationDatabasePath(it) }
        webViewAppCacheEnabled?.let { setAppCacheEnabled(it) }
        webViewAppCachePath?.let { setAppCachePath(it) }
        webViewDatabaseEnabled?.let { databaseEnabled = it }
        webViewDomStorageEnabled?.let { domStorageEnabled = it }
        webViewGeolocationEnabled?.let { webViewGeolocationEnabled = it }
        webViewJavaScriptCanOpenWindowsAutomatically?.let { javaScriptCanOpenWindowsAutomatically = it }
        webViewDefaultTextEncodingName?.let { defaultTextEncodingName = it }
        webViewUserAgentString?.let { userAgentString = it }
        webViewNeedInitialFocus?.let { setNeedInitialFocus(it)}
        webViewCacheMode?.let { cacheMode = it }
        webViewMixedContentMode?.let { mixedContentMode = it }
        webViewOffscreenPreRaster?.let { if (VERSION.SDK_INT >= VERSION_CODES.M) {
          offscreenPreRaster = it
        }
        }
      }

      if (data != null) {
        webView?.loadData(data.orEmpty(), mimeType, encoding)
      } else {
        webView?.loadUrl(url.orEmpty())
      }
    }
    run { // SwipeRefreshLayout
      swipeRefreshLayout?.isEnabled = showSwipeRefreshLayout
      if (showSwipeRefreshLayout) {
        swipeRefreshLayout?.post { swipeRefreshLayout?.isRefreshing = true }
      }
      if (swipeRefreshColors == null) {
        swipeRefreshLayout?.setColorSchemeColors(swipeRefreshColor)
      } else {
        swipeRefreshLayout?.setColorSchemeColors(swipeRefreshColor)
      }
      swipeRefreshLayout?.setOnRefreshListener { webView?.reload() }
    }
    run { // Divider
      gradient?.visibility = if (showDivider && gradientDivider) View.VISIBLE else View.GONE
      divider?.visibility = if (showDivider && !gradientDivider) View.VISIBLE else View.GONE
      if (gradientDivider) {
        val dividerWidth = getWidth(this)
        val bitmap = getGradientBitmap(dividerWidth, dividerHeight.toInt(), dividerColor)
        val drawable = BitmapDrawable(resources, bitmap)
        gradient?.background = drawable
        val params = gradient?.layoutParams as CoordinatorLayout.LayoutParams
        params.height = dividerHeight.toInt()
        gradient?.layoutParams = params
      } else {
        divider?.setBackgroundColor(dividerColor)
        val params = divider?.layoutParams as LinearLayout.LayoutParams
        params.height = dividerHeight.toInt()
        divider?.layoutParams = params
      }
    }
    run { // ProgressBar
      progressBar?.visibility = if (showProgressBar) View.VISIBLE else View.GONE
      progressBar?.progressDrawable?.setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN)
      progressBar?.minimumHeight = progressBarHeight.toInt()
      val params = CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, progressBarHeight.toInt())
      val toolbarHeight = resources.getDimension(R.dimen.toolbarHeight)
      when (progressBarPosition) {
        ProgressBarPosition.TOP_OF_TOOLBAR -> params.setMargins(0, 0, 0, 0)
        ProgressBarPosition.BOTTOM_OF_TOOLBAR -> params.setMargins(0, toolbarHeight.toInt() - progressBarHeight.toInt(), 0, 0)
        ProgressBarPosition.TOP_OF_WEBVIEW -> params.setMargins(0, toolbarHeight.toInt(), 0, 0)
        ProgressBarPosition.BOTTOM_OF_WEBVIEW -> params.setMargins(0, getHeight(this) - progressBarHeight.toInt(), 0, 0)
      }
      progressBar?.layoutParams = params
    }
    run { // Menu
      val drawable = GradientDrawable()
      drawable.cornerRadius = resources.getDimension(R.dimen.defaultMenuCornerRadius)
      drawable.setColor(menuColor)
      menuBackground?.background = drawable
      shadowLayout?.setShadowColor(menuDropShadowColor)
      shadowLayout?.setShadowSize(menuDropShadowSize)
      val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      val margin = (resources.getDimension(R.dimen.defaultMenuLayoutMargin) - menuDropShadowSize).toInt()
      params.setMargins(0, margin, margin, 0)
      params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
      params.addRule(if (rtl) RelativeLayout.ALIGN_PARENT_LEFT else RelativeLayout.ALIGN_PARENT_RIGHT)
      shadowLayout?.layoutParams = params
      menuRefresh?.visibility = if (showMenuRefresh) View.VISIBLE else View.GONE
      menuRefresh?.setBackgroundResource(menuSelector)
      menuRefresh?.gravity = menuTextGravity
      menuRefreshTv?.setText(stringResRefresh)
      menuRefreshTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize)
      menuRefreshTv?.typeface = TypefaceUtil[this, menuTextFont.orEmpty()]
      menuRefreshTv?.setTextColor(menuTextColor)
      menuRefreshTv?.setPadding(menuTextPaddingLeft.toInt(), 0, menuTextPaddingRight.toInt(), 0)
      menuFind?.visibility = if (showMenuFind) View.VISIBLE else View.GONE
      menuFind?.setBackgroundResource(menuSelector)
      menuFind?.gravity = menuTextGravity
      menuFindTv?.setText(stringResFind)
      menuFindTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize)
      menuFindTv?.typeface = TypefaceUtil[this, menuTextFont.orEmpty()]
      menuFindTv?.setTextColor(menuTextColor)
      menuFindTv?.setPadding(menuTextPaddingLeft.toInt(), 0, menuTextPaddingRight.toInt(), 0)
      menuShareVia?.visibility = if (showMenuShareVia) View.VISIBLE else View.GONE
      menuShareVia?.setBackgroundResource(menuSelector)
      menuShareVia?.gravity = menuTextGravity
      menuShareViaTv?.setText(stringResShareVia)
      menuShareViaTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize)
      menuShareViaTv?.typeface = TypefaceUtil[this, menuTextFont.orEmpty()]
      menuShareViaTv?.setTextColor(menuTextColor)
      menuShareViaTv?.setPadding(menuTextPaddingLeft.toInt(), 0, menuTextPaddingRight.toInt(), 0)
      menuCopyLink?.visibility = if (showMenuCopyLink) View.VISIBLE else View.GONE
      menuCopyLink?.setBackgroundResource(menuSelector)
      menuCopyLink?.gravity = menuTextGravity
      menuCopyLinkTv?.setText(stringResCopyLink)
      menuCopyLinkTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize)
      menuCopyLinkTv?.typeface = TypefaceUtil[this, menuTextFont.orEmpty()]
      menuCopyLinkTv?.setTextColor(menuTextColor)
      menuCopyLinkTv?.setPadding(menuTextPaddingLeft.toInt(), 0, menuTextPaddingRight.toInt(), 0)
      menuOpenWith?.visibility = if (showMenuOpenWith) View.VISIBLE else View.GONE
      menuOpenWith?.setBackgroundResource(menuSelector)
      menuOpenWith?.gravity = menuTextGravity
      menuOpenWithTv?.setText(stringResOpenWith)
      menuOpenWithTv?.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize)
      menuOpenWithTv?.typeface = TypefaceUtil[this, menuTextFont.orEmpty()]
      menuOpenWithTv?.setTextColor(menuTextColor)
      menuOpenWithTv?.setPadding(menuTextPaddingLeft.toInt(), 0, menuTextPaddingRight.toInt(), 0)
    }
  }

  private val maxWidth: Int
    get() = if (forward?.visibility == View.VISIBLE) {
      getWidth(this) - dpToPx(this, 100)
    } else {
      getWidth(this) - dpToPx(this, 52)
    }

  private fun updateIcon(icon: ImageButton?, @DrawableRes drawableRes: Int) {
    val states = StateListDrawable()
    run {
      val bitmap = getColoredBitmap(this, drawableRes, iconPressedColor)
      val drawable = BitmapDrawable(resources, bitmap)
      states.addState(intArrayOf(android.R.attr.state_pressed), drawable)
    }
    run {
      val bitmap = getColoredBitmap(this, drawableRes, iconDisabledColor)
      val drawable = BitmapDrawable(resources, bitmap)
      states.addState(intArrayOf(-android.R.attr.state_enabled), drawable)
    }
    run {
      val bitmap = getColoredBitmap(this, drawableRes, iconDefaultColor)
      val drawable = BitmapDrawable(resources, bitmap)
      states.addState(intArrayOf(), drawable)
    }
    icon?.setImageDrawable(states)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initializeOptions()
    setContentView(R.layout.finest_web_view)
    bindViews()
    layoutViews()
    initializeViews()
  }

  override fun onBackPressed() {
    if (menuLayout?.visibility == View.VISIBLE) {
      hideMenu()
    } else if (backPressToClose || webView?.canGoBack() == false) {
      exitActivity()
    } else {
      webView?.goBack()
    }
  }

  override fun onClick(v: View) {
    val viewId = v.id
    if (viewId == R.id.close) {
      if (rtl) {
        showMenu()
      } else {
        exitActivity()
      }
    } else if (viewId == R.id.back) {
      if (rtl) {
        webView?.goForward()
      } else {
        webView?.goBack()
      }
    } else if (viewId == R.id.forward) {
      if (rtl) {
        webView?.goBack()
      } else {
        webView?.goForward()
      }
    } else if (viewId == R.id.more) {
      if (rtl) {
        exitActivity()
      } else {
        showMenu()
      }
    } else if (viewId == R.id.menuLayout) {
      hideMenu()
    } else if (viewId == R.id.menuRefresh) {
      webView?.reload()
      hideMenu()
    } else if (viewId == R.id.menuFind) {
      webView?.showFindDialog("", true)
      hideMenu()
    } else if (viewId == R.id.menuShareVia) {
      val sendIntent = Intent()
      sendIntent.action = Intent.ACTION_SEND
      sendIntent.putExtra(Intent.EXTRA_TEXT, webView?.url)
      sendIntent.type = "text/plain"
      startActivity(Intent.createChooser(sendIntent, resources.getString(stringResShareVia)))
      hideMenu()
    } else if (viewId == R.id.menuCopyLink) {
      val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
      val clip = ClipData.newPlainText("ClipboardManagerUtil", webView?.url)
      clipboardManager.setPrimaryClip(clip)
      val snackBar = coordinatorLayout?.let { Snackbar.make(it, getString(stringResCopiedToClipboard), Snackbar.LENGTH_LONG) }
      val snackBarView = snackBar?.view
      snackBarView?.setBackgroundColor(toolbarColor)
      if (snackBarView is ViewGroup) {
        updateChildTextView(snackBarView)
      }
      snackBar?.show()
      hideMenu()
    } else if (viewId == R.id.menuOpenWith) {
      val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webView?.url))
      startActivity(browserIntent)
      hideMenu()
    }
  }

  private fun updateChildTextView(viewGroup: ViewGroup?) {
    if (viewGroup == null || viewGroup.childCount == 0) {
      return
    }
    for (i in 0 until viewGroup.childCount) {
      val view = viewGroup.getChildAt(i)
      if (view is TextView) {
        view.setTextColor(titleColor)
        view.typeface = TypefaceUtil[this, titleFont.orEmpty()]
        view.setLineSpacing(0f, 1.1f)
        view.includeFontPadding = false
      }
      if (view is ViewGroup) {
        updateChildTextView(view)
      }
    }
  }

  private fun showMenu() {
    menuLayout?.visibility = View.VISIBLE
    val popupAnim = AnimationUtils.loadAnimation(this, R.anim.popup_flyout_show)
    shadowLayout?.startAnimation(popupAnim)
  }

  private fun hideMenu() {
    val popupAnim = AnimationUtils.loadAnimation(this, R.anim.popup_flyout_hide)
    shadowLayout?.startAnimation(popupAnim)
    popupAnim.setAnimationListener(object : AnimationListener {
      override fun onAnimationStart(animation: Animation) {}
      override fun onAnimationEnd(animation: Animation) {
        menuLayout?.visibility = View.GONE
      }

      override fun onAnimationRepeat(animation: Animation) {}
    })
  }

  private fun exitActivity() {
    super.onBackPressed()
    overridePendingTransition(animationCloseEnter, animationCloseExit)
  }

  override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
    if (toolbarScrollFlags == 0) {
      return
    }
    gradient?.translationY = verticalOffset.toFloat()
    gradient?.alpha = 1 - abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange.toFloat()
    when (progressBarPosition) {
      ProgressBarPosition.BOTTOM_OF_TOOLBAR -> progressBar?.translationY = verticalOffset.toFloat().coerceAtLeast(progressBarHeight - appBarLayout.totalScrollRange)
      ProgressBarPosition.TOP_OF_WEBVIEW -> progressBar?.translationY = verticalOffset.toFloat()
      ProgressBarPosition.TOP_OF_TOOLBAR, ProgressBarPosition.BOTTOM_OF_WEBVIEW -> {
      }
      else -> {
      }
    }
    menuLayout?.translationY = verticalOffset.toFloat().coerceAtLeast(-resources.getDimension(R.dimen.defaultMenuLayoutMargin))
  }

  private fun requestCenterLayout() {
    val maxWidth: Int = if (webView?.canGoBack() == true || webView?.canGoForward() == true) {
      getWidth(this) - dpToPx(this, 48) * 4
    } else {
      getWidth(this) - dpToPx(this, 48) * 2
    }
    title?.maxWidth = maxWidth
    urlTv?.maxWidth = maxWidth
    title?.requestLayout()
    urlTv?.requestLayout()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      layoutViews()
    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
      layoutViews()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    unregister(this@FinestWebViewActivity, key)
    if (webView == null) {
      return
    }
    webView?.onPause()
    destroyWebView()
  }

  // Wait for zoom control to fade away
  // https://code.google.com/p/android/issues/detail?id=15694
  // http://stackoverflow.com/a/5966151/1797648
  private fun destroyWebView() {
    Handler().postDelayed({
                            if (webView != null) {
                              webView?.destroy()
                            }
                          }, ViewConfiguration.getZoomControlsTimeout() + 1000L)
  }

  inner class MyWebChromeClient : WebChromeClient() {
    override fun onProgressChanged(view: WebView, progress: Int) {
      var progress = progress
      onProgressChanged(this@FinestWebViewActivity, key, progress)
      if (showSwipeRefreshLayout) {
        if (swipeRefreshLayout?.isRefreshing == true && progress == 100) {
          swipeRefreshLayout?.post { swipeRefreshLayout?.isRefreshing = false }
        }
        if (swipeRefreshLayout?.isRefreshing == false && progress != 100) {
          swipeRefreshLayout?.post { swipeRefreshLayout?.isRefreshing = true }
        }
      }
      if (progress == 100) {
        progress = 0
      }
      progressBar?.progress = progress
    }

    override fun onReceivedTitle(view: WebView, title: String) {
      onReceivedTitle(this@FinestWebViewActivity, key, title)
    }

    override fun onReceivedTouchIconUrl(view: WebView, url: String, precomposed: Boolean) {
      onReceivedTouchIconUrl(this@FinestWebViewActivity, key, url, precomposed)
    }
  }

  inner class MyWebViewClient : WebViewClient() {
    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
      onPageStarted(this@FinestWebViewActivity, key, url)
      if (!url.contains("docs.google.com") && url.endsWith(".pdf")) {
        webView?.loadUrl("http://docs.google.com/gview?embedded=true&url=$url")
      }
    }

    override fun onPageFinished(view: WebView, url: String) {
      onPageFinished(this@FinestWebViewActivity, key, url)
      if (updateTitleFromHtml) {
        title?.text = view.title
      }
      urlTv?.text = getHost(url)
      requestCenterLayout()
      if (view.canGoBack() || view.canGoForward()) {
        back?.visibility = if (showIconBack) View.VISIBLE else View.GONE
        forward?.visibility = if (showIconForward) View.VISIBLE else View.GONE
        back?.isEnabled = !disableIconBack && if (rtl) view.canGoForward() else view.canGoBack()
        forward?.isEnabled = !disableIconForward && if (rtl) view.canGoBack() else view.canGoForward()
      } else {
        back?.visibility = View.GONE
        forward?.visibility = View.GONE
      }
      if (injectJavaScript != null) {
        webView?.evaluateJavascript(injectJavaScript.orEmpty(), null)
      }
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
      return if (url.endsWith(".mp4")) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "video/*")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        view.context.startActivity(intent) // If we return true, onPageStarted, onPageFinished won't be called.
        true
      } else if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("smsto:") || url.startsWith("mms:") || url.startsWith("mmsto:")) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        view.context.startActivity(intent)
        true // If we return true, onPageStarted, onPageFinished won't be called.
      } else if (url.startsWith("mailto:")) {
        val mt = MailTo.parse(url)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "text/html"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mt.to))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, mt.subject)
        emailIntent.putExtra(Intent.EXTRA_CC, mt.cc)
        emailIntent.putExtra(Intent.EXTRA_TEXT, mt.body)
        startActivity(emailIntent)
        true
      } else {
        super.shouldOverrideUrlLoading(view, url)
      }
    }

    override fun onLoadResource(view: WebView, url: String) {
      onLoadResource(this@FinestWebViewActivity, key, url)
    }

    override fun onPageCommitVisible(view: WebView, url: String) {
      onPageCommitVisible(this@FinestWebViewActivity, key, url)
    }
  }
}