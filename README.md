# utxo
Java 기반으로 UTXO 기반의 샘플 블록체인 프로그램을 개발한다.



### History

- 초기 버전 업로드
- Block 생성 시간 측정하는 부분 추가 -> BlockUtil 내부에 작성, 실제 측정은 WalletUtil에서 진행

<br/>



### TDL

- 여러 UTXO 중에서 해당 거래에 사용할 UTXO를 가장 최소로 선택하는 알고리즘 구현
- 여러 트랜잭션의 모음 크기에 따라 블록을 생성하는 부분 구현
- Golang 기반으로 프로그램 구현(goroutine 사용해보고 싶다)

<br/>



### Gradle

Bouncycastle

- Private key와 Public key 쌍을 만드는데 사용

- ```java
  compile group: 'org.bouncycastle', name: 'bcprov-jdk16', version: '1.45'
  ```

<br/>



### Global Object

```java
public static ArrayList<Block>      blockchain      = new ArrayList<>(); // 블록체인
public static HashMap<String, UTXO> utxos           = new HashMap<>(); // UTXO
```

- blockchain: 거래로 인하여 생성된 블록을 연결한 리스트 구조

- utxos: Unspent(사용되지 않은) 트랜잭션들의 모음

  | String     | UTXO |
  | ---------- | ---- |
  | Public key | utxo |

<br/>



### Classes

##### entity

- Block
  - 블럭 구성 정보
  - 블럭 해시값 계산, 머클루트 생성, 블록 마이닝 등
- Transaction
  - 거래 별 정보
  - 트랜잭션 해시값 계산, 거래에 사용되는 총 금액 계산, 거래 프로세스 진행 등
- UTXO
  - Unspent 된 트랜잭션들에 대한 정보
- Wallet
  - 특정 사용자의 지갑 정보
  - 비밀키-공개키 생성, 본인의 총 Unspent 트랜잭션 액수 계산, 송금 프로세스 등

##### service (CryptoUtil, WalletUtil을 제외한 나머지는 테스트의 편의를 위해 구성)

- CryptoUtil: 해시값 계산 등의 암호화 유틸리티 
- WalletUtil: 지갑 정보 출력 및 Wallet-to-wallet의 송금 기능 등을 제공하는 유틸리티
  - userDatabase: 테스트가 정상적으로 완료되었는지 확인하기 위해 publickey-username을 매핑한 구조
- BlockUtil: 블록에 대한 정보를 보여주는 유틸리티
- UtxoUtil: UTXO에 대한 정보를 보여주는 유틸리티

<br/>



### Test scenario

```java
public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        /*
         * 1. 제네시스 지갑(일종의 화폐발행) 생성
         * - 모든 거래는 지갑을 통해서 진행됨
         * - 초기 1000.0만큼 부여
         */
        Wallet genesisWallet    = walletUtil.createGenesisWallet(1000f);
        
        /*
         * 2. GenesisWallet -> Peter에게 500.0만큼 송금
         *    ㄴ GenesisWallet: 1000.0 - 500.0 = 500.0
         *    ㄴ Peter : 500.0
         */
        Wallet peter            = walletUtil.makeWallet("peter");
        walletUtil.sendMoneyTo(genesisWallet, peter, 500f);    //genesis 500,

        /*
         * 3. Peter -> Alice에게 200.0만큼 송금
         *    ㄴ GenesisWallet: 500.0
         *    ㄴ Peter: 500.0 - 300.0 = 200.0
         *    ㄴ Alice: 300.0
         */
        Wallet alice            = walletUtil.makeWallet("alice"); // peter 200
        walletUtil.sendMoneyTo(peter, alice, 300f);

        /*
         * 4. Alice -> James에게 100.0만큼 송금
         *    ㄴ GenesisWallet: 500.0
         *    ㄴ Peter: 300.0
         *    ㄴ Alice: 300.0 - 100.0 = 200.0
         *    ㄴ James: 100.0
         */
        Wallet james            = walletUtil.makeWallet("james"); // james 100
        walletUtil.sendMoneyTo(alice, james, 100f);

        /*
         * 5. Alice -> Bob에게 150.0만큼 송금
         *    ㄴ GenesisWallet: 500.0
         *    ㄴ Peter: 300.0
         *    ㄴ Alice: 200.0 - 150.0 = 50.0
         *    ㄴ James: 100.0
         *    ㄴ Bob: 150.0
         */
        Wallet bob            = walletUtil.makeWallet("bob");   // alice 50 bob 150
        walletUtil.sendMoneyTo(alice, bob, 150f);


        System.out.println("============== Summary ==============");
        walletUtil.printUserList();
        utxoUtil.getAllUTXOs();
        blockUtil.printBlockchainInfo();
        BlockUtil.getAverage();
    }
```

<br/>



### 실행 결과

```shell
============== Summary ==============
[User list information]
[0] Username : genesis, PublicKey : EC Public Key
            X: 65f5f1d59d0e0493878a268362f08039b584edd75e18c388
            Y: d1f6a654a6417954bd63f28b18d64452a1ac630224b25e36

[1] Username : peter, PublicKey : EC Public Key
            X: 7cdb55d8f472a355b68418bfa7099f5883e83548a3f5e202
            Y: cb19bf0968a2245bbe877194a345885e6e7bc1f79e027e39

[2] Username : alice, PublicKey : EC Public Key
            X: 23f0fdaacd30a8464168de2859ba7e32df5cae060da43694
            Y: c1ea8f6a0eb8d05a884007c7887c898f4149e1cec9fbea4f

[3] Username : bob, PublicKey : EC Public Key
            X: 41824c6bf1158ba63eee87b8d3f3d69fa9192e750fb9e9af
            Y: 2bdf9b562928e1f628fc2ffc530ec9b9e6cff664e42b9d41

[4] Username : james, PublicKey : EC Public Key
            X: 87077b963b76233307018fef7eba8f1fe0ee8d9189a011c
            Y: 96308a9d75d78b221643a86fee536ef188dc76b274af7a3a




[UTXO information]
[0] UTXO{id='9b06b5f0f46e61acb43ddbcae4a3e87b738baae431c8117fe5fbb335e6e8f082', receiver=EC Public Key
            X: 65f5f1d59d0e0493878a268362f08039b584edd75e18c388
            Y: d1f6a654a6417954bd63f28b18d64452a1ac630224b25e36
, amount=500.0, transactionId='cc15ad4e87cdc9d4bb8baa289724b1e4f0e44aa4a52fec78f71f03a016d576ba'}
[1] UTXO{id='cda1e79e58c0761d98cdc18889da88cd0dd9c7b708fd59941757314643317451', receiver=EC Public Key
            X: 7cdb55d8f472a355b68418bfa7099f5883e83548a3f5e202
            Y: cb19bf0968a2245bbe877194a345885e6e7bc1f79e027e39
, amount=200.0, transactionId='f126d50c5c3d12d6f353cd8e8480e712728f8a1f72eb3409fbdccd119a57e2e8'}
[2] UTXO{id='dc849b3bf0a45634a04aec6fd0a3a7799eff174e17011276ceb56dbd9a8a7deb', receiver=EC Public Key
            X: 23f0fdaacd30a8464168de2859ba7e32df5cae060da43694
            Y: c1ea8f6a0eb8d05a884007c7887c898f4149e1cec9fbea4f
, amount=50.0, transactionId='91e957ae9f210f99cd538648bbe5ca29e5c1598667c06510158f7bb607ed2303'}
[3] UTXO{id='ee3404f6ee1af6587f58053d1a5767f82db92540204aad8d2eddca82288423ed', receiver=EC Public Key
            X: 87077b963b76233307018fef7eba8f1fe0ee8d9189a011c
            Y: 96308a9d75d78b221643a86fee536ef188dc76b274af7a3a
, amount=100.0, transactionId='35b1b0b576057df960fe521b9fa6de85a92cb34c2fb1a9d564a167bfe3798aac'}
[4] UTXO{id='aa9b9e6d78a2ea9ac78d9be3e0ef8dc050a8683d850910a70634ff1a1fc9a9eb', receiver=EC Public Key
            X: 41824c6bf1158ba63eee87b8d3f3d69fa9192e750fb9e9af
            Y: 2bdf9b562928e1f628fc2ffc530ec9b9e6cff664e42b9d41
, amount=150.0, transactionId='91e957ae9f210f99cd538648bbe5ca29e5c1598667c06510158f7bb607ed2303'}



[Blockchain information]
Block[0] {
	- hash=8e89903f3bc90cff302dcb7d29cbb7b8570d3a5071123191bd5071a47f541e51
	- previousHash=0
	- nonce=0
	- merkleRoot=
	- timeStamp=1611732600534
	- data=e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
	- transactions=

}
Block[1] {
	- hash=0004a2f0b3025fa473b8c0ccf395e78aff4ae2a1d3383e29fd8b65c0f8725569
	- previousHash=8e89903f3bc90cff302dcb7d29cbb7b8570d3a5071123191bd5071a47f541e51
	- nonce=66
	- merkleRoot=cc15ad4e87cdc9d4bb8baa289724b1e4f0e44aa4a52fec78f71f03a016d576ba
	- timeStamp=1611732600882
	- data=8299adebdc9bde1c4bbb3b53bd0d5cd820f05e7711b09c2fa936555fa76ca8cf
	- transactions=
		- [0]
			- transactionId=cc15ad4e87cdc9d4bb8baa289724b1e4f0e44aa4a52fec78f71f03a016d576ba
			- sender=genesis
			- receiver=peter
			- amount=500.0

}
Block[2] {
	- hash=000207053a7b91751de1724930ea51cffd1b53e3bc840bbaf677ce270ab28a2b
	- previousHash=0004a2f0b3025fa473b8c0ccf395e78aff4ae2a1d3383e29fd8b65c0f8725569
	- nonce=8758
	- merkleRoot=f126d50c5c3d12d6f353cd8e8480e712728f8a1f72eb3409fbdccd119a57e2e8
	- timeStamp=1611732600950
	- data=7e2dd37bf2cdb842b31a0a58cb8b44a3aa4f1438048ce219db0941a3b95e17d7
	- transactions=
		- [0]
			- transactionId=f126d50c5c3d12d6f353cd8e8480e712728f8a1f72eb3409fbdccd119a57e2e8
			- sender=peter
			- receiver=alice
			- amount=300.0

}
Block[3] {
	- hash=000fe331fb1a0af32fa6b64574cfe72e50d8db46070178be9dd1d940f1c8197b
	- previousHash=000207053a7b91751de1724930ea51cffd1b53e3bc840bbaf677ce270ab28a2b
	- nonce=6409
	- merkleRoot=35b1b0b576057df960fe521b9fa6de85a92cb34c2fb1a9d564a167bfe3798aac
	- timeStamp=1611732601123
	- data=f57884ad874defd981e426daf96f57c41820c573f576d4813fd630a206dececa
	- transactions=
		- [0]
			- transactionId=35b1b0b576057df960fe521b9fa6de85a92cb34c2fb1a9d564a167bfe3798aac
			- sender=alice
			- receiver=james
			- amount=100.0

}
Block[4] {
	- hash=000fa5a58c943b496c187017b1d09d4556f25b70fc662034e04befd636e2cc45
	- previousHash=000fe331fb1a0af32fa6b64574cfe72e50d8db46070178be9dd1d940f1c8197b
	- nonce=317
	- merkleRoot=91e957ae9f210f99cd538648bbe5ca29e5c1598667c06510158f7bb607ed2303
	- timeStamp=1611732601210
	- data=70c5f1f391617ddf62db8a8bdaaa2c8aa3f6981ae318583d21c2ed22bf7051eb
	- transactions=
		- [0]
			- transactionId=91e957ae9f210f99cd538648bbe5ca29e5c1598667c06510158f7bb607ed2303
			- sender=alice
			- receiver=bob
			- amount=150.0

}


[Block creation information]
- Minimum Time : 3ms
- Maximum Time : 155ms
- Average Time : 61ms

Process finished with exit code 0

```







### 

