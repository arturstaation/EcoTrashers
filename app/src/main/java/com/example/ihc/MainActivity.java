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

    int pausado;
    private SharedPreferences getHigh;
    private SharedPreferences getMoeda;
    SharedPreferences getVolume;
    private long highscore;
    private long moedas;

    private int mutado;


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
        getVolume = getSharedPreferences("mutado", Context.MODE_PRIVATE);


        highscore = getHigh.getLong("highscore", 0L);
        moedas = getMoeda.getLong("moedas", 0L);
        mutado = getVolume.getInt("mutado", 0);

        vidas = 3;
        pontos = 0;
        lixo = -1;
        pausado = 0;

        if(mutado == 1){
            ImageView mute = findViewById(R.id.imagem_mutado);
            mute.setVisibility(View.VISIBLE);
            ImageView desmutado = findViewById(R.id.imagem_volume);
            desmutado.setVisibility(View.INVISIBLE);
        }else{

            ImageView mute = findViewById(R.id.imagem_mutado);
            mute.setVisibility(View.INVISIBLE);
            ImageView desmutado = findViewById(R.id.imagem_volume);
            desmutado.setVisibility(View.VISIBLE);
        }


    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void onJogar(View view) {


        setContentView(R.layout.activity_jogar);
        lixo_imagem = findViewById(R.id.lixo_imagem);
        if(pausado == 0){

            tempo = 10000;

            GerarLixo();

            indice = 1;

            tempoRestante = tempo;
        }

        else{

            if (lixo == 0) {
                lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_light));


            } else if (lixo == 1) {
                lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_blue_light));

            } else if (lixo == 2) {
                lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));

            } else {
                lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_light));


            }

            startCountdown();

        }



        texto_pontos = findViewById(R.id.texto_pontos);
        texto_vidas = findViewById(R.id.texto_vidas);
        lixo_metal = findViewById(R.id.lixo_metal);
        lixo_papel = findViewById(R.id.lixo_papel);
        lixo_plastico = findViewById(R.id.lixo_plastico);
        lixo_vidro = findViewById(R.id.lixo_vidro);

        lixo_metal.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        lixo_papel.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_blue_light));
        lixo_plastico.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
        lixo_vidro.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light));

        texto_vidas.setText("Vidas: " + vidas);
        texto_pontos.setText("Pontos: " + pontos);

        atualizarTempoRestante();


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
                if(mutado == 0) {
                    if (mediaPlayer != null) {

                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                    mediaPlayer = MediaPlayer.create(this, R.raw.audio_certo);
                    mediaPlayer.start();



                    mediaPlayer.setOnCompletionListener(mp -> {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    });

                }
                texto_pontos.setText("Pontos: " + pontos);
            } else {
                vidas = vidas - 1;
                if(mutado == 0) {
                    if (mediaPlayer != null) {

                        mediaPlayer.release();
                        mediaPlayer = null;
                    }


                    mediaPlayer = MediaPlayer.create(this, R.raw.audio_errado);
                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(mp -> {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    });
                }
                tempo = tempo - 1000;
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
                            if(mutado == 0) {
                                if (mediaPlayer != null) {

                                    mediaPlayer.release();
                                    mediaPlayer = null;

                                }
                                mediaPlayer = MediaPlayer.create(this, R.raw.audio_certo);
                                mediaPlayer.start();


                                mediaPlayer.setOnCompletionListener(mp -> {
                                    if (mediaPlayer != null) {
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                    }
                                });
                            }
                            texto_pontos.setText("Pontos: " + pontos);
                        } else {
                            vidas = vidas - 1;

                            if(mutado == 0) {
                                if (mediaPlayer != null) {

                                    mediaPlayer.release();
                                    mediaPlayer = null;
                                }


                                mediaPlayer = MediaPlayer.create(this, R.raw.audio_errado);
                                mediaPlayer.start();

                                mediaPlayer.setOnCompletionListener(mp -> {
                                    if (mediaPlayer != null) {
                                        mediaPlayer.release();
                                        mediaPlayer = null;
                                    }
                                });
                            }
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


                if(mutado == 0) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.audio_errado);
                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(mp -> {
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    });
                }

                tempo = tempo - 1000;
                texto_vidas.setText("Vidas: " + vidas);


                if (vidas <= 0) {

                    gameOver();
                } else {

                    tempoRestante = tempo;
                    startCountdown();

                    GerarLixo();
                }

            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void gameOver() {

        countDownTimer.cancel();
        tempoRestante = 0;

        if(mutado == 0) {
            if (mediaPlayer != null) {

                mediaPlayer.release();
                mediaPlayer = null;
            }


            mediaPlayer = MediaPlayer.create(this, R.raw.audio_gameover);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            });
        }

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


    public void onPerder(View view) {


        vidas = 3;
        pontos = 0;
        pausado = 0;
        setContentView(R.layout.activity_main);

        if(mutado == 1){
            ImageView mute = findViewById(R.id.imagem_mutado);
            mute.setVisibility(View.VISIBLE);
            ImageView desmutado = findViewById(R.id.imagem_volume);
            desmutado.setVisibility(View.INVISIBLE);
        }else{

            ImageView mute = findViewById(R.id.imagem_mutado);
            mute.setVisibility(View.INVISIBLE);
            ImageView desmutado = findViewById(R.id.imagem_volume);
            desmutado.setVisibility(View.VISIBLE);
        }

    }

    @SuppressLint("SetTextI18n")
    public void onPerfil(View view) {

        setContentView(R.layout.activity_perfil);
        TextView texto_highscore = findViewById(R.id.texto_highscore);
        TextView texto_saldo = findViewById(R.id.texto_saldo);
        texto_highscore.setText("Seu recorde é: " + highscore);
        texto_saldo.setText("Seu saldo é: " + moedas);
    }


    public void GerarLixo() {


        lixo = new Random().nextInt(4);
        if (lixo == 0) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_light));


        } else if (lixo == 1) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_blue_light));

        } else if (lixo == 2) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));

        } else {
            lixo_imagem.setColorFilter(ContextCompat.getColor(MainActivity.this, android.R.color.holo_green_light));


        }

    }
    @SuppressLint("SetTextI18n")

    public void onPausar(View view){

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        setContentView(R.layout.activity_pausado);
        TextView pontosatual = findViewById(R.id.texto_highscore);
        pontosatual.setText("Sua pontuação atual é: " + pontos);
        TextView vidasr = findViewById(R.id.texto_saldo);
        vidasr.setText("Você ainda tem " + vidas + " vidas");
        pausado = 1;


        if(mutado == 1){
            ImageView mute = findViewById(R.id.imagem_mutado);
            mute.setVisibility(View.VISIBLE);
            ImageView desmutado = findViewById(R.id.imagem_volume);
            desmutado.setVisibility(View.INVISIBLE);
        }else{

            ImageView mute = findViewById(R.id.imagem_mutado);
            mute.setVisibility(View.INVISIBLE);
            ImageView desmutado = findViewById(R.id.imagem_volume);
            desmutado.setVisibility(View.VISIBLE);
        }




    }

    public void onDesistir(View view){
        gameOver();
    }

    public void onMutar(View view){

        SharedPreferences.Editor edtiMutado = getVolume.edit();
        edtiMutado.putInt("mutado", 1);
        edtiMutado.apply();
        mutado = 1;
        ImageView mute = findViewById(R.id.imagem_mutado);
        mute.setVisibility(View.VISIBLE);
        ImageView desmutado = findViewById(R.id.imagem_volume);
        desmutado.setVisibility(View.INVISIBLE);


    }
    public void onDesmutar(View view){
        SharedPreferences.Editor edtiMutado = getVolume.edit();
        edtiMutado.putInt("mutado", 0);
        edtiMutado.apply();
        mutado = 0;
        ImageView mute = findViewById(R.id.imagem_mutado);
        mute.setVisibility(View.INVISIBLE);
        ImageView desmutado = findViewById(R.id.imagem_volume);
        desmutado.setVisibility(View.VISIBLE);
    }


}