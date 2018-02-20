//Criado por Eduardo dos santos - Megamil.net

package net.megamil.meuaudiominhavida;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    Button buttonStart, buttonStop, buttonPlayLastRecordAudio, buttonStopPlayingRecording ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = findViewById(R.id.button);
        buttonStop = findViewById(R.id.button2);
        buttonPlayLastRecordAudio = findViewById(R.id.button3);
        buttonStopPlayingRecording = findViewById(R.id.button4);

        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        random = new Random();

        /*****************************************/
        //Iniciar gravação
        /*****************************************/
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermission()) {
                    System.out.println("buttonStart.setOnClickListener");
                    AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        decrementarBarra();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Iniciou a gravação", Toast.LENGTH_LONG).show();
                }
                else {

                    requestPermission();

                }

            }
        });

        /*****************************************/
        //Parar manualmente
        /*****************************************/
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parar();
            }
        });

        /*****************************************/
        //Tocar audio recem gravado
        /*****************************************/
        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException, SecurityException, IllegalStateException {
                System.out.println("Tocando");
                Toast.makeText(MainActivity.this, "Tocando", Toast.LENGTH_LONG).show();

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();

                System.out.println("Audio aqui "+AudioSavePathInDevice);

                File file = new File(AudioSavePathInDevice);
                System.out.println("arquivo?" + file.getTotalSpace() + file.getAbsolutePath());
                byte[] bytes;
                try {
                    bytes = FileUtils.readFileToByteArray(file);
                    String encoded = Base64.encodeToString(bytes, 0);
                    Log.i("~~~~~~~~ Encoded: ", encoded);
                    System.out.print(encoded);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //byte[] decoded = Base64.decode(encoded, 0);
                //Log.i("~~~~~~~~ decoded: ", decoded.toString());


                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        /*****************************************/
        //Parar a reprodução
        /*****************************************/
        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("Parar de tocar");

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null){

                    mediaPlayer.stop();
                    mediaPlayer.release();

                    MediaRecorderReady();

                }

            }
        });
    }

    /*****************************************/
    //Decrementar progressBar
    /*****************************************/
    public void decrementarBarra(){
        final ProgressBar mProgressBar;
        CountDownTimer mCountDownTimer;
        final int[] i = {0};

        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setProgress(100);
        mCountDownTimer=new CountDownTimer(10000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                //Decrementa o progresso
                i[0]++;
                mProgressBar.setProgress((int) 100 - (i[0] *10));

            }

            @Override
            public void onFinish() {
                //Zera o progresso ao final
                System.out.println("Encerrado");
                i[0]++;
                mProgressBar.setProgress(0);
                parar();
            }
        };
        mCountDownTimer.start();
    }

    /*****************************************/
    //Parando a gravação do audio
    /*****************************************/
    public void parar() {

        mediaRecorder.stop();
        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(true);
        buttonStart.setEnabled(true);
        buttonStopPlayingRecording.setEnabled(false);

        Toast.makeText(MainActivity.this, "Gravação finalizada", Toast.LENGTH_LONG).show();

    }

    /*****************************************/
    //Preparando gravação e armazenamento
    /*****************************************/
    public void MediaRecorderReady(){

        System.out.println("Preparando Gravação");
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);

    }

    public String CreateRandomAudioFileName(int string){
        System.out.println("Gerando arquivo de audio");
        StringBuilder stringBuilder = new StringBuilder( string );

        int i = 0 ;
        while(i < string ) {

            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();

    }

    /*****************************************/
    //Solicitando permissão para Leitura/Gravação e Gravação/Tocar Audio
    /*****************************************/
    private void requestPermission() {
        System.out.println("Solicitando permissões");
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        System.out.println("onRequestPermissionsResult");
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {

                        Toast.makeText(MainActivity.this, "Permitido", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Negado",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean checkPermission() {
        System.out.println("checkPermission");
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
}
