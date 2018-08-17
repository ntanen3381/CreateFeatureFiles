# Creating Feature files and Step Definitions files for Cucumber from Excel Spreadsheet
This project was built to make the process of creating new Cucumber.io projects or adding feature files to a current Cucumber.io easier and more organized for developers. It will read and create multiple Feature files and their corresponding Step Definitions files from an excel spreadsheet. Each Feature in the spreadsheet must have the same syntax as the example Feature files provided by the Cucumber Framework: https://docs.cucumber.io/gherkin/reference/#feature. Each Feature in the file that will be parsed by this tool must have an '&' in between each Feature. As of right now there is an issue with the current jxl.jar package to read in Excel Workbooks of the different extenstions. As of right now, the only Excel extension that can be parsed through is .xls, as shown in the sample.xls file.
### Running the tool
You can easily run the tool in terminal or Command Line, just use the command:
```
  java createfeaturefile/ReadAndParseFile
```
