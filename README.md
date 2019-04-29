# API for Spring Cloud Config Server


# 1. configServerDomain/editConfig
컨피그 서버에 바꿀 설정키와 값을 요청하면 레파지토리의 설정파일이 수정되는 API입니다.
해당 엔드포인트로 JSON형식으로 요구정보를 보내면 config server repository의 yml파일이 수정됩니다.
저장소정보와 수정/추가할 key, value만 보내면 yml에 수정/추가 됩니다.


![설정파일 수정요청](./image/config3.png)

수정요청사항을 Json형식으로 요청합니다. 
 - username: 깃허브의 계정이름
 - repo : repository의 이름
 - rootPath : 수정할 파일이 있는 경로
 - token : 쓰기 권한이 있는 깃계정 토큰 OAuth2
 - key: 설정파일(yml)에서 추가하거나 수정할 키(property)의 이름
 - newValue : key의 값에 해당하는 값. String이 아니면 더블쿼트를 입력하지 않는것이 중요합니다
 
 수정전의 yml파일 내용입니다.
 
 ![원래 설정파일](./image/config2.png)
 
 test.testsub가 있으면 수정하고 없으면 추가하라는 POST 요청을 한 결과를 리턴합니다. 
 
 ![요청대로 변경된 설정파일 수정결과](./image/config1.png)
 
 # 2. configServerDomain/refreshAll
 config server와 연결된 repository에서 수정이 발생할때마다 저장소 전체를 새로고침하는 API입니다.
 수정할때마다 http://configServerDomain/monitor 에 path=*를 POST하도록 합니다.
 Git 저장소 Webhook 주소로 http://configServerDomain/refreshAll 추가하면됩니다.
 Webhook에서 configServerDomain은 외부에서 접속가능한 ip여야합니다.
 refreshAll을 사용하면 설정 수정이 발생해도 무중단 수정이 가능해집니다.
