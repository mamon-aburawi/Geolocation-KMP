package provider

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity

@SuppressLint("StaticFieldLeak")
internal object GeoLocationContextProvider : Application.ActivityLifecycleCallbacks {

    var applicationContext: Context? = null

    var currentActivity: ComponentActivity? = null
        private set

    fun initialize(context: Context) {
        val app = context.applicationContext as Application
        this.applicationContext = app
        app.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is ComponentActivity) {
            currentActivity = activity
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity === activity) {
            currentActivity = null
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}