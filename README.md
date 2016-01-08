# README

[![Build Status](https://api.travis-ci.org/mauriciojost/olympus-photosync.svg)](https://travis-ci.org/mauriciojost/olympus-photosync)

This application allows to wirelessly download media from Olympus OMD E-Mx cameras to your PC.

Currently this application has only been tested with Olympus OMD E-M10 and Olympus TG-860. Other similar cameras (for example Olympus OMD E-M1 and E-M5) should work too. If you own one of these two cameras and want them to be supported, you can help by sending by mail the logs you get when trying to execute it. 

## Get started and sync your photos

### Install/unzip the application 

First [**download** the application package from here](https://bitbucket.org/mauriciojost/olympus-photosync/downloads). If you use a Linux distribution I strongly suggest to download the `.deb` package for Debian/Ubuntu or the `.rpm` one for Red Hat or similar distributions.

#### Using a `zip` package

Installing the application is not mandatory. You can simply download the latest version (packaged as `photosync-XX.zip`) and unzip it somewhere (for example in `/home/user/` or `C:\`). Then the executables will be under the unzipped directory, on the `bin/` subdirectory.

When requested to execute `photosync` you will have to follow different steps depending on the OS you use: 

 - If you are using Linux execute `<PHOTOSYNC>/bin/photosync` (you might need to set the file `<PHOTOSYNC>/bin/photosync` as `executable`). 
 
 - If you are using Windows go to `<PHOTOSYNC>\bin` and execute `photosync.bat`.

#### Using the Linux packages (`deb` and `rpm` files)

Packages `.deb` and `.rpm` are available for Linux distributions. You can install them using `dpkg` and `rpm` tools.

For instance the `.deb` package can be installed in Ubuntu typing: 

```
sudo apt-get install openjdk-7-jre
sudo dpkg -i photosync_x.x_all.deb
```

From now on, when requested to execute `photosync` you will have to follow these steps:

 - Simply run `photosync` executable, which should be in the `PATH` environment variable.
 
### Configure

Before you run the application you need to configure it. 

The application uses several parameters that can be set either through command line arguments, or by modifying the configuration file. Command line arguments take precedence over the configuration file.

#### Configure using command line arguments

There are several parameters to be set. To list these parameters you can execute `photosync --help`.

For instance you can set the server name that is used by executing: 

```
photosync --server-name 192.168.0.10
```

#### Configure using the configuration file

This project uses the `typesafehub/config` library to cope with configuration files. Here you have a [valid **sample configuration file**](src/main/resources/application.conf).

A file containing the default settings is shipped with the package, and will be loaded by default. This can be changed by doing: 

```
photosync -Dconfig.file=/path/to/conf/file
```

##### Linux packages
In case of installation through a Linux package, a different configuration file is set to be used: it is placed in `/etc/photosync/application.conf` for convenient modification.

Remember to modify the `output.directory` to a write-able directory path, for instance: 

```
...
output.directory=/mnt/nas/photos/
...
```

### Run the application

To transfer media from your camera to your PC follow these steps:

1. Turn on the WIFI service of your camera using `Private` mode in `Wi-Fi Connect Settings`. 

    This step is **very important**, if not set up correctly the phone won't let this application download media files! To change to `Private` mode go to the menu of your camera, and set `Wi-Fi Connect Settings` to `Private` as shown in the following image.

    ![Camera in private mode](doc/images/camera-in-wifi-connect-settings-private-mode.jpg)

2. Connect your PC to the WIFI provided by the camera. 

    The SSID of the WIFI network should be something like "E-M10-V5PG53223". Your PC should be connected to the camera WIFI. To verify such, you can open a web browser (like Explorer, Chrome, Firefox, etc.) and set as URL either `http://oishare/` or `http://192.168.0.10/`. If browsing any of these URLs results in a nice dark web page that mentions Olympus somewhere as follows, then you can proceed:

    ![PC correctly connected to the camera](doc/images/oishare-wifi-connected-ok.jpg)

3. Now you can launch the application executing `photosync` (depending on how you installed the application and your OS)

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

To build the multi-platform package do the following:

```
sbt universal:packageBin
```

To build Linux packages go to the [extras/packager/](extras/packager/) directory and read the [README.md](extras/packager/README.md) documentation.

### Contribute

This project is `open source` so you can help make it better.

If **you find issues** please [**file an issue** in here](https://github.com/mauriciojost/olympus-photosync/issues) or send me by mail the logs you got, that will really help me trying to understand what's wrong. 

If **you own a OMD E-MX camera different than E-M10** and want it to be also supported, contact me by e-mail too. Taking only 20 minutes of your time you can help me adapt the application to your camera.

If **you want to simply contribute** you can create a pull request in the [**main repository at GitHub**](https://github.com/mauriciojost/olympus-photosync). Keep in mind that there is a [**mirror repository at BitBucket**](https://bitbucket.org/mauriciojost/olympus-photosync) that will be probably marked as `deprecated` in the future.

If **you need more information** about the project you can send me an e-mail to `mauriciojostx@gmail.com`.

If **you like the project** feel free to star it! 



