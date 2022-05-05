## 👊 생드백(Thandbag) - BackEnd 
![thandbag_main_thumbnail](https://user-images.githubusercontent.com/87135478/150528634-b8623912-648a-49a9-9a0a-b980a5c45610.png)

<br />

## 🗂 Summary
### 항해99 4기, 실전 프로젝트 7조
> \* 서비스명 : 생드백 (*Think + Sandbag = Thandbag!*)  
> \* 서비스 설명 : 일상 생활속에서 받은 스트레스, 고민을 샌드백을 때리면서 재미있게 풀 수 있는 스트레스 해소 서비스  
    
- [\[사이트 바로가기\]](https://thandbag.com)  **과금으로 인해 현재는 서버를 운영하지 않고 있습니다.**
- [\[시연영상 바로가기\]](https://www.youtube.com/watch?v=TDr55gjFYGs)  

<br />

## 👥 멤버
- Back-end: [고성범](https://github.com/SeongBeomKo), [오규화(조장)](https://github.com/59-devv)
- Front-end: [이준명](https://github.com/Leejunmyung), [전용태](https://github.com/yong313), [정상일](https://github.com/jsni94)
- Design : 정서윤, 황지현
- [\[Front-End Github\]](https://github.com/thandbag/thandbag_FE)

<br />

## 🗓 프로젝트 기간
- 2021년 12월 18일 ~ 2022년 01월 28일


<br />


## ♟ Information Architecture
![image](https://user-images.githubusercontent.com/87135478/150537317-d428e046-b7da-4f19-8176-351bd04c1b65.png)

<br />

## 🧩 Architecture

![architecture](https://user-images.githubusercontent.com/87135478/151476552-4cae69e4-a62b-46d1-afc5-8bae92349223.png)


<br />


## 🗺 ER Diagram
![image](https://user-images.githubusercontent.com/87135478/151312641-0f6a5210-d4d2-4aa3-a1dd-ca7d8fc10d82.png)

<br />

## ⚙️ Back-End 기술 스택

|분류|기술|
| :-: |:- |
|IDE|<img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=for-the-badge&logo=IntelliJ IDEA&logoColor=white">|
|Language|<img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white">|
|Framework|<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white"> <img src="https://img.shields.io/badge/Springboot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white">|
|Build Tool|<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">|
|DB|<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">|
|Memory DB|<img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">|
|Server|<img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=AmazonAWS&logoColor=white"> <img src="https://img.shields.io/badge/Amazon S3-569A31?style=for-the-badge&logo=Amazon S3&logoColor=white">|
|CI/CD|<img src="https://img.shields.io/badge/Travis CI-3EAAAF?style=for-the-badge&logo=Travis CI&logoColor=white">|
|Test|<img src="https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=JUnit5&logoColor=white">|
|Load Test|<img src="https://img.shields.io/badge/Apache JMeter-D22128?style=for-the-badge&logo=Apache JMeter&logoColor=white">|
|VCS|<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">|
|Reverse Proxy|<img src="https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=NGINX&logoColor=white">|
|API Document Tool|<img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">|

<br />

## 📌 API 명세서
- [\[API 명세서 바로가기\]](https://typical-guanaco-54b.notion.site/API-bda198d1807e4eff8de732ebe02f3134)


<br />

## 🤝 Code Convention

> 1. 변수 네이밍 규칙 : ```Camel Case```
> 2. 한 줄의 글자 수 : ```최대 80자```
> 3. 한 줄 주석 표기 : ```/*  */```
> 4. 여러 줄 주석 표기 : 
> ```java
>   /*-
>    *
>    *
>    */
> ```
> 5. 삼항연산자 표기 : 
> ```
>   alpha = (BooleanExpression) ? beta
>                               : gamma
> ```
> 6. If문에서 &&, ||' 표기
> ```
>   if((condition1 && condition2)
>     || (condition3 && condition4) 
>     || (condition5 && condition6) {
>     do something();
>   }
> ```


<br />

## 👀 유저 피드백  
>  \* 피드백 수집일자 : 2022년 1월 22일 ~ 2022년 1월 25일  
>  \* 피드백 수 : 총 30개  
* 서비스 만족도 평균 점수 : 4.23점 (5점)  
* 스트레스 해소 만족도 점수 : 4.2점 (5점)  
* 긍정적인 피드백 Top3  
    * 생드백을 때리면서 스트레스를 해소한다는 아이디어가 참신하고 좋았다.  
    * 생드백을 때릴 때, 생드백이 맞는 모션과 함께 타격감이 있어서 재미있었다.  
    * 간단하게 이용할 수 있고 인터페이스가 익숙하면서 편하다.  
* 개선에 대한 피드백 Top3  
    * 일부 페이지에서 하단 네비게이션 바가 보여지지 않아 불편했다. (네비게이션 바를 모든 페이지에서 볼 수 있도록 개선 완료)  
    * 채팅을 어떨 때 이용할 수 있는지 모르겠다. (채팅 메뉴에서 이용안내 가이드를 볼 수 있도록 개선 완료)  
    * 첫 화면이 로그인이라서, 로그인을 하지 않으면 아무것도 할 수 없다. (게시글 조회는 로그인을 하지 않아도 되도록 개선 완료)  

<br />

## 👣 런칭 성과
>  \* 런칭일자 : 2022년 01월 22일(토)  
>  \* 성과 집계일자 : 2022년 01월 27일(목) 15:00

### 1. 인스타그램 광고 진행 성과 요약
* 3일간 인스타그램 광고 진행을 통해 약 2만명에게 도달하였으며 이 중, 182명이 사이트에 방문하였음  
* 방문자 중 약 67%는 여성이었음  
* 방문자 중 약 91%는 13-17세의 연령층이었음  
![image](https://user-images.githubusercontent.com/87135478/150996596-5a31264d-0eef-4ad4-8d77-9170158c42ea.png)

<br />

### 2. 개발자 커뮤니티/포럼 홍보 성과 요약
* Okky, 뽐뿌 개발자포럼을 통해 서비스 홍보를 진행하였음
* 사용자, 세션수 등에서 인스타그램 광고를 통한 성과 지표보다 높은 성과를 얻었음
![image](https://user-images.githubusercontent.com/87135478/150999046-b5c3e854-37fe-4dfa-b2c9-20cce48e975d.png)


<br />

### 3. 런칭 이후 누적 데이터 분석 (DataBase)
* 가입 회원 수 : 141명
* 작성된 생드백(게시글) 수 : 총 86개 
* 터트린 생드백 수 : 36개 (전체 대비 41.8%)
* 작성된 잽(댓글) 수 : 총 93개
* 가장 많은 생드백이 작성된 고민 카테고리 Top3 : 기타(27개), 공부(13개), 진로고민(13개)
* 기간동안 생드백이 맞은 횟수 : 총 6,995대 (1인 평균 49.6대)

<br />

### 4. 런칭 이후 누적 데이터 분석 (Google Analytics)
* 사용자 : 741명 (First Visit 기준)
* Page View : 1만
* 이벤트 수 : 1.5만
* 모바일 / PC 비율 : 6.5 / 3.5
* 사용자 재방문 : 12.6%

<br />

## 👍 꼼꼼한 TestCode 작성을 통한 코드 신뢰도 향상

> \* 총 154개의 테스트 코드 작성  
> \* 87%의 커버리지 달성

<br />

![image](https://user-images.githubusercontent.com/22443546/152502943-5a47de3f-e0d9-4ed5-b30b-c0432c827074.png)


<br />

## ⛔️ Trouble Shooting

```
1. 전체DB 조회 시, 반환 속도가 느린 문제 
```
>
> 🛠 해결방법 : DB 인덱싱을 통한 data 반환 속도 향상 (초당 122개 반환 -> 초당 486개 반환)  

> ❗️ 확인된 개선 사항
> 
> 유효성 검사를 하기위해 spirng data JPA에서 findByUsername과 findByNickname과 같은 query method등의 조회가 잦음. Update나 insert가 발생하는 것보다 조회가 많이 발생한다고 판단.
> 
> ex) 마이페이지에서 정보 수정(닉네임이 아니라 MBTI나 이미지를 변경 하기 위해서도 유효성 검사를 진행)
> 
> ex2) 채팅목록을 불러오거나 회원가입 시에도 해당 query method를 호출함.

> indexing을 하기에는 아직 데이터 테이블이 크지는 않으나, 현재 DB 테이블에서는 user 테이블이 가장 크기 때문에, indexing을 적용하여 성능을 확인해 보기로함.
>
> ❗️ 테스트 시나리오
>
> Given: 10000명의 회원이 DB에 저장되어 있음.
> 
> When: 초당 10명이 동시에 자신의 마이페이지 정보를 수정함
> 
> Then: DB인덱싱을 해준것이 아래 결과 스크린 삿과 같이 throughput이 약 4배 개선됨. 
>
> ❗️ 코드  
> <img src=https://user-images.githubusercontent.com/87135478/150985188-6b64bf0b-454d-4cd8-9750-f9fb7b119f2a.png width="500" height="70">  
>  
> ❓ As-Is (Throughput : 122.4/sec)  
> <img src=https://user-images.githubusercontent.com/87135478/150985231-af835a6c-2594-435f-84ca-57b2fa5b94ea.png width="800" height="130">  
>   
> 💡 To-Be (Throughput : 485.9/sec)  
> <img src=https://user-images.githubusercontent.com/87135478/150985255-34abe50e-7919-4a29-b61d-6504590f2081.png width="800" height="130">  

<br/>
<br />

```
2. JPA에서 데이터 조회 시 발생하는 n+1 문제
```
> 
> ❗️ 문제 : Spring Data JPA를 통해 게시글 목록을 전체 조회할 경우, 연관관계가 있는 데이터들이 페치 전략과 상관 없이 전부 추가로 조회되는 쿼리가 발생
>
> 🛠 해결방법 : @Query를 사용한 정적 쿼리문은 페이징 처리가 되지 않아서 @EntityGraph로 Eager로 가져와야 하는것만 fetch join을 해주어서 쿼리문이 한번만 실행되게 해주었다.  
>
> ❓ As-Is  
> <img src=https://user-images.githubusercontent.com/87135478/150987705-9227882e-2592-4985-a58c-e59d5e1d6392.png width="800">
> 
> 💡 To-Be  
> <img src=https://user-images.githubusercontent.com/87135478/150987733-7e63ef58-8b76-417d-99c8-375256813124.png width="800">

<br />
<br />

```
3. 테스트코드 실행 시, 실제 DB로 테스트를 진행하는 문제
```
> 
> ❗️ 문제 : 실제로 사용하는 DB를 테스트에서도 사용할 경우, 데이터에 영향을 미칠 수 있기 때문에 Memory DB에서 테스트를 진행하여야 함
>
> 💡 To-Be (SpringBootTest 진행 시 Test 패키지의 application.properties에, 메모리DB 사용 설정)  
> <img src=https://user-images.githubusercontent.com/87135478/150990025-68a85892-2353-4dfa-ae71-52f1dc2c3aaa.png width="300">
>
> 💡 To-Be (RepositoryTest 진행 시, @DataJpaTest 어노테이션을 활용하여 메모리DB 환경에서 JPA 관련 Bean들만 주입받아 테스트)  
> <img src=https://user-images.githubusercontent.com/87135478/150990293-40a70e01-7cf4-4a05-83c3-643f2777d2c6.png>

<br />
<br />

```
4. 파일 업로드 시, 1mb 이상의 파일이 업로드 되지 않는 문제 (NginX)
```
> 
> ❗️ 문제 : 1mb 미만의 파일은 정상적으로 업로드 되지만, 1mb 이상은 업로드 되지 않음
>
> ❓ 원인 : NginX에서 파일업로드 용량을 1mb로 제한해두었음 (기본 설정)  
>
> 💡 To-Be (nginx.conf 파일 수정을 통한 업로드 제한 용량 변경)  
> <img src=https://user-images.githubusercontent.com/87135478/150990790-849479ae-97e5-45db-83ae-3ec8e5f49e63.png>

<br />
<br />

```
5. 제목, 내용 등에 긴 글 작성이 되지 않을 경우
```
> 
> ❓ 원인 : MySql에서 Data Type이 VARCHAR()로 되어 있어서, 글자수 제한이 존재하였음
>
> 💡 To-Be (LONGTEXT() 타입으로 변경하여 해결)  
> <img src=https://user-images.githubusercontent.com/87135478/150993226-1f9e3a46-967b-4d29-9b13-7081612b2f42.png>

<br />
<br />

```
6. Travis CI 를 통한 배포자동화 진행 시, properties 파일의 보안 처리 문제
```
> 
> ❓ 원인 : properties 파일의 설정값을 읽어와야 하기 때문에, .gitignore에 추가하면 설정을 읽을 수 없어 빌드를 실패함
>
> 💡 To-Be (암호화를 한 후 Travis CI에서 배포 시 복호화 할 수 있도록 설정)  
> ```
> travis encrypt-file application.properties --add
> ```
> ![image](https://user-images.githubusercontent.com/87135478/150993869-45b61130-78aa-4119-887e-ca19b753f13b.png)

<br />
<br />

```
7. 게시글, 채팅의 시간이 한국 시간과 9시간 차이가 나는 문제
```
> 
> ❓ 원인 : EC2, RDS의 시간 기본값이 UTC(협정세계시)으로 설정되어있기 때문
>
> 💡 To-Be (EC2 시간대 변경)   
> ```
> # EC2 시간대 변경
> sudo rm /etc/localtime
> sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime
> ```
>
> 💡 To-Be (RDS 시간대 변경)   
> ![image](https://user-images.githubusercontent.com/87135478/150994438-751f9f57-b2c8-44a1-a50f-cbc87531d1b1.png)


<br />
<br />

```
8. 웹소켓 연결 시도 시 토큰 인증 에러가 나는 문제
```

> ❓ 원인 : Bearer Token을 사용한 후 Bearer 타입까지 포함된 토큰 문자열을 인증하려고 하였기 때문
>
> 💡 To-Be (Stomp Interceptor에서 타입 부분을 잘라냄)
>
> ```
> String jwtToken = accessor
>         .getFirstNativeHeader("Authorization")
>         .substring(7);
> ```
> 
> 🔑 Bearer Token을 사용한 이유
> 
> ```
> # Bearer Token의 정의
> A security token with the property that any party in possession of the token (a "bearer") can use the token
> in any way that any other party in possession of it can. Using a bearer token does not require a bearer to 
> prove possession of cryptographic key material(proof-of-possession).
> 토큰을 소유한 모든 당사자가 토큰을 소유한 다른 당사자가 할 수 있는 방식으로 토큰을 사용할 수 있는 속성이 있는 보안 토큰이다. 
> 보유자 토큰을 사용하는 경우 보유자가 암호화 키 자료(소유 증명)의 소유를 증명할 필요가 없다.
> ```
> 
> ```
> # 일반적으로  토큰은 요청 헤더의 Authorization 필드에 담아져 보내지는데, Authorization은 아래와 같은 구조를 갖고 있다.
> - Authorization: [type] [credential]
> - Bearer는 Authorization Type의 한 종류로, JWT / OAuth에 대한 토큰을 사용할 경우 주로 사용된다.
> ```

<br />
<br />

```
9. EC2 용량제한 문제로 배포가 되지 않는 문제
```

> ❓ 원인 : EC2의 용량 기본 설정이 8GiB로 설정되어있어서, 용량 부족으로 Build 파일이 배포되지 않았음
>
> 💡 To-Be 
>
> ```
> # 1. AWS EC2 웹페이지에서 용량 설정 변경해주기 (8Gib -? 16GiB)
> # 2. EC2 Ubuntu에서 용량이 확장된 디스크 마운트 시켜주기  
>  ~$ sudo growpart /dev/xvda 1
> # 3. Linux 파일시스템에서 디스크 확장 적용 하기
>  ~$ sudo resize2fs /dev/xvda1
> ```
> [\[자세한 내용 - 블로그 보기\]](https://59-devv.github.io/troubleshooting/ts_ec2_storage/)
> 
<br />

## 🔓 적용하지 못한 부분

```
1. 샌드백 클릭/터치 시 동시성 문제
```
> 샌드백을 여러 유저가 동시에 클릭할 경우, 동시성 문제로 인해 hit수가 제대로 업데이트가 되지 않을 수 있겠다고 생각하였음.
> 웹소켓을 연결하여, 접속한 유저들이 실시간으로 샌드백의 타격 수가 올라가는것을 확인할 수 있도록 하려고 하였으나 시간 관계 상 적용하지 못하였음.
> 
> * 대안 - 샌드백을 치려고 페이지에 접속할때, 때리기 전과 후의 횟수를 모두 서버로 전송하여 두 횟수의 차이만 업데이트 하도록 보완하였음.

<br />

```
2. thandbag을 때린 사람의 랭킹 조회 기능
``` 
> thandbag을 떄린 유저들의 목록과 랭킹이 조회 되도록 하게 해달라는 기능이 사용자 피드백으로 왔으나, 서버와 프론트엔드 에서 빠르게 적용할 수 있는 기능이 아니여서 적용하지 못함.
> 
> * 백엔드에서는 DB정규화와 API 추가 및 수정 등이 필요할 것으로 판단되며,
> * 프론트엔드에서는 샌드백때리기 기능을 웹소켓을 사용한 실시간 기능으로 업데이트해야 할 것으로 판단됨.

<br />

```
3. 기타 고려 사항이었지만 시간 관계상 적용하지 못한 점
```
> - 가독성 향상 및 타입 에러 방지를 위해 queryDSL 적용
> - 서버에 과한 요청으로 부하를 주기 쉬운 like 요청 제한 처리(redis 사용)
> - MSA 아키텍쳐 설계방식에 맞춰 기능 단위로 서버를 나누어 서버 안정성 개선

