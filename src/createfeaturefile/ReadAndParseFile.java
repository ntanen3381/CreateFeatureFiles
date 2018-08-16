package createfeaturefile;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jxl.Sheet;
import jxl.Workbook;

public class ReadAndParseFile {

	@SuppressWarnings("resource")
	private static int createMavenProject(String path, Scanner scan) throws IOException {
		String projectName = "", featureFileName = "", feature = "", content = "";
		Matcher featureMatch = null;
		Sheet sheet = null;

		try {
			sheet = Workbook.getWorkbook(new File (path)).getSheet(0);
			for (int row = 0; row < sheet.getRows(); row++) {
				for (int col = 0; col < sheet.getColumns(); col++) {
					content += sheet.getCell(col, row).getContents() + " ";
				}
				content += "\n";
			}
			featureMatch = Pattern.compile("(Feature:[\\D\\s][^&]+)").matcher(content);
		} catch (Exception e) {
			System.out.println("Error: Incorrect path to csv file, try another one.");
			return 0;
		}

		System.out.println("Enter \"1\" if a current Cucumber project exists, enter \"2\" to create a new Cucumber project, enter \"0\" to go back:");
		while(true) {
			String option = scan.nextLine();
			if (option.equals("1")) {
				System.out.println("Enter full path of Cucumber project:");
				path = scan.nextLine();
				projectName = path.substring(path.lastIndexOf('/') + 1, path.length());
				break;
			} else if (option.equals("2")) {
				Runtime rt = Runtime.getRuntime();
				Process p = null;

				System.out.println("Enter new project name:");
				projectName = scan.nextLine();
				while (projectName.isEmpty()) {
					System.out.println("Please enter a valid project name:");
					projectName = scan.nextLine();
				}
				projectName = projectName.toLowerCase();
				projectName = projectName.replaceAll("[\".,\\/#!$%\\\\^&\\*;:{}=\\-'~()?] ", "");
				System.out.println("Enter path to save new Cucumber project:");
				path = scan.nextLine();
				if (new File(path + "/" + projectName).exists()) {
					System.out.println("Project " + projectName +" already exists, try again.");
					return -1;
				}
				System.out.println("Creating new project...");
				p = rt.exec("cmd /c cd / && cd " + path + " && mvn archetype:generate -DarchetypeGroupId=io.cucumber "
						+ "-DarchetypeArtifactId=cucumber-archetype -DarchetypeVersion=2.3.1.2 -DgroupId=" + projectName + " -DartifactId=" + projectName
						+ " -Dpackage=" + projectName + " -Dversion=1.0.0-SNAPSHOT -DinteractiveMode=false");
				BufferedReader reader = 
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				};
				if (p.exitValue() != 0) {
					System.out.println("Error: Maven is not installed or Java environment variable is not set, try again.");
					p.destroy();
					return 0;
				}
				p.destroy();
				path += "/" + projectName;
				System.out.println("Project " + projectName + " created.");
				break;
			} else if (option.equals("0")) {
				return 0;
			} else {
				System.out.println("Please enter a valid option, either \"0\", \"1\", or \"2\":");
			}
		}

		while (featureMatch.find()) {
			feature = featureMatch.group(1);
			featureFileName = feature.substring(feature.indexOf(' ') + 1, feature.indexOf('\n') - 3);
			featureFileName = featureFileName.replaceAll(" ", "_");
			featureFileName = featureFileName.replaceAll("[\".,\\/#!$%\\\\^&\\*;:{}=\\-'~()?] ", "");
			featureFileName = featureFileName.toLowerCase();
			featureFileName = featureFileName.replace("?", "");

			try {
				File file = new File(path + "/src/test/resources/" + projectName + "/" + featureFileName + ".feature");
				if (!file.exists()) {
					file.createNewFile();
					BufferedWriter wr = new BufferedWriter(new FileWriter(file));
					wr.write(feature);
					wr.close();
					System.out.println("File " + featureFileName + ".feature created.");
				} else {
					System.out.println("Error: Feature file " + featureFileName + ".feature already exists,"
							+ " please try again" );
					return 0;
				}
			} catch (Exception e) {
				System.out.println("Error: Path for new project or path to current project does not exist, try again.");
				return -1;
			}

			try {
				System.out.println("Creating step definitions file...");
				String stepsDefFile = featureFileName.substring(0, 1).toUpperCase();

				for (int j = 1; j < featureFileName.length(); j++) {
					if (featureFileName.charAt(j) != '_') {
						stepsDefFile += featureFileName.charAt(j);
					} else {
						stepsDefFile += featureFileName.substring(j + 1, j + 2).toUpperCase();
						j++;
					}
				}
				stepsDefFile += "Stepdefs";
				Runtime rt = Runtime.getRuntime();
				Process p = rt.exec("cmd /c cd " + path + " && mvn test");

				Scanner s = new Scanner(p.getInputStream()).useDelimiter("\\A");
				String result = s.hasNext() ? s.next() : "";
				s.close();
				p.destroy();

				Matcher stepsMatch = Pattern.compile("(@Given[\\S\\s]+})").matcher(result);
				stepsMatch.find();
				File file = new File(path + "/src/test/java/" + projectName + "/" + stepsDefFile + ".java");
				file.createNewFile();
				BufferedWriter wr = new BufferedWriter(new FileWriter(file));
				wr.write("package " + projectName + ";\n\n" + "import cucumber.api.PendingException;\r\n" + 
						"import cucumber.api.java.en.Given;\r\n" + 
						"import cucumber.api.java.en.When;\r\n" + 
						"import cucumber.api.java.en.Then;\r\n" + 
						"import static org.junit.Assert.*;\n\npublic class " + stepsDefFile + " {\n\n");
				wr.write(stepsMatch.group(1) + "\n}");
				wr.close();
				System.out.println("File " + stepsDefFile + ".java created.");
			} catch (FileNotFoundException e) {
				System.out.println("Error: Path for new project or path to current project does not exist, try again.");
				return -1;
			} catch (IllegalStateException e) {
				System.out.println("Error: Step Definitions file already exist, try again.");
				return 0;
			}
		}
		return 1;
	}

	public static void main(String[] args) throws IOException {
		String path = "";
		Scanner scan = new Scanner(System.in);

		System.out.println("Enter path to csv file, enter \"0\" to cancel:");
		path = scan.nextLine();
		while (!path.equals("0")) {
			while (ReadAndParseFile.createMavenProject(path, scan) < 0);
			System.out.println("Enter another path to csv file, enter \"0\" to cancel:");
			path = scan.nextLine();
		}
		System.out.println("Program terminated.");
		scan.close();
	}
}