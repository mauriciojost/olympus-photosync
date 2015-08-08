# README

This application allows to automatically download through WIFI all photos and videos taken with the camera Olumpus OMD E-M10.

## User

### Run the application

Follow these steps: 

- Connect to the WIFI service of your Olympus OMD E-M10 camera. 

- To launch the application from Linux execute `./bin/photosync`.

- To launch the application from Windows go to `bin` and execute `photosync.bat`.

It will start synchronizing files.

### User notes

You can verify that you are correctly connected to your camera by opening a browser (like Google Chrome or Internet Explorer) and browsing `http://oishare/`.

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
