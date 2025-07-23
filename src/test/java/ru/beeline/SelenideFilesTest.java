package ru.beeline;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class SelenideFilesTest  {

    @Test
    void downloadFileTest() throws Exception {
        open("https://github.com/junit-team/junit-framework/blob/main/README.md");
        File downloaded = $(".BlobViewHeader-module__Box_3--Kvpex [href*='main/README.md']")
                .download();

        try(InputStream is = new FileInputStream(downloaded)){
            byte[] data = is.readAllBytes();
            String dataAsString = new String(data, StandardCharsets.UTF_8);
            Assertions.assertTrue(dataAsString.contains("Contributions to JUnit are both welcomed and appreciated."));
        }
    }
    @Test
    void uploadFileTest () {

    }
}
