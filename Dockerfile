from ansrivas/scala-sbt:2_11_11-1_0_1

# Copy all the code in here
COPY . /root

# Create a fat jar
RUN sbt clean compile assembly

CMD java -Dlog4j.configuration=file:"/root/src/main/resources/log4j.properties" -jar /root/dist/main.jar /root/dataset
