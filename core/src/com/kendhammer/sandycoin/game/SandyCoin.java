package com.kendhammer.sandycoin.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class SandyCoin extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture[] man;
    private Texture dizzy;
    private int manState = 0;
    private int pause = 0;
    private float gravity = 0.2f;
    private float velocity = 0;
    private int manY = 0;
    private int score = 0;
    private Random random;
    private Rectangle manRectangle;
    private BitmapFont font;

    private ArrayList<Integer> coinXs = new ArrayList<>();
    private ArrayList<Integer> coinYs = new ArrayList<>();
    private ArrayList<Rectangle> coinRetangles = new ArrayList<>();
    private Texture coin;
    private int coinCount;

    private ArrayList<Integer> bombXs = new ArrayList<Integer>();
    private ArrayList<Integer> bombYs = new ArrayList<Integer>();
    private ArrayList<Rectangle> bombRetangles = new ArrayList<>();
    private Texture bomb;
    private int bombCount;
    private int gameState = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");

        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");
        dizzy = new Texture("dizzy-1.png");

        manY = Gdx.graphics.getHeight() / 2;

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        random = new Random();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
        manRectangle = new Rectangle();
    }

    private void makeCoin() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    private void makeBomb() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        bombYs.add((int) height);
        bombXs.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if(gameState == 1){
            //Game is live
            //Make Bombs
            if (bombCount < 250) {
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }

            bombRetangles.clear();
            for (int i = 0; i < bombXs.size(); i++) {
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 8);
                bombRetangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
            }

            //Make Coins
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }
            coinRetangles.clear();
            for (int i = 0; i < coinXs.size(); i++) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 8);
                coinRetangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
            }

            if (Gdx.input.justTouched()) {
                velocity = -10;
            }
            if (pause < 4) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }

            velocity = velocity + gravity;
            manY -= velocity;

            if (manY <= 0) {
                manY = 0;
            }

        }else if(gameState == 0){
            //Waiting to start
            if(Gdx.input.justTouched()){
                gameState = 1;
            }
        }else if(gameState == 2){
            //Game Over
            if(Gdx.input.justTouched()){
                gameState = 1;
                manY = Gdx.graphics.getHeight() /2;
                score = 0;
                velocity = 0;
                coinXs.clear();
                coinYs.clear();
                coinRetangles.clear();
                coinCount = 0;
                bombXs.clear();
                bombYs.clear();
                bombCount = 0;
                bombRetangles.clear();
            }

        }

        if(gameState == 2){
            batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
        }else{
            batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);

        }
        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

        for (int i = 0; i < coinRetangles.size(); i++){
            if(Intersector.overlaps(manRectangle, coinRetangles.get(i))){
                score++;
                coinRetangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }

        for (int i = 0; i < bombRetangles.size(); i++){
            if(Intersector.overlaps(manRectangle, bombRetangles.get(i))){
                gameState = 2;
            }
        }

        font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
