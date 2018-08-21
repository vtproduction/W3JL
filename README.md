# W3JL 

**W3JL** is a lightweight implementation of [Web3J](https://github.com/web3j/web3j) and [GoEther](https://github.com/ethereum/go-ethereum). W3JL is stand for "Web3J Lightweight"


## Why W3JL

Since Web3J and GoEther is very completable yet complex and not easy to use. W3JL is a very tiny implementation of W3J and GoEther. W3JL provides simple interface to create, manage wallet, interact and make transaction with RPC server, as well as handle ERC20 token.

W3JL contains 4 main interfaces:

* `WalletRepository` provides functions of create and backup ERC20 wallet.
* `EthRepository` provides functions to interact with Ethereum network.
* `TokenRepository` is a set of functions to interact with ERC20 Token, and call smart contract functions.
* `GethRepository` implements GoEther's basic functions, containing account lock/unlock, and sign transaction.

W3JL is also contain ` W3JLFactory ` class, play as a builder, to create an instance of interfaces above


## WalletRepository


Since the type of wallet uses in TomoWallet is [BIP39 wallet](https://iancoleman.io/bip39/), which can be generated from **unique mnemonic string** (Information of mnemonic can be found [here](https://en.bitcoin.it/wiki/Seed_phrase)), then the flow of generating wallet address is descibed below

![WalletRepository](https://i.imgur.com/pgvjyBC.png)

The Wallet Repository contains functions to implement this flow:


```
interface WalletRepository {
    fun createMnemonics(entropy: ByteArray): String
    fun createMnemonics(): String
    fun createWalletFromMnemonic(mnemonics: String, password: String?) : Single<W3JLWallet>
    fun createWalletFromPrivateKey(privateKey: String, password: String?) : Single<W3JLWallet>
    fun createWalletFromJsonString(jsonString: String, password: String) : Single<W3JLWallet>
    fun backupWallet(wallet: W3JLWallet, password: String) : Single<String>
}
```

The operation of creating wallet itself not require password. However, when user backup the information of wallet (address, public key, private key), the password is required, that why we need the 2nd argument ` password: String? `. If the password is presented, the function itself will generate wallet with encrypted json string, and vice versa.

The implementation of `createWalletFromMnemonic` function:

```
private fun createHDWalletFromMnemonic(mnemonics: String): Single<W3JLWallet> {
        return Single.create { emitter  ->
            try {
                val pathArray = ETH_TYPE.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val passphrase = ""
                val list = mnemonics.split(" ")
                val creationTimeSeconds = System.currentTimeMillis() / 1000
                val ds = DeterministicSeed(list, null, passphrase, creationTimeSeconds)
                val seedBytes = ds.seedBytes
                var dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes)
                for (i in 1 until pathArray.size) {
                    val childNumber: ChildNumber
                    childNumber = if (pathArray[i].endsWith("'")) {
                        val number = Integer.parseInt(pathArray[i].substring(0,
                                pathArray[i].length - 1))
                        ChildNumber(number, true)
                    } else {
                        val number = Integer.parseInt(pathArray[i])
                        ChildNumber(number, false)
                    }
                    dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber)
                }
                val keyPair = ECKeyPair.create(dkKey.privKeyBytes)
                val c = Credentials.create(keyPair.privateKey.toString(RADIX))
                val wallet = W3JLWallet()
                wallet.mnemonic = mnemonics
                wallet.source = W3JLWallet.Source.MNEMONIC
                wallet.address = c.address
                wallet.privateKey = keyPair.privateKey.toString(RADIX)
                wallet.publicKey = keyPair.publicKey.toString(RADIX)
                wallet.jsonSource = ""
                wallet.createAt = Calendar.getInstance().timeInMillis
                emitter.onSuccess(wallet)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }
    
```

which use `m/44'/60'/0'/0/0` as derivation path (see [here](https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki)), which is compatible with common wallet, such as MyEtherWallet, TrustWallet, Eidoo,... 

Because the flow from mnemonic to keypair then public address is trivial, but the opposite way is impossible, then in order to save (backup) wallet information, we provide the ` password ` field, as well as the ` backupWallet ` function, which generate the password-encrypted json String contains all information of wallet **exclude mnemonic**. The json String of wallet can be safely shared among applications and environment and can only be decrypted back into original wallet with password. To retrive the wallet from jsonString, simply call the ` createWalletFromJsonString ` method. 


## EthRepository

Since the WalletRepository only create and manage single wallet, EthRepository (which 'Eth' stand for 'Ethereum') prodives methods to interact with Ethereum RPC. Here is what EthRepository do:

```
interface EthRepository {

    fun getAccountBalance(address: String) : Single<BigInteger>
    fun getAccountBalance(address: String, number : Int) : Single<String>
    fun getAccountTransactionCount(address: String) : Single<BigInteger>
    @Throws(InterruptedException::class, ExecutionException::class, IOException::class, CipherException::class)
    fun transfer(password: String, walletFile: File, to: String, amount: BigInteger): Single<String>
    @Throws(InterruptedException::class, ExecutionException::class, IOException::class, CipherException::class)
    fun transfer(password: String, walletFile: File, to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger): Single<String>
    @Throws(InterruptedException::class, ExecutionException::class)
    fun transfer(privateKey: String, to: String, amount: BigInteger): Single<String>
    @Throws(InterruptedException::class, ExecutionException::class)
    fun transfer(privateKey: String, to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger): Single<String>
    
}

```

These methods itself describe their purpose. The ` getAccountBalance ` method take the address as first argument, and return the entire balance **at Wei unit**. If the 2nd argument ` number ` is presented, the result will be round up to ` number ` characters, and readable. For example, with the same address, if ` getAccountBalance(address) ` returns ` 86974361716683340358 ` Wei, then ` getAccountBalance(address, 5) ` will return ` 86,974 ` **Ether**. 

Other methods are used to make Eth transaction. It will take private key, or pair of jsonString (store as file) and password to decrypt, the address to transfer Ether, the number of Ether to be sent, and gasPrice as well as gasLimit, optional. If gasPrice and gasLimit are not present, then default value will be use:

```
//default gasPrice
private val GAS_PRICE = BigInteger.valueOf(20_000_000_000L)

//default gasLimit
private val GAS_LIMIT = BigInteger.valueOf(4300000)
```

The algorithm inside transfer method is 

* Unlock the wallet and get private key (if need). 

* Obtain wallet ` nonce ` value. (see more about nonce [here](https://ethereum.stackexchange.com/questions/27432/what-is-nonce-in-ethereum-how-does-it-prevent-double-spending))
* Create raw transaction format, contain nonce, gasPrice, gasLimit, address to send and amount to send
* Sign the transaction with the private key 
* Send signed transaction into RPC server and return the transaction Id

```

override fun transfer(privateKey: String,  to: String, amount: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger): Single<String> {
        return Single.create{ emitter ->
            try {
                val credentials = Credentials.create(privateKey)
                if (credentials == null){
                    emitter.onError(Exception("Invalid credential!"))
                    return@create
                }
                val from = credentials.address
                val ethGetTransactionCount = web3j.ethGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).sendAsync().get()

                val nonce = ethGetTransactionCount.transactionCount
                val rawTransaction = RawTransaction.createEtherTransaction(
                        nonce, gasPrice, gasLimit, to, amount)
                val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
                val hexValue = Numeric.toHexString(signedMessage)
                val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get()
                emitter.onSuccess(ethSendTransaction.transactionHash)
            }catch (e : Exception){
                emitter.onError(e)
            }
        }
    }
    
```


## TokenRepository


This service will handler operations agains ERC20 Token. It is much more like EthRepository

```
interface TokenRepository {

    fun setTokenInfo(tokenInfo: W3JLTokenInfo)
    fun getBalance(address : String) : Single<BigDecimal>
    fun getBalance(address : String, tokenInfo: W3JLTokenInfo) : Single<BigDecimal>
    fun transferToken(
        from : String,
        to: String,
        privateKey : String,
        amount: BigInteger,
        gasPrice: BigInteger,
        gasLimit: BigInteger) : Single<String>
}


```

The diffrent part is the ` W3JLTokenInfo `. This object holds information of specific ERC20 token

```
class W3JLTokenInfo() : Parcelable {
    var address: String = "" //the address of token smart contract
    var name: String = "" //Name of token, Eg: TomoCoin
    var symbol: String = "" //Token symbol, Eg: TOMO
    var decimals: Int = 18 //Token decimals, mostly, equal 18
}
```

And these token functions, actually, call the smart contract coordinate functions. For example, the 
` getBalance ` function call the contract ` balanceOf ` function. The algorithm behind is pretty understandable:

```
private fun balanceOf(owner: String): Function {
        return Function(
                "balanceOf",
                listOf(Address(owner)),
                listOf(object : TypeReference<Uint256>() {

                }))
}

```

Step-by-step explaination:

* We are calling the smart contract function with name ` balanceOf `, so it will be the first argument.
* The smart contract ` balanceOf ` function take address as argument, so we will pass the array with only one member, the ` owner `.
* The return type of this smart contract is type of [Uint256](https://ethereum.stackexchange.com/questions/29946/what-is-uint256), is describe as third argument.

To call the smart contract function, we simply call the send ether transaction, like above, but has a bit different:

* We make a transaction to smart contract address, instead of address.
* We will send **Zero Ether**.
* We will attach the byte data which contain the Function that we just create earlier.

Then, the transaction will be execute as it will be, returning the id of transaction.

The full code of ` transferToken ` function:

```
override fun transferToken(from: String, to: String, privateKey: String, amount: BigInteger,
                                     gasPrice: BigInteger,
                                     gasLimit: BigInteger): Single<String> {
        return Single.create { emitter ->
            try {
                val data = createTokenTransferData(to, amount)
                val nonce = web3j
                        .ethGetTransactionCount(from, DefaultBlockParameterName.LATEST)
                        .sendAsync().get().transactionCount
                val signedMessage = GethService(context)
                        .signTransaction(from, privateKey, mTokenInfo!!.address, BigInteger.valueOf(0), gasPrice, gasLimit, nonce.toLong(), data, 3)
                emitter.onSuccess(web3j
                        .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                        .sendAsync().get().transactionHash)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

private fun createTokenTransferData(to: String, tokenAmount: BigInteger): ByteArray {
        val params = Arrays.asList(Address(to), Uint256(tokenAmount))
        val returnTypes = Arrays.asList<TypeReference<*>>(object : TypeReference<Bool>() {

        })
        val function = Function("transfer", params, returnTypes)
        val encodedFunction = FunctionEncoder.encode(function)
        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction))
    }


```

## W3JLFactory

So, instead of create these repositories above directly by calling their implementation, we provide a class with build methods, to create their instance. According with 3 over 4 repositories, there are 3 build methods: ` buildW3JLWallet() ` , ` buildW3JLEth() `, ` buildW3JLToken() `. Additionally, these methods come along with some configs/modifications:

* the ` withContext(context: Context) ` config, pass the ` context ` to create default File association used in ` W3JLWallet `
* the ` withKeyStoreFile(keyStoreFile: File) ` config, used when you want to determine specific file location to save the wallet. If the ` withContext() ` function is called before, the keystore is not required anymore, instead, default location will be used
* the ` withNetworkProvider ` config, determines the Network Provider when create instance of web3. Normally, we use [Infura](https://infura.io/) as network provider. If the provider is not set, the default localhost will be used

```

val web3j = Web3jFactory.build(if 
	(networkProvider.isEmpty()) HttpService() else HttpService(networkProvider) )

```

These builder functions will also check if the requirements for specific service are presented or not and throw exception if need. So, the correct way to create instance of services is

```
val w3JLEth = W3JLFactory()
                .withContext(this)
                .withKeyStoreFile(File(applicationInfo.dataDir, "keystore")) //optional
                .withNetworkProvider("https://{mainnet|rinkeby|ropsten|kovan}.infura.io/v3/<API KEY>")
                .buildW3JLEth()
	
```


```
val ethService = W3JLFactory()
                .withNetworkProvider("https://{mainnet|rinkeby|ropsten|kovan}.infura.io/v3/<API KEY>")
                .buildW3JLEth()

```


```
val tokenInfo = W3JLTokenInfo(
                "0x8b353021189375591723E7384262F45709A3C3dC",
                "TomoCoin",
                "TOMO",
                18
        )
val tokenService = W3JLFactory()
                .withContext(this)
                .withNetworkProvider("https://{mainnet|rinkeby|ropsten|kovan}.infura.io/v3/<API KEY>")
                .withTokenInfo(tokenInfo)
                .buildW3JLToken()

```
