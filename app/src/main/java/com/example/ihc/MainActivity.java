package com.example.ihc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {



    int vidas;
    long pontos;

    int lixo;
    private SharedPreferences getHigh;
    private SharedPreferences getMoeda;
    private long highscore;
    private long moedas;



    private long tempoRestante;
    private CountDownTimer countDownTimer;

    float indice;
    TextView texto_pontos;
    TextView texto_vidas;
    ImageView lixo_metal;
    ImageView lixo_papel;
    ImageView lixo_plastico;
    ImageView lixo_vidro;
    ImageView lixo_imagem;

    MediaPlayer mediaPlayer;

    // Dificuldade
    int acertos = 20; // a cada 20 acertos
    float reducao = 0.05F; // reduz 5% do tempo atual

    long tempo = 10000; // tempo inicial


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getHigh = getSharedPreferences("highscore", Context.MODE_PRIVATE);
        getMoeda = getSharedPreferences("moedas", Context.MODE_PRIVATE);

        highscore = getHigh.getLong("highscore", 0L);
        moedas = getMoeda.getLong("moedas", 0L);

        if(mediaPlayer !=null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.audio_menu);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mediaPlayer != null){
                    mediaPlayer.release();
                    mediaPlayer = null;

                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.audio_menu);
                    mediaPlayer.start();
                }
            }
        });


    }
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void onJogar(View view) {
        mediaPlayer.release();
        mediaPlayer = null;

        tempo = 10000;
        vidas = 3;
        pontos = 0;
        indice = 1;
        tempoRestante = tempo;
        setContentView(R.layout.activity_jogar);

        texto_pontos = findViewById(R.id.texto_pontos);
        texto_vidas = findViewById(R.id.texto_vidas);
        lixo_metal = findViewById(R.id.lixo_metal);
        lixo_papel = findViewById(R.id.lixo_papel);
        lixo_plastico = findViewById(R.id.lixo_plastico);
        lixo_vidro = findViewById(R.id.lixo_vidro);
        lixo_imagem = findViewById(R.id.lixo_imagem);

        lixo_metal.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        lixo_papel.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_blue_light));
        lixo_plastico.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
        lixo_vidro.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light));

        texto_vidas.setText("Vidas: " + vidas);
        texto_pontos.setText("Pontos: " + pontos);

        atualizarTempoRestante();

        GerarLixo();

        startCountdown();

        View.OnClickListener lixoClickListener = v -> {
            int lixoSelecionado = -1;
            int id = v.getId();

            if (id == R.id.lixo_metal) {
                lixoSelecionado = 0;
            } else if (id == R.id.lixo_papel) {
                lixoSelecionado = 1;
            } else if (id == R.id.lixo_plastico) {
                lixoSelecionado = 2;
            } else if (id == R.id.lixo_vidro) {
                lixoSelecionado = 3;
            }

            if (lixoSelecionado == lixo) {
                pontos++;
                if(mediaPlayer != null){

                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                    mediaPlayer = MediaPlayer.create(this, R.raw.audio_certo);
                    mediaPlayer.start();



                mediaPlayer = MediaPlayer.create(this, R.raw.audio_certo);
                mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(mediaPlayer != null){
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            });
                texto_pontos.setText("Pontos: " + pontos);
            } else {
                vidas = vidas - 1;
                if(mediaPlayer != null){

                    mediaPlayer.release();
                    mediaPlayer = null;
                }


                mediaPlayer = MediaPlayer.create(this, R.raw.audio_errado);
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(mediaPlayer != null){
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    }
                });
                tempo = tempo - 1000;
                texto_vidas.setText("Vidas: " + vidas);
            }

            GerarLixo();
            countDownTimer.cancel();

            if(pontos % acertos == 0){
                indice = (indice - reducao);
            }

            if ((long) (tempo * indice) < 2000){
                tempoRestante = 2000;
            } else {
                tempoRestante = (long) (tempo * indice);
            }
            startCountdown();

            if (vidas <= 0) {
                gameOver();
            }
        };

        lixo_metal.setOnClickListener(lixoClickListener);
        lixo_papel.setOnClickListener(lixoClickListener);
        lixo_plastico.setOnClickListener(lixoClickListener);
        lixo_vidro.setOnClickListener(lixoClickListener);


        lixo_imagem.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE);
                return true;
            }
            return false;
        });

        View.OnDragListener lixoDragListener = (v, event) -> {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.GREEN);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    return true;

                case DragEvent.ACTION_DROP:
                    int lixoSelecionado = -1;

                    if (v.getId() == R.id.lixo_metal) {
                        lixoSelecionado = 0;
                    } else if (v.getId() == R.id.lixo_papel) {
                        lixoSelecionado = 1;
                    } else if (v.getId() == R.id.lixo_plastico) {
                        lixoSelecionado = 2;
                    } else if (v.getId() == R.id.lixo_vidro) {
                        lixoSelecionado = 3;
                    }

                    if (lixoSelecionado != -1) {
                        if (lixoSelecionado == lixo) {
                            pontos++;
                            if(mediaPlayer != null){

                                mediaPlayer.release();
                                mediaPlayer = null;

                            }
                            mediaPlayer = MediaPlayer.create(this, R.raw.audio_certo);
                            mediaPlayer.start();


                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    if(mediaPlayer != null){
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                    }
                                }
                            });
                            texto_pontos.setText("Pontos: " + pontos);
                        } else {
                            vidas = vidas -1;
                            if(mediaPlayer != null){

                                mediaPlayer.release();
                                mediaPlayer = null;
                            }


                            mediaPlayer = MediaPlayer.create(this, R.raw.audio_errado);
                            mediaPlayer.start();

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    if(mediaPlayer != null){
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                    }
                                }
                            });
                            tempo -= 1000;
                            texto_vidas.setText("Vidas: " + vidas);
                        }

                        GerarLixo();
                        countDownTimer.cancel();

                        if (pontos % acertos == 0) {
                            indice = (indice - reducao);
                        }

                        if ((long) (tempo * indice) < 2000) {
                            tempoRestante = 2000;
                        } else {
                            tempoRestante = (long) (tempo * indice);
                        }
                        startCountdown();

                        if (vidas <= 0) {
                            gameOver();
                        }
                    }

                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.TRANSPARENT);


                    View dragView = (View) event.getLocalState();
                    dragView.setVisibility(View.VISIBLE);
                    return true;

                default:
                    return false;
            }
        };

        lixo_metal.setOnDragListener(lixoDragListener);
        lixo_papel.setOnDragListener(lixoDragListener);
        lixo_plastico.setOnDragListener(lixoDragListener);
        lixo_vidro.setOnDragListener(lixoDragListener);
    }


    @SuppressLint("SetTextI18n")
    private void atualizarTempoRestante() {
        try {
            TextView textoTempo = findViewById(R.id.texto_tempo);
            textoTempo.setText("Tempo Restante: " + (tempoRestante / 1000) + "s");
        } catch (NullPointerException ignored) {

        }
    }
    private void startCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(tempoRestante, 1000) {
            public void onTick(long millisUntilFinished) {
                tempoRestante = millisUntilFinished;
                atualizarTempoRestante();
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                tempoRestante = 0;








                atualizarTempoRestante();
                vidas = vidas - 1;

                if(mediaPlayer !=null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.audio_errado);
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(mediaPlayer != null){
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    }
                });


                tempo = tempo - 1000;
                texto_vidas.setText("Vidas: " + vidas);



                if (vidas <= 0) {

                    gameOver();
                } else {

                    tempoRestante = tempo;

                    GerarLixo();
                }

            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void gameOver() {

        countDownTimer.cancel();
        tempoRestante = 0;

        if(mediaPlayer != null){

            mediaPlayer.release();
            mediaPlayer = null;
        }


        mediaPlayer = MediaPlayer.create(this, R.raw.audio_gameover);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mediaPlayer != null){
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });

        setContentView(R.layout.activity_perder);
        TextView derrota = findViewById(R.id.texto_highscore);
        derrota.setText("Sua pontuação foi: " + pontos);
        moedas = moedas + pontos;
        SharedPreferences.Editor editMoedas = getMoeda.edit();
        editMoedas.putLong("moedas", moedas);
        editMoedas.apply();

        TextView pmaxima = findViewById(R.id.texto_pontuacaomaxima);
        TextView saldo = findViewById(R.id.texto_saldo);
        saldo.setText("Seu saldo atual é de: " + moedas);

        if (pontos > highscore) {
            highscore = pontos;
            SharedPreferences.Editor editHigh = getHigh.edit();
            editHigh.putLong("highscore", highscore);
            editHigh.apply();
            pmaxima.setText("Parabéns, você bateu seu recorde! Novo recorde: " + highscore);
        } else {
            pmaxima.setText("Seu recorde é: " + highscore);
        }
    }


    public void onPerder(View view){

        setContentView(R.layout.activity_main);
    }

    @SuppressLint("SetTextI18n")
    public void onPerfil(View view){

        setContentView(R.layout.activity_perfil);
        TextView texto_highscore = findViewById(R.id.texto_highscore);
        TextView texto_saldo = findViewById(R.id.texto_saldo);
        texto_highscore.setText("Seu recorde é: " + highscore);
        texto_saldo.setText("Seu saldo é: " + moedas);
    }



    public void GerarLixo(){


        lixo = new Random().nextInt(4);
        if (lixo == 0) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_light));
            if(mediaPlayer != null){

                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(this, R.raw.audio_metal);
            mediaPlayer.start();


        } else if (lixo == 1) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_blue_light));
            if(mediaPlayer != null){

                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(this, R.raw.audio_papel);
            mediaPlayer.start();
        } else if (lixo == 2) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));
            if(mediaPlayer != null){

                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(this, R.raw.audio_plastico);
            mediaPlayer.start();
        } else {
            lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_light));
            if(mediaPlayer != null){

                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = MediaPlayer.create(this, R.raw.audio_vidro);
            mediaPlayer.start();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                 if(mediaPlayer != null){
                     mediaPlayer.release();
                     mediaPlayer = null;
                 }
            }
        });
        startCountdown();

    }

}