package org.jetbrains.anko

import android.content.Context

inline fun <T> T.createAnkoContext(
    ctx: Context,
    init: AnkoContext<T>.() -> Unit,
    setContentView: Boolean = false
                                  ): AnkoContext<T> {
    val dsl = AnkoContextImpl(ctx, this, setContentView)
    dsl.init()
    return dsl
}