package zerobase.dividends.web;

import zerobase.dividends.model.Auth;
import zerobase.dividends.security.TokenProvider;
import zerobase.dividends.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final MemberService memberService;

  private final TokenProvider tokenProvider;

  @PostMapping("/signup")
  public ResponseEntity<?> signUp(@RequestBody Auth.SignUp request) {
    var result = this.memberService.register(request);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody Auth.SignIn request) {

    var member = this.memberService.authenticate(request);
    var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());
    log.info("user login : " + request.getUsername());
    return ResponseEntity.ok(token);
  }
}
