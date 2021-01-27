# utxo
Java 기반으로 UTXO 기반의 샘플 블록체인 프로그램을 개발한다.



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

    }
```

<br/>



### 실행 결과

```shell
============== Summary ==============
[User list information]
[0] Username : peter, PublicKey : EC Public Key
            X: 1bb15da4de1e513e1483a7c3ef10e65c5938cea8dd139db8
            Y: f3a936e5ffb1611922be838f7b9ef7093b83e15a5ac46210

[1] Username : alice, PublicKey : EC Public Key
            X: d996e37ecd109f8b3cef16a576fad796d992561ef9f0e283
            Y: f80eee5fe8471ef000398c425357bafb536913ed4918f159

[2] Username : genesis, PublicKey : EC Public Key
            X: 52cc41183eafd378ca46c3258d01bc01b0d71222c8da9c9f
            Y: 65ae0c2f6d406f3473a3040b880192a3fceb2f58864f1603

[3] Username : james, PublicKey : EC Public Key
            X: dc61933616a959f393786be0767520ca512d2529dec31456
            Y: de2399ab3ca5c776bb701bbffb6b84fe4eb5835c88355b41

[4] Username : bob, PublicKey : EC Public Key
            X: ceba6539c86139550c0060d4393b06d46ff0d67171235328
            Y: 4512019189de64f6d728b2c24585cb27efdfab9753d9b5dd




[UTXO information]
[0] UTXO{id='8ce654ade680b6861d0c6d594e9abd8af5398068c17c80abb44c487a53ec91c2', receiver=EC Public Key
            X: d996e37ecd109f8b3cef16a576fad796d992561ef9f0e283
            Y: f80eee5fe8471ef000398c425357bafb536913ed4918f159
, amount=50.0, transactionId='e2964dcaf7a1a082ef94e068e3ac5f722d522c81b2b1e3839063b902a76256d5'}
[1] UTXO{id='0a84d6f1c94851d5b9d5e39ca2d0955b1f528c5df06c812488d6e2da7db2ed25', receiver=EC Public Key
            X: ceba6539c86139550c0060d4393b06d46ff0d67171235328
            Y: 4512019189de64f6d728b2c24585cb27efdfab9753d9b5dd
, amount=150.0, transactionId='e2964dcaf7a1a082ef94e068e3ac5f722d522c81b2b1e3839063b902a76256d5'}
[2] UTXO{id='0880526370536a7d0f21949d3a94cbc78f7dac15da600e8a6ff21058524f9171', receiver=EC Public Key
            X: 52cc41183eafd378ca46c3258d01bc01b0d71222c8da9c9f
            Y: 65ae0c2f6d406f3473a3040b880192a3fceb2f58864f1603
, amount=500.0, transactionId='798cb4449813730fdeb0ea2f817d2b253348849ac5472a235cd1afebed9e12c5'}
[3] UTXO{id='c87d87f335733255fb7fe6e0ad99bb43a8bfd64f82d584d0e303ccb09abd45cd', receiver=EC Public Key
            X: 1bb15da4de1e513e1483a7c3ef10e65c5938cea8dd139db8
            Y: f3a936e5ffb1611922be838f7b9ef7093b83e15a5ac46210
, amount=200.0, transactionId='a4321270ebad3053f7d91c4afeff9383ddc1103934b588742d538d5028f3c498'}
[4] UTXO{id='dbd7ccebe2eeed38370cc8103b7ab80c92a569e7294c83288b9ad3425e2579ed', receiver=EC Public Key
            X: dc61933616a959f393786be0767520ca512d2529dec31456
            Y: de2399ab3ca5c776bb701bbffb6b84fe4eb5835c88355b41
, amount=100.0, transactionId='84b960d2bd381fff8f0ea9699bcad1331dcad9c637337e534a43cb2a8d7063bc'}



[Blockchain information]
Block[0] {
	- hash=8e89903f3bc90cff302dcb7d29cbb7b8570d3a5071123191bd5071a47f541e51
	- previousHash=0
	- nonce=0
	- merkleRoot=
	- timeStamp=1611729987675
	- data=e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
	- transactions=

}
Block[1] {
	- hash=000b4e5e3cf13bd6b45077bb212a980c38f810639a96aaa3f3b83a87e6dc814b
	- previousHash=8e89903f3bc90cff302dcb7d29cbb7b8570d3a5071123191bd5071a47f541e51
	- nonce=1837
	- merkleRoot=798cb4449813730fdeb0ea2f817d2b253348849ac5472a235cd1afebed9e12c5
	- timeStamp=1611729988100
	- data=f9fd84b440efb576d76139b28deec82573750b16ba50ebbc8caffa10cb56f529
	- transactions=
		- [0]
			- transactionId=798cb4449813730fdeb0ea2f817d2b253348849ac5472a235cd1afebed9e12c5
			- sender=genesis
			- receiver=peter
			- amount=500.0

}
Block[2] {
	- hash=000f4c5d7cbeb64b8cbae925a614c658ebb56dafc777496cc5cd5da798774840
	- previousHash=000b4e5e3cf13bd6b45077bb212a980c38f810639a96aaa3f3b83a87e6dc814b
	- nonce=3094
	- merkleRoot=a4321270ebad3053f7d91c4afeff9383ddc1103934b588742d538d5028f3c498
	- timeStamp=1611729988235
	- data=ffabc60ebf68246546e9a529ae1484b8cacebf10628cbe57214acbbb30965a8e
	- transactions=
		- [0]
			- transactionId=a4321270ebad3053f7d91c4afeff9383ddc1103934b588742d538d5028f3c498
			- sender=peter
			- receiver=alice
			- amount=300.0

}
Block[3] {
	- hash=0001e229a7fca60a0312a0db1eb6d2b97092e8e0ddc35f62a0f60e5f401d1c87
	- previousHash=000f4c5d7cbeb64b8cbae925a614c658ebb56dafc777496cc5cd5da798774840
	- nonce=8553
	- merkleRoot=84b960d2bd381fff8f0ea9699bcad1331dcad9c637337e534a43cb2a8d7063bc
	- timeStamp=1611729988369
	- data=6ce8ad6069b271208b59e88a89c71eae8fe7805f2e7b5767fbef3438f3da9f07
	- transactions=
		- [0]
			- transactionId=84b960d2bd381fff8f0ea9699bcad1331dcad9c637337e534a43cb2a8d7063bc
			- sender=alice
			- receiver=james
			- amount=100.0

}
Block[4] {
	- hash=00034d30d387ac79bf6c39fc07e57dd73c92099f04516311e075403d500ed70f
	- previousHash=0001e229a7fca60a0312a0db1eb6d2b97092e8e0ddc35f62a0f60e5f401d1c87
	- nonce=4432
	- merkleRoot=e2964dcaf7a1a082ef94e068e3ac5f722d522c81b2b1e3839063b902a76256d5
	- timeStamp=1611729988542
	- data=28a379ccc83b2c708586fa2f61a3c20a41a17a4c2344b74571f11ead094e2b34
	- transactions=
		- [0]
			- transactionId=e2964dcaf7a1a082ef94e068e3ac5f722d522c81b2b1e3839063b902a76256d5
			- sender=alice
			- receiver=bob
			- amount=150.0

}


Process finished with exit code 0

```







### 

