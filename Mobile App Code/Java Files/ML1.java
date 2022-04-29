package com.example.sdl_mobileinterface;

import static java.lang.Math.round;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sdl_mobileinterface.ml.Model1;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.nnapi.NnApiDelegate;

import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.io.IOException;
import java.nio.MappedByteBuffer;


public class ML1 extends AppCompatActivity {

    private EditText edHour;
    private TextView tvDay_label;
    private TextView tvHour_label;
    private EditText edDay;
    //private TextView tvDay;
    //private TextView tvHour;
    private TextView tvOcc;
    private Button btn;

    private float day = 0;
    private float hour = 0;
    //float occupancy = 0;

    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ml1);

        edHour = findViewById(R.id.edHour);
        tvDay_label = findViewById(R.id.tvDay_label) ;
        //tvHour = findViewById(R.id.tvHour);
        tvHour_label = findViewById(R.id.tvHour_label);
        edDay = findViewById(R.id.edDay);
        //tvDay = findViewById(R.id.tvDay);
        tvOcc = findViewById(R.id.tvOcc);
        btn = findViewById(R.id.btn);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                day = Float.valueOf(edDay.getText().toString());
                hour = Float.valueOf(edHour.getText().toString());
                String resultsOfAI = runAI(getCurrentFocus(), day, hour);
                //below should be how to run the machine learning in the app
                tvOcc.setText(resultsOfAI);
            }
        });

    }



    //everything with the below should be the tensorflow setup
    public String runAI(View v, float Day, float Hour) {
    //public String runAI(double Day, int Hour, double Occupancy) {
        //AI runner code goes here
        String FullNotFull = "Error!";


        //start running the TensorFlow Lite model here
        try {
            //Toast.makeText(this, "Connect request", Toast.LENGTH_SHORT).show();
            Model1 model= Model1.newInstance(this);
            //tflite = new Interpreter(loadModelFile());
            //Toast.makeText(this, "Loaded model", Toast.LENGTH_SHORT).show();
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 3}, DataType.FLOAT32);
            float[] inputBuffer = new float[3];

            inputBuffer[0] = (float) Day;   //day
            inputBuffer[1] = (float) Hour; //hour
            inputBuffer[2] = 0; //(float) Occupancy;  //Occupancy
            inputFeature0.loadArray(inputBuffer);

            // Runs model inference and gets result.
            Model1.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
            //Toast.makeText(this, "Model run successfully", Toast.LENGTH_SHORT).show();
            //process the outputs
            int numberOfOutputs = 1;
            float[] outputBuffer  = new float[numberOfOutputs];

            for (int i = 0; i < numberOfOutputs; i++) {
                outputBuffer[i] = outputFeature0.getFloatValue(i);
                //Log.d("UserActivity", "TEST");
            }
            int Capacity = 0;
            //if (outputBuffer[1]>outputBuffer[0]) Capacity = 1;

            Log.d("UserActivity", "Capacity = " + Capacity);
            if (Capacity == 0)  FullNotFull = "NotFull";
            if (Capacity == 1)  FullNotFull = "Full";
            Log.d("UserActivity", "Capacity = " + Capacity);
            Log.d("UserActivity", "FullNotFull = " + FullNotFull);
            //Log.d("UserActivity", snapshot.getKey() );
            //add confidence value to output string
            FullNotFull = FullNotFull + " (confidence = " + outputBuffer[Capacity] + "%)";

        } catch (Exception e) {
            e.printStackTrace();
        }
        //return the results
        //return " Day" + round(Day) + " Hour" + round(Hour) + " Availabiity" + FullNotFull;
        return "Availabiity = " + FullNotFull;
    }

//this ends the tensorflow machine learning setup
//

//    private MappedByteBuffer loadModelFile() throws IOException {
//        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("C:\Users\adrie\Documents\SUNY POLY\Spring 2022\ECE 488\Mobile Interface\app\src\main\ml\model1.tflite");
//        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
//        FileChannel fileChannel = inputStream.getChannel();
//        long startOffset = fileDescriptor.getStartOffset();
//        long declaredLength = fileDescriptor.getDeclaredLength();
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
//    }


}