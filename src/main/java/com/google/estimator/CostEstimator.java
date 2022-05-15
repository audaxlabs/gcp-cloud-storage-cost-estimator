package com.google.estimator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.codec.Charsets;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.estimator.dto.Region;

/**
 * Hello world!
 *
 */
public class CostEstimator {
	public static String sorceFilePath ="";

	public static void main(String... args) throws IOException, ParseException, InterruptedException {
		Scanner scanner = new Scanner(System.in);
		sorceFilePath =args[0];

		sourceInfo(sorceFilePath);
		System.out.println("Press 0 for Cloud Storage Price or Press 1 for exit");
		if (scanner.nextInt() == 0) {
			TranferPrice();
		}
		scanner.close();

	}

	public static Double sourceSize(String congifPath) throws InterruptedException, IOException {
		String size = "";
		double sizeGB = 0.00104858;

		ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
		String mountCommand = getProperty(congifPath, "source_command");
		processBuilder.command(new String[] { "bash", "-c", mountCommand });
		try {
			Process process = processBuilder.start();
			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {

				output.append(String.valueOf(line) + "\n");
				String[] sb = output.toString().split(" ");
				size = sb[0];

			}

		} catch (Exception e) {

		}
		return sizeGB * Integer.parseInt(size);
	}

	public static void sourceInfo(String filePath) throws IOException, ParseException, InterruptedException {
		Region region = new Region();
		region.setResourceGroup(getProperty(filePath, "resorceGroup"));
		region.setContinent(getProperty(filePath, "continent"));
		region.setRegionName(getProperty(filePath, "region"));
		region.setSize(sourceSize(filePath));
		region.setOperational(getProperty(filePath, "operationClass"));

		System.out.println("Source Bucket Info:-");
		System.out.println("resorceGroup:- " + region.getResourceGroup());
		System.out.println("Continent:- " + region.getContinent());
		System.out.println("region:- " + region.getRegionName());
		System.out.println("size:- " + region.getSize() + " GB");
		System.out.println("======================================");

		region.setOperationalGroup(region.getResourceGroup().replace("Storage", "Ops"));
		calculate(region);
		System.out.println("======================================");

	}

	public static void TranferPrice() throws FileNotFoundException, IOException, ParseException {
		Region region = new Region();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Select Storage Type");
		List<String> resourceGroupList = new ArrayList<String>();
		resourceGroupList.add("NearlineStorage");
		resourceGroupList.add("ColdlineStorage");
		resourceGroupList.add("ArchiveStorage");
		resourceGroupList.add("DRAStorage");
		resourceGroupList.add("Standard Storage");
		System.out.println("======================================");
		for (int i = 0; i < resourceGroupList.size(); i++) {
			System.out.println(i + "->" + resourceGroupList.get(i));
		}
		System.out.println("======================================");
		System.out.print("PLEASE PROVIDE OPTION#:");
		int opt = scanner.nextInt();

		if (opt == 4) {
			List<String> standardstoragelist = new ArrayList<String>();
			standardstoragelist.add("RegionalStorage");
			standardstoragelist.add("MultiRegionalStorage");

			System.out.println("======================================");
			for (int i = 0; i < standardstoragelist.size(); i++) {
				System.out.println(i + "->" + standardstoragelist.get(i));
			}
			System.out.println("======================================");
			System.out.print("PLEASE PROVIDE OPTION#:");
			region.setResourceGroup(standardstoragelist.get(new Integer(scanner.nextInt())));

		} else {
			region.setResourceGroup(resourceGroupList.get(new Integer(opt)));

		}

		region.setOperationalGroup(region.getResourceGroup().replace("Storage", "Ops"));

		System.out.println("Select Continent");
		Map<Integer, String> continentMap = new HashMap<Integer, String>();
		continentMap.put(0, "North America");
		continentMap.put(1, "Asia");
		continentMap.put(2, "Indonesia");
		continentMap.put(3, "Europe");
		continentMap.put(4, "Australia");
		System.out.println("======================================");
		for (int i = 0; i < continentMap.size(); i++) {

			System.out.println(i + "->" + continentMap.get(i));

		}
		System.out.println("======================================");
		System.out.print("PLEASE PROVIDE OPTION#:");

		region.setContinent(continentMap.get(new Integer(scanner.nextInt())));
		JSONParser jsonParser = new JSONParser();

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("Continent.json");

		//String text = new String(is.readAllBytes(), StandardCharsets.UTF_8); 
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String inputLine;
		StringBuffer text = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			text.append(inputLine);
		}
		in.close();
		
		JSONObject continetObject = new JSONObject(text.toString());
		JSONArray resorceArr = continetObject.getJSONArray("resource");
		List<String> regionsList = new ArrayList<String>();
		for (int i = 0; i < resorceArr.length(); i++) {
			if (resorceArr.getJSONObject(i).getString("continent").equals(region.getContinent())) {
				JSONArray regionArr = resorceArr.getJSONObject(i).getJSONArray("region");
				for (int j = 0; j < regionArr.length(); j++) {
					regionsList.add(regionArr.getString(j));
				}

			}

		}
		System.out.println("Select Number of the Region");
		System.out.println("======================================");
		for (int i = 0; i < regionsList.size(); i++) {
			System.out.println(i + "->" + regionsList.get(i));
		}
		System.out.println("======================================");
		System.out.print("PLEASE PROVIDE OPTION#:");

		region.setRegionName(regionsList.get(new Integer(scanner.nextInt())));

		System.out.println("Select the Operation");
		List<String> operationclass = new ArrayList<String>();
		operationclass.add("Class A operations");
		operationclass.add("Class B operations");
		operationclass.add("Free operations");
		System.out.println("======================================");
		for (int i = 0; i < operationclass.size(); i++) {

			System.out.println(i + "->" + operationclass.get(i));

		}
		System.out.println("======================================");
		System.out.print("PLEASE PROVIDE OPTION#:");

		if (scanner.nextInt() == 0) {
			List<String> operationclassA = new ArrayList<String>();

			operationclassA.add("Nearline Class A Operations");
			operationclassA.add("Durable Reduced Availability Class A Operations");
			operationclassA.add("Regional Standard Class A Operations");
			operationclassA.add("Archive Class A Operations");
			operationclassA.add("Multi-Region Standard Class A Operations");
			operationclassA.add("Coldline Class A Operations");
			for (int i = 0; i < operationclassA.size(); i++) {

				System.out.println(i + "->" + operationclassA.get(i));

			}
			System.out.println("======================================");
			System.out.print("PLEASE PROVIDE OPTION#:");
			region.setOperational(operationclassA.get(new Integer(scanner.nextInt())));

		}

		else if (scanner.nextInt() == 1) {
			List<String> operationclassB = new ArrayList<String>();
			operationclassB.add("Nearline Class B Operations");
			operationclassB.add("Durable Reduced Availability Class B Operations");
			operationclassB.add("Regional Standard Class B Operations");
			operationclassB.add("Archive Class B Operations");
			operationclassB.add("Multi-Region Standard Class B Operations");
			operationclassB.add("Coldline Class B Operations");
			for (int i = 0; i < operationclassB.size(); i++) {

				System.out.println(i + "->" + operationclassB.get(i));

			}
			System.out.println("======================================");
			System.out.print("PLEASE PROVIDE OPTION#:");
			region.setOperational(operationclassB.get(new Integer(scanner.nextInt())));

		} else {
			region.setOperational(operationclass.get(new Integer(scanner.nextInt())));
		}
		System.out.println("Enter Storage Size In GB");
		region.setSize(scanner.nextLong());
		calculate(region);
		System.out.println("Press 0 for Cloud Storage Price or Press 1 for exit");
		if (scanner.nextInt() == 0) {
			TranferPrice();
		}
	}

	public static String getProperty(String filePath, String key) throws IOException {
		FileInputStream fis = new FileInputStream(filePath);
		Properties prop = new Properties();

		prop.load(fis);

		return prop.getProperty(key);

	}

	public static String operation(Region region) throws IOException {
		String key = "AIzaSyAT0t98XajD5o-ZCp_B71nkmb9dOSDI6Gg";
		long displayQuantity = 10000;

		URL url = new URL("https://cloudbilling.googleapis.com/v1/services/95FF-2EF5-5EA1/skus?key=" + key);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		JSONObject json = new JSONObject(content.toString());
		if (json != null) {

			if (json.has("skus")) {
				JSONArray skusArr = json.getJSONArray("skus");
				for (int i = 0; i < skusArr.length(); i++) {
					JSONObject skus = skusArr.getJSONObject(i);
					JSONObject category = skus.getJSONObject("category");
					if (skus.getString("description").equals(region.getOperational())) {
						if (category.getString("resourceGroup").equals(region.getOperationalGroup())) {

							JSONArray pricingInfoArray = skus.getJSONArray("pricingInfo");
							JSONObject pricingExpressionObjet = pricingInfoArray.getJSONObject(0)
									.getJSONObject("pricingExpression");

							JSONArray tieredRates = pricingExpressionObjet.getJSONArray("tieredRates");
							JSONObject unitPricSObject = tieredRates.getJSONObject(0).getJSONObject("unitPrice");

							region.setOperationalPrice(String.valueOf(
									(unitPricSObject.getInt("nanos") * Math.pow(10, -9) * displayQuantity) + " "
											+ unitPricSObject.getString("currencyCode") + " (per 10,000 operations)"));

						}

					}

				}

			}

		}

		return region.getOperationalPrice();

	}

	public static void calculate(Region region) throws IOException, ParseException {
		String key = "AIzaSyAT0t98XajD5o-ZCp_B71nkmb9dOSDI6Gg";
		URL url = new URL("https://cloudbilling.googleapis.com/v1/services/95FF-2EF5-5EA1/skus?key=" + key);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		JSONObject json = new JSONObject(content.toString());
		if (json != null) {
			if (json.has("skus")) {
				JSONArray skusArr = json.getJSONArray("skus");
				for (int i = 0; i < skusArr.length(); i++) {
					JSONObject skus = skusArr.getJSONObject(i);
					JSONObject category = skus.getJSONObject("category");
					if (category.getString("resourceGroup").equals(region.getResourceGroup())) {

						JSONArray regionArr = skus.getJSONArray("serviceRegions");
						if (regionArr.get(0).equals(region.getRegionName().toLowerCase())) {
							JSONArray pricingInfoArray = skus.getJSONArray("pricingInfo");
							{
								JSONObject pricingExpressionObjet = pricingInfoArray.getJSONObject(0)
										.getJSONObject("pricingExpression");
								JSONArray tiredArray = pricingExpressionObjet.getJSONArray("tieredRates");
								JSONObject unitPricSObject = tiredArray.getJSONObject(0).getJSONObject("unitPrice");
								region.setPrice(String
										.valueOf((unitPricSObject.getInt("nanos") * Math.pow(10, -9)
												+ unitPricSObject.getInt("units")) * region.getSize())
										+ " " + unitPricSObject.getString("currencyCode") + " " + pricingExpressionObjet
												.getString("usageUnitDescription").replace("gibibyte", "GB"));

							}

						}
					}
				}

			}

			if (region.getPrice() != null) {
				System.out.println("======================================");
				System.out.println("Resource Group:" + region.getResourceGroup());
				System.out.println("Continent:" + region.getContinent());
				System.out.println("Region:" + region.getRegionName());
				System.out.println("Size:" + region.getSize() + " GB");
				System.out.println("EstimatedCost:" + region.getPrice());
				System.out.println("OperationCost:" + operation(region));

				System.out.println("======================================");

			} else {
				System.out.println("The price is not available for perticular region/StorageType selected.");
				TranferPrice();
			}

		}

	}

}
