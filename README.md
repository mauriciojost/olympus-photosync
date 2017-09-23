# README

[![Build Status](https://api.travis-ci.org/mauriciojost/olympus-photosync.svg)](https://travis-ci.org/mauriciojost/olympus-photosync) 
[![Coverage Status](https://coveralls.io/repos/github/mauriciojost/olympus-photosync/badge.svg?branch=master)](https://coveralls.io/github/mauriciojost/olympus-photosync?branch=master) 

[![Download](https://img.shields.io/badge/download-installer-aa3333.svg)](https://bitbucket.org/mauriciojost/olympus-photosync/downloads) [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](/LICENSE.md) 

This application allows to wirelessly download media from Olympus OMD E-Mx cameras to your PC.

It has been tested on the following cameras: 

- _Olympus TG-860_(thanks Lee!)
- _Olympus TG-5_ (thanks Jim!)
- _Olympus OMD E-M10_ (thanks to myself!)

It should work _out-of-the-box_ with other similar cameras (like _Olympus OMD E-M1_ and _E-M5_).

This application has been tested on _Linux_, _Windows_ and should correctly run on _Mac OS_. Contact me if you have an issue.

## Get started

### Install/unzip the application 

First [**download** the application package from here](https://bitbucket.org/mauriciojost/olympus-photosync/downloads). If you use a Linux distribution I strongly suggest to download the _.deb_ package for Debian/Ubuntu or the _.rpm_ one for _Red Hat_ like distributions.

#### Using a _zip_ or _tgz_ package

These packages are provided so that the application can be used without installation. Download the latest version (for instance _photosync-XX.zip_) and decompress it somewhere (for example in _/home/user/_). Then the executables will be under the unzipped directory, on the _<PHOTOSYNC>/bin/_ subdirectory.

In the coming sections of this document, when requested to execute _photosync_ you will have to: 

 - (if under Linux) execute _<PHOTOSYNC>/bin/photosync_ (you might need to set the file _<PHOTOSYNC>/bin/photosync_ as _executable_, doing `chmod +x bin/photosync`). 
 
 - (if under Windows) navigate to the folder _<PHOTOSYNC>\bin_ and execute _photosync.bat_.

#### Using the Linux packages (_deb_ and _rpm_ files)

Packages _.deb_ and _.rpm_ are available for Linux distributions. You can install them using _dpkg_ and _rpm_ tools.

For instance the _.deb_ package can be installed in _Ubuntu_ typing: 

```
sudo apt-get install openjdk-7-jre
sudo dpkg -i photosync_x.x_all.deb
```

In the coming sections of this document, when requested to execute _photosync_, simply execute _photosync_ from any current directory (as it will be already in the _PATH_ environment variable).
 
### Configure

The application should work _out-of-the-box_ for the cameras listed above. However you may want to customize its execution, for instance to download photos to a non-default directory.

For such cases, the application uses several parameters that can be set either through command line arguments, or by modifying the configuration file. Command line arguments take precedence over the configuration file.

#### Command line arguments

There are several parameters to be set. To list these parameters you can execute: 

```
photosync --help
```

For instance you can set the server name that is used by executing: 

```
photosync --server-name 192.168.0.10
```

#### Configuration file

This project uses the _typesafehub/config_ library to cope with configuration files, Find here a [valid **configuration file**](src/main/resources/application.conf).

Just copy its content somewhere, modify it as wished, and launch the application as follows: 

```
photosync -Dconfig.file=/path/to/application.conf 
```

### Run the application

To transfer media from your camera to your PC follow these steps:

1. Turn on the WIFI service of your camera using _Private_ mode in _Wi-Fi Connect Settings_. 

    This step is **very important**, if not set up correctly the camera won't let this application download media files! To change to _Private_ mode go to the menu of your camera, and set _Wi-Fi Connect Settings_ to _Private_ as shown in the following image.

    ![Camera in private mode](doc/images/camera-in-wifi-connect-settings-private-mode.jpg)

2. Connect your PC to the WIFI provided by the camera. 

    The SSID of the WIFI network should be something like "E-M10-V5PG53223". Your PC should be connected to the camera WIFI. To verify such, you can open a web browser (like Explorer, Chrome, Firefox, etc.) and set as URL either _http://oishare/_ or _http://192.168.0.10/_. If browsing any of these URLs results in a nice dark web page that mentions Olympus somewhere as follows, then you can proceed:

    ![PC correctly connected to the camera](doc/images/oishare-wifi-connected-ok.jpg)

3. Now you can launch the application executing _photosync_ (depending on how you installed the application and your OS)

    The application will start copying files from your camera to a local directory (as configured).

## Develop

This application is written in _Scala_ and uses _SBT_. Find below some useful commands for basic actions.

```
sbt compile  # To compile the application
sbt test     # To test it
sbt run      # To launch it
```

### Build packages

The project uses _sbt-native-packager_ so you can build packages for many operating systems / distributions.

```
sbt universal:packageBin         # To build the multi-platform _zip_ package
sbt universal:packageZipTarball  # To build the multi-platform tarball
sbt debian:packageBin            # To build a Debian package (_.deb_)
sbt rpm:packageBin               # To build an RPM package (_.rpm_) (must have _rpm_ tool installed if in _Debian_ OS)
sbt windows:packageBin           # To build a Windows installer (from Windows)
sbt docker:publishLocal          # To build a docker image
```

### Contribute

This project is _open source_ so you can help make it better.

**Found issues?** Then please [**file an issue** in here](https://github.com/mauriciojost/olympus-photosync/issues) or send me by mail the logs you got, that will really help me trying to understand what's wrong. 

**Own a OMD E-MX camera not supported?** Then please contact me by e-mail too. Taking only 20 minutes of your time you can help me adapt the application to your camera.

**Want to simply contribute?** You can create a _PR_ in the [**main repository at GitHub**](https://github.com/mauriciojost/olympus-photosync). Keep in mind that there is a [**mirror repository at BitBucket**](https://bitbucket.org/mauriciojost/olympus-photosync) that will be probably marked as _deprecated_ in the future.

**Need more information?** Send me an e-mail to _mauriciojostx@gmail.com_.

**Liked the project?** Then please star it! 


