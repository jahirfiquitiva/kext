package jahirfiquitiva.libs.kext.helpers

import android.util.Log

open class Rec(val tag: String = "kext", val canLog: Boolean = true) {
    fun i(message: String, throwable: Throwable? = null) {
        if (canLog) {
            if (throwable != null) Log.i(tag, message, throwable)
            else Log.i(tag, message)
        }
    }
    
    fun d(message: String, throwable: Throwable? = null) {
        if (canLog) {
            if (throwable != null) Log.d(tag, message, throwable)
            else Log.d(tag, message)
        }
    }
    
    fun w(message: String, throwable: Throwable? = null) {
        if (canLog) {
            if (throwable != null) Log.w(tag, message, throwable)
            else Log.w(tag, message)
        }
    }
    
    fun e(message: String, throwable: Throwable? = null) {
        if (canLog) {
            if (throwable != null) Log.e(tag, message, throwable)
            else Log.e(tag, message)
        }
    }
    
    fun wtf(message: String, throwable: Throwable? = null) {
        if (canLog) {
            if (throwable != null) Log.wtf(tag, message, throwable)
            else Log.wtf(tag, message)
        }
    }
}