# ![revenue_12155113](https://github.com/user-attachments/assets/1f82db3b-7cb5-467e-b23c-2a3cd8525fb1) 주식 배당금 프로젝트
 미국 주식의 배당금 정보(Yahoo Finance)를 스크래핑하여 제공하는 API 서비스입니다.

 ## 각 API 명세서
 
 ### 1) 회원가입 API

- **URL**: `/auth/signup`
- **Method**: `POST`
- **Description**: 새로운 사용자를 등록하는 API

### 2) 로그인 API

- **URL**: `/auth/signin`
- **Method**: `POST`
- **Description**: 사용자 인증 후 JWT 토큰을 발급하는 API

### 회사 정보 API

#### 3) 회사명 자동완성 API

- **URL**: `/company/autocomplete`
- **Method**: `GET`
- **Description**: 자동완성 기능을 위한 API. 검색하고자 하는 prefix를 입력으로 받아 해당 prefix로 검색되는 회사명 리스트 중 10개를 반환합니다.

#### 4) 회사 추가 API

- **URL**: `/company`
- **Method**: `POST`
- **Description**: 새로운 회사 정보를 추가합니다. 추가하고자 하는 회사의 ticker를 입력으로 받아 해당 회사의 정보를 스크래핑하고 저장합니다.

#### 5) 회사 목록 조회 API

- **URL**: `/company`
- **Method**: `GET`
- **Description**: 서비스에서 관리하고 있는 모든 회사 목록을 반환합니다. 반환 결과는 Page 인터페이스 형태입니다.

#### 6) 회사 삭제 API

- **URL**: `/company/{ticker}`
- **Method**: `DELETE`
- **Description**: ticker에 해당하는 회사 정보를 삭제합니다. 삭제 시 회사의 배당금 정보와 캐시도 모두 삭제되어야 합니다.

### 7) 배당금 정보 조회 API

- **URL**: `/finance/dividend/{companyName}`
- **Method**: `GET`
- **Description**: 회사 이름을 입력으로 받아 해당 회사의 메타 정보와 배당금 정보를 반환하는 API.
 
 ## Development Environment
 <img src="https://img.shields.io/badge/windows-0078D6?style=for-the-badge&logo=windows&logoColor=white"><img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white"><img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"><img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/H2-1C3B1F?style=for-the-badge&logo=h2database&logoColor=white"><img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"><img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">

 ## Dependencies
- `Spring Web`
- `Spring Security`
- `Spring Data JPA`
- `Spring Data Redis`
- `JSON Web Token (JWT)`
- `Jackson Datatype JSR310`
- `H2 Database`
- `Jsoup`
- `Apache Commons Collections`
- `Lombok`
- `JUnit Jupiter`
```
 dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'org.springframework.boot:spring-boot-starter-security'

    implementation group: 'io.jsonwebtoken', name: 'jjwt', version: '0.9.1'
    implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.4'
    implementation group: 'com.h2database', name: 'h2', version: '1.4.200'
    implementation group: 'org.jsoup', name: 'jsoup', version: '1.7.2'
    implementation group: 'org.apache.commons', name: 'commons-collections4', version: '4.3'

    compileOnly group: 'org.projectlombok', name: 'lombok', version: '1.18.22'
    annotationProcessor group: 'org.projectlombok', name: 'lombok', version: '1.18.22'

    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.1'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.1'
}
