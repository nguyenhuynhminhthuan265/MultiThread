package com.example.multithread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ProgressBar myBarHorizontal;


    EditText txtDataBox;
    TextView txtPercent;
    Button btnDoItAgain;
    int progressStep = 10;
    final int MAX_PROGRESS = 100;
    int globalVar = 0;
    int accum;

    {
        accum = 0;
    }
    long startingMills = System.currentTimeMillis();
    boolean isRunning = false;
    String PATIENCE = "Some important data is being collected now. "
            + "\nPlease be patient...wait...\n ";
    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtDataBox=findViewById(R.id.edtInput);
        btnDoItAgain=findViewById(R.id.btnClick);
        myBarHorizontal=findViewById(R.id.progressBar);

        txtPercent=findViewById(R.id.txtPercent);
        btnDoItAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnDoItAgain.setEnabled(false);
// reset and show progress bars
        accum = 0;
        myBarHorizontal.setMax(MAX_PROGRESS);
        myBarHorizontal.setProgress(0);
        myBarHorizontal.setVisibility(View.VISIBLE);

// create-start background thread were the busy work will be done
        Thread myBackgroundThread = new Thread( backgroundTask, "backAlias1" );
        myBackgroundThread.start();
    }

    private Runnable foregroundRunnable = new Runnable() {
        @Override
        public void run() {
            try {
// update UI, observe globalVar is changed in back thread

// advance ProgressBar
                myBarHorizontal.incrementProgressBy(progressStep);
                accum += progressStep;
                txtPercent.setText(accum+"%");

// are we done yet?
                if (accum >= myBarHorizontal.getMax()) {

                    myBarHorizontal.setVisibility(View.INVISIBLE);
                    accum=0;
                    btnDoItAgain.setEnabled(true);
                }
            } catch (Exception e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }
    };
    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {
// busy work goes here...
            try {
                for (int n = 0; n < 10; n++) {
// this simulates 1 sec. of busy activity
                    Thread.sleep(1000);
// change a global variable here...
                    globalVar++;
// try: next two UI operations should NOT work
// Toast.makeText(getApplication(), "Hi ", 1).show();
// txtDataBox.setText("Hi ");
// wake up foregroundRunnable delegate to speak for you
                    myHandler.post(foregroundRunnable);
                }
            } catch (InterruptedException e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }// run
    };// backgroundTask
}
