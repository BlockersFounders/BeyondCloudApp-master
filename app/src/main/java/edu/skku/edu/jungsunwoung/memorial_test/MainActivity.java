package edu.skku.edu.jungsunwoung.memorial_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.blockchain.CoinType;
import com.samsung.android.sdk.blockchain.ListenableFutureTask;
import com.samsung.android.sdk.blockchain.SBlockchain;
import com.samsung.android.sdk.blockchain.account.Account;
import com.samsung.android.sdk.blockchain.account.ethereum.EthereumAccount;
import com.samsung.android.sdk.blockchain.coinservice.CoinNetworkInfo;
import com.samsung.android.sdk.blockchain.coinservice.CoinServiceFactory;
import com.samsung.android.sdk.blockchain.coinservice.TransactionResult;
import com.samsung.android.sdk.blockchain.coinservice.ethereum.EthereumService;
import com.samsung.android.sdk.blockchain.coinservice.ethereum.EthereumUtils;
import com.samsung.android.sdk.blockchain.exception.AvailabilityException;
import com.samsung.android.sdk.blockchain.exception.SsdkUnsupportedException;
import com.samsung.android.sdk.blockchain.network.EthereumNetworkType;
import com.samsung.android.sdk.blockchain.ui.CucumberWebView;
import com.samsung.android.sdk.blockchain.ui.OnSendTransactionListener;
import com.samsung.android.sdk.blockchain.wallet.HardwareWallet;
import com.samsung.android.sdk.blockchain.wallet.HardwareWalletManager;
import com.samsung.android.sdk.blockchain.wallet.HardwareWalletType;

import org.web3j.protocol.core.methods.response.EthCall;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
//implement=cucmber할때 추가함


    Button connectBtn;
    Button generateAccountBtn;
    Button getAccountsBtn;
    Button paymentSheetBtn;
    Button sendSmartContractBtn;
    Button webViewInitBtn;
    Button saveBtn;
    Button printBtn;

    TextView show;
    EditText input;
    EditText input2;
    EditText input3;
    String real;
    private SBlockchain sBlockchain;
    private HardwareWallet wallet;
    private Account generatedAccount;
    private Object CoinNetworkInfo;
    private CucumberWebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sBlockchain = new SBlockchain();

        try {
            sBlockchain.initialize(this);
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
        }

        connectBtn=findViewById(R.id.connect);
        generateAccountBtn=findViewById(R.id.generateAccount);
        getAccountsBtn=findViewById(R.id.getAccounts);
        show=findViewById(R.id.show_result);


        saveBtn=findViewById(R.id.saveBtn);
        printBtn=findViewById(R.id.printBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print();
            }
        });

/*
        webViewInitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webViewInit();
            }
        });
*/

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();

            }
        });
        generateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate();

            }
        });
        getAccountsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccounts();
            }
        });
        /*
        paymentSheetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentSheet();
            }
        });
*/

    }
    //블록에 저장하는 함수

    private void save(){
        input=findViewById(R.id.name_field);
        input2=findViewById(R.id.birth_field);
        input3=findViewById(R.id.text_field);


        Function functionGetPostCount = FunctionUtils.createBLock(input.getText().toString(),input2.getText().toString(),input3.getText().toString());
        String data = FunctionEncoder.encode(functionGetPostCount);
        //edittext에 적은 값을 가지고 와서 functionutils.java에 있는 함수를 활용해 function으로 변환하고 encode한다.->encode된 값=data

        Log.d("onhy",data);
        CoinNetworkInfo coinNetworkInfo = new CoinNetworkInfo(
                CoinType.ETH,
                EthereumNetworkType.ROPSTEN,
                "https://ropsten.infura.io/v3/70ddb1f89ca9421885b6268e847a459d"
        );
        List<Account> accounts=sBlockchain.getAccountManager().getAccounts(wallet.getWalletId(),CoinType.ETH,EthereumNetworkType.ROPSTEN);
        EthereumService ethereumService= (EthereumService) CoinServiceFactory.getCoinService(this,coinNetworkInfo);


        HardwareWallet connectedHardwareWallet =
                sBlockchain.getHardwareWalletManager().getConnectedHardwareWallet();

        try {
            ethereumService
                    .sendSmartContractTransaction(
                            connectedHardwareWallet,
                            //지갑과 연결
                            (EthereumAccount) accounts.get(0),
                            //내 계좌
                            "0x07d55a62b487d61a0b47c2937016f68e4bcec0e9",
                            //트랜잭션 주소
                            EthereumUtils.convertGweiToWei(new BigDecimal("10")),
                            new BigInteger("500000"),
                            data,
                            //encodedfunction -> 블록에 저장하려는 파라미터 값 ->이것은 hex형식의 string타입으로 저장되어있다(주의/string타입임)
                            //ex) "0x323h2300000002341200000000~~~~"
                            null,
                            null
                    )
                    .setCallback(
                            new ListenableFutureTask.Callback<TransactionResult>() {
                                @Override
                                public void onSuccess(TransactionResult result) {
                                    Log.d("hell",data);
                                    //확인하기위한 log

                                    //success
                                }
                                @Override
                                public void onFailure(ExecutionException exception) {
                                    //failure
                                }
                                @Override
                                public void onCancelled(InterruptedException exception) {
                                    //cancelled
                                }
                            });
        } catch (AvailabilityException e) {
            //handle exception
        }
    }

    //블록에 기록된 값을 화면에 띄워주는 함수
    //이 함수의 목적은 블록에 가장 마지막에 저장되어 있는 값을 불러온다. 나중에 수정을 한다면 블록에 있는 모든 값을 띄워주는 것으로 바꿀수도 있다.->추후 업데이트?
    private void print(){

        CoinNetworkInfo coinNetworkInfo = new CoinNetworkInfo(
                CoinType.ETH,
                EthereumNetworkType.ROPSTEN,
                "https://ropsten.infura.io/v3/70ddb1f89ca9421885b6268e847a459d"
        );
        List<Account> accounts=sBlockchain.getAccountManager().getAccounts(wallet.getWalletId(),CoinType.ETH,EthereumNetworkType.ROPSTEN);
        EthereumService ethereumService= (EthereumService) CoinServiceFactory.getCoinService(this,coinNetworkInfo);

//count 부분
        ethereumService
                .callSmartContractFunction(
                        (EthereumAccount) accounts.get(0),
                        "0x07d55a62b487d61a0b47c2937016f68e4bcec0e9",
                        "0x7355a424"
                        //이 값은 블록에 저장된 데이터의 개수를 불러오는 함수의 encoding된 값이다.
                )
                .setCallback(new ListenableFutureTask.Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        //result는 hex형식의 string 값이다

                        Log.d("tet",result);
                        Function functionGetPost = FunctionUtils.countBlock();
                        List<TypeReference<Type>> outputParameters = functionGetPost.getOutputParameters();
                        List<Type> types = FunctionReturnDecoder.decode(result, outputParameters);
                        Type type = types.get(0);
                        BigInteger post = (BigInteger) type.getValue();
                        int length=post.intValue()-1;
                        //-1하는 이유는 데이터 array는 0부터 시작하기 때문이다. 마지막 값을 불러오려면 data[length-1]하는 원리랑 같다
                        String to = Integer.toString(length);
                        Log.d("this",to);

                        //여기서 callsmartcontractfunction을 한번 더 불러준다. 위에서 길이를 받아와서 그 길이를 input (int) 값으로 넣어준다.
                        ethereumService

                                .callSmartContractFunction(
                                        (EthereumAccount) accounts.get(0),
                                        "0x07d55a62b487d61a0b47c2937016f68e4bcec0e9",
                                        "0x9507d39a000000000000000000000000000000000000000000000000000000000000000"+to
                                        //to값은 위에 길이를 string으로 변환한 것이다. 우선은 블록에 10개 이상이 저장되어있지는 않기 때문에 임의로 한자리라고 가정하고 했다. 자리수가 늘어나면 0을 하나 없애면 된다
                                )
                                .setCallback(new ListenableFutureTask.Callback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        Log.d("hello",result);
                                        Function functionGetPost = FunctionUtils.callBlock(length);
                                        List<TypeReference<Type>> outputParameters = functionGetPost.getOutputParameters();
                                        List<Type> types = FunctionReturnDecoder.decode(result, outputParameters);
                                        Log.d("hi",(String)types.get(0).getValue());
                                        Log.d("hi",(String)types.get(1).getValue());
                                        Log.d("hi",(String)types.get(2).getValue());
                                        //확인차 log값
                                        show.setText((String)types.get(0).getValue()+(String)types.get(1).getValue()+(String)types.get(2).getValue());
                                        //textview의 값을 바꾸는 부분.여기서 get(0)은 이름 get(1)은 생몰 get(2)는 유언으로 파싱해서 가져올 수 있다. 일단은 통째로 붙여서 넣었다.

                                        //success
                                    }
                                    @Override
                                    public void onFailure(ExecutionException exception) {
                                        //failure
                                    }
                                    @Override
                                    public void onCancelled(InterruptedException exception) {
                                        //cancelled
                                    }
                                });

                        //success
                    }
                    @Override
                    public void onFailure(ExecutionException exception) {
                        //failure
                    }
                    @Override
                    public void onCancelled(InterruptedException exception) {
                        //cancelled
                    }
                });
        //호출부분


    }

    private void getAccounts(){
        List<Account> accounts=sBlockchain.getAccountManager().getAccounts(wallet.getWalletId(),CoinType.ETH,EthereumNetworkType.ROPSTEN);
        Log.d("MyApp2", Arrays.toString(new List[]{accounts}));
        //리스트 형태여야되서. 다시 리스트로,..? 젠장

    }//로컬캐시에 있는걸 가져오는거기때문에 async를 할 필요가 없음.  그래서 local variable를 만듬
    private void generate(){
        CoinNetworkInfo coinNetworkInfo = new CoinNetworkInfo(
                CoinType.ETH,
                EthereumNetworkType.ROPSTEN,
                "https://ropsten.infura.io/v3/70ddb1f89ca9421885b6268e847a459d"
        );

        sBlockchain.getAccountManager()
                .generateNewAccount(wallet,coinNetworkInfo)
                .setCallback(new ListenableFutureTask.Callback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        generatedAccount=account;
                        Log.d("hi","hibbbbbbb");
                        Log.d("My APP","hi"+account.toString());


                    }

                    @Override
                    public void onFailure(@NotNull ExecutionException e) {

                    }

                    @Override
                    public void onCancelled(@NotNull InterruptedException e) {

                    }
                });
    }
    private void connect(){
        sBlockchain.getHardwareWalletManager()
                .connect(HardwareWalletType.SAMSUNG,true)
                .setCallback(new ListenableFutureTask.Callback<HardwareWallet>() {
                    @Override
                    public void onSuccess(HardwareWallet hardwareWallet) {
                        wallet = hardwareWallet;
                        Log.d("hi","hi");

                    }

                    @Override
                    public void onFailure(ExecutionException e) {

                    }

                    @Override
                    public void onCancelled(InterruptedException e) {

                    }
                });
    }
    /*
    private void webViewInit(){
        CoinNetworkInfo coinNetworkInfo = new CoinNetworkInfo(
                CoinType.ETH,
                EthereumNetworkType.ROPSTEN,
                "https://ropsten.infura.io/v3/70ddb1f89ca9421885b6268e847a459d"
        );
        List<Account> accounts=sBlockchain.getAccountManager().getAccounts(wallet.getWalletId(),CoinType.ETH,EthereumNetworkType.ROPSTEN);
        EthereumService ethereumService= (EthereumService) CoinServiceFactory.getCoinService(this,coinNetworkInfo);

        webView.init(ethereumService,accounts.get(0),this);
        webView.loadUrl("https://faucet.metamask.io/");
    }
    private void paymentSheet(){


        sBlockchain.getAccountManager()
                .getAccounts(
                        wallet.getWalletId(),
                        CoinType.ETH,
                        EthereumNetworkType.ROPSTEN
                );
        CoinNetworkInfo coinNetworkInfo = new CoinNetworkInfo(
                CoinType.ETH,
                EthereumNetworkType.ROPSTEN,
                "https://ropsten.infura.io/v3/70ddb1f89ca9421885b6268e847a459d"
        );
        List<Account> accounts=sBlockchain.getAccountManager().getAccounts(wallet.getWalletId(),CoinType.ETH,EthereumNetworkType.ROPSTEN);
        EthereumService ethereumService= (EthereumService) CoinServiceFactory.getCoinService(this,coinNetworkInfo);
        Intent intent =ethereumService
                .createEthereumPaymentSheetActivityIntent(
                        this,
                        wallet,
                        (EthereumAccount) accounts.get(0),
                        "0xdf1BF1B0aDfE5b1711B7e7fFF394c088cAd8c5a0",
                        new BigInteger("10000000000000000"),
                        null,
                        null
                );
        startActivityForResult(intent, 0);

        //string s1=특정 스마트 컨트랙을 컨트롤하려면 ...악
    }

    @Override
    public void onSendTransaction(
            @NotNull String requestId,
            @NotNull EthereumAccount fromAccount,
            @NotNull String toAddress,
            @org.jetbrains.annotations.Nullable BigInteger value,
            @org.jetbrains.annotations.Nullable String data,
            @org.jetbrains.annotations.Nullable BigInteger nonce
    ) {
        HardwareWallet connectedHardwareWallet =
                sBlockchain.getHardwareWalletManager().getConnectedHardwareWallet();
        Intent intent =
                webView.createEthereumPaymentSheetActivityIntent(
                        this,
                        requestId,
                        connectedHardwareWallet,
                        toAddress,
                        value,
                        data,
                        nonce
                );

        startActivityForResult(intent, 0);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 0) {
            return;
        }

        webView.onActivityResult(requestCode, resultCode, data);
    }
    private String emoji, name, comment, date;

*/
}
