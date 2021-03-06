/*
 * Copyright (c) 2019. Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jahirfiquitiva.libs.kext.extensions

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date

/**
 * @author Christophe Beyls - cbeyls
 * https://gist.github.com/cbeyls/72fbc1a24735beb792e2173b0199cbff
 */
interface KParcelable : Parcelable {
    override fun describeContents() = 0
    override fun writeToParcel(dest: Parcel, flags: Int)
}

// Creator factory functions

inline fun <reified T> parcelableCreator(
    crossinline create: (Parcel) -> T
                                        ) =
    object : Parcelable.Creator<T> {
        override fun createFromParcel(source: Parcel) = create(source)
        override fun newArray(size: Int) = arrayOfNulls<T>(size)
    }

inline fun <reified T> parcelableClassLoaderCreator(
    crossinline create: (Parcel, ClassLoader?) -> T
                                                   ) =
    object : Parcelable.ClassLoaderCreator<T> {
        override fun createFromParcel(source: Parcel, loader: ClassLoader?) =
            create(source, loader)
        
        override fun createFromParcel(source: Parcel) =
            createFromParcel(source, T::class.java.classLoader)
        
        override fun newArray(size: Int) = arrayOfNulls<T>(size)
    }

// Parcel extensions

fun Parcel.readBooleanCompat() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        readBoolean()
    } else {
        readInt() != 0
    }

fun Parcel.writeBooleanCompat(value: Boolean) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        writeBoolean(value)
    } else {
        writeInt(if (value) 1 else 0)
    }

inline fun <reified T : Enum<T>> Parcel.readEnum() =
    readInt().let { if (it >= 0) enumValues<T>()[it] else null }

fun <T : Enum<T>> Parcel.writeEnum(value: T?) =
    writeInt(value?.ordinal ?: -1)

inline fun <T> Parcel.readNullable(reader: () -> T) =
    if (readInt() != 0) reader() else null

inline fun <T> Parcel.writeNullable(value: T?, writer: (T) -> Unit) {
    if (value != null) {
        writeInt(1)
        writer(value)
    } else {
        writeInt(0)
    }
}

fun Parcel.readDate() =
    readNullable { Date(readLong()) }

fun Parcel.writeDate(value: Date?) =
    writeNullable(value) { writeLong(it.time) }

fun Parcel.readBigInteger() =
    readNullable { BigInteger(createByteArray()) }

fun Parcel.writeBigInteger(value: BigInteger?) =
    writeNullable(value) { writeByteArray(it.toByteArray()) }

fun Parcel.readBigDecimal() =
    readNullable { BigDecimal(BigInteger(createByteArray()), readInt()) }

fun Parcel.writeBigDecimal(value: BigDecimal?) = writeNullable(value) {
    writeByteArray(it.unscaledValue().toByteArray())
    writeInt(it.scale())
}

fun <T : Parcelable> Parcel.readTypedObjectCompat(c: Parcelable.Creator<T>) =
    readNullable { c.createFromParcel(this) }

fun <T : Parcelable> Parcel.writeTypedObjectCompat(value: T?, parcelableFlags: Int) =
    writeNullable(value) { it.writeToParcel(this, parcelableFlags) }

inline fun <reified T> Parcel.readArrayOf(): Array<T> =
    arrayOf<T>().apply { readArray(T::class.java.classLoader) }

inline fun <reified T> Parcel.readArrayListOf(): ArrayList<T> =
    arrayListOf<T>().apply { readList(this.toList(), T::class.java.classLoader) }

inline fun <reified T : Parcelable> Parcel.readParcelable(): T? =
    readParcelable(T::class.java.classLoader)
