BitcoinBlue
========
Bitcoin Blue is application specifically designed for the Argentine Dollar Blue (Dolar Blue) market and individuals trading Bitcoin in Argentina. 

Bitcoin Blue can convert and calculate the latest Dolar Blue/ARS value based on the current exchange rate of Bitcoin (BTC) from multiple exchanges.  Bitcoin and Dollar Blue rates are continually updated from multiple data sources (La Nacion, DolarBlue.net, etc), includes both Dolar Blue and official exchange rates.

The application can calculate values for USD/ARS/BTC based on Bitcoin, ARS, or USD and includes options to calculate a sales price, fees, and commission in addition to export those values as a receipt for in the moment trades (CVS export)

Building
========
The project designed to be used with a recent version of Android Studio (currently 2.1.1). To build from source, checkout the source and build using gradle build system. 

You will need Java version:

* JDK 1.7
 
Use the Android SDK manager to install the following components:

* `ANDROID_HOME` environment variable pointing to the directory where the SDK is installed
* Android SDK Tools 23.0.3
* Gradle Version 2.1.0
* Android SDK Build Tools 23.0.2
* Android 6.0 (API 23) 
 * Android Extras:
    * Android Support AppCompat rev v7:23.1.1
    * Android Support Repository rev 32

####Build commands

From the command line or console:

    git clone https://github.com/thanksmister/BitcoinBlue.git
    cd BitcoinBlue

Linux/Mac type:

    ./gradlew build

Windows type:

    gradlew.bat build

 - Look in `BitcoinBlue/app/build/outputs/apk` to see the generated apk. 
   There are versions for debug and unsigned release.

You can also install the latest version from the Google Play store at:

https://play.google.com/store/apps/details?id=com.thanksmister.btcblue

Licensing
========
Copyright (c)  2015.  ThanksMister LLC
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at
  
http://www.apache.org/licenses/LICENSE-2.0
  
Unless required by applicable law or agreed to in writing, software distributed 
under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.

