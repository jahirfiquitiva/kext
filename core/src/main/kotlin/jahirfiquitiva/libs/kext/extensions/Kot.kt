package jahirfiquitiva.libs.kext.extensions

inline fun <reified T> T?.notNull(what: (T) -> Unit) {
    if (this != null) what(this)
}