EXTRACT_DIR := "./dataset"

.DEFAULT_GOAL := help

help:          ## Show available options with this Makefile
	@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//' | sed -e 's/##//'

clean:         ## Clean removes any previous directories named "dataset" in present working directory
clean:
	@echo "Removing the directory dataset if its present in current working directory"
	@rm -rf ./dataset && \
	docker rmi -f ansrivas/yelp_dataset:latest

untar:         ## Untar the input .tar file to a predefined location
untar:
ifeq ($(strip $(FILEPATH)),)
	$(error FILEPATH is undefined. Usage: make untar/run_local/run_docker FILEPATH=your_file.tar)
endif
	@echo "Now extracting the files from given tar" && \
	mkdir -p ${EXTRACT_DIR} && \
	tar -xvf ${FILEPATH}

assembly:      ## Create an assembly (fat jar) from the scala project
assembly:
	sbt clean compile assembly

run_local:     ## Run the fat jar after compilation and assembly LOCALLY
run_local:	untar assembly
	java -jar ./dist/main.jar ${EXTRACT_DIR}

run_docker:    ## Run the fat jar after compilation and assembly via docker
run_docker:	untar
	docker build -t ansrivas/yelp_dataset:latest . && \
	docker run -it --rm -v `pwd`/dataset:/root/dataset  ansrivas/yelp_dataset:latest
