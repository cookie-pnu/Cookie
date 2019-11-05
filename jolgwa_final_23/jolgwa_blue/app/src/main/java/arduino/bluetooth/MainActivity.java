package arduino.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.content.Intent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import arduino.bluetooth.R;
import android.support.annotation.WorkerThread;
import android.widget.Toolbar;

import arduino.bluetooth.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionException;
import com.naver.speech.clientapi.SpeechRecognitionListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.naver.speech.clientapi.SpeechRecognizer;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1234;
    ImageButton Start;
    TextView levtext;
    private BluetoothSPP bt;
    private DBHelper dbHelper;
    private static final String TAG=MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "kdlnbbewm0";
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private String mResult;
    private AudioWriterPCM writer;
    private arduino.bluetooth.DbOpenHelper mDbOpenHelper;
    private int level=1;
    private int levelHuddle=2;

    private int exp=0;

    //음성인식
    private void handleMessage(Message msg){
        switch (msg.what){
            case R.id.clientReady:
                Toast.makeText(MainActivity.this, "준비 되었습니다.", Toast.LENGTH_SHORT).show();
                writer=new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath()+"/NaverSpeechTest");
                writer.open("Test");
                break;
            case R.id.audioRecording:
                writer.write((short[])msg.obj);
                break;
            case R.id.partialResult:
                mResult=(String)(msg.obj);
                //Toast.makeText(MainActivity.this, mResult, Toast.LENGTH_SHORT).show();

                //Log.d(TAG, mResult);
                break;
            case R.id.finalResult:
                SpeechRecognitionResult speechRecognitionResult=(SpeechRecognitionResult)msg.obj;
                List<String> results=speechRecognitionResult.getResults();
                StringBuilder strBuf=new StringBuilder();
                for(String result:results){
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult=strBuf.toString();
                //Toast.makeText(MainActivity.this, mResult, Toast.LENGTH_SHORT).show();

                if(mResult.contains("쿠키")){
                    if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                        bt.send("k", true);
                        Toast.makeText(MainActivity.this,"쿠키", Toast.LENGTH_SHORT).show();

                        ++exp;
                    }
                    else{
                        Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "you call cookie");
                    //naverRecognizer.getSpeechRecognizer().stop();
                }
                if(mResult.contains("이리와")||mResult.contains("일로와")||mResult.contains("일루와")||mResult.contains("앞으로가")||mResult.contains("앞으로")){

                    if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                        bt.send("f", true);
                        Toast.makeText(MainActivity.this,"이리와", Toast.LENGTH_SHORT).show();

                        ++exp;
                    }
                    else{
                        Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "you call come");
                    //naverRecognizer.getSpeechRecognizer().stop();
                }
                if(mResult.contains("뒤로가")||mResult.contains("뒤로 가")||mResult.contains("뒤로")||mResult.contains("키로가")||mResult.contains("키로 가")){
                    if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                        bt.send("b", true);
                        Toast.makeText(MainActivity.this,"뒤로가", Toast.LENGTH_SHORT).show();

                        ++exp;
                    }
                    else{
                        Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "you call go back");
                    //naverRecognizer.getSpeechRecognizer().stop();
                }
                if(mResult.contains("가만히")||mResult.contains("멈춰")){
                    if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                        bt.send("s", true);
                        Toast.makeText(MainActivity.this,"멈춰", Toast.LENGTH_SHORT).show();

                        ++exp;
                    }
                    else{
                        Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "you call stop");
                    //naverRecognizer.getSpeechRecognizer().stop();
                }
                if(mResult.contains("산책")||mResult.contains("산책가자")){
                    if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                        bt.send("7", true);
                        Toast.makeText(MainActivity.this,"산책", Toast.LENGTH_SHORT).show();
                        ++exp;
                    }
                    else{
                        Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "you call lets go");
                    //naverRecognizer.getSpeechRecognizer().stop();
                }
                if(mResult.contains("돌아")||mResult.contains("도라")||mResult.contains("토라")||mResult.contains("톨아")){
                    if(level>3){
                        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                            bt.send("4", true);
                            Toast.makeText(MainActivity.this,"돌아", Toast.LENGTH_SHORT).show();
                            ++exp;
                        }
                        else{
                            Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this,"레벨이 부족해요", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "you call turn");
                    //naverRecognizer.getSpeechRecognizer().stop();
                }
                if(mResult.contains("손")){
                    if(level>1){
                        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                            bt.send("6", true);
                            Toast.makeText(MainActivity.this,"손", Toast.LENGTH_SHORT).show();

                            ++exp;
                        }
                        else{
                            Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this,"레벨이 부족해요", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "you call hand");
                }
                if(mResult.contains("쫑긋")||mResult.contains("쫑끗")){
                    if(level>2){
                        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                            bt.send("9", true);
                            Toast.makeText(MainActivity.this,"쫑긋", Toast.LENGTH_SHORT).show();

                            ++exp;
                        }
                        else{
                            Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this,"레벨이 부족해요", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "you call ear");
                    //naverRecognizer.getSpeechRecognizer().stop();
                }
                if(exp>=levelHuddle){
                    ++level;
                    exp=0;
                    levtext.setText(String.valueOf(level));
                    //dbHelper.update(level);

                    Toast.makeText(MainActivity.this,"레벨이 올랐어요", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.recognitionError:
                if(writer!=null){
                    writer.close();
                }
                mResult="Error code:"+msg.obj.toString();
                Toast.makeText(MainActivity.this, mResult, Toast.LENGTH_SHORT).show();
                Start.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(getApplicationContext(), "LEVINFO.db", null, 1);
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();
        //level=dbHelper.getResult();


        //블루투스
        bt = new BluetoothSPP(this); //Initializing


        levtext=(TextView)findViewById(R.id.lev);
        levtext.setText(String.valueOf(level));

        //음성인식
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        Start = (ImageButton) findViewById(R.id.start_reg);
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionResult = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
                    if (permissionResult == PackageManager.PERMISSION_DENIED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("권한이 필요합니다.").setMessage("이 기능을 사용하기 위해서는 권한이 필요합니다. 계속하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                                            }
                                        }
                                    }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(MainActivity.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }).create().show();
                        } else {
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                        }
                    } else {
                        if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                            mResult = "";
                            //Toast.makeText(MainActivity.this, "준비중.....", Toast.LENGTH_SHORT).show();
                            naverRecognizer.recognize();
                        } else {
                            Log.d(TAG, "stop and wait Final Result");
                            Start.setEnabled(false);
                            naverRecognizer.getSpeechRecognizer().stop();
                        }
                    }
                } else {
                    if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                        mResult = "";
                        Toast.makeText(MainActivity.this, "준비중.....", Toast.LENGTH_SHORT).show();
                        naverRecognizer.recognize();
                    } else {
                        Log.d(TAG, "stop and wait Final Result");
                        Start.setEnabled(false);
                        naverRecognizer.getSpeechRecognizer().stop();
                    }
                }
            }
        });


        //블루투스
        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }
            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }
            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton btnConnect = findViewById(R.id.btnConnect); //연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
        ImageButton btnforward=findViewById(R.id.foward_B); // 앞으로
        btnforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.send("f", true);
                }
                else{
                    Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageButton btnback=findViewById(R.id.back_B); // 뒤로
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.send("b", true);
                }
                else{
                    Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageButton btnleft=findViewById(R.id.left_B); // 왼
        btnleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.send("l", true);
                }
                else{
                    Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageButton btnright=findViewById(R.id.right_B); // 오
        btnright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.send("r", true);
                }
                else{
                    Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageButton btnstop=findViewById(R.id.stop_B); // 멈춤
        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.send("s", true);
                }
                else{
                    Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnbob=findViewById(R.id.bob_B); // 밥주기
        btnbob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.send("j", true);
                    ++exp;
                }
                else{
                    Toast.makeText(MainActivity.this,"블루투스 연결이 안되어있어요", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        naverRecognizer.getSpeechRecognizer().initialize();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        naverRecognizer.getSpeechRecognizer().release();
    }
    static class RecognitionHandler extends Handler{
        private final WeakReference<MainActivity> mActivity;
        RecognitionHandler(MainActivity activity){
            mActivity=new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg){
            MainActivity activity=mActivity.get();
            if(activity!=null){
                activity.handleMessage(msg);
            }
        }
    }

    public void setup() {
        //Button btnSend = findViewById(R.id.btnSend); //데이터 전송
        //btnSend.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //        bt.send("f", true);
        //    }
        //});
    }
    public  boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }


    }


    //액션바 커스터마이징
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_title, null);

        actionBar.setCustomView(actionbar);



        return true;
    }

}
class  NaverRecognizer implements SpeechRecognitionListener{
    private final static String TAG=NaverRecognizer.class.getSimpleName();
    private Handler mHandler;
    private SpeechRecognizer mRecognizer;
    public NaverRecognizer(Context context,Handler handler,String clientId){
        this.mHandler=handler;
        try{
            mRecognizer=new SpeechRecognizer(context,clientId);
        } catch (SpeechRecognitionException e){
            e.printStackTrace();
        }
        mRecognizer.setSpeechRecognitionListener(this);
    }
    public SpeechRecognizer getSpeechRecognizer(){
        return mRecognizer;
    }
    public void recognize(){
        try {
            mRecognizer.recognize(new SpeechConfig(SpeechConfig.LanguageType.KOREAN,SpeechConfig.EndPointDetectType.AUTO));
        } catch (SpeechRecognitionException e){
            e.printStackTrace();
        }
    }
    @Override
    @WorkerThread
    public void onInactive(){
        Message msg=Message.obtain(mHandler,R.id.clientInactive);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onReady(){
        Message msg=Message.obtain(mHandler,R.id.clientReady);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onRecord(short[] speech){
        Message msg=Message.obtain(mHandler,R.id.audioRecording,speech);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onPartialResult(String result){
        Message msg=Message.obtain(mHandler,R.id.partialResult,result);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onEndPointDetected(){
        Log.d(TAG,"Event occured:EndpointDetected");
    }
    @Override
    @WorkerThread
    public void onResult(SpeechRecognitionResult result){
        Message msg=Message.obtain(mHandler,R.id.finalResult,result);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onError(int errorCode){
        Message msg=Message.obtain(mHandler,R.id.recognitionError,errorCode);
        msg.sendToTarget();
    }
    @Override
    @WorkerThread
    public void onEndPointDetectTypeSelected(SpeechConfig.EndPointDetectType epdType){
        Message msg=Message.obtain(mHandler,R.id.endPointDetectTypeSelected,epdType);
        msg.sendToTarget();
    }

}
