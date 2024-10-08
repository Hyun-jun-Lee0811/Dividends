package zerobase.dividends.scheduler;

import zerobase.dividends.model.Company;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.model.constants.CacheKey;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.persist.entity.DividendEntity;
import zerobase.dividends.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

  private final CompanyRepository companyRepository;
  private final Scraper yahooFinanceScraper;
  private final DividendRepository dividendRepository;

  @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
  @Scheduled(cron = "${scheduler.scrap.yahoo}")
  public void yahooFinanceScheduling() {
    List<CompanyEntity> companies = this.companyRepository.findAll();

    for (var company : companies) {
      log.info(company.getName());
      ScrapedResult scrapResult = this.yahooFinanceScraper.scrap(
          new Company(company.getTicker(), company.getName()));

      scrapResult.getDividends().stream()
          .map(e -> new DividendEntity(company.getId(), e))
          .forEach(e -> {
            boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(),
                e.getDate());
            if (!exists) {
              this.dividendRepository.save(e);
            }
          });

      try {
        Thread.sleep(30000);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }
}
