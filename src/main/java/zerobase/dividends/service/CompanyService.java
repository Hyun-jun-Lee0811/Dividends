package zerobase.dividends.service;

import zerobase.dividends.exception.impl.NoCompanyException;
import org.springframework.data.domain.Page;
import zerobase.dividends.model.Company;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.persist.entity.DividendEntity;
import zerobase.dividends.scraper.Scraper;

@Service
@AllArgsConstructor
public class CompanyService {

  private final Trie trie;
  private final Scraper yahooFinanceScraper;
  private final CompanyRepository companyRepository;
  private final DividendRepository dividendRepository;

  public Company save(String ticker) {
    boolean exists = this.companyRepository.existsByTicker(ticker);
    if (exists) {
      throw new RuntimeException("already exists ticker -> " + ticker);
    }
    return this.storeCompanyAndDividend(ticker);
  }

  public Page<CompanyEntity> getAllCompany(Pageable pageable) {
    return this.companyRepository.findAll(pageable);
  }

  private Company storeCompanyAndDividend(String ticker) {
    Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
    if (ObjectUtils.isEmpty(company)) {
      throw new RuntimeException("failed to scrap ticker -> " + ticker);
    }
    ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);
    CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
    List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
        .map(e -> new DividendEntity(companyEntity.getId(), e))
        .collect(Collectors.toList());
    this.dividendRepository.saveAll(dividendEntities);
    return company;
  }

  public List<String> getCompanyNamesByKeyword(String keyword) {
    Pageable limit = PageRequest.of(0, 10);
    Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(
        keyword, limit);
    return companyEntities.stream()
        .map(e -> e.getName())
        .collect(Collectors.toList());
  }

  public void addAutocompleteKeyword(String keyword) {
    this.trie.put(keyword, null);
  }

  public void deleteAutocompleteKeyword(String keyword) {
    this.trie.remove(keyword);
  }

  public String deleteCompany(String ticker) {
    var company = this.companyRepository.findByTicker(ticker)
        .orElseThrow(NoCompanyException::new);

    this.dividendRepository.findAllByCompanyId(company.getId());
    this.companyRepository.delete(company);
    this.deleteAutocompleteKeyword(company.getName());

    return company.getName();
  }
}
