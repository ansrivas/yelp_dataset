# yelp_dataset #
---
[![Build Status](https://travis-ci.org/ansrivas/yelp_dataset.svg?branch=master)](https://travis-ci.org/ansrivas/yelp_dataset)


Welcome to yelp dataset analysis

This is a spark application which reads in the yelp dataset published in json format [json_dataset](https://www.yelp.com/dataset/download) and runs some basic sql queries on top of it.

Dependencies:
---

### 1. Standalone sbt version:
To execute this, you will need scala and sbt installed on your system.

`make run_local FILEPATH=<path_to_your_json_dataset.tar>`

### 2. Docker version:
To execute docker version, only docker installation is needed on your system.

`make run_docker FILEPATH=<path_to_your_json_dataset.tar>`
> Note: This takes looooooooong to build as sbt tries to download a lot of data.


Usage:
----

To run the application, execute `make` in the root of the project.

```
$ make
help:           Show available options with this Makefile
clean:          Clean removes any previous directories named "dataset" in present working directory
untar:          Untar the input .tar file to a predefined location
assembly:       Create an assembly (fat jar) from the scala project
run_local:      Run the fat jar after compilation and assembly LOCALLY
run_docker:     Run the fat jar after compilation and assembly via docker
```


## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with
any pull requests, please state that the contribution is your original work and that you license
the work to the project under the project's open source license. Whether or not you state this
explicitly, by submitting any copyrighted material via pull request, email, or other means you
agree to license the material under the project's open source license and warrant that you have the
legal authority to do so.

## License ##

This code is open source software licensed under the
[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0) license.
