package com.example.socket_2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity{

    private EditText edttext,ip,port;
    private TextView textview1,ps,ps2;
    private Button button1,connect;
    String tmp;                // 暫存文字訊息
    Socket clientSocket;
    Thread t;
    public static Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        edttext = (EditText)findViewById(R.id.edttext);
        ip = (EditText)findViewById(R.id.ip);
        port = (EditText)findViewById(R.id.port);
        button1 = (Button)findViewById(R.id.button1);
        connect = (Button)findViewById(R.id.connect);
        textview1 = (TextView)findViewById(R.id.textView1);
        ps = (TextView)findViewById(R.id.ps);
        ps2 = (TextView)findViewById(R.id.ps2);

        button1.setOnClickListener(btnlistener);
        connect.setOnClickListener(con);

    }
    private View.OnClickListener con = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            //啟動執行緒
            t = new Thread(readData);
            t.start();
        }
    };

    private View.OnClickListener btnlistener = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(clientSocket.isConnected()){

                BufferedWriter bw;
                try {
                    // 取得網路輸出串流
                    bw = new BufferedWriter( new OutputStreamWriter(clientSocket.getOutputStream()));
                    // 寫入訊息
                    bw.write(edttext.getText()+"\n");
                    // 立即發送
                    bw.flush();
                }
                catch (IOException e) {
                }
                // 將文字方塊清空
                edttext.setText("");
                ps2.setText("傳送成功");
            }
        }
    };

    // 顯示更新訊息
    private Runnable updateText = new Runnable() {
        public void run() {
            // 加入新訊息並換行
            textview1.append(tmp + "\n");
        }
    };

    // 取得網路資料
    private Runnable readData = new Runnable() {
        public void run() {
            // server端的IP
            InetAddress serverIp;
            try {

                serverIp = InetAddress.getByName(ip.getText().toString());
                int serverPort =Integer.parseInt( port.getText().toString().replaceAll("[\\D]", "")) ;
                clientSocket = new Socket(serverIp, serverPort);

                // 取得網路輸入串流
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));

                // 當連線後
                while (clientSocket.isConnected()) {
                    // 取得網路訊息
                    tmp = br.readLine();

                    // 如果不是空訊息則
                    if(tmp!=null)
                        // 顯示新的訊息
                        mHandler.post(updateText);
                }

            } catch (IOException e) {
            }
        }
    };

}