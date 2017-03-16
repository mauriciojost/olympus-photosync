# README

[![Build Status](https://api.travis-ci.org/mauriciojost/olympus-photosync.svg)](https://travis-ci.org/mauriciojost/olympus-photosync)

This application allows to wirelessly download media from Olympus OMD E-Mx cameras to your PC.

Currently this application has only been tested with Olympus OMD E-M10 and Olympus TG-860. Other similar cameras (for example Olympus OMD E-M1 and E-M5) should work too. If you own one of these two cameras and want them to be supported, you can help by sending by mail the logs you get when trying to execute it. 

[**DOWNLOAD**](https://bitbucket.org/mauriciojost/olympus-photosync/downloads)

## Get started and sync your photos

### Install/unzip the application 

First [**download** the application package from here](https://bitbucket.org/mauriciojost/olympus-photosync/downloads). If you use a Linux distribution I strongly suggest to download the _.deb_ package for Debian/Ubuntu or the _.rpm_ one for Red Hat or similar distributions.

#### Using a _zip_ package

Installing the application is not mandatory. You can simply download the latest version (packaged as _photosync-XX.zip_) and unzip it somewhere (for example in _/home/user/_ or _C:\_). Then the executables will be under the unzipped directory, on the _bin/_ subdirectory.

When requested to execute _photosync_ you will have to follow different steps depending on the OS you use: 

 - If you are using Linux execute _<PHOTOSYNC>/bin/photosync_ (you might need to set the file _<PHOTOSYNC>/bin/photosync_ as _executable_). 
 
 - If you are using Windows go to _<PHOTOSYNC>\bin_ and execute _photosync.bat_.

#### Using the Linux packages (_deb_ and _rpm_ files)

Packages _.deb_ and _.rpm_ are available for Linux distributions. You can install them using _dpkg_ and _rpm_ tools.

For instance the _.deb_ package can be installed in Ubuntu typing: 

```
sudo apt-get install openjdk-7-jre
sudo dpkg -i photosync_x.x_all.deb
```

From now on, when requested to execute _photosync_ you will have to follow these steps:

 - Simply run _photosync_ executable, which should be in the _PATH_ environment variable.
 
### Configure

Before you run the application you need to configure it. 

The application uses several parameters that can be set either through command line arguments, or by modifying the configuration file. Command line arguments take precedence over the configuration file.

#### Configure using command line arguments

There are several parameters to be set. To list these parameters you can execute: 

```
photosync --help
```

For instance you can set the server name that is used by executing: 

```
photosync --server-name 192.168.0.10
```

#### Configure using the configuration file

This project uses the _typesafehub/config_ library to cope with configuration files, and it should work correctly with the default configuration. However you may want to customize it to use different directories or in case your camera is not fully supported yet. Here you have a [valid **sample configuration file**](src/main/resources/application.conf), the same being used by default.

Just put such configuration file somewhere and launch the application as follows: 

```
photosync -Dconfig.file=/path/to/application.conf 
```

### Run the application

To transfer media from your camera to your PC follow these steps:

1. Turn on the WIFI service of your camera using _Private_ mode in _Wi-Fi Connect Settings_. 

    This step is **very important**, if not set up correctly the phone won't let this application download media files! To change to _Private_ mode go to the menu of your camera, and set _Wi-Fi Connect Settings_ to _Private_ as shown in the following image.

    ![Camera in private mode](doc/images/camera-in-wifi-connect-settings-private-mode.jpg)

2. Connect your PC to the WIFI provided by the camera. 

    The SSID of the WIFI network should be something like "E-M10-V5PG53223". Your PC should be connected to the camera WIFI. To verify such, you can open a web browser (like Explorer, Chrome, Firefox, etc.) and set as URL either _http://oishare/_ or _http://192.168.0.10/_. If browsing any of these URLs results in a nice dark web page that mentions Olympus somewhere as follows, then you can proceed:

    ![PC correctly connected to the camera](doc/images/oishare-wifi-connected-ok.jpg)

3. Now you can launch the application executing _photosync_ (depending on how you installed the application and your OS)

    The application will start copying files from your camera to a local directory (as configured).

## Develop

This application is written in Scala and uses SBT. Find below some useful commands for basic actions.

To compile the application do:

```
sbt compile
```

To test the application:

```
sbt test
```

To launch the application:

```
sbt run
```

### Build packages

The project uses _sbt-native-packager_ so you can build packages for many operating systems / distributions.

To build the multi-platform _zip_ package:
```
sbt universal:packageBin
```

To build the multi-platform tarball:
```
sbt universal:packageZipTarball
```

To build a Debian package (_.deb_):
```
sbt debian:packageBin
```

To build an RMP package (_.rmp_):
```
sbt rpm:packageBin
```

To build a Windows installer:
```
sbt windows:packageBin
```

To build a docker image:
```
sbt docker:publishLocal
```

### Contribute

This project is _open source_ so you can help make it better.

If **you find issues** please [**file an issue** in here](https://github.com/mauriciojost/olympus-photosync/issues) or send me by mail the logs you got, that will really help me trying to understand what's wrong. 

If **you own a OMD E-MX camera different than E-M10** and want it to be also supported, contact me by e-mail too. Taking only 20 minutes of your time you can help me adapt the application to your camera.

If **you want to simply contribute** you can create a pull request in the [**main repository at GitHub**](https://github.com/mauriciojost/olympus-photosync). Keep in mind that there is a [**mirror repository at BitBucket**](https://bitbucket.org/mauriciojost/olympus-photosync) that will be probably marked as _deprecated_ in the future.

If **you need more information** about the project you can send me an e-mail to _mauriciojostx@gmail.com_.

If **you like the project** feel free to star it! 



