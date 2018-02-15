# RELEASE NOTES


## RELEASE: 0.2

- #3 Improve documentation
- #1 #2 #7 Add parameters
- #7 Enrich --help parameter descriptions
- #6 Generate log files by default
- #9 Change e-mail account in documentation
- #8 Dump of camera server response
- #11 Document the importance of Wi-Fi Settings Private mode
- Set line delimiters for dumps
- Set to v0.2


## RELEASE: 0.3

- Refactoring
- Set version to 0.3-SNAPSHOT
- Set up travis-ci
- Set scala version in .travis.yml


## RELEASE: 0.4

- Add LICENSE.md and CONTRIBUTORS.md files
- Bump to v0.5 without log configurations
- #15 Add packager
- Clean plugins.sbt
- Use good e-mail address in CONTRIBUTORS.md
- Add log4j configuration back
- Update version in ArgumentsParserBuilder
- Log configuration being used
- #16 Use configuration file
- Fix file configuration loading
- Use good contact e-mail address
- Include configuration file in Linux package
- Use IP address by default
- #17 Set right permissions for /var/log/photosync
- Mention project website when executing --help


## RELEASE: 0.5

- Mention project website when executing --help
- #18 Use scripts to set up /var/log/photosync
- Ease version bumping
- Bump to v0.6


## RELEASE: 0.6

- Mention project website when executing --help
- #18 Use scripts to set up /var/log/photosync
- Ease version bumping
- Bump to v0.6


## RELEASE: 0.7

- Fix link to current version documentation
- Upgrade scala, sbt and dependencies
- Use simpler HTML retrieved logs format
- Use JavaAppPackaging (sbt-native-packager)
- Update the scala version in .travis.yml
- Fully rely on sbt-native-packager for packaging
- Bump to v0.7


## RELEASE: 0.8

- Add scoverage plugin
- Enable coveralls
- Optimize FilesManager.sync()
- Make FilesManager handle failures directly
- Add progress logs
- Fix build.bash logs
- Generate intermediate directories for output
- Detect & synchronize all subfolders automatically #26
- Make FilesManager.listLocalFiles fails if directory does not exist
- Use 5-args regexes to detect files from remote
- Initial file is 1 instead of 0 in progress logs
- Add date and time to FileInfo
- Fix parsing of time
- Fix date detection algorithm
- Add creation date in logs of CameraClient
- Add media filtering based on dates #23
- Transform date filter into from-until
- Update arguments documentation about date range filtering
- Fix file settings for the date range filtering
- Bump to v0.8


## RELEASE: 0.9

- Add another supported camera
- Use better immutable collections
- Initial Bitbucket Pipelines configuration
- Log uncaught exceptions #29
- Create directory before listing syncd files to avoid failure
- Bump to v0.9
- Update Constants.scala
- Improve build.bash


## RELEASE: 0.10

- Display versions in Constant.scala & version.sbt before packaging
- Add documentation via plantuml
- Use Laika to generate html from markdown documentation
- Update Jenkinsfile to publish the right documentation directory
- Minor refactoring & code coverage increase around .mkdirs method
- Specify which files to download based on filename pattern #33
- Minor fixes in the command line help
- Document FilesManager
- Bump to v0.10


## RELEASE: 0.11

- Very first version of a GUI
- Add first version of GUI #20
- Bump to v0.10-BETA-GUI
- Bump to v0.12-SNAPSHOT
- Bump to v0.11 (add gui)


## RELEASE: 0.12

- Minor refactoring on packager script
- Disable coverage for GUI (which is in beta)
- Fix scoverage ignore
- Fix regex in coverage exclusion setting
- GUI contains file list
- Include from & until in GUI
- Use a ADT to express the DownloadedStatus in the SyncPlanItem
- GUI v2 (still in beta)
- Bump to V0.13-SNAPSHOT
- Bump to v0.12


## RELEASE: 0.13

- Ignore gui files for coverage measurement
- Update Jenkinsfile accordingly
- Re-apply changes done via Github
- Provide custom docker image for Jenkinsfile
- Jenkinsfile generates releases too
- Include GUI in coverage reports (taking GUI out of BETA!)
- Setting new minimum coverage (90%)
- Include dump-init-file flag for command line #36
- Fix broken close app
- add auto-camera shutdown feature
- Merge pull request #37 from friism/add-auto-shutdown
- build full Docker image
- use version that rpm and deb don't choke on
- add full dockerfile
- Merge pull request #39 from friism/build-in-docker
- Merge pull request #40 from friism/fix-bug
- Re-organize docker related content (ci & run images)
- Minor refactoring over CameraClient
- Improve HttoCameraMock
- Minor refactoring and add OR.ORF thumbnail
- Externalize generic GET method from CameraClient to reuse in thumbnails
- Add CameraClient.thumbnailFile feature
- Added thumbnails (part I)
- Add thumbnails to the GUI #35
- Bump to v0.13


## RELEASE: 0.14.0

- Add RELEASE-NOTES.md
- In Windows, launch GUI by default #46
- Make files pattern no case-sensitive #51
- Update documentation for #51
- At the end of the sync clearly display where the photos are #50
- Ease releasing
- Update correctly the versions in Constants.scala and version.sbt
- Update release notes


