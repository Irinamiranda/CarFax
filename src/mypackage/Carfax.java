package mypackage;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import java.util.*;

public class Carfax {
    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", "C:\\MyIdeaProjects2021\\Utils\\drivers\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://carfax.com");

        driver.findElementByXPath("//a[.='Find a Used Car']").click();
        String title = driver.getTitle();
        Assert.assertTrue(title.contains("Used Cars"));

        //4.	Choose “Tesla” for  Make.
        WebElement makeSelect = driver.findElementByXPath("//select[@class='form-control search-make search-make--lp']");
        makeSelect.click();
        Thread.sleep(500);
        driver.findElementById("make_Tesla").click();

        //5.	Verify that the Select Model dropdown box contains 4 current Tesla models - (Model 3, Model S, Model X, Model Y).
        WebElement selector = driver.findElementByXPath("//select[@class='form-control search-model  search-model--lp']");
        //List<WebElement> models = driver.findElementsByXPath("//optgroup[@class='current-models']//option[@class='model-option']");
        List<WebElement> models = driver.findElementsByCssSelector("optgroup.current-models > .model-option");
        List<String> expectedModels = Arrays.asList("Model 3", "Model S", "Model X", "Model Y");
        List<String> actualModels = new ArrayList<>();
        for (WebElement model : models) {
            actualModels.add(model.getAttribute("value"));
        }

        Assert.assertEquals(actualModels,expectedModels);

        //6.	Choose “Model S” for Model.
        WebElement modelSelect = driver.findElementByCssSelector("optgroup.current-models > .model-option");
        modelSelect.click();
        Thread.sleep(500);
        driver.findElementById("model_Model-S").click();

        //7.	Enter the zip code 22182 and click Next
        Thread.sleep(500);
        driver.findElementByCssSelector("input.search-zip").sendKeys("22182");
        driver.findElementById("make-model-form-submit").click();

        //8.	Verify that the page contains the text “Step 2 – Show me cars with”
        Thread.sleep(500);
        String title2 = driver.getPageSource();
        Assert.assertTrue(title2.contains("Step 2 - Show me cars with"));

        //9.	Check all 4 checkboxes.
        Thread.sleep(500);
        List<WebElement> checkBoxes = driver.findElementsByCssSelector(".four-pillar-form label.checkbox");
        for (WebElement checkBox : checkBoxes) {
            if(!checkBox.isSelected())
            checkBox.click();
        }

        //10.	Save the count of results from “Show me X Results” button. In this case it is 10.

        String resultNo = driver.findElementByXPath("//button[@class='button large primary-green show-me-search-submit']//span[@class='totalRecordsText']").getText();

        //11.	Click on “Show me x Results” button.
        driver.findElementByXPath("//button[@class='button large primary-green show-me-search-submit']").click();
        Thread.sleep(2000);

        //12.	Verify the results count by getting the actual number of results displayed in the page by getting the count of WebElements that represent each result

        List<WebElement> results = driver.findElementsByXPath("//article[@class='srp-list-item']");
        String actualNo = "" + results.size();
        Assert.assertEquals(actualNo,resultNo );

        //13.	Verify that each result header contains “Tesla Model S”.
        List<WebElement> headers = driver.findElementsByXPath("//div[@class='listing-header']//h4");
        String expectedHeader = "Tesla Model S";
        for (WebElement header : headers) {
            String actualHeader = header.getText();
            Assert.assertTrue(actualHeader.contains(expectedHeader));
        }

        //14.	Get the price of each result and save them into a List in the order of their appearance. (You can exclude “Call for price” options)
        List<WebElement> elPrices = driver.findElementsByXPath("//span[@class='srp-list-item-price']");
        List<Integer> pricesNotSorted = getPrice(elPrices);

        //15.	Choose “Price - High to Low” option from the Sort By menu

        WebElement sortButton = driver.findElementByXPath("//select[@class='srp-header-sort-select srp-header-sort-select-desktop--srp']");
        sortButton.click();
        driver.findElementById("priceDesc").click();
        Thread.sleep(2000);

        //16.	Verify that the results are displayed from high to low price.
        elPrices = driver.findElementsByXPath("//span[@class='srp-list-item-price']");
        ArrayList<Integer> pricesSorted = getPrice(elPrices);

        Collections.sort(pricesNotSorted);
        Collections.reverse(pricesNotSorted);
        Assert.assertEquals(pricesNotSorted, pricesSorted);

        //17.	Choose “Mileage - Low to High” option from Sort By menu
        List<WebElement> mileages = driver.findElementsByXPath("//div[@class='srp-list-item-basic-info srp-list-item-special-features']//span[1]");
        List<Integer> mileageNotSorted = getMileage(mileages);

        sortButton = driver.findElementByXPath("//select[@class='srp-header-sort-select srp-header-sort-select-desktop--srp']");
        sortButton.click();
        driver.findElementById("mileageAsc").click();
        Thread.sleep(2000);

        //18.	Verify that the results are displayed from low to high mileage.

        List<WebElement> mileagesSorted = driver.findElementsByXPath("//div[@class='srp-list-item-basic-info srp-list-item-special-features']//span[1]");
        List<Integer> mileageSorted = getMileage(mileagesSorted);
        Collections.sort(mileageNotSorted);
        Assert.assertEquals(mileageNotSorted, mileageSorted);

        //19.	Choose “Year - New to Old”menu option from Sort By
        List<WebElement> years = driver.findElementsByXPath("//h4[@class='srp-list-item-basic-info-model']");
        List<Integer> yearsNotSorted = getYears(years);

        sortButton = driver.findElementByXPath("//select[@class='srp-header-sort-select srp-header-sort-select-desktop--srp']");
        sortButton.click();
        driver.findElementById("yearDesc").click();
        Thread.sleep(2000);

        //20.	Verify that the results are displayed from new to old year.
        years = driver.findElementsByXPath("//h4[@class='srp-list-item-basic-info-model']");
        List<Integer> yearsSorted = getYears(years);

        Collections.sort(yearsNotSorted);
        Collections.reverse(yearsNotSorted);

        Assert.assertEquals(yearsSorted, yearsNotSorted);


    }
    public static ArrayList<Integer> getMileage(List<WebElement> list){
        ArrayList<Integer> mileages = new ArrayList<>();
        for (WebElement e : list) {
            String str = e.getText();
            str = str.replaceAll("[^0-9]", "");
            mileages.add(Integer.parseInt(str));
        }
        return mileages;

    }

    public static ArrayList<Integer> getYears(List<WebElement> list){
        ArrayList<Integer> years = new ArrayList<>();
        for (WebElement e : list) {
            String str = e.getText();
            str = str.substring(0,4);
            years.add(Integer.parseInt(str));
        }
        return years;

    }
    public static ArrayList<Integer> getPrice(List<WebElement> list) {
        ArrayList<Integer> prices = new ArrayList<>();
        for (WebElement e : list) {
            String str = e.getText();
            if (!(e.getText().contains("Call for Price"))) {
                str = str.replaceAll("[^0-9]", "");
                prices.add(Integer.parseInt(str));
            }
        }
        return prices;
    }


}
