# README

This application allows to download media through WIFI from digital cameras of the series Olympus OMD E-Mx.

Currently this application has only been tested with OMD E-M10 (because the developer owns one). Other similar cameras
such as Olympus OMD E-M1 and E-M5 should work too. If someone is interested in helping me to support them better,
and has such camera, please contact me.

## Getting started

### Run the application

This application should run on Windows, Linux and even MacOS.
To transfer media from your camera to your PC follow these steps:

- Turn on the WIFI service of your camera.

- Connect your PC to the WIFI provided by the camera (the SSID of the WIFI network should be something like
"E-M10-V5PG53223").



- Your PC should be connected to the camera WIFI. To verify such, you can open a web browser (like Explorer,
Chrome, Firefox, etc.) and set as URL either `http://oishare/` or `http://192.168.0.10/`. If browsing any of these URLS
results in a nice dark web page that mentions Olympus somewhere as follows, then you can proceed:

![PC correctly connected to the camera](doc/images/oishare-wifi-connected-ok.jpg)

- Now you can launch the application.

 - If you are using Linux/MacOS execute `<PHOTOSYNC>/bin/photosync`.

 - If you are using Windows go to `<PHOTOSYNC>\bin` and execute `photosync.bat`.

A console application will launch, and it will start copying files from your camera to a local directory. By default,
synchronized media files should be put in a directory called `output`.

## Developer

### Build packages

To build the multi-platform package do the following:

```
sbt universal:packageBin
```

### Launch the application

```
sbt run
```

If you want to contribute send me an e-mail to mauriciojost@gmail.com .
