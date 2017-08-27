/*
 * Copyright (c) 2017. Jahir Fiquitiva
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

package jahirfiquitiva.libs.kauextensions.extensions

import android.content.Context
import android.util.Log

fun Context.printVerbose(verbose:String) = Log.v(getLogTag(), verbose)

fun Context.printDebug(debug:String) = Log.d(getLogTag(), debug)

fun Context.printInfo(info:String) = Log.i(getLogTag(), info)

fun Context.printWarning(warning:String) = Log.w(getLogTag(), warning)

fun Context.printError(error:String) = Log.e(getLogTag(), error)

fun Context.printError(error:String, th:Throwable) = Log.e(getLogTag(), error, th)