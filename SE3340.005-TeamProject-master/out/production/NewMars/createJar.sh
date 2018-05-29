#!/bin/sh
javac -classpath Mars4_5.jar mars/tools/NumberPyramids.java
./CreateMarsJar.bat
java -jar Mars.jar

