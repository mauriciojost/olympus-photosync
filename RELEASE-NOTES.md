# RELEASE NOTES


## RELEASE: v0.2

- commit #3 Improve documentation
- commit #1 #2 #7 Add parameters
- commit #7 Enrich --help parameter descriptions
- commit #6 Generate log files by default
- commit #9 Change e-mail account in documentation
- commit #8 Dump of camera server response
- commit #11 Document the importance of Wi-Fi Settings Private mode
- commit Set line delimiters for dumps
- commit Set to v0.2


## RELEASE: v0.3

- commit Refactoring
- commit Set version to 0.3-SNAPSHOT
- commit Set up travis-ci
- commit Set scala version in .travis.yml


## RELEASE: v0.4

- commit Add LICENSE.md and CONTRIBUTORS.md files
- commit Bump to v0.5 without log configurations
- commit #15 Add packager
- commit Clean plugins.sbt
- commit Use good e-mail address in CONTRIBUTORS.md
- commit Add log4j configuration back
- commit Update version in ArgumentsParserBuilder
- commit Log configuration being used
- commit #16 Use configuration file
- commit Fix file configuration loading
- commit Use good contact e-mail address
- commit Include configuration file in Linux package
- commit Use IP address by default
- commit #17 Set right permissions for /var/log/photosync
- commit Mention project website when executing --help


## RELEASE: v0.5

- commit Mention project website when executing --help
- commit #18 Use scripts to set up /var/log/photosync
- commit Ease version bumping
- commit Bump to v0.6


## RELEASE: v0.6

- commit Mention project website when executing --help
- commit #18 Use scripts to set up /var/log/photosync
- commit Ease version bumping
- commit Bump to v0.6


## RELEASE: v0.7

- commit Fix link to current version documentation
- commit Upgrade scala, sbt and dependencies
- commit Use simpler HTML retrieved logs format
- commit Use JavaAppPackaging (sbt-native-packager)
- commit Update the scala version in .travis.yml
- commit Fully rely on sbt-native-packager for packaging
- commit Bump to v0.7


## RELEASE: v0.8

- commit Add scoverage plugin
- commit Enable coveralls
- commit Optimize FilesManager.sync()
- commit Make FilesManager handle failures directly
- commit Add progress logs
- commit Fix build.bash logs
- commit Generate intermediate directories for output
- commit Detect & synchronize all subfolders automatically #26
- commit Make FilesManager.listLocalFiles fails if directory does not exist
- commit Use 5-args regexes to detect files from remote
- commit Initial file is 1 instead of 0 in progress logs
- commit Add date and time to FileInfo
- commit Fix parsing of time
- commit Fix date detection algorithm
- commit Add creation date in logs of CameraClient
- commit Add media filtering based on dates #23
- commit Transform date filter into from-until
- commit Update arguments documentation about date range filtering
- commit Fix file settings for the date range filtering
- commit Bump to v0.8


## RELEASE: v0.9

- commit Add another supported camera
- commit Use better immutable collections
- commit Initial Bitbucket Pipelines configuration
- commit Log uncaught exceptions #29
- commit Create directory before listing syncd files to avoid failure
- commit Bump to v0.9
- commit Update Constants.scala
- commit Improve build.bash


## RELEASE: v0.10

- commit Display versions in Constant.scala & version.sbt before packaging
- commit Add documentation via plantuml
- commit Use Laika to generate html from markdown documentation
- commit Update Jenkinsfile to publish the right documentation directory
- commit Minor refactoring & code coverage increase around .mkdirs method
- commit Specify which files to download based on filename pattern #33
- commit Minor fixes in the command line help
- commit Document FilesManager
- commit Bump to v0.10


## RELEASE: v0.11

- commit Very first version of a GUI
- commit Add first version of GUI #20
- commit Bump to v0.10-BETA-GUI
- commit Bump to v0.12-SNAPSHOT
- commit Bump to v0.11 (add gui)


## RELEASE: v0.12

- commit Minor refactoring on packager script
- commit Disable coverage for GUI (which is in beta)
- commit Fix scoverage ignore
- commit Fix regex in coverage exclusion setting
- commit GUI contains file list
- commit Include from & until in GUI
- commit Use a ADT to express the DownloadedStatus in the SyncPlanItem
- commit GUI v2 (still in beta)
- commit Bump to V0.13-SNAPSHOT
- commit Bump to v0.12


## RELEASE: v0.13

- commit Ignore gui files for coverage measurement
- commit Update Jenkinsfile accordingly
- commit Re-apply changes done via Github
- commit Provide custom docker image for Jenkinsfile
- commit Jenkinsfile generates releases too
- commit Include GUI in coverage reports (taking GUI out of BETA!)
- commit Setting new minimum coverage (90%)
- commit Include dump-init-file flag for command line #36
- commit Fix broken close app
- commit add auto-camera shutdown feature
- commit Merge pull request #37 from friism/add-auto-shutdown
- commit build full Docker image
- commit use version that rpm and deb don't choke on
- commit add full dockerfile
- commit Merge pull request #39 from friism/build-in-docker
- commit Merge pull request #40 from friism/fix-bug
- commit Re-organize docker related content (ci & run images)
- commit Minor refactoring over CameraClient
- commit Improve HttoCameraMock
- commit Minor refactoring and add OR.ORF thumbnail
- commit Externalize generic GET method from CameraClient to reuse in thumbnails
- commit Add CameraClient.thumbnailFile feature
- commit Added thumbnails (part I)
- commit Add thumbnails to the GUI #35
- commit Bump to v0.13



## RELEASE: 0.14.0

- commit Add RELEASE-NOTES.md
- commit In Windows, launch GUI by default #46
- commit Make files pattern no case-sensitive #51
- commit Update documentation for #51
- commit At the end of the sync clearly display where the photos are #50
- commit Ease releasing
- commit Update correctly the versions in Constants.scala and version.sbt
- commit Update release notes
