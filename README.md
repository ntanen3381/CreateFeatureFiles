# Creating Feature files and Step Definitions files for Cucumber from Excel Spreadsheet
This project was built to make the process of creating new Cucumber.io projects or adding feature files to a current Cucumber.io easier and more organized for developers. It will read and create multiple Feature files and their corresponding Step Definitions files from an excel spreadsheet. Each Feature in the spreadsheet must have the same syntax as the example Feature files provided by the Cucumber Framework: https://docs.cucumber.io/gherkin/reference/#feature. Each Feature in the file that will be parsed by this tool must have an '&' in between each Feature.
### Running the tool
You can easily run the tool in terminal or Command Line, just use the command:
```
  java createfeaturefile/ReadAndParseFile
```
### Important Notes
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
