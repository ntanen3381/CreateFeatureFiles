package createfeaturefile;
import java.util.Scanner;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedHashMap;

public class ReadAndParseFile {
	private String feature;

	public int parseCSV(String path, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> map ) {
		BufferedReader br = null;

		try {
			Matcher featureMatch = null, scenarioMatch = null, keywordMatch = null;
			String line, scenario = "";

			br = new BufferedReader(new FileReader(path));
			while ((line = br.readLine()) != null) {
				if (line.contains("Feature:")) {
					featureMatch = Pattern.compile("Feature:\\,([\\D+\\s+][^\\,]+)").matcher(line);
					featureMatch.find();
					map.put(featureMatch.group(1), new LinkedHashMap<String, LinkedHashMap<String,String>>());
					this.feature = featureMatch.group(1);
				} else if (line.contains("Scenario:")) {
					scenarioMatch = Pattern.compile("Scenario:\\,([\\D+\\s+][^\\,]+)").matcher(line);
					scenarioMatch.find();
					map.get(featureMatch.group(1)).put(scenarioMatch.group(1), new LinkedHashMap<String,String>());
					scenario = scenarioMatch.group(1);
					scenarioMatch.reset();
				} else if (line.contains("Given")) {
					keywordMatch = Pattern.compile("Given\\,([\\D+\\s+][^\\,]+)").matcher(line);
					keywordMatch.find();
					map.get(featureMatch.group(1)).get(scenario).put(keywordMatch.group(1), "Given");
				} else if (line.contains("When")) {
					keywordMatch = Pattern.compile("When\\,([\\D+\\s+][^\\,]+)").matcher(line);
					keywordMatch.find();
					map.get(featureMatch.group(1)).get(scenario).put(keywordMatch.group(1), "When");
				} else if (line.contains("And")) {
					keywordMatch = Pattern.compile("And\\,([\\D+\\s+][^\\,]+)").matcher(line);
					keywordMatch.find();
					map.get(featureMatch.group(1)).get(scenario).put(keywordMatch.group(1), "And");
				} else if (line.contains("Then")) {
					keywordMatch = Pattern.compile("Then\\,([\\D+\\s+][^\\,]+)").matcher(line);
					keywordMatch.find();
					map.get(featureMatch.group(1)).get(scenario).put(keywordMatch.group(1), "Then");
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: Incorrect path to csv file, try another one.");
			return -1;
		} catch (IOException e) {
			e.printStackTrace(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int createMavenProject(LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> map, Scanner scan) throws IOException {
		String path = "", projectName = "", featureFileName = "";

		System.out.println("Enter \"1\" if a current Cucumber project exists, enter \"2\" to create a new Cucumber project, enter \"0\" to go back:");
		Set<String> features = map.keySet();

		for (String feature : features) {
			featureFileName = feature.replaceAll(" ", "_");
			featureFileName = featureFileName.replaceAll("[\".,\\/#!$%\\\\^&\\*;:{}=\\-'~()?] ", "");
		}
		featureFileName = featureFileName.toLowerCase();
		featureFileName = featureFileName.replace("?", "");
		while(true) {
			String option = scan.nextLine();
			if (option.equals("1")) {
				System.out.println("Enter full path of Cucumber project:");
				path = scan.nextLine();
				projectName = path.substring(path.lastIndexOf('/') + 1, path.length());
				break;
			} else if (option.equals("2")){
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
				Runtime rt = Runtime.getRuntime();
				System.out.println("Creating new project...");
				Process p = rt.exec("cmd /c cd / && cd " + path + " && mvn archetype:generate -DarchetypeGroupId=io.cucumber "
						+ "-DarchetypeArtifactId=cucumber-archetype -DarchetypeVersion=2.3.1.2 -DgroupId=" + projectName + " -DartifactId=" + projectName
						+ " -Dpackage=" + projectName + " -Dversion=1.0.0-SNAPSHOT -DinteractiveMode=false");
				BufferedReader reader = 
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				while (reader.readLine() != null);
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

		try {
			File file = new File(path + "/src/test/resources/" + projectName + "/" + featureFileName + ".feature");
			if (!file.exists()) {
				file.createNewFile();
				BufferedWriter wr = new BufferedWriter(new FileWriter(file));
				wr.write("Feature: " + this.feature + "\n\n");
				Set<String> scenarios = map.get(this.feature).keySet();
				for (String scenario : scenarios) {
					wr.write("\tScenario: " + scenario + "\n");
					Set<String> keywords = map.get(this.feature).get(scenario).keySet();
					for (String keyword : keywords) {
						wr.write("\t\t" + map.get(this.feature).get(scenario).get(keyword) + " " + keyword + "\n");
					}
					wr.write("\n");
				}
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
			String stepsDefFile = featureFileName.substring(0, 1).toUpperCase();

			for (int i = 1; i < featureFileName.length(); i++) {
				if (featureFileName.charAt(i) != '_') {
					stepsDefFile += featureFileName.charAt(i);
				} else {
					stepsDefFile += featureFileName.substring(i + 1, i + 2).toUpperCase();
					i++;
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
			System.out.println(result);
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
		return 1;
	}

	public static void main(String[] args) throws IOException {
		ReadAndParseFile file = new ReadAndParseFile();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> map = 
				new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>>();
		String path = "";
		Scanner scan = new Scanner(System.in);

		System.out.println("Enter path to csv file, enter \"0\" to terminate:");
		path = scan.nextLine();
		while (!path.equals("0")) {
			int flag = file.parseCSV(path, map);
			if (flag >= 0) {
				while (file.createMavenProject(map, scan) < 0);
				System.out.println("Enter another path to csv file, enter \"0\" to cancel:");
			} else {
				System.out.println("Enter path to csv file, enter \"0\" to cancel:");
			}
			path = scan.nextLine();
		}
		System.out.println("Program terminated.");
		scan.close();
	}
}