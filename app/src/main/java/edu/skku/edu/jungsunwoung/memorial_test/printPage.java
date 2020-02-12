package edu.skku.edu.jungsunwoung.memorial_test;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.blockchain.CoinType;
import com.samsung.android.sdk.blockchain.ListenableFutureTask;
import com.samsung.android.sdk.blockchain.SBlockchain;
import com.samsung.android.sdk.blockchain.account.Account;
import com.samsung.android.sdk.blockchain.account.ethereum.EthereumAccount;
import com.samsung.android.sdk.blockchain.coinservice.CoinNetworkInfo;
import com.samsung.android.sdk.blockchain.coinservice.CoinServiceFactory;
import com.samsung.android.sdk.blockchain.coinservice.ethereum.EthereumService;
import com.samsung.android.sdk.blockchain.exception.SsdkUnsupportedException;
import com.samsung.android.sdk.blockchain.network.EthereumNetworkType;
import com.samsung.android.sdk.blockchain.wallet.HardwareWallet;
import com.samsung.android.sdk.blockchain.wallet.HardwareWalletType;

import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class printPage extends AppCompatActivity {
    private SBlockchain sBlockchain;
    private HardwareWallet wallet;
    Button printBtn;
    TextView text;
    ListView mListView;
    ArrayList<String> data;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_page);

        Intent intent=getIntent();
        printBtn=findViewById(R.id.print);
        sBlockchain = new SBlockchain();
        text=findViewById(R.id.textView);
        try {
            sBlockchain.initialize(this);
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
        }
        connect();
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView = findViewById(R.id. listView );
                adapter =new ArrayAdapter<String>(printPage.this, android.R.layout. simple_list_item_1 , data);
                mListView.setAdapter(adapter);
            }
        });

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

                        data = new ArrayList<>();
                        Function functionGetPost = FunctionUtils.countBlock();
                        List<TypeReference<Type>> outputParameters = functionGetPost.getOutputParameters();
                        List<Type> types = FunctionReturnDecoder.decode(result, outputParameters);
                        Type type = types.get(0);
                        BigInteger post = (BigInteger) type.getValue();
                        int length = post.intValue() - 1;

                        for (int i = 0; i < length; i++) {
                            String to = Integer.toString(i);
                            ethereumService
                                    .callSmartContractFunction(
                                            (EthereumAccount) accounts.get(0),
                                            "0x07d55a62b487d61a0b47c2937016f68e4bcec0e9",
                                            "0x9507d39a000000000000000000000000000000000000000000000000000000000000000" + to
                                            //to값은 위에 길이를 string으로 변환한 것이다. 우선은 블록에 10개 이상이 저장되어있지는 않기 때문에 임의로 한자리라고 가정하고 했다. 자리수가 늘어나면 0을 하나 없애면 된다
                                    )
                                    .setCallback(new ListenableFutureTask.Callback<String>() {
                                        @Override
                                        public void onSuccess(String result) {
                                            Log.d("hello", result);
                                            Function functionGetPost = FunctionUtils.callBlock(length);
                                            List<TypeReference<Type>> outputParameters = functionGetPost.getOutputParameters();
                                            List<Type> types = FunctionReturnDecoder.decode(result, outputParameters);
                                            Log.d("hi", (String) types.get(0).getValue());
                                            Log.d("hi", (String) types.get(1).getValue());
                                            Log.d("hi", (String) types.get(2).getValue());
                                            //확인차 log값

                                            data.add((String) types.get(0).getValue() + "\n" + (String) types.get(1).getValue());

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
                        text.setText("Completed. Please Click the button");
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

    private void connect(){


        sBlockchain.getHardwareWalletManager()
                .connect(HardwareWalletType.SAMSUNG,true)
                .setCallback(new ListenableFutureTask.Callback<HardwareWallet>() {
                    @Override
                    public void onSuccess(HardwareWallet hardwareWallet) {
                        wallet = hardwareWallet;
                        print();
                    }

                    @Override
                    public void onFailure(ExecutionException e) {

                    }

                    @Override
                    public void onCancelled(InterruptedException e) {

                    }
                });
//count 부분

        //호출부분


    }

}
