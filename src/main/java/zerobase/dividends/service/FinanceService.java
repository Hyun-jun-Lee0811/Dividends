package zerobase.dividends.service;

import zerobase.dividends.exception.impl.NoCompanyException;
import zerobase.dividends.model.Company;
import zerobase.dividends.model.Dividend;
import zerobase.dividends.model.ScrapedResult;
import zerobase.dividends.model.constants.CacheKey;
import zerobase.dividends.persist.CompanyRepository;
import zerobase.dividends.persist.DividendRepository;
import zerobase.dividends.persist.entity.CompanyEntity;
import zerobase.dividends.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

  private final CompanyRepository companyRepository;
  private final DividendRepository dividendRepository;

  @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
  public ScrapedResult getDividendByCompanyName(String companyName) {
    CompanyEntity company = this.companyRepository.findByName(companyName)
        .orElseThrow(NoCompanyException::new);

    List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(
        company.getId());

    List<Dividend> dividends = dividendEntities.stream()
        .map(e -> new Dividend(e.getDate(), e.getDividend()))
        .collect(Collectors.toList());

    return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);
  }
}