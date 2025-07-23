package ru.beeline;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.json.Json;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilesParsingTestHomeWork {

    private ClassLoader cl = FilesParsingTestHomeWork.class.getClassLoader();

    @Test
    @DisplayName("Тестирование всех файлов в архиве zip")
    void zipFileParsingTest () throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("zipa.zip")
        ))  {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {
                    try (CSVReader csvReader = new CSVReader(new InputStreamReader(zis))) {
                        List<String[]> data = csvReader.readAll();
                        Assertions.assertEquals ("name", data.get(0)[0]);
                        Assertions.assertEquals (2, data.size());
                        Assertions.assertArrayEquals(
                                new  String[] {"name","phoneNumber","email","address","userAgent","hexcolor"},
                                data.get(0)
                        );
                    }
                    break;
                }
                else if (entry.getName().endsWith(".pdf")) {
                        PDF pdf = new PDF(zis);
                        Assertions.assertEquals ("Ian Mitchell", pdf.author);
                        return;
                }
                else if (entry.getName().endsWith(".xlsx")) {
                    XLS xls = new XLS(zis);

                    String actualValue = xls.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue();

                    Assertions.assertTrue(actualValue.contains("Sales_Rep_ID"));
                }
            }
        }
    }
    @Test
    @DisplayName("Тестирование разбора файла JSON библиотекой Jackson")
    void jsonFileParsingTest () throws Exception{
        JsonFactory factory = new JsonFactory();

        try (InputStream is = cl.getResourceAsStream("JSON.json");
             JsonParser parser = factory.createParser(is)){
            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            Assertions.assertEquals("name", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("Marilyne Hill", parser.getText());

            Assertions.assertEquals("phoneNumber", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("+13536085110", parser.getText());

            Assertions.assertEquals("characteristic", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());

            Assertions.assertEquals("email", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("nveum@zieme.net", parser.getText());

            Assertions.assertEquals("address", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            Assertions.assertEquals("street", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("6282 Reichel Course Apt. 591", parser.getText());

            Assertions.assertEquals("city", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("North Rogerport, NC 03149", parser.getText());
            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());

            Assertions.assertEquals("userAgent", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_5_3) AppleWebKit/5341 (KHTML, like Gecko) Chrome/40.0.837.0 Mobile Safari/5341", parser.getText());

            Assertions.assertEquals("hexcolor", parser.nextFieldName());
            Assertions.assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            Assertions.assertEquals("#27254f", parser.getText());

            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            Assertions.assertEquals(JsonToken.END_OBJECT, parser.nextToken());

        }
    }

}
