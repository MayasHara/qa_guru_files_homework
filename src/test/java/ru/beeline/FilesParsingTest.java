package ru.beeline;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Configuration;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import dev.failsafe.internal.util.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class FilesParsingTest {
    @Test
    void pdfFileParsingTest() throws Exception {
        open ("https://docs.junit.org/current/user-guide/");
        File downloaded = $("[href*='junit-user-guide-5.13.3.pdf']").download();
        try (InputStream is = new FileInputStream(downloaded)){

            PDF pdf = new PDF(downloaded);
            Assertions.assertEquals ("Stefan Bechtold, Sam Brannen, Johannes Link, Matthias Merdes, Marc Philipp, Juliette de Rancourt, Christian Stein", pdf.author);

        }
    }
    @Test
    void xlsFileParsingTest() throws Exception {
        Configuration.pageLoadStrategy = "eager";
        open("https://sample.cat/ru/xls");
        File download = $("[href='https://disk.sample.cat/samples/xlsx/sample1.xlsx']").download();
        XLS xls = new XLS(download);

        String actualValue = xls.excel.getSheetAt(0).getRow(0).getCell(1).getStringCellValue();

        Assertions.assertTrue(actualValue.contains("Sales_Rep_ID"));

    }

    private ClassLoader cl = FilesParsingTest.class.getClassLoader();
    @Test
    void csvFileParsingTest() throws Exception {

        try (InputStream is = cl.getResourceAsStream("example_2.5kb.csv");
             CSVReader csvReader = new CSVReader(new InputStreamReader(is))) {

            List<String[]> data = csvReader.readAll();
            Assertions.assertEquals (2, data.size());
            Assertions.assertArrayEquals(
                    new  String[] {"name","phoneNumber","email","address","userAgent","hexcolor"},
                    data.get(0)
            );

            Assertions.assertArrayEquals(
                    new  String[] {"Dr. Mariam Gutmann III","582.788.4315","christiansen.noble@hotmail.com","9178 Macey Isle Apt. 758"},
                    data.get(1)
            );

        }

    }

    @Test
    void zipFileParsingTest () throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("sample.zip")
        ))  {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
        }
    }
}
