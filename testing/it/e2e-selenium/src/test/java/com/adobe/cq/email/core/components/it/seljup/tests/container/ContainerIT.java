/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobe.cq.email.core.components.it.seljup.tests.container;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InsertComponentDialog;
import com.adobe.cq.email.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainerIT extends AuthorBaseUITest {

    private static String PN_LAYOUT_NAME = "./layout";
    private String testPage;

    @BeforeEach
    public void setupBeforeEach() throws ClientException, InterruptedException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("test", "Test Page Title", rootPage,
                "/conf/core-email-components-examples/settings/wcm" + "/templates/email-template").getSlingPath();
        // open the page in the editor
        EditorPage editorPage = new PageEditorPage(testPage);
        editorPage.open();
        // create the container component
        createContainer();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
                HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Test: Select half - half layout")
    public void test33() throws InterruptedException {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        openEditDialog(webDriver, act);
        setValueInCombobox(webDriver, act, "3-3");
        viewAsPublished(webDriver);
        List<WebElement> elements = webDriver.findElements(By.cssSelector("[class='layout-column grid-3']"));
        assertEquals(4, elements.size());
        // skipping first element (outer layout)
        WebElement firstColumn = elements.get(1);
        assertTrue(firstColumn.isDisplayed());
        WebElement secondColumn = elements.get(2);
        assertTrue(secondColumn.isDisplayed());
        assertEquals(firstColumn.getSize().getWidth(), secondColumn.getSize().getWidth(), 1);
    }

    @Test
    @DisplayName("Test: Select one third - two third layout")
    public void test24() throws InterruptedException {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        openEditDialog(webDriver, act);
        setValueInCombobox(webDriver, act, "2-4");
        viewAsPublished(webDriver);
        List<WebElement> grid2Elements = webDriver.findElements(By.cssSelector("[class='layout-column grid-2']"));
        assertEquals(1, grid2Elements.size());
        WebElement firstColumn = grid2Elements.get(0);
        assertTrue(firstColumn.isDisplayed());
        List<WebElement> grid4Elements = webDriver.findElements(By.cssSelector("[class='layout-column grid-4']"));
        assertEquals(1, grid4Elements.size());
        WebElement secondColumn = grid4Elements.get(0);
        assertTrue(secondColumn.isDisplayed());
        int firstColumnWidth = firstColumn.getSize().getWidth();
        int secondColumnWidth = secondColumn.getSize().getWidth();
        assertTrue(firstColumnWidth < secondColumnWidth);
        assertEquals(firstColumnWidth, (double) (secondColumnWidth / 2), 1);
    }

    @Test
    @DisplayName("Test: Select two third - one third layout")
    public void test42() throws InterruptedException {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        openEditDialog(webDriver, act);
        setValueInCombobox(webDriver, act, "4-2");
        viewAsPublished(webDriver);
        List<WebElement> grid4Elements = webDriver.findElements(By.cssSelector("[class='layout-column grid-4']"));
        assertEquals(1, grid4Elements.size());
        WebElement firstColumn = grid4Elements.get(0);
        assertTrue(firstColumn.isDisplayed());
        List<WebElement> grid2Elements = webDriver.findElements(By.cssSelector("[class='layout-column grid-2']"));
        assertEquals(1, grid2Elements.size());
        WebElement secondColumn = grid2Elements.get(0);
        assertTrue(secondColumn.isDisplayed());
        int firstColumnWidth = firstColumn.getSize().getWidth();
        int secondColumnWidth = secondColumn.getSize().getWidth();
        assertTrue(firstColumnWidth > secondColumnWidth);
        assertEquals((double) firstColumnWidth / 2, secondColumnWidth, 1);
    }

    @Test
    @DisplayName("Test: Select third - third - third layout")
    public void test333() throws InterruptedException {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        openEditDialog(webDriver, act);
        setValueInCombobox(webDriver, act, "2-2-2");
        viewAsPublished(webDriver);
        List<WebElement> elements = webDriver.findElements(By.cssSelector("[class='layout-column grid-2']"));
        assertEquals(3, elements.size());
        WebElement firstColumn = elements.get(0);
        assertTrue(firstColumn.isDisplayed());
        WebElement secondColumn = elements.get(1);
        assertTrue(secondColumn.isDisplayed());
        WebElement thirdColumn = elements.get(2);
        assertTrue(thirdColumn.isDisplayed());
        int firstColumnWidth = firstColumn.getSize().getWidth();
        int secondColumnWidth = secondColumn.getSize().getWidth();
        int thirdColumnWidth = thirdColumn.getSize().getWidth();
        assertEquals(firstColumnWidth, secondColumnWidth, 1);
        assertEquals(firstColumnWidth, thirdColumnWidth, 1);
    }

    private void createContainer() throws InterruptedException {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        Actions act = new Actions(webDriver);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        click(act, webDriver.findElement(By.cssSelector("[data-text=\"Drag components here\"]")));
        click(act, webDriver.findElement(By.xpath("//*[@id=\"EditableToolbar\"]/button[1]/coral-icon")));
        InsertComponentDialog insertComponentDialog = new InsertComponentDialog();
        insertComponentDialog.selectComponent("/apps/core-email-components-examples/components/container");
    }

    private void openEditDialog(WebDriver webDriver, Actions act) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        click(act, webDriver.findElement(By.id("sidepanel-toggle-button")));
        click(act, webDriver.findElement(By.cssSelector("[title=\"Content Tree\"]")));
        click(act, webDriver.findElement(By.xpath("//span[.='Table Layout']")));
        click(act, webDriver.findElement(By.cssSelector("[data-action='CONFIGURE']")));

    }

    private void setValueInCombobox(WebDriver webDriver, Actions act, String value) throws InterruptedException {
        Commons.useDialogSelect(PN_LAYOUT_NAME, value);
        click(act, webDriver.findElement(By.cssSelector("[title='Done']")));
    }

    private void viewAsPublished(WebDriver webDriver) throws InterruptedException {
        webDriver.get(webDriver.getCurrentUrl().replace("/editor.html", "") + "?wcmmode=disabled");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    private void click(Actions act, WebElement element) throws InterruptedException {
        act.moveToElement(element).click().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    public <T> Collector<T, ?, T> toSingleton() {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            if (list.size() != 1) {
                throw new IllegalStateException();
            }
            return list.get(0);
        });
    }

}
