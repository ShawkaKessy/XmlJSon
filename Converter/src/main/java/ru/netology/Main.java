package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.bean.CsvToBeanBuilder;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Выберите формат: 1 - CSV, 2 - XML");
            int choice = 2;

            if (choice == 1) {
                // Обработка CSV
                String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
                String csvFileName = "data.csv";

                List<Employee> employees = parseCSV(columnMapping, csvFileName);
                String json = listToJson(employees);
                writeString("data.json", json);

                System.out.println("CSV обработан. JSON файл создан: data.json");
            } else if (choice == 2) {
                // Обработка XML
                String xmlFileName = "data.xml";

                List<Employee> employees = parseXML(xmlFileName);
                String json = listToJson(employees);
                writeString("data2.json", json);

                System.out.println("XML обработан. JSON файл создан: data2.json");
            } else {
                System.out.println("Неверный выбор.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try {
            return new CsvToBeanBuilder<Employee>(new FileReader(fileName))
                    .withType(Employee.class)
                    .withMappingStrategy(createColumnMapping(columnMapping))
                    .build()
                    .parse();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static com.opencsv.bean.ColumnPositionMappingStrategy<Employee> createColumnMapping(String[] columnMapping) {
        com.opencsv.bean.ColumnPositionMappingStrategy<Employee> strategy =
                new com.opencsv.bean.ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping(columnMapping);
        return strategy;
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(fileName);
            Element root = document.getDocumentElement();
            NodeList nodeList = root.getElementsByTagName("employee");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    long id = Long.parseLong(getTagValue("id", element));
                    String firstName = getTagValue("firstName", element);
                    String lastName = getTagValue("lastName", element);
                    String country = getTagValue("country", element);
                    int age = Integer.parseInt(getTagValue("age", element));

                    employees.add(new Employee(id, firstName, lastName, country, age));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    private static String getTagValue(String tagName, Element element) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String fileName, String json) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        }
    }
}
