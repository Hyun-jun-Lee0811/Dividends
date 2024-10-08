package zerobase.dividends.scraper;

import zerobase.dividends.model.Company;
import zerobase.dividends.model.Dividend;
import zerobase.dividends.model.ScrapedResult;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import zerobase.dividends.model.constants.Month;

@Component
public class YahooFinanceScraper implements Scraper {

  private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&frequency=1mo";
  private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
  private static final long START_TIME = 86400;

  @Override
  public ScrapedResult scrap(Company company) {
    var scrapedResult = new ScrapedResult();
    scrapedResult.setCompany(company);

    try {
      long now = System.currentTimeMillis() / 1000;
      String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
      Connection connection = Jsoup.connect(url)
          .userAgent(
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
      Document document = connection.get();

      Elements parsingDivs = document.select(".table.svelte-ewueuo");
      Element tableEle = parsingDivs.get(0);
      Element tbody = tableEle.children().get(1);

      List<Dividend> dividends = new ArrayList<>();
      for (Element e : tbody.children()) {
        String txt = e.text();
        if (!txt.endsWith("Dividend")) {
          continue;
        }

        String[] split = txt.split(" ");
        int month = Month.strToNumber(split[0]);
        int day = Integer.valueOf(split[1].replace(",", ""));
        int year = Integer.valueOf(split[2]);
        String dividend = split[3];
        if (month < 0) {
          throw new RuntimeException("Unexpected Month enum value -> " + split[0]);
        }
        dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));
      }
      scrapedResult.setDividends(dividends);

    } catch (IOException e) {
      e.printStackTrace();
    }
    return scrapedResult;
  }

  @Override
  public Company scrapCompanyByTicker(String ticker) {
    String url = String.format(SUMMARY_URL, ticker, ticker);

    try {
      Document document = Jsoup.connect(url).userAgent(
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
          .get();

      Element titleEle = document.getElementsByTag("h1").get(1);
      String title = titleEle.text();
      return new Company(ticker, title);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
