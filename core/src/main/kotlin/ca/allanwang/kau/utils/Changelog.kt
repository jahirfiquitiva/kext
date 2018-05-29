package ca.allanwang.kau.utils

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.annotation.XmlRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import jahirfiquitiva.libs.kext.R
import jahirfiquitiva.libs.kext.extensions.bind
import jahirfiquitiva.libs.kext.extensions.string
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.xmlpull.v1.XmlPullParser

/**
 * Utils originally created by Allan Wang
 * Available at https://github.com/AllanWang/KAU
 * I have added them here (copy/pasted) because this lib doesn't really uses/needs all its features
 * at a 100%.
 * Anyway, full credits go to Allan, for these awesome extensions
 */

/**
 * Internals of the changelog dialog
 * Contains an mainAdapter for each item, as well as the tags to parse
 */

fun Context.showChangelog(
    @XmlRes xmlRes: Int,
    @StringRes title: Int,
    @StringRes btnText: Int,
    @ColorInt textColor: Int? = null,
    customize: MaterialDialog.Builder.() -> Unit = {}
                         ) {
    showChangelog(xmlRes, string(title), string(btnText), textColor, customize)
}

fun Context.showChangelog(
    @XmlRes xmlRes: Int,
    title: String,
    btnText: String,
    @ColorInt textColor: Int? = null,
    customize: MaterialDialog.Builder.() -> Unit = {}
                         ) {
    doAsync {
        val items = parse(this@showChangelog, xmlRes)
        uiThread {
            val builder = MaterialDialog.Builder(this@showChangelog)
                .title(title)
                .positiveText(btnText)
                .adapter(ChangelogAdapter(items, textColor), null)
            builder.customize()
            builder.show()
        }
    }
}

fun parse(context: Context, @XmlRes xmlRes: Int): List<Pair<String, ChangelogType>> {
    val items = mutableListOf<Pair<String, ChangelogType>>()
    val parser: XmlResourceParser? = try {
        context.resources.getXml(xmlRes)
    } catch (ignored: Exception) {
        null
    }
    var closed = false
    try {
        var eventType = parser?.eventType
        while (eventType != XmlPullParser.END_DOCUMENT && eventType != null) {
            if (eventType == XmlPullParser.START_TAG)
                ChangelogType.values.any { type -> parser?.let { type.add(it, items) } ?: false }
            eventType = parser?.next()
        }
    } catch (ignored: Exception) {
        closed = true
        try {
            parser?.close()
        } catch (ignored: Exception) {
        }
    } finally {
        if (!closed) parser?.close()
    }
    return items
}

class ChangelogAdapter(
    private val items: List<Pair<String, ChangelogType>>,
    @ColorInt val textColor: Int? = null
                      ) :
    RecyclerView.Adapter<ChangelogAdapter.ChangelogVH>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ChangelogVH(parent.inflate(items[viewType].second.layout))
    
    override fun onBindViewHolder(holder: ChangelogVH, position: Int) {
        holder.text?.text = items[position].first
        if (textColor != null) {
            holder.text?.setTextColor(textColor)
            holder.bullet?.setTextColor(textColor)
        }
    }
    
    override fun getItemId(position: Int) = position.toLong()
    
    override fun getItemViewType(position: Int) = position
    
    override fun getItemCount() = items.size
    
    class ChangelogVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView? by bind(R.id.kau_changelog_text)
        val bullet: TextView? by bind(R.id.kau_changelog_bullet)
    }
}

enum class ChangelogType(val tag: String, val attr: String, @LayoutRes val layout: Int) {
    TITLE("version", "title", R.layout.kau_changelog_title),
    ITEM("item", "text", R.layout.kau_changelog_content);
    
    companion object {
        val values = values()
    }
    
    /**
     * Returns true if tag matches; false otherwise
     */
    fun add(parser: XmlResourceParser, list: MutableList<Pair<String, ChangelogType>>): Boolean {
        if (parser.name != tag) return false
        if (parser.getAttributeValue(null, attr).isNotBlank())
            list.add(Pair(parser.getAttributeValue(null, attr), this))
        return true
    }
}