/*
 * Copyright (c) 2018.
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
package com.jahirfiquitiva.dons.activities

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import ca.allanwang.kau.utils.snackbar
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.github.javiersantos.piracychecker.PiracyChecker
import com.github.javiersantos.piracychecker.allow
import com.github.javiersantos.piracychecker.callback
import com.github.javiersantos.piracychecker.doNotAllow
import com.github.javiersantos.piracychecker.enums.InstallerID
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError
import com.github.javiersantos.piracychecker.enums.PirateApp
import com.github.javiersantos.piracychecker.onError
import com.github.javiersantos.piracychecker.piracyChecker
import com.jahirfiquitiva.dons.R
import com.jahirfiquitiva.dons.utils.LicKonfigurations
import com.jahirfiquitiva.dons.viewmodels.IAPItem
import com.jahirfiquitiva.dons.viewmodels.IAPsViewModel
import jahirfiquitiva.libs.kext.extensions.getAppName
import jahirfiquitiva.libs.kext.extensions.hasContent
import jahirfiquitiva.libs.kext.extensions.isUpdate
import jahirfiquitiva.libs.kext.extensions.stringArray
import jahirfiquitiva.libs.kext.ui.activities.ActivityWFragments

abstract class LicDonActivity<Configs : LicKonfigurations> :
    ActivityWFragments<Configs>(), BillingProcessor.IBillingHandler {
    
    private var checker: PiracyChecker? = null
    private var billingProcessor: BillingProcessor? = null
    private var iapsReady = false
    
    private val iapsViewModel by lazy {
        ViewModelProviders.of(this).get(IAPsViewModel::class.java)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        destroyChecker()
        destroyBillingProcessor()
    }
    
    private fun destroyChecker() {
        checker?.destroy()
        checker = null
    }
    
    private fun destroyBillingProcessor() {
        billingProcessor?.release()
        billingProcessor = null
        iapsReady = false
    }
    
    // Checker
    @Suppress("unused")
    fun startLicenseCheck(force: Boolean = false) {
        val update = isUpdate
        if (update || !configs.functional || force) {
            checker = getLicenseChecker()
            checker?.let {
                with(it) {
                    callback {
                        allow { showLicensedSnack(update, force) }
                        doNotAllow { _, app -> showNotLicensedDialog(app) }
                        onError { error -> showLicenseErrorDialog(error) }
                    }
                    start()
                }
            } ?: {
                showLicensedSnack(update, force)
            }()
        }
    }
    
    open fun getLicenseChecker(): PiracyChecker? {
        destroyChecker() // Important
        val licKey = getLicKey().orEmpty()
        return piracyChecker {
            if (licKey.hasContent() && licKey.length > 50)
                enableGooglePlayLicensing(licKey)
            enableInstallerId(InstallerID.GOOGLE_PLAY)
            if (amazonInstallsEnabled())
                enableInstallerId(InstallerID.AMAZON_APP_STORE)
            enableUnauthorizedAppsCheck(checkLPF())
            enableStoresCheck(checkStores())
            enableDebugCheck(true)
            enableEmulatorCheck(true)
            enableFoldersCheck(false)
            enableAPKCheck(false)
        }
    }
    
    private fun showLicensedSnack(update: Boolean, force: Boolean = false) {
        configs.functional = true
        if (!update || force) {
            snackbar(getString(R.string.license_valid, getAppName()))
        } else if (update) {
            onAppLicensed()
        }
    }
    
    private fun showNotLicensedDialog(pirateApp: PirateApp?) {
        configs.functional = false
        onAppNotLicensed(pirateApp?.name)
    }
    
    private fun showLicenseErrorDialog(error: PiracyCheckerError) {
        configs.functional = false
        onLicenseError(error.toString())
    }
    
    // IAPs
    @Suppress("unused")
    fun initIAPs(whenReady: () -> Unit) {
        if (enableIAPs()) {
            if (iapsReady) {
                whenReady()
                return
            }
            destroyBillingProcessor()
            billingProcessor = BillingProcessor(this, getLicKey(), this)
            billingProcessor?.let {
                if (!it.isInitialized) it.initialize()
                try {
                    iapsReady = it.isInitialized && it.isOneTimePurchaseSupported
                } catch (ignored: Exception) {
                }
            } ?: {
                onBillingError(0, null)
            }()
        } else {
            onBillingError(0, Exception("In-App Purchases were not enabled"))
        }
    }
    
    private fun loadIAPItems() {
        billingProcessor?.let { it ->
            if (!it.isInitialized) it.initialize()
            if (it.isInitialized) {
                iapsViewModel.destroy(this)
                iapsViewModel.iapBillingProcessor = it
                iapsViewModel.observe(this) {
                    if (it.isNotEmpty()) {
                        onIAPsLoaded(ArrayList(it))
                    } else {
                        onIAPsError(0, null)
                    }
                    iapsViewModel.destroy(this)
                }
                iapsViewModel.loadData(
                    stringArray(
                        R.array.iaps_items) ?: arrayOf(""), true)
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        billingProcessor?.let {
            if (!(it.handleActivityResult(requestCode, resultCode, data))) {
                super.onActivityResult(requestCode, resultCode, data)
            }
        } ?: { super.onActivityResult(requestCode, resultCode, data) }()
    }
    
    override fun onBillingInitialized() {
        iapsReady = true
        loadIAPItems()
    }
    
    override fun onPurchaseHistoryRestored() {}
    
    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        billingProcessor?.let {
            if (it.consumePurchase(productId)) {
                onIAPPurchased(productId)
            }
        }
    }
    
    override fun onBillingError(errorCode: Int, error: Throwable?) {
        destroyBillingProcessor()
        onIAPsError(errorCode, error.toString())
    }
    
    open fun amazonInstallsEnabled(): Boolean = false
    open fun checkLPF(): Boolean = true
    open fun checkStores(): Boolean = true
    
    abstract fun onAppLicensed()
    abstract fun onAppNotLicensed(pirateAppName: String?)
    abstract fun onLicenseError(error: String)
    
    open fun enableIAPs(): Boolean = false
    abstract fun onIAPsLoaded(items: ArrayList<IAPItem>)
    abstract fun onIAPPurchased(productId: String)
    abstract fun onIAPsError(code: Int = 0, reason: String? = null)
    
    abstract fun getLicKey(): String?
}