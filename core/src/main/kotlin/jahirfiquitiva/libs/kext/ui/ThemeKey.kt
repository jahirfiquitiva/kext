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
package jahirfiquitiva.libs.kext.ui

import androidx.annotation.IntDef
import jahirfiquitiva.libs.kext.helpers.AMOLED
import jahirfiquitiva.libs.kext.helpers.AUTO_AMOLED
import jahirfiquitiva.libs.kext.helpers.AUTO_DARK
import jahirfiquitiva.libs.kext.helpers.DARK
import jahirfiquitiva.libs.kext.helpers.LIGHT
import jahirfiquitiva.libs.kext.helpers.TRANSPARENT

@IntDef(LIGHT, DARK, AMOLED, TRANSPARENT, AUTO_DARK, AUTO_AMOLED)
@Retention(AnnotationRetention.SOURCE)
annotation class ThemeKey
