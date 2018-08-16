# Creating Feature files and Step Definitions files for Cucumber from Excel
This project was built to make the process of creating new Cucumber.io projects or adding feature files to a current Cucumber.io easier and more organized for developers. It will read and create multiple Feature files and their corresponding Step Definitions files from an excel spreadsheet/csv or text file. Each Feature in the spreadsheet must have the same syntax as the example Feature files provided by the Cucumber Framework: https://docs.cucumber.io/gherkin/reference/#feature. Each Feature in the file that will be parsed by this tool must have a '$' in between each Feature. It is recommended to read in the file to be parsed as a csv or text file instead of an excel spreadsheet.
### Running the tool
You can easily run the tool in terminal or Command Line, just use the command:
```
  java createfeaturefile/ReadAndParseFile
```
