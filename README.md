# Creating Feature files and Step Definitions files for Cucumber from Excel Spreadsheet

## Table of Contents
* [Introduction](#introduction)
* [Running the tool](#running-the-tool)
* [Important Notes](#important-notes)
* [Linux/Mac OS (Terminal)](#linuxmac-os-terminal))
* [Windows OS (Command Line)](#windows-os-command-line)
* [Known Issues](#known-issues)

### Introduction
This project was built to make the process of creating new Cucumber.io projects or adding feature files to a current Cucumber.io easier and more organized for developers. It will read and create multiple Feature files and their corresponding Step Definitions files from an excel spreadsheet. Each Feature in the spreadsheet must have the same syntax as the example Feature files provided by the Cucumber Framework: https://docs.cucumber.io/gherkin/reference/#feature. Each Feature in the file that will be parsed by this tool must have an '&' in between each Feature.
### Running the tool
You can easily run the tool in terminal or Command Line, just use the command:
```
  java createfeaturefile/ReadAndParseFile
```
### Important Notes
You need to download the JExcel package for eclipse and then add jxl.jar file to your IDE. You can download the file here: https://sourceforge.net/projects/jexcelapi/files/jexcelapi/.
In order for the tool to run you need to have the latest build of Maven install on your computer, I recommend installing it through Node.js using this command:
```
  npm install mvn -g
```
It is also necessary that you have the latest version of Java installed on your computer and both the %JAVA_HOME% and %PATH% environment variables set to the path of the jdk directory (%PATH% variable needs to include its current contents + the %JAVA_HOME% variable). Best way to do this is through these commands:
## Linux/Mac OS (Terminal)
```
export JAVA_HOME=<path-to-jdk>
export PATH=$PATH:$JAVA_HOME
```
## Windows OS (Command Line)
```
  set JAVA_HOME=<path=to-jdk>
  set PATH=%PATH%;%JAVA_HOME%
```
Currently this tool will only generate the Step Definitions file in the Java language, in order to test the Step Definitions file for each Feature file you must install the Cucumber package through your IDE. IDEs recommended: Eclipse, IntelliJ IDEA, NetBeans

## Known Issues
As of right now there is an issue with the current jxl.jar package to read in Excel Workbooks of the different extenstions. As of right now, the only Excel extension that can be parsed through is .xls, as shown in the sample.xls file. The tool is currently only formatted to run commands to create the Cucumber projects and Step Definition files through the Command Line on Windows OS, more support will come for Terminal on Mac OS and Linux.
