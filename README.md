## Pinky and the brain team

![Banner](banner.png)

Repository for Google HashCode challenge

February 2016

## Development

Requires JDK8 and Intellij 15. With Intellij open `build.gradle` and import
the existing project.

## Submission to the Judge system

The following commands generate the required files in `build/judge`

    .\gradlew sourceCode simulations
    
The gradle `simulations` task passes the project property `repeat` to the simulation,
so you can repeat simulations with `-Prepeat=500`

## Usage

The main class `pinkyandthebrain.Main` executes all the simulations named
on the command line. The following system properties are checked:

- save, `-Dsave=false`, defaults to `false`, stores the drones commands in a file
suitable to be sent to the judge system
- d, `-Dd=build/judge`, defaults to `judge`, set the directory where commands files
are stored
- repeat, `-Drepeat=1000`, defaults to `1`

## Players

The player strategy can be indicated in a file `simulation.properties` in the property
`player` (see attached demo `simulation.properties.example`)
