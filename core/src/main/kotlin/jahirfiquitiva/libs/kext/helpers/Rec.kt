package jahirfiquitiva.libs.kext.helpers

import android.util.Log

open class Rec(private val tag: String = "kext", private val canLog: Boolean = true) {
    
    fun v(message: String?, throwable: Throwable? = null) {
        log(Log.VERBOSE, message, throwable)
    }
    
    fun i(message: String?, throwable: Throwable? = null) {
        log(Log.ASSERT, message, throwable)
    }
    
    fun d(message: String?, throwable: Throwable? = null) {
        log(Log.DEBUG, message, throwable)
    }
    
    fun w(message: String?, throwable: Throwable? = null) {
        log(Log.WARN, message, throwable)
    }
    
    fun e(message: String?, throwable: Throwable? = null) {
        log(Log.ERROR, message, throwable)
    }
    
    fun wtf(message: String?, throwable: Throwable? = null) {
        log(Log.ASSERT, message, throwable)
    }
    
    fun print(priority: Int, message: String?, throwable: Throwable? = null) {
        log(priority, message, throwable)
    }
    
    private fun log(priority: Int, message: String?, throwable: Throwable? = null) {
        val actMessage = message ?: "Null Message"
        if (canLog) {
            when {
                throwable != null -> Log.e(tag, actMessage, throwable)
                priority !in (Log.VERBOSE..Log.ERROR) -> Log.wtf(tag, actMessage)
                else -> Log.println(priority, tag, actMessage)
            }
        }
    }
}