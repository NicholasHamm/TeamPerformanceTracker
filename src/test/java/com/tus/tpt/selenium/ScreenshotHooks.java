package com.tus.tpt.selenium;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotHooks {

    @Autowired
    private DriverFactory driverFactory;

    @After
    public void takeScreenshotOnFailure(Scenario scenario) throws IOException {
        if (!scenario.isFailed()) {
            return;
        }

        if (driverFactory.driver() == null) {
            return;
        }

        if (!(driverFactory.driver() instanceof TakesScreenshot)) {
            return;
        }

        Files.createDirectories(Path.of("target", "screenshots"));

        String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");

        File src = ((TakesScreenshot) driverFactory.driver()).getScreenshotAs(OutputType.FILE);
        File dest = Path.of("target", "screenshots", safeName + ".png").toFile();

        FileUtils.copyFile(src, dest);
    }
}