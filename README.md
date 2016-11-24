# Riot
Retro-style IO Toolkit

A collection of uncommon Input- / OutputStream implementations along with Helper Classes for File/Folder/Stream & String handling.


Release
-------

The base release 1.0.x corresponds to the unmodified collections implementation, as they've been extracted from legacy projects.


Releases are deployed automatically to the deploy branch of this github repostory. 
To add a dependency on *Riot* using maven, modify your *repositories* section to include the git based repository.

	<repositories>
	 ...
	  <repository>
	    <id>Riot-Repository</id>
	    <name>Riot'S Git-based repo</name>
	    <url>https://raw.githubusercontent.com/Holzschneider/Riot/deploy/</url>
	  </repository>
	...
	</repositories>
	
and modify your *dependencies* section to include the *Riot* dependency
 
	  <dependencies>
	  ...
	  	<dependency>
	  		<groupId>de.dualuse</groupId>
	  		<artifactId>Riot</artifactId>
	  		<version>LATEST</version>
	  	</dependency>
	  ...
	  </dependencies>


To add the repository and the dependency using gradle refer to this

	repositories {
	    maven {
	        url "https://raw.githubusercontent.com/Holzschneider/Riot/deploy/"
	    }
	}

and this

	dependencies {
	  compile 'de.dualuse:Fancy:1.0.+'
	}
