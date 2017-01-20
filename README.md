# Riot
**R**etro-style **IO** **T**oolkit

A collection of uncommon Input- / OutputStream implementations along with Helper Classes for File/Folder/Stream & String handling.


Release
-------

The base release 1.x.y corresponds to the unmodified collections implementation, as they've been extracted from legacy projects.


Releases are deployed automatically to the deploy branch of this github repostory. 
To add a dependency on *Riot* using maven, modify your *repositories* section to include the git based repository.

	<repositories>
	 ...
	  <repository>
	    <id>dualuse-Repository</id>
	    <name>dualuse's Git-based repo</name>
	    <url>https://dualuse.github.io/maven</url>
	  </repository>
	...
	</repositories>
	
and modify your *dependencies* section to include the *Riot* dependency
 
	  <dependencies>
	  ...
	  	<dependency>
	  		<groupId>de.dualuse</groupId>
	  		<artifactId>Riot</artifactId>
	  		<version>[1,)</version>
	  	</dependency>
	  ...
	  </dependencies>


To add the repository and the dependency using gradle refer to this

	repositories {
	    maven {
	        url "https://dualuse.github.io/maven"
	    }
	}

and this

	dependencies {
	  compile 'de.dualuse:Riot:1.+'
	}
