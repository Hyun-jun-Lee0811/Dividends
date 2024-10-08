package zerobase.dividends.web;

import zerobase.dividends.service.FinanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {

  private FinanceService financeService;

  @GetMapping("/dividend/{companyName}")
  public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
    var result = this.financeService.getDividendByCompanyName(companyName);//ScrapResult
    return ResponseEntity.ok(result);
  }
}
