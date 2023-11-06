package com.example.ihc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
    private TextView textoTempo;


    TextView texto_pontos;
    TextView texto_vidas;
    ImageView lixo_metal;
    ImageView lixo_papel;
    ImageView lixo_plastico;
    ImageView lixo_vidro;
    ImageView lixo_imagem;


    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getHigh = getSharedPreferences("highscore", Context.MODE_PRIVATE);
        getMoeda = getSharedPreferences("moedas", Context.MODE_PRIVATE);

        highscore = getHigh.getLong("highscore", 0L);
        moedas = getMoeda.getLong("moedas", 0L);

    }
    public void onJogar(View view) {
        vidas = 3;
        pontos = 0;
        tempoRestante = 10000;
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

        lixo = new Random().nextInt(4);
        if (lixo == 0) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_orange_light));
        } else if (lixo == 1) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_blue_light));
        } else if (lixo == 2) {
            lixo_imagem.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
        } else {
            lixo_imagem.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light));
        }

        // Inicialize o contador de tempo
        startCountdown();

        View.OnClickListener lixoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    texto_pontos.setText("Pontos: " + pontos);
                } else {
                    vidas--;
                    texto_vidas.setText("Vidas: " + vidas);
                }

                // Sorteie um novo lixo após cada clique
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

                // Reinicie o contador de tempo
                countDownTimer.cancel();
                tempoRestante = 10000;
                startCountdown();

                // Verifique se o jogo acabou
                if (vidas <= 0) {
                    gameOver();
                }
            }
        };

        lixo_metal.setOnClickListener(lixoClickListener);
        lixo_papel.setOnClickListener(lixoClickListener);
        lixo_plastico.setOnClickListener(lixoClickListener);
        lixo_vidro.setOnClickListener(lixoClickListener);
    }

    private void atualizarTempoRestante() {
        try {
            textoTempo = findViewById(R.id.texto_tempo);
            textoTempo.setText("Tempo Restante: " + (tempoRestante / 1000) + "s");
        } catch (NullPointerException e) {

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

            public void onFinish() {
                tempoRestante = 0;
                atualizarTempoRestante();
                vidas--;
                texto_vidas.setText("Vidas: " + vidas);



                if (vidas <= 0) {
                    // Se o jogo terminar, chame a função de gameOver
                    gameOver();
                } else {
                    // Se não, reinicie o contador
                    tempoRestante = 10000;

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
                    startCountdown();
                }

            }
        }.start();
    }

    private void gameOver() {
        tempoRestante = 0;
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

    public void onPerfil(View view){

        setContentView(R.layout.activity_perfil);
        TextView texto_highscore = findViewById(R.id.texto_highscore);
        TextView texto_saldo = findViewById(R.id.texto_saldo);
        texto_highscore.setText("Seu recorde é: " + highscore);
        texto_saldo.setText("Seu saldo é: " + moedas);
    }



}