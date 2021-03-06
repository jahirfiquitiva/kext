# kext

[![](https://jitpack.io/v/com.jahirfiquitiva/kext.svg)](https://jitpack.io/#com.jahirfiquitiva/kext)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0a93d8de47ba48749849f5959b36c6ad)](https://www.codacy.com/app/jahirfiquitiva/kext?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jahirfiquitiva/kext&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.com/jahirfiquitiva/kext.svg?branch=master)](https://travis-ci.com/jahirfiquitiva/kext)

A bunch of Kotlin extensions to be used in my android apps, which also contains a few extensions from [Allan Wang](https://github.com/AllanWang)'s [KAU library](https://github.com/AllanWang/KAU)

## Documentation

Yet to be added, but I think functions are easy to understand at least while I get the time to add the documentation.

## Import

Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency
```groovy
dependencies {
    implementation 'com.jahirfiquitiva.kext:core:{last-version}'
	// Only if you use/need ViewModels and/or Lifecycle from Android Architecture Components
    implementation 'com.jahirfiquitiva.kext:archhelpers:{last-version}'
    // Only if you use/need ZoomableImageView
    implementation 'com.jahirfiquitiva.kext:ziv:{last-version}'
}
```


## License


	Copyright (c) 2018 Jahir Fiquitiva

	Licensed under the CreativeCommons Attribution-ShareAlike 
	4.0 International License. You may not use this file except in compliance 
	with the License. You may obtain a copy of the License at

	   http://creativecommons.org/licenses/by-sa/4.0/legalcode

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

