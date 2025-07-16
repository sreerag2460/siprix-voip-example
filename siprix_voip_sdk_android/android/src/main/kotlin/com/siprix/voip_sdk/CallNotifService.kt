@file:Suppress("SpellCheckingInspection")
package com.siprix.voip_sdk

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Person
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.siprix.ISiprixRinger
import com.siprix.ISiprixServiceListener
import com.siprix.SiprixCore
import com.siprix.SiprixRinger


open class CallNotifService : Service() {
    private lateinit var _ringer: ISiprixRinger
    private lateinit var _appResources : LabelResources
    private lateinit var _eventsListener : CoreEventsListener

    private var _wakeLock: WakeLock? = null
    private val _binder: IBinder = LocalBinder()

    private var _foregroundModeStarted: Boolean = false
    private var _requestCode: Int = 1

    inner class LocalBinder : Binder() {
        val service: CallNotifService
            get() =// Return this instance of LocalService so clients can call public methods.
                this@CallNotifService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        _eventsListener = CoreEventsListener(this)
        _appResources = LabelResources(this)
        _ringer = SiprixRinger(this)
        createNotifChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        stopForegroundMode()
        notifMgr.cancelAll()

        if(core != null) {
            core?.setServiceListener(null)
            core?.setModelListener(null)
            core?.unInitialize()
            core = null
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(intent: Intent): IBinder? {
        return _binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand $intent")
        val result = super.onStartCommand(intent, flags, startId)

        if(intent != null) {
            if (kActionIncomingCallReject == intent.action) {
                handleIncomingCallIntent(intent)
            }

            if(kActionAppStarted == intent.action) {
                core?.setServiceListener(_eventsListener)
            }

            if(kActionIncomingCallStopRinger == intent.action) {
                _ringer.stop()
            }
        }
        return result
    }

    private fun createNotifChannel() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //NotificationChannel msgChannel = new NotificationChannel(kMsgChannelId,
            //        appName, NotificationManager.IMPORTANCE_DEFAULT);
            //msgChannel.enableLights(true);
            //notifMgr_.createNotificationChannel(msgChannel);

            val callChannel = NotificationChannel(
                kCallChannelId, _appResources.appName, NotificationManager.IMPORTANCE_HIGH
            )
            callChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            callChannel.description = _appResources.channelDescr
            //callChannel.enableLights(true);
            notifMgr.createNotificationChannel(callChannel)
        }
    }

    protected fun getIntentActivity(action: String?, bundle: Bundle): PendingIntent {
        val activityIntent = packageManager.getLaunchIntentForPackage(this.packageName)
        if(activityIntent==null) {
            Log.e(TAG, "Can't get launch intent!")
        }
        activityIntent?.action = action

        activityIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activityIntent?.putExtras(bundle)
        return PendingIntent.getActivity(
            this, _requestCode++, activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    protected fun getIntentService(action: String?, bundle: Bundle): PendingIntent {
        val srvIntent = Intent(action)
        srvIntent.setClassName(this, CallNotifService::class.java.name)
        srvIntent.putExtras(bundle)
        return PendingIntent.getService(
            this, _requestCode++, srvIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun handleIncomingCallIntent(intent: Intent) {
        val args = intent.extras
        val callId = args?.getInt(kExtraCallId) ?: 0
        if (callId <= 0) return
        //if (kActionIncomingCallAccept == intent.action) {
        //    core!!.callAccept(callId, false) //TODO add 'withVideo'
        //} else
        if (kActionIncomingCallReject == intent.action) {
            core!!.callReject(callId)
        }
        cancelNotification(callId)
    }

    protected fun cancelNotification(callId: Int) {
        notifMgr.cancel(getNotifId(callId))
    }

    protected val appResources: LabelResources
        get() = _appResources

    protected val notifMgr: NotificationManager
        get() = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    protected fun getNotifId(callId: Int) : Int{
        return kCallBaseNotifId + callId
    }

    fun buildBundle(callId: Int, accId: Int,
                     withVideo: Boolean, hdrFrom: String?, hdrTo: String?) : Bundle{
        val bundle = Bundle()
        bundle.putInt(kExtraCallId, callId)
        bundle.putInt(kExtraAccId, accId)
        bundle.putBoolean(kExtraWithVideo, withVideo)
        bundle.putString(kExtraHdrFrom, hdrFrom)
        bundle.putString(kExtraHdrTo, hdrTo)
        return bundle
    }

    open fun displayIncomingCallNotification(
        callId: Int, accId: Int,
        withVideo: Boolean, hdrFrom: String?, hdrTo: String?
    ) {
        Log.d(TAG, "displayIncomingCallNotification $callId")
        val bundle = buildBundle(callId, accId, withVideo, hdrFrom, hdrTo)

        val contentIntent = getIntentActivity(kActionIncomingCall, bundle)
        val pendingAcceptCall = getIntentActivity(kActionIncomingCallAccept, bundle)
        val pendingRejectCall = getIntentService(kActionIncomingCallReject, bundle)
        val contentStr = buildContentString(hdrFrom)

        if (VERSION.SDK_INT >= 31) {
            val caller = Person.Builder().setName(contentStr).setImportant(true).build()
            val builder: Notification.Builder = Notification.Builder(this, kCallChannelId)
                .setSmallIcon(_appResources.iconId)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setFullScreenIntent(contentIntent, true)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setStyle(Notification.CallStyle.forIncomingCall(caller, pendingRejectCall, pendingAcceptCall))

            notifMgr.notify(getNotifId(callId), builder.build())
        } else {
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, kCallChannelId)
                .setSmallIcon(_appResources.iconId)
                .setContentTitle(_appResources.contentLabel)
                .setContentText(contentStr)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(contentIntent)
                .setFullScreenIntent(contentIntent, true)
                .setOngoing(true)
                .addAction(0, _appResources.rejectBtnLabel, pendingRejectCall)
                .addAction(0, _appResources.acceptBtnLabel, pendingAcceptCall)
                .setDeleteIntent(getIntentService(kActionIncomingCallStopRinger, bundle))
                .setCategory(NotificationCompat.CATEGORY_CALL)

            notifMgr.notify(getNotifId(callId), builder.build())
        }
    }

    fun stopForegroundMode() {
        releaseWakelock()
        if (VERSION.SDK_INT >= 33){
            stopForeground(STOP_FOREGROUND_REMOVE)
        }else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        _foregroundModeStarted = false
    }

    fun startForegroundMode(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE)
            != PackageManager.PERMISSION_GRANTED) return false

        acquireWakelock()

        val contentIntent = getIntentActivity(kActionForeground, Bundle())
        val builder: Notification.Builder = if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, kCallChannelId)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        builder.setSmallIcon(_appResources.iconId)
            .setContentTitle(_appResources.appName)
            .setContentText(_appResources.foregroundDescr)
            .setContentIntent(contentIntent)
            .build() // getNotification()

        if (VERSION.SDK_INT >= 29) {
            startForeground(kForegroundId, builder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            startForeground(kForegroundId, builder.build())
        }
        _foregroundModeStarted = true
        return true
    }

    fun isForegroundMode() :Boolean {
        return _foregroundModeStarted
    }

    private fun acquireWakelock() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK)
            != PackageManager.PERMISSION_GRANTED) return

        if (_wakeLock == null) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            _wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Siprix:WakeLock.")
        }
        if (_wakeLock != null && !_wakeLock!!.isHeld) {
            _wakeLock!!.acquire()
        }
    }

    private fun releaseWakelock() {
        if (_wakeLock != null && _wakeLock!!.isHeld) {
            _wakeLock!!.release()
        }
    }

    //Handle core events
    class CoreEventsListener(service : CallNotifService) : ISiprixServiceListener {
        private val _service = service

        override fun onRingerState(start: Boolean) {
            if (start) _service._ringer.start()
            else       _service._ringer.stop()
        }

        override fun onCallTerminated(callId: Int, statusCode: Int) {
            _service.cancelNotification(callId)
        }

        override fun onCallIncoming(
            callId: Int, accId: Int, withVideo: Boolean,
            hdrFrom: String, hdrTo: String
        ) {
            Log.i(TAG, "onCallIncoming $callId")
            if (!_service.isAppInForeground()) {
                _service.displayIncomingCallNotification(callId, accId, withVideo, hdrFrom, hdrTo)
            }
        }
    }

    private fun isAppInForeground(): Boolean {
        val am = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appProcs = am.runningAppProcesses
        for (app in appProcs) {
            if (app.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                val found = listOf(*app.pkgList).contains(packageName)
                if (found) return true
            }
        }
        return false
    }

    protected fun parseDisplayName(hdrFrom: String?)  : String {
        if(hdrFrom == null) return "?"
        val startIndex = hdrFrom.indexOf("\"")
        val endIndex = if(startIndex == -1) -1 else hdrFrom.indexOf("\"", startIndex + 1)
        return if(endIndex==-1) "?" else hdrFrom.substring(startIndex+1, endIndex)
    }

    protected fun parseExt(hdrFrom: String?)  : String {
        if(hdrFrom == null) return "?"
        val startIndex = hdrFrom.indexOf(':')
        val endIndex = if(startIndex == -1) -1 else hdrFrom.indexOf("@", startIndex + 1)
        return if(endIndex==-1) "?" else hdrFrom.substring(startIndex+1, endIndex)
    }

    protected fun buildContentString(hdrFrom: String?) : String {
        //hdrFrom has format: "displName" <sip:ext@domain:port>
        if(hdrFrom==null) return "???"

        //Return string same as uses flutter app in 'CallModel.nameAndExt'
        val displName = parseDisplayName(hdrFrom)
        val sipExt = parseExt(hdrFrom)
        return if(displName.isEmpty()) sipExt else "$displName ($sipExt)"
    }

    class LabelResources (service : CallNotifService) {
        private val _service = service
        val appName: String
        val channelDescr: String
        val foregroundDescr: String

        val contentLabel: String
        val rejectBtnLabel: String
        val acceptBtnLabel: String
        val iconId: Int

        init {
            appName = getStrResource("app_name") ?:
                        if(service.applicationInfo!=null) service.applicationInfo.loadLabel(service.packageManager).toString()
                        else _service.packageName

            channelDescr = getStrResource(kResourceChannelDescrLabel)?: "Incoming calls notifications channel"
            foregroundDescr = getStrResource(kResourceForegroundDescrLabel)?: "Siprix call notification service"

            contentLabel = getStrResource(kResourceContentLabel) ?: "Incoming call"
            rejectBtnLabel = getStrResource(kResourceRejectBtnLabel)?: "Reject call"
            acceptBtnLabel = getStrResource(kResourceAcceptBtnLabel)?: "Accept call"

            val res = getResource(kResourceNotifIcon, "drawable")
            iconId = if(res != 0) res else getResource("ic_launcher", "mipmap")
        }

        companion object {
            const val kResourceForegroundDescrLabel = "foreground_descr_label"
            const val kResourceChannelDescrLabel = "channel_descr_label"
            const val kResourceRejectBtnLabel = "reject_btn_label"
            const val kResourceAcceptBtnLabel = "accept_btn_label"
            const val kResourceContentLabel = "content_label"
            const val kResourceNotifIcon = "ic_notif_icon"
        }

        @SuppressLint("DiscouragedApi")
        private fun getStrResource(resName: String): String? {
            val stringRes = _service.resources.getIdentifier(resName, "string", _service.packageName)
            return if(stringRes != 0) _service.getString(stringRes) else null
        }

        @SuppressLint("DiscouragedApi")
        private fun getResource(resName: String, defType: String): Int {
            return _service.resources.getIdentifier(resName, defType, _service.packageName)
        }
    }

    companion object {
        private const val TAG = "CallNotifService"
        const val kCallChannelId = "kSiprixCallChannelId_"
        //const val kMsgChannelId = "kSiprixMsgChannelId"

        const val kActionAppStarted = "kActionAppStarted"
        const val kActionForeground = "kActionForeground"
        
        const val kActionIncomingCall = "kActionIncomingCall"
        const val kActionIncomingCallAccept = "kActionIncomingCallAccept"
        const val kActionIncomingCallReject = "kActionIncomingCallReject"
        const val kActionIncomingCallStopRinger = "kActionIncomingCallStopRinger"

        const val kExtraCallId   = "kExtraCallId"
        const val kExtraAccId    = "kExtraAccId"
        const val kExtraWithVideo= "kExtraWithVideo"
        const val kExtraHdrFrom  = "kExtraHdrFrom"
        const val kExtraHdrTo    = "kExtraHdrTo"

        const val kCallBaseNotifId = 555
        const val kForegroundId = 777

        //Single instance, provides access to calling functionality
        private var core: SiprixCore? = null

        fun createSiprixCore(appContext : Context): SiprixCore {
            if(core == null) {
                core = SiprixCore(appContext)
            }
            return core!!
        }
    }
}
